package com.sil.facturas.presentationgui.fxcontrollers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sil.facturas.app.core.AppContext;
import com.sil.facturas.domain.enums._Ruta;
import com.sil.facturas.domain.interfaces.IDebugService;
import com.sil.facturas.infrastructure.config.UIDataConfig;
import com.sil.facturas.infrastructure.servicios.config.ConfigService;
import com.sil.facturas.infrastructure.servicios.ocr.BloqueDTO;
import com.sil.facturas.infrastructure.servicios.ocr.BloqueOCR;
import com.sil.facturas.infrastructure.servicios.ocr.ModeloOCR;
import com.sil.facturas.infrastructure.servicios.ocr.ModeloOCRService;
import com.sil.facturas.infrastructure.servicios.ocr.aux_ocr.OCRItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Screen;

public class FxROIEditorController {

  @FXML private ToolBar toolbar;
  @FXML private ScrollPane scrollPane;
  @FXML private Pane centerPane;
  @FXML private ImageView imageView;
  @FXML private Pane overlay;

  @FXML private Button btnROI;
  @FXML private Button btnAdd;
  @FXML private Button btnOCR;
  @FXML private Button btnListo;
  @FXML private Label lblCoords;
  @FXML private ComboBox<String> comboCamposOCR;

  private enum ModoEditor {
    ROI,
    BLOQUE
  }

  private ModoEditor modo = ModoEditor.ROI;

  private UIDataConfig uiData;
  private ModeloOCRService modOCRservice = new ModeloOCRService();
  private Rectangle currentRect;
  private double startX, startY;
  private double screenHeight;

  private double targetHeight;

  // private final List<Rectangle> listaROIs = new ArrayList();
  private List<BloqueOCR> bloques = new ArrayList<>();
  private final ModeloOCR.Builder builder = new ModeloOCR.Builder();

  private static final double MIN_WIDTH = 10;
  private static final double MIN_HEIGHT = 10;

