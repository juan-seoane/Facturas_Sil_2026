package com.sil.facturas.presentationgui.fxcontrollers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sil.facturas.app.core.AppContext;
import com.sil.facturas.domain.enums._Ruta;
import com.sil.facturas.domain.interfaces.IDebugService;
import com.sil.facturas.domain.records.ROI;
import com.sil.facturas.infrastructure.config.UIDataConfig;
import com.sil.facturas.infrastructure.servicios.config.ConfigService;
import com.sil.facturas.infrastructure.servicios.ocr.ModeloOCR;
import com.sil.facturas.infrastructure.servicios.ocr.ModeloOCRService;
import com.sil.facturas.infrastructure.servicios.ocr.aux_ocr.*;
import com.sil.facturas.infrastructure.servicios.ocr.editor.ModeloOCRParser;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
  private final ModeloOCRParser ocrParser = new ModeloOCRParser();
  private final ModeloOCR.Builder builder = new ModeloOCR.Builder();

  private static final double MIN_WIDTH = 10;
  private static final double MIN_HEIGHT = 10;

  @FXML
  private void initialize() {

    screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
    targetHeight = screenHeight * 0.95;

    if (AppContext.getConfigService() == null || AppContext.getUsuarioActual() != "admin") {
      try {
        AppContext.setUsuarioActual("admin");
        AppContext.setConfigService(new ConfigService());
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

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

      //normalizarBloques(bloques, imgW, imgH);

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
  private void onCargarModelo() {

    Alert tipoDialog = new Alert(Alert.AlertType.CONFIRMATION);
    tipoDialog.setTitle("Cargar modelo");
    tipoDialog.setHeaderText("¿Qué tipo de archivo quieres cargar?");
    tipoDialog.setContentText("Selecciona una opción:");

    ButtonType btnModelo = new ButtonType("Modelo OCR completo");
    ButtonType btnBloques = new ButtonType("Modelo de bloques");
    ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

    tipoDialog.getButtonTypes().setAll(btnModelo, btnBloques, btnCancelar);

    Optional<ButtonType> tipo = tipoDialog.showAndWait();
    if (tipo.isEmpty() || tipo.get() == btnCancelar) return;

    FileChooser fc = new FileChooser();
    fc.setInitialDirectory(new File(_Ruta.MODELOSOCR.getRuta()));

    if (tipo.get() == btnBloques) {
      fc.setTitle("Cargar modelo de bloques");
      fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON", "*.json"));
      File f = fc.showOpenDialog(null);
      if (f != null) cargarBloques(f.toPath());
      return;
    }

    if (tipo.get() == btnModelo) {
      fc.setTitle("Cargar modelo OCR completo");
      fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON", "*.json"));
      File f = fc.showOpenDialog(null);
      if (f != null) cargarModeloOCR(f.toPath());
    }
  }

    @FXML
    private void onListo() {

    // 1. Comprobar que hay ROIs
    if (this.modOCRservice.builder.zonas.isEmpty()) {
        new Alert(Alert.AlertType.WARNING,
            "[FxROIEditorController>onListo] No hay zonas definidas.")
            .showAndWait();
        return;
    }

    // 2. Preguntar qué tipo de guardado quiere el usuario
    Alert tipoDialog = new Alert(Alert.AlertType.CONFIRMATION);
    tipoDialog.setTitle("Guardar modelo");
    tipoDialog.setHeaderText("¿Cómo quieres guardar este trabajo?");
    tipoDialog.setContentText("Elige una opción:");

    ButtonType btnBloques = new ButtonType("Guardar como BLOQUES");
    ButtonType btnModelo = new ButtonType("Guardar como MODELO OCR");
    ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

    tipoDialog.getButtonTypes().setAll(btnBloques, btnModelo, btnCancelar);

    Optional<ButtonType> tipo = tipoDialog.showAndWait();
    if (tipo.isEmpty() || tipo.get() == btnCancelar) return;

    // 3. Pedir nombre del archivo
    TextInputDialog dialog = new TextInputDialog();
    dialog.setTitle("Nombre del archivo");
    dialog.setHeaderText("Introduce el nombre base del archivo");
    dialog.setContentText("Nombre:");

    Optional<String> result = dialog.showAndWait();
    if (result.isEmpty()) return;

    String nombre = result.get().trim();
    if (nombre.isEmpty()) return;

    try {

        // 4. GUARDAR SOLO BLOQUES
        if (tipo.get() == btnBloques) {

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Path destino = Paths.get(_Ruta.MODELOSOCR.getRuta() + "/bloques_" + nombre + ".json");


            guardarBloquesJSON(destino, bloques);

            IDebugService.print("[ROIEditor] Guardado bloques en: " + destino);
            return;
        }

        // 5. GUARDAR MODELO OCR COMPLETO
        if (tipo.get() == btnModelo) {

            // Guardar info del modelo en el builder
            this.modOCRservice.setInfoModelo(
                this.modOCRservice.rutaImagen,
                600,
                nombre,
                "1.0"
            );

            // Construir modelo final
            ModeloOCR modelo = this.modOCRservice.build();
            ModeloOCRDTO dto = ModeloOCRDTO.toDTO(modelo);

            Path destino = Paths.get(_Ruta.MODELOSOCR.getRuta() + "/modelo_" + nombre + ".json");
            this.modOCRservice.guardarJSON(dto, destino);

            IDebugService.print("[ROIEditor] Guardado modelo OCR en: " + destino);
        }

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

      // SOLO pedir nombre si es un bloque genérico
      if (nombre.equalsIgnoreCase("bloque")
          || nombre.equalsIgnoreCase("generico")
          || nombre.equalsIgnoreCase("nuevoBloque")) {

        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Nuevo bloque genérico");
        dialog.setHeaderText("Introduce el nombre del bloque");
        dialog.setContentText("Nombre:");

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty() || result.get().isBlank()) {
          new Alert(Alert.AlertType.WARNING, "Debes introducir un nombre para el bloque")
              .showAndWait();
          return;
        }

        nombre = result.get().trim();
      }

      // Crear bloque con el nombre final
      BloqueOCR b = new BloqueOCR(nombre, nombre, "generico");

      b.setX(currentRect.getX() / viewW);
      b.setY(currentRect.getY() / viewH);
      b.setWidth(currentRect.getWidth() / viewW);
      b.setHeight(currentRect.getHeight() / viewH);

      bloques.add(b);

      modOCRservice.addROI(nombre, currentRect, viewW, viewH);

      IDebugService.print("[ROIEditor] Bloque creado: " + b);
      dibujarBloquesEnOverlay();
      onGuardarBloques();

      return;
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

  private void dibujarBloquesEnOverlay() {
    overlay.getChildren().clear();

    double viewW = imageView.getBoundsInParent().getWidth();
    double viewH = imageView.getBoundsInParent().getHeight();

    for (BloqueOCR b : bloques) {
      IDebugService.print("[FxCntrlROIEditor>dibujarBloquesEnOverlay] Bloque: " + b);
      Rectangle r =
          new Rectangle(
              b.getX() * viewW, b.getY() * viewH, b.getWidth() * viewW, b.getHeight() * viewH);

      r.setStroke(Color.CYAN);
      r.getStrokeDashArray().addAll(6.0, 4.0);
      r.setFill(Color.TRANSPARENT);

      overlay.getChildren().add(r);
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

  private void cargarModeloOCR(Path path) {
    try {
        // 1. Parsear modelo OCR completo

      ModeloOCR modelo = ocrParser.parse(path.toFile());

      // 2. Limpiar overlay
      overlay.getChildren().clear();

      // 3. Dibujar ZONAS del modelo (las ROIs del builder)
      double viewW = imageView.getImage().getWidth();
      double viewH = imageView.getImage().getHeight();

      for (Map.Entry<String, ROI> entry : modelo.zonas().entrySet()) {
        ROI roi = entry.getValue();

        Rectangle r = new Rectangle(roi.x1(), roi.y1(), roi.x2() - roi.x1(), roi.y2() - roi.y1());

        r.setStroke(Color.CYAN);
        r.setFill(Color.TRANSPARENT);
        overlay.getChildren().add(r);

        // Registrar en el builder
        modOCRservice.addROI(entry.getKey(), r, viewW, viewH);
      }
      IDebugService.print("[FxCntrlROIEditor>cargarModeloOCR] Zonas: " + modelo.zonas());
      IDebugService.print("[FxCntrlROIEditor>cargarModeloOCR] Bloques: " + modelo.bloques);

      // 4. Dibujar BLOQUES del modelo
      for (Bloque b: modelo.bloques)
      {
        IDebugService.print("[FxCntrlROIEditor>cargarModeloOCR] Bloque: " + b + " - Zona: " + b.zona);
        dibujarBloqueDeModelo(b);
      }

      IDebugService.print("[ROIEditor] Modelo OCR cargado desde: " + path);

    } catch (Exception e) {
      IDebugService.printError("[ROIEditor] Error cargando modelo OCR: " + e.getMessage());
    }
  }

  private void cargarBloques(Path path) {
      try {
          // 1. Cargar lista de BloqueOCR desde JSON
          bloques = cargarBloquesJSON(path);

          // 2. Dibujar en overlay (usa coordenadas relativas)
          dibujarBloquesEnOverlay();

          // 3. Registrar ROIs en el ModeloOCRService
          registrarBloquesComoROIs();

          IDebugService.print("[ROIEditor] Bloques cargados desde: " + path);

      } catch (Exception e) {
          IDebugService.printError("[ROIEditor] Error cargando bloques: " + e.getMessage());
      }
  }

  private void dibujarBloqueDeModelo(Bloque b) {

    // BLOQUE
    Rectangle rb =
        new Rectangle(
            b.zona.x1(), b.zona.y1(),
            b.zona.width(), b.zona.height());
    rb.setStroke(Color.RED);
    rb.setFill(Color.TRANSPARENT);
    overlay.getChildren().add(rb);

    // CABECERA
    if (b.cabecera != null) {
      Rectangle rc =
          new Rectangle(
              b.cabecera.x1(), b.cabecera.y1(),
              b.cabecera.width(), b.cabecera.height());
      rc.setStroke(Color.BLUE);
      rc.setFill(Color.TRANSPARENT);
      overlay.getChildren().add(rc);
    }

    // LINEA BASE
    if (b.lineaBase != null) {
      Rectangle rl =
          new Rectangle(
              b.lineaBase.x1(), b.lineaBase.y1(),
              b.lineaBase.width(), b.lineaBase.height());
      rl.setStroke(Color.GREEN);
      rl.setFill(Color.TRANSPARENT);
      overlay.getChildren().add(rl);
    }

    // FINAL EXTRACTOS
    if (b.finalExtractos != null) {
      Rectangle rf =
          new Rectangle(
              b.finalExtractos.x1(), b.finalExtractos.y1(),
              b.finalExtractos.width(), b.finalExtractos.height());
      rf.setStroke(Color.MAGENTA);
      rf.setFill(Color.TRANSPARENT);
      overlay.getChildren().add(rf);
    }

    // CAMPOS
    for (Campo c : b.campos) {
      Rectangle rc =
          new Rectangle(
              c.zona().x1(), c.zona().y1(),
              c.zona().width(), c.zona().height());
      rc.setStroke(Color.ORANGE);
      rc.setFill(Color.TRANSPARENT);
      overlay.getChildren().add(rc);
    }
  }
}
