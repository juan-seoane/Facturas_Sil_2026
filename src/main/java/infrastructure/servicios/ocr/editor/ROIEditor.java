package infrastructure.servicios.ocr.editor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import domain.records.ROI;
import infrastructure.filesystem._Ruta;
import infrastructure.servicios.ocr.OCRService;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class ROIEditor extends Application {

  private enum Modo {
    NINGUNO,
    ROI
  }

  private Modo modoActual = Modo.NINGUNO;

  private Image imagenOriginal;
  private ImageView imageView;

  private double scaleX = 1.0;
  private double scaleY = 1.0;

  private double scaleX_ROI;
  private double scaleY_ROI;

  private double startX, startY;
  private Rectangle currentRect;

  private TextField campoNombre;
  private Label lblCoords;

  private Map<String, ROI> zonas = new LinkedHashMap<>();

  private String rutaImagen;

  @Override
  public void start(Stage stage) throws Exception {

    // ============================================================
    // 1) Cargar imagen desde disco
    // ============================================================
    String usuario = "admin";
    String nombreImagen = "test1.png";

    rutaImagen = _Ruta.DATOS.getRuta() + "/" + usuario.toUpperCase() + "/scans/" + nombreImagen;

    File f = new File(rutaImagen);
    if (!f.exists()) {
      new Alert(Alert.AlertType.ERROR, "[ROIEditor] La imagen NO existe:\n" + rutaImagen)
          .showAndWait();
      return;
    }

    imagenOriginal = new Image(new FileInputStream(f));

    // ============================================================
    // 2) Crear ImageView y activar autoescalado
    // ============================================================
    imageView = new ImageView(imagenOriginal);
    imageView.setPreserveRatio(true);
    imageView.setSmooth(true);

    // Activar modo autoescalado
    imageView.setFitWidth(1);
    imageView.setFitHeight(1);

    // NO usar mouseTransparent → rompe layout y eventos
    imageView.setPickOnBounds(false);

    // Recalcular escala real cuando la imagen se renderice
    imageView
        .imageProperty()
        .addListener(
            (obs, oldImg, newImg) -> {
              if (newImg != null) Platform.runLater(() -> actualizarEscala());
            });

    // ============================================================
    // 3) Crear overlay (capa transparente para dibujar ROIs)
    // ============================================================
    Pane overlay = new Pane();
    overlay.setPickOnBounds(true); // debe recibir eventos
    overlay.setMouseTransparent(false);

    // ============================================================
    // 4) Recolocar overlay EXACTAMENTE encima de la imagen
    // ============================================================
    imageView
        .boundsInParentProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              overlay.relocate(newVal.getMinX(), newVal.getMinY());
              overlay.setPrefSize(newVal.getWidth(), newVal.getHeight());
            });

    // ============================================================
    // 5) Eventos del overlay (dibujo del ROI)
    // ============================================================
    overlay.setOnMousePressed(e -> iniciarROI(e, overlay));
    overlay.setOnMouseDragged(this::actualizarROI);


    // ============================================================
    // 6) Contenedor principal de la imagen: StackPane
    // ============================================================
    StackPane centerPane = new StackPane(imageView, overlay);
    StackPane.setAlignment(overlay, Pos.CENTER);

    // Permitir que el StackPane crezca dentro del BorderPane
    centerPane.setMinSize(0, 0);
    centerPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

    // Autoescalar la imagen al tamaño del StackPane
    imageView.fitWidthProperty().bind(centerPane.widthProperty());
    imageView.fitHeightProperty().bind(centerPane.heightProperty());

    // ============================================================
    // 7) Toolbar (botones de control)
    // ============================================================
    Button btnROI = new Button("ROI");
    btnROI.setOnAction(
        e -> {
          modoActual = Modo.ROI;
          overlay.toFront();
          System.out.println("[ROIEditor] >>> Overlay activado y al frente");
        });

    campoNombre = new TextField();
    campoNombre.setPromptText("nombre del campo");

    lblCoords = new Label("x1,y1 → x2,y2");

    Button btnAdd = new Button("+");
    btnAdd.setOnAction(e -> registrarROI());

    Button btnOCR = new Button("OCR TEST");
    btnOCR.setOnAction(e -> {

        previsualizarOCR();
    });

    Button btnListo = new Button("LISTO");
    btnListo.setOnAction(e -> guardarModelo());

    ToolBar toolbar = new ToolBar(btnROI, campoNombre, lblCoords, btnAdd, btnOCR, btnListo);

    // ============================================================
    // 8) RootPane (BorderPane)
    // ============================================================
    BorderPane root = new BorderPane();
    root.setTop(toolbar);
    root.setCenter(centerPane);
    BorderPane.setAlignment(centerPane, Pos.CENTER);

    // ============================================================
    // 9) Stage
    // ============================================================
    stage.setScene(new Scene(root, 1200, 800));
    stage.setTitle("Editor de ROIs");
    stage.show();
  }

  private void actualizarEscala() {
    double w = imageView.getLayoutBounds().getWidth();
    double h = imageView.getLayoutBounds().getHeight();

    if (w <= 0 || h <= 0) return;

    scaleX = imagenOriginal.getWidth() / w;
    scaleY = imagenOriginal.getHeight() / h;

    System.out.println("[ROIEditor] Escala REAL: scaleX=" + scaleX + " scaleY=" + scaleY);
  }

  private void iniciarROI(MouseEvent e, Pane overlay) {
    System.out.println(
        "[ROIEditor] >>> iniciarROI() llamado. Click en overlay: x=" + e.getX() + " y=" + e.getY());

    if (modoActual != Modo.ROI) {
      System.out.println("[ROIEditor] >>> Modo no es ROI, ignorado");
      return;
    }

    System.out.println("[ROIEditor] >>> Modo ROI activo, creando rectángulo");

    scaleX_ROI = scaleX;
    scaleY_ROI = scaleY;

    startX = e.getX();
    startY = e.getY();

    currentRect = new Rectangle();
    currentRect.setX(startX);
    currentRect.setY(startY);
    currentRect.setStroke(Color.RED);
    currentRect.setFill(Color.color(1, 0, 0, 0.2));

    overlay.getChildren().add(currentRect);

    System.out.println("[ROIEditor] >>> Rectángulo añadido al overlay");
  }

  private void actualizarROI(MouseEvent e) {
    if (modoActual != Modo.ROI) return;

    double w = e.getX() - startX;
    double h = e.getY() - startY;

    currentRect.setWidth(w);
    currentRect.setHeight(h);

    lblCoords.setText(
        (int) startX + "," + (int) startY + " → " + (int) (startX + w) + "," + (int) (startY + h));
  }

  private void registrarROI() {
    if (currentRect == null) return;

    String nombre = campoNombre.getText().trim();
    if (nombre.isEmpty()) return;

    int x1 = (int) Math.round(currentRect.getX() * scaleX_ROI);
    int y1 = (int) Math.round(currentRect.getY() * scaleY_ROI);
    int x2 = (int) Math.round((currentRect.getX() + currentRect.getWidth()) * scaleX_ROI);
    int y2 = (int) Math.round((currentRect.getY() + currentRect.getHeight()) * scaleY_ROI);

    ROI roi = new ROI(x1, y1, x2, y2);
    zonas.put(nombre, roi);

    campoNombre.clear();
    lblCoords.setText("x1,y1 → x2,y2");
    currentRect = null;
  }

  private BufferedImage extraerROI(ROI roi) {
    try {
      double rx = roi.x1();
      double ry = roi.y1();
      double rw = roi.x2() - roi.x1();
      double rh = roi.y2() - roi.y1();

      if (rw < 0) {
        rx += rw;
        rw = -rw;
      }
      if (rh < 0) {
        ry += rh;
        rh = -rh;
      }

      if (rw < 2 || rh < 2) throw new IllegalArgumentException("[ROIEditor] ROI pequeño");

      int x = (int) Math.floor(rx);
      int y = (int) Math.floor(ry);
      int w = (int) Math.ceil(rw);
      int h = (int) Math.ceil(rh);

      BufferedImage full = SwingFXUtils.fromFXImage(imagenOriginal, null);

      if (x < 0) x = 0;
      if (y < 0) y = 0;
      if (x + w > full.getWidth()) w = full.getWidth() - x;
      if (y + h > full.getHeight()) h = full.getHeight() - y;

      if (w <= 0 || h <= 0)
        throw new IllegalArgumentException("[ROIEditor] ROI fuera de la imagen");

      return full.getSubimage(x, y, w, h);

    } catch (Exception ex) {
      System.out.println("[ROIEditor] Error al extraer ROI:");
      ex.printStackTrace();
      return null;
    }
  }

  private void previsualizarOCR() {

      if (currentRect == null) {
          new Alert(Alert.AlertType.WARNING, "[ROIEditor] Dibuja un ROI primero").show();
          return;
      }

    System.out.println(
        "[ROIEditor>previsualizarOCR] >>> Rect: x="
            + currentRect.getX()
            + "\ty="
            + currentRect.getY()
            + "\n>>>>>> w="
            + currentRect.getWidth()
            + "\th="
            + currentRect.getHeight());

    // 1) Coordenadas del rectángulo en el overlay
    double ox = currentRect.getX();
    double oy = currentRect.getY();
    double ow = currentRect.getWidth();
    double oh = currentRect.getHeight();

    // Normalizar por si el usuario dibujó hacia la izquierda o arriba
    if (ow < 0) {
      ox += ow;
      ow = -ow;
    }
    if (oh < 0) {
      oy += oh;
      oh = -oh;
    }

    // 2) Convertir a coordenadas reales de la imagen
    double x1 = ox * scaleX;
    double y1 = oy * scaleY;
    double x2 = (ox + ow) * scaleX;
    double y2 = (oy + oh) * scaleY;

    ROI roiReal = new ROI(x1, y1, x2, y2);

    // 3) Extraer ROI real
    BufferedImage recorte = extraerROI(roiReal);
    if (recorte == null) {
      new Alert(Alert.AlertType.ERROR, "[ROIEditor] No se pudo extraer el ROI").show();
      return;
    }

    try {
      OCRService ocr = new OCRService();
      String texto = ocr.ocr(recorte);

      TextArea ta = new TextArea(texto);
      ta.setWrapText(true);
      ta.setPrefSize(600, 400);

      Alert a = new Alert(Alert.AlertType.INFORMATION);
      a.setTitle("OCR del ROI");
      a.setHeaderText("Texto detectado:");
      a.getDialogPane().setContent(ta);
      a.show();

    } catch (Exception ex) {
      ex.printStackTrace();
      new Alert(Alert.AlertType.ERROR, "[ROIEditor] Error OCR: " + ex.getMessage()).show();
    }
  }

  private void guardarModelo() {
    TextInputDialog dialog = new TextInputDialog();
    dialog.setTitle("Guardar modelo");
    dialog.setHeaderText("Introduce el nombre del modelo:");
    dialog.setContentText("Nombre:");

    Optional<String> result = dialog.showAndWait();
    if (result.isEmpty()) return;

    guardarModeloComoJSON(result.get().trim());
  }

  private void guardarModeloComoJSON(String nombreModelo) {
    try {
      JsonObject root = new JsonObject();

      // Metadatos del modelo
      root.addProperty("nombre", nombreModelo);
      root.addProperty("version", "1.0");
      root.addProperty("dpi", 300);
      root.addProperty("rutaImagen", rutaImagen);

      // Zonas OCR
      JsonObject zonasJson = new JsonObject();
      for (var entry : zonas.entrySet()) {
        String nombre = entry.getKey();
        ROI r = entry.getValue();

        JsonObject rj = new JsonObject();
        rj.addProperty("x1", r.x1());
        rj.addProperty("y1", r.y1());
        rj.addProperty("x2", r.x2());
        rj.addProperty("y2", r.y2());

        zonasJson.add(nombre, rj);
      }

      root.add("zonas", zonasJson);

      // Serializar a JSON
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      String json = gson.toJson(root);

      // Guardar archivo
      Path out = Paths.get("data/datos/modelosOCR", "modelo_" + nombreModelo + ".json");
      Files.createDirectories(out.getParent());
      Files.writeString(out, json);

      System.out.println("[ROIEditor] Modelo OCR guardado en: " + out.toAbsolutePath());

    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public static void main(String[] args) {
    launch(args);
  }
}