  @FXML
  private void initialize() {

    screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
    targetHeight = screenHeight * 0.95;

    // if (AppContext.config() == null || AppContext.getUsuarioActual() != "admin") {
    //   try {
    //     AppContext.setConfigService(new ConfigService());
    //   } catch (Exception e) {
    //     e.printStackTrace();
    //   }
    // }

    cargarUIData(
        Paths.get(
            _Ruta.CONFIG.getRuta()
                + "/"
                + AppContext.getUsuarioActual().toUpperCase()
                + "/uidata.json"));

    imageView.setPreserveRatio(true);
    imageView.setFitHeight(targetHeight);

    overlay.setPickOnBounds(true);
    overlay.setMouseTransparent(false);

    imageView
        .boundsInParentProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              double w = newVal.getWidth();
              double h = newVal.getHeight();

              centerPane.setPrefSize(w, h);
              overlay.setPrefSize(w, h);

              overlay.setLayoutX(newVal.getMinX());
              overlay.setLayoutY(newVal.getMinY());
            });

    overlay.setOnMousePressed(this::iniciarROI);
    overlay.setOnMouseDragged(this::actualizarROI);
    overlay.setOnMouseReleased(this::finalizarROI);

    // NO cargar bloques aquí
    // preguntarYcargarBloques();
  }

  // ============================
  // CARGAR IMAGEN DESDE FUERA
  // ============================

  public void cargarImagen(String ruta) throws FileNotFoundException {
    File f = new File(ruta);
    Image img = new Image(new FileInputStream(f));
    imageView.setImage(img);

    this.modOCRservice.setImagenBase(img);
    this.modOCRservice.setInfoModelo(ruta, 600, "Modelo OCR", "1.0");
  }

  public void cargarUIData(Path pathUIData) {
    try {
      this.uiData = UIDataConfig.fromJson(pathUIData);

      // Cargar en el ComboBox
      cargarComboCamposOCR(this.uiData);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void cargarBloquesDespuesDeImagen() {

    // 1) Preguntar si quiere cargar bloques
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Cargar bloques");
    alert.setHeaderText("¿Deseas cargar un archivo de bloques?");
    alert.setContentText("Esto cargará y normalizará los bloques sobre la imagen actual.");

    ButtonType btnSi = new ButtonType("Sí");
    ButtonType btnNo = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

    alert.getButtonTypes().setAll(btnSi, btnNo);

    Optional<ButtonType> result = alert.showAndWait();
    if (result.isEmpty() || result.get() == btnNo) {
      return; // Usuario dijo NO → no hacemos nada
    }

    // 2) Elegir archivo JSON
    FileChooser fc = new FileChooser();
    fc.setTitle("Selecciona un archivo de bloques");
    fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON", "*.json"));

    File f = fc.showOpenDialog(null);
    if (f == null) return;

    try {
      bloques = cargarBloquesJSON(f.toPath());

      // 3) Normalizar usando tamaño REAL de la imagen cargada
      double imgW = imageView.getImage().getWidth();
      double imgH = imageView.getImage().getHeight();

      normalizarBloques(bloques, imgW, imgH);

      // 4) Dibujar en pantalla
      dibujarBloquesEnOverlay();

      // 5) Registrar como ROIs en el ModeloOCR
      registrarBloquesComoROIs();

    } catch (Exception e) {
      IDebugService.printError("Error cargando bloques: " + e.getMessage());
    }
  }

  // ============================
  // INICIAR ROI
  // ============================
  private void iniciarROI(MouseEvent e) {
    startX = e.getX();
    startY = e.getY();

    currentRect = new Rectangle(startX, startY, 0, 0);

    if (modo == ModoEditor.BLOQUE) {
      currentRect.setStroke(Color.CYAN);
      currentRect.getStrokeDashArray().addAll(6.0, 4.0);
    } else {
      currentRect.setStroke(Color.YELLOW);
    }
    currentRect.setFill(Color.WHITE);
    currentRect.setStrokeWidth(2);

    // Abrir agujero visual
    currentRect.setBlendMode(BlendMode.DIFFERENCE);

    // No robar eventos
    currentRect.setMouseTransparent(true);

    overlay.getChildren().add(currentRect);
  }

  // ============================
  // ACTUALIZAR ROI
  // ============================
  private void actualizarROI(MouseEvent e) {
    double x = Math.min(startX, e.getX());
    double y = Math.min(startY, e.getY());
    double w = Math.abs(e.getX() - startX);
    double h = Math.abs(e.getY() - startY);

    currentRect.setX(x);
    currentRect.setY(y);
    currentRect.setWidth(w);
    currentRect.setHeight(h);

    lblCoords.setText(String.format("%.1f,%.1f → %.1f,%.1f", startX, startY, e.getX(), e.getY()));
  }

  private void OCRTest() {
    // VALIDACIONES BÁSICAS
    // SI RECT ES NULL
    if (currentRect == null) {
      Alert alert =
          new Alert(
              Alert.AlertType.WARNING, "[FxROIEditorController>OCRTest] No hay ROI seleccionado.");
      alert.showAndWait();
      return;
    }
    // SI EL RECTANG TIENE ALGÜN VALOR CERO O NEGATIVO
    double w = currentRect.getWidth();
    double h = currentRect.getHeight();

    if (w <= 0 || h <= 0) {
      new Alert(
              Alert.AlertType.WARNING,
              "El ROI es inválido: ancho o alto cero.\n"
                  + "Dibuja el rectángulo de arriba a abajo y de izquierda a derecha.")
          .showAndWait();
      return;
    }

    String texto =
        this.modOCRservice.realizarOCR(
            currentRect,
            imageView.getBoundsInParent().getWidth(),
            imageView.getBoundsInParent().getHeight());

    IDebugService.print("[ROIEditor>OCRTest] TEXTO DETECTADO:" + texto);
    // Alert alert = new Alert(Alert.AlertType.INFORMATION);
    // alert.setTitle("Resultado OCR");
    // alert.setHeaderText("Texto detectado:");
    // alert.setContentText(texto);
    // alert.showAndWait();
  }

  // ============================
  // FINALIZAR ROI
  // ============================
  private void finalizarROI(MouseEvent e) {
    IDebugService.print("ROI final: " + currentRect.getX() + ", " + currentRect.getY());
    OCRTest();
  }

  @FXML
  private void onListo() {
    if (this.modOCRservice.builder.zonas.isEmpty()) {
      new Alert(Alert.AlertType.WARNING, "[FxROIEditorController>onListo] No hay zonas definidas.")
          .showAndWait();
      return;
    }

    try {
      // Preguntar nombre del modelo
      TextInputDialog dialog = new TextInputDialog();
      dialog.setTitle("Guardar modelo OCR");
      dialog.setHeaderText("Introduce el nombre del modelo OCR");
      dialog.setContentText("Nombre:");

      Optional<String> result = dialog.showAndWait();
      if (result.isEmpty()) return;

      String nombreModelo = result.get().trim();
      if (nombreModelo.isEmpty()) return;

      // GUARDAR EL NOMBRE EN EL BUILDER
      this.modOCRservice.setInfoModelo(
          this.modOCRservice.rutaImagen, // ya la tienes guardada
          600, // o el dpi real
          nombreModelo, // <-- aquí el nombre del modelo de factura
          "1.0");
      // Construir modelo final
      ModeloOCR modelo = this.modOCRservice.build();
      this.modOCRservice.guardarJSON(
          modelo, Paths.get(_Ruta.MODELOSOCR.getRuta() + "/modelo_" + nombreModelo + ".json"));

    } catch (Exception e) {
      IDebugService.printError("Error " + e.getMessage());
    }
  }

  @FXML
  private void onAddROI() {
    double w = currentRect.getWidth();
    double h = currentRect.getHeight();

    if (w < MIN_WIDTH || h < MIN_HEIGHT) {
      new Alert(
              Alert.AlertType.WARNING,
              "El ROI es demasiado pequeño (" + (int) w + "x" + (int) h + "). Repita el recuadro.")
          .showAndWait();
      return;
    }
    if (currentRect == null) {
      IDebugService.printError("[FxROIEditorController>onAddROI] No hay ROI seleccionado");
      return;
    }

    String nombre = comboCamposOCR.getValue();
    if (nombre == null) {
      new Alert(Alert.AlertType.WARNING, "Selecciona un campo OCR").showAndWait();
      return;
    }
    nombre = nombre.trim();
    if (nombre.isBlank()) {
      new Alert(Alert.AlertType.WARNING, "Introduce un nombre en la barra superior").showAndWait();
      return;
    }
    if (nombre.startsWith("[BLOQUE]")) {
      modo = ModoEditor.BLOQUE;
      nombre = nombre.replace("[BLOQUE] ", "").trim();
    } else {
      modo = ModoEditor.ROI;
    }

    double viewW = imageView.getBoundsInParent().getWidth();
    double viewH = imageView.getBoundsInParent().getHeight();

    if (modOCRservice.isROIEmpty(currentRect, imageView)) {
      new Alert(Alert.AlertType.WARNING, "El ROI no contiene información útil.").showAndWait();
      return;
    }

    if (modo == ModoEditor.BLOQUE) {

      BloqueOCR b = new BloqueOCR(nombre, nombre, "generico");

      b.setX(currentRect.getX() / viewW);
      b.setY(currentRect.getY() / viewH);
      b.setWidth(currentRect.getWidth() / viewW);
      b.setHeight(currentRect.getHeight() / viewH);

      bloques.add(b);

      // AÑADIR ROI AL MODELO OCR
      modOCRservice.addROI(nombre, currentRect, viewW, viewH);

      IDebugService.print("[ROIEditor] Bloque creado: " + b);
      dibujarBloquesEnOverlay();
      onGuardarBloques(); // Guardar bloques antes de añadir la ROI, para que el service tenga la
                          // info actualizada

      return; // ← IMPORTANTE: no llamar a modOCRservice
    }

    // USAR SOLO EL SERVICE
    this.modOCRservice.addROI(nombre, currentRect, viewW, viewH);

    IDebugService.print("[FxROIEditorController>onAddROI] Zona '" + nombre + "' guardada.");

    comboCamposOCR.setValue(null);
  }

  private void cargarComboCamposOCR(UIDataConfig uiData) {
    comboCamposOCR.getItems().clear();

    // 1. Campos OCR (valores)
    uiData.getOcr().stream()
        .filter(item -> "campo".equals(item.getType()))
        .map(OCRItem::getId)
        .forEach(comboCamposOCR.getItems()::add);

    // 2. Bloques OCR (estructuras)
    uiData.getOcr().stream()
        .filter(item -> "bloque".equals(item.getType()))
        .map(item -> "[BLOQUE] " + item.getId())
        .forEach(comboCamposOCR.getItems()::add);

    // 3. Parámetros especiales
    uiData.getOcr().stream()
        .filter(item -> "parametro".equals(item.getType()))
        .map(item -> "[PARAM] " + item.getId())
        .forEach(comboCamposOCR.getItems()::add);

    comboCamposOCR.getSelectionModel().clearSelection();
  }

  // private void onCargarBloques() {
  //     try {
  //     bloques = cargarBloquesJSON(Path.of("bloques.json"));

  //     overlay.getChildren().clear();
  //     for (BloqueOCR b : bloques) {
  //         dibujarBloquesEnOverlay();
  //     }

  //     System.out.println("Bloques cargados.");
  //     } catch (Exception e) {
  //     IDebugService.printError("Error " + e.getMessage());
  //     }
  // }

  private void onGuardarBloques() {
    try {
      guardarBloquesJSON(Path.of("bloques.json"), bloques);
      System.out.println("Bloques guardados.");
    } catch (Exception e) {
      IDebugService.printError("Error " + e.getMessage());
    }
  }

  public void guardarBloquesJSON(Path path, List<BloqueOCR> bloques) throws IOException {
    List<BloqueDTO> lista = new ArrayList<>();
    for (BloqueOCR b : bloques) {
      lista.add(BloqueDTO.toDTO(b));
    }

    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    Files.writeString(path, gson.toJson(lista));
  }

  public List<BloqueOCR> cargarBloquesJSON(Path path) throws IOException {
    Gson gson = new Gson();
    Type tipoLista = new TypeToken<List<BloqueDTO>>() {}.getType();

    List<BloqueDTO> listaDTO = gson.fromJson(Files.readString(path), tipoLista);

    List<BloqueOCR> resultado = new ArrayList<>();
    for (BloqueDTO dto : listaDTO) {
      resultado.add(BloqueDTO.fromDTO(dto));
    }

    return resultado;
  }

  // public void guardar() {
  //     Path p = Path.of("bloques.json");
  //     try {
  //         guardarBloquesJSON(p, bloques);
  //     } catch (IOException e) {
  //         IDebugService.printError("Error " + e.getMessage());
  //     }
  // }

  // public void cargar() {
  //     Path p = Path.of("bloques.json");
  //     try {
  //         bloques = cargarBloquesJSON(p);
  //     } catch (IOException e) {
  //         IDebugService.printError("Error " + e.getMessage());
  //     }

  // // Redibujar
  // dibujarBloquesEnOverlay();
  // }

  private void dibujarBloquesEnOverlay() {
    overlay.getChildren().clear();

    double viewW = imageView.getBoundsInParent().getWidth();
    double viewH = imageView.getBoundsInParent().getHeight();

    for (BloqueOCR b : bloques) {
      Rectangle r =
          new Rectangle(
              b.getX() * viewW, b.getY() * viewH, b.getWidth() * viewW, b.getHeight() * viewH);

      r.setStroke(Color.CYAN);
      r.getStrokeDashArray().addAll(6.0, 4.0);
      r.setFill(Color.TRANSPARENT);

      overlay.getChildren().add(r);
    }
  }

  //   private void dibujarBloqueEnCanvas(BloqueOCR b) {
  //     double viewW = imageView.getBoundsInParent().getWidth();
  //     double viewH = imageView.getBoundsInParent().getHeight();

  //     Rectangle r =
  //         new Rectangle(
  //             b.getX() * viewW, b.getY() * viewH, b.getWidth() * viewW, b.getHeight() * viewH);

  //     r.setStroke(Color.CYAN);
  //     r.getStrokeDashArray().addAll(6.0, 4.0);
  //     r.setFill(Color.TRANSPARENT);

  //     overlay.getChildren().add(r);
  //   }

  private void normalizarBloques(List<BloqueOCR> bloques, double imgW, double imgH) {
    for (BloqueOCR b : bloques) {
      b.setX(b.getX() / imgW);
      b.setY(b.getY() / imgH);
      b.setWidth(b.getWidth() / imgW);
      b.setHeight(b.getHeight() / imgH);
    }
  }

  private void registrarBloquesComoROIs() {
    double viewW = imageView.getImage().getWidth();
    double viewH = imageView.getImage().getHeight();

    for (BloqueOCR b : bloques) {
      // Convertir bloque normalizado a rectángulo absoluto
      double x = b.getX() * viewW;
      double y = b.getY() * viewH;
      double w = b.getWidth() * viewW;
      double h = b.getHeight() * viewH;

      Rectangle r = new Rectangle(x, y, w, h);

      // Registrar como ROI en el ModeloOCR
      modOCRservice.addROI(b.getNombre(), r, viewW, viewH);
    }
  }
}
