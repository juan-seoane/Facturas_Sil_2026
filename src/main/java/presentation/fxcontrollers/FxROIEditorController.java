
package presentation.fxcontrollers;

import infrastructure.filesystem._Ruta;
import infrastructure.servicios.config.Config;
import infrastructure.servicios.ocr.ModeloOCR;
import infrastructure.servicios.ocr.ModeloOCRService;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import javafx.stage.Screen;
import presentation.config.OCRItem;
import presentation.config.UIDataConfig;

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

    private UIDataConfig uiData;
    private ModeloOCRService modOCRservice = new ModeloOCRService();
    private Rectangle currentRect;
    private double startX, startY;
    private double screenHeight;

    private double targetHeight;
    //private final List<Rectangle> listaROIs = new ArrayList();
    private final ModeloOCR.Builder builder = new ModeloOCR.Builder();

    private static final double MIN_WIDTH = 10;
    private static final double MIN_HEIGHT = 10;

    @FXML
    private void initialize() {

        screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
        targetHeight = screenHeight * 0.95;
        // cargar Config de "admin" si no existe ya una Config actual (porque el usuario no ha pasado por el login)
        if (Config.configActual == null || Config.configActual.getUsuario() != "admin" ) {
        try {
            Config.configActual = Config.getConfig("admin");
        } catch (Exception e) {
            e.printStackTrace();
        }
        }
        cargarUIData(
            Paths.get(
                _Ruta.CONFIG.getRuta()
                    + "/"
                    + Config.configActual.getUsuario().toUpperCase()
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
        // VALIDACIONES BÁSICAS
        // SI RECT ES NULL
        if (currentRect == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "No hay ROI seleccionado.");
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

        String texto = this.modOCRservice.realizarOCR(currentRect,
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
        if (this.modOCRservice.builder.zonas.isEmpty()) {
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
            if (nombreModelo.isEmpty())
                return;

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
            e.printStackTrace();
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
            System.out.println("[FxROIEditorController>onAddROI] No hay ROI seleccionado");
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

        double viewW = imageView.getBoundsInParent().getWidth();
        double viewH = imageView.getBoundsInParent().getHeight();

        if (modOCRservice.isROIEmpty(currentRect, imageView)) {
            new Alert(Alert.AlertType.WARNING, "El ROI no contiene información útil.").showAndWait();
            return;
        }

        // USAR SOLO EL SERVICE
        this.modOCRservice.addROI(nombre, currentRect, viewW, viewH);

        System.out.println("[FxROIEditorController>onAddROI] Zona '" + nombre + "' guardada.");

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

}
