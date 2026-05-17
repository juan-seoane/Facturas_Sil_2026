package infrastructure.servicios.ocr.editor;

import java.util.LinkedHashMap;
import java.util.Map;

import domain.records.ROI;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class ROIEditor extends Application {

    private enum Modo { NINGUNO, ROI }

    private Modo modoActual = Modo.NINGUNO;

    private double startX, startY;
    private Rectangle currentRect;

    private TextField campoNombre;
    private Label lblCoords;
    private Map<String, ROI> zonas = new LinkedHashMap<>();

    @Override
    public void start(Stage stage) {

        // --- Imagen ---
        Image image = new Image("file:///home/juans/Github/Facturas_SIL_2026/data/datos/ADMIN/scans/test1.png");
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        Pane overlay = new Pane();
        overlay.setPickOnBounds(false);

        StackPane centerPane = new StackPane(imageView, overlay);

        // --- Barra superior ---
        Button btnROI = new Button("ROI");
        btnROI.setOnAction(e -> modoActual = Modo.ROI);

        campoNombre = new TextField();
        campoNombre.setPromptText("nombre del campo");

        lblCoords = new Label("x1,y1 → x2,y2");

        Button btnAdd = new Button("+");
        btnAdd.setOnAction(e -> registrarROI());

        Button btnListo = new Button("LISTO");
        btnListo.setOnAction(e -> guardarModelo());

        ToolBar toolbar = new ToolBar(btnROI, campoNombre, lblCoords, btnAdd, btnListo);

        // --- Layout principal ---
        BorderPane root = new BorderPane();
        root.setTop(toolbar);
        root.setCenter(centerPane);

        // --- Mover ventana ---
        final double[] offsetX = new double[1];
        final double[] offsetY = new double[1];

        toolbar.setOnMousePressed(e -> {
            offsetX[0] = e.getSceneX();
            offsetY[0] = e.getSceneY();
        });

        toolbar.setOnMouseDragged(e -> {
            stage.setX(e.getScreenX() - offsetX[0]);
            stage.setY(e.getScreenY() - offsetY[0]);
        });

        // --- Dibujar ROI ---
        overlay.setOnMousePressed(e -> {
            if (modoActual != Modo.ROI) return;

            startX = e.getX();
            startY = e.getY();

            currentRect = new Rectangle();
            currentRect.setX(startX);
            currentRect.setY(startY);
            currentRect.setStroke(Color.RED);
            currentRect.setFill(Color.color(1, 0, 0, 0.2));
            overlay.getChildren().add(currentRect);
        });

        overlay.setOnMouseDragged(e -> {
            if (modoActual != Modo.ROI) return;

            double w = e.getX() - startX;
            double h = e.getY() - startY;

            currentRect.setWidth(w);
            currentRect.setHeight(h);

            lblCoords.setText(
                (int) startX + "," + (int) startY + " → " +
                (int) (startX + w) + "," + (int) (startY + h)
            );
        });

        overlay.setOnMouseReleased(e -> {
            if (modoActual != Modo.ROI) return;
        });

        // --- Escalar imagen ---
        imageView.fitWidthProperty().bind(stage.widthProperty());
        imageView.fitHeightProperty().bind(stage.heightProperty().subtract(60));

        stage.setScene(new Scene(root, 1200, 800));
        stage.setTitle("Editor de ROIs");
        stage.show();
    }

	private void registrarROI() {

		if (currentRect == null) {
			System.out.println("No hay ROI dibujado.");
			return;
		}

		String nombre = campoNombre.getText().trim();
		if (nombre.isEmpty()) {
			System.out.println("El nombre del campo está vacío.");
			return;
		}

		ROI roi = new ROI(
			(int) currentRect.getX(),
			(int) currentRect.getY(),
			(int) (currentRect.getX() + currentRect.getWidth()),
			(int) (currentRect.getY() + currentRect.getHeight())
		);

		zonas.put(nombre, roi);

		System.out.println("ROI registrado: " + nombre + " = " + roi);

		// limpiar
		campoNombre.clear();
		lblCoords.setText("x1,y1 → x2,y2");
		currentRect = null;
	}


    private void guardarModelo() {
        System.out.println("=== MODELO OCR ===");
        zonas.forEach((k, v) -> System.out.println(k + " = " + v));
        System.out.println("===================");
    }

    public static void main(String[] args) {
        launch();
    }
}
