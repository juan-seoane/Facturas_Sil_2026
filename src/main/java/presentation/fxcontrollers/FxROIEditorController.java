
package presentation.fxcontrollers;

import infrastructure.servicios.ocr.OCRService;
import java.awt.image.BufferedImage;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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


    private OCRService ocrService;
    private Rectangle currentRect;
    private double startX, startY;
    private double scaleX = 1, scaleY = 1;
    private double screenHeight;
    private double targetHeight;

  @FXML
  private void initialize() {

    this.ocrService = new OCRService();

    screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
    targetHeight = screenHeight * 0.80;

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
    public void cargarImagen(Image img) {
        imageView.setImage(img);
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

  @FXML
  private void onOCRTest() {
    if (currentRect == null) {
      Alert alert = new Alert(Alert.AlertType.WARNING, "No hay ROI seleccionado.");
      alert.showAndWait();
      return;
    }

    String texto = realizarOCR(currentRect);

    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Resultado OCR");
    alert.setHeaderText("Texto detectado:");
    alert.setContentText(texto);
    alert.showAndWait();
  }

  private String realizarOCR(Rectangle roi) {
    try {
      Image img = imageView.getImage();
      if (img == null) return "[ERROR] No hay imagen cargada";

      BufferedImage bimg = SwingFXUtils.fromFXImage(img, null);

      double scaleX = bimg.getWidth() / imageView.getBoundsInParent().getWidth();
      double scaleY = bimg.getHeight() / imageView.getBoundsInParent().getHeight();

      int x = (int) (roi.getX() * scaleX);
      int y = (int) (roi.getY() * scaleY);
      int w = (int) (roi.getWidth() * scaleX);
      int h = (int) (roi.getHeight() * scaleY);

      BufferedImage sub = bimg.getSubimage(x, y, w, h);

      // OCR RAW
      String texto = ocrService.ocr(sub);

      // NORMALIZADO
      String normalizado = ocrService.normalizar(texto);

      return normalizado;

    } catch (Exception ex) {
      ex.printStackTrace();
      return "[ERROR OCR] " + ex.getMessage();
    }
  }

    // ============================
    // FINALIZAR ROI
    // ============================
    private void finalizarROI(MouseEvent e) {
        System.out.println("ROI final: " + currentRect.getX() + ", " + currentRect.getY());
    }
}
