
package presentation.fxcontrollers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import domain.records.ROI;
import infrastructure.filesystem._Ruta;
import infrastructure.servicios.ocr.ModeloOCR;
import infrastructure.servicios.ocr.ModeloOCRService;
import infrastructure.servicios.ocr.OCRService;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToolBar;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
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

    @FXML private TextField campoNombre;
    @FXML private Label lblCoords;


    private ModeloOCRService service = new ModeloOCRService();
    private OCRService ocrService;
    private Rectangle currentRect;
    private double startX, startY;
    private double screenHeight;

    private double targetHeight;
    //private final List<Rectangle> listaROIs = new ArrayList();
    private final ModeloOCR.Builder builder = new ModeloOCR.Builder();


  public FxROIEditorController() {
      builder.zonas = new LinkedHashMap<>();
      builder.ocrPorZona = new LinkedHashMap<>();
  }

  @FXML
  private void initialize() {

    this.ocrService = new OCRService();
    
    screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
    targetHeight = screenHeight * 0.95;

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
  }

    // ============================
    // CARGAR IMAGEN DESDE FUERA
    // ============================

public void cargarImagen(String ruta) throws FileNotFoundException {
    File f = new File(ruta);
    Image img = new Image(new FileInputStream(f));
    imageView.setImage(img);

    service.setImagenBase(img);
    service.setInfoModelo(ruta, 300, "Modelo OCR", "1.0");
}


    // ============================
    // INICIAR ROI
    // ============================
    private void iniciarROI(MouseEvent e) {
        startX = e.getX();
        startY = e.getY();

        currentRect = new Rectangle(startX, startY, 0, 0);
        currentRect.setFill(Color.WHITE);
        currentRect.setStroke(Color.YELLOW);
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
    if (currentRect == null) {
      Alert alert = new Alert(Alert.AlertType.WARNING, "No hay ROI seleccionado.");
      alert.showAndWait();
      return;
    }

    String texto = service.realizarOCR(currentRect,
    imageView.getBoundsInParent().getWidth(),
    imageView.getBoundsInParent().getHeight());

    System.out.println("[ROIEditor>OCRTest] TEXTO DETECTADO:" + texto);
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
    System.out.println("ROI final: " + currentRect.getX() + ", " + currentRect.getY());
    OCRTest();
  }

  @FXML
  private void onListo() {
    if (builder.zonas.isEmpty()) {
      new Alert(Alert.AlertType.WARNING, "No hay zonas definidas.").showAndWait();
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

        // Construir modelo final
        ModeloOCR modelo = service.build();
        service.guardarJSON(modelo, Paths.get(_Ruta.MODELOSOCR.getRuta() + "/modelo_" + nombreModelo + ".json"));

    } catch (Exception e) {
        e.printStackTrace();
    }
  }

    
  @FXML
  private void onAddROI() {

    if (currentRect == null) {
        System.out.println("[ADD ROI] No hay ROI seleccionado");
        return;
    }

    String nombre = campoNombre.getText().trim();
    if (nombre.isEmpty()) {
        new Alert(Alert.AlertType.WARNING, "Introduce un nombre en la barra superior").showAndWait();
        return;
    }

    // Tamaño real del ImageView (o del overlay si coincide)
    double viewW = imageView.getBoundsInParent().getWidth();
    double viewH = imageView.getBoundsInParent().getHeight();

    // Convertir ROI JavaFX → ROI real
    ROI roiReal = service.convertirAFisico(currentRect, viewW, viewH);

    // OCR automático
    String textoOCR = service.realizarOCR(currentRect, viewW, viewH);

    // Guardar en el builder
    builder.zonas.put(nombre, roiReal);
    builder.ocrPorZona.put(nombre, textoOCR);

    System.out.println("[ADD ROI] Zona '" + nombre + "' guardada.");
    System.out.println("ROI: " + roiReal);
    System.out.println("OCR: " + textoOCR);

    campoNombre.clear();
  }



}
