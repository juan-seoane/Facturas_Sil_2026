package com.sil.facturas.presentationgui.fxcontrollers;

import static com.sil.facturas.domain.interfaces.IDebugService.*;

import com.sil.facturas.domain.ocr.Bloque;
import com.sil.facturas.domain.ocr.Campo;
import com.sil.facturas.domain.ocr.ModeloOCR;
import com.sil.facturas.domain.ocr.enums._OffsetTipo;
import com.sil.facturas.infrastructure.services.ocr.ModeloOCRService;
import com.sil.facturas.infrastructure.services.ocr.aux_ocr.BloqueOCR;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class FxCntrlModeloOCRVisualizer {
    @FXML private ScrollPane scrollPane;
    @FXML private Pane canvasPane;

    private ModeloOCRService modeloService;
    @FXML
    private ImageView imageView;

    private double scale = 1.0;
    private double realImageWidth;
    private double realImageHeight;
    private ModeloOCR modelo;
    private Map<String, Bloque> mapaBloques = new HashMap<>();

public void initialize() {
        modeloService = new ModeloOCRService();

        BorderPane root = (BorderPane) scrollPane.getParent();
        root.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        root.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);


        scrollPane.setFitToWidth(false);
        scrollPane.setFitToHeight(true);
        scrollPane.setPannable(true);
        scrollPane.setMaxWidth(Double.MAX_VALUE);
        scrollPane.setMaxHeight(Double.MAX_VALUE);

        canvasPane.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        canvasPane.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        canvasPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        imageView.setPreserveRatio(true);
        imageView.fitHeightProperty().bind(scrollPane.heightProperty());
        imageView.setSmooth(true);

        // Ajustar canvasPane al tamaño real del ImageView
        imageView
        .boundsInParentProperty()
        .addListener(
            (obs, oldB, newB) -> {
                if (realImageWidth > 0) {
                    scale = newB.getWidth() / realImageWidth;
                    redibujarModelo();

                    Stage stage = (Stage) canvasPane.getScene().getWindow();
                    stage.setWidth(newB.getWidth() + 40); // margen
                }
            }
        );
    }

@FXML
private void onCargarModelo() {
    FileChooser fc = new FileChooser();
    fc.setTitle("Seleccionar modelo OCR");
    fc.getExtensionFilters().add(
        new FileChooser.ExtensionFilter("Modelo OCR (*.json)", "*.json")
    );

    File file = fc.showOpenDialog(canvasPane.getScene().getWindow());
    if (file == null) return;

    modelo = modeloService.cargarModelo(file);

    // === RECONSTRUIR MAPA DE BLOQUES ===
    mapaBloques.clear();
    for (Bloque b : modelo.getBloques()) {
        mapaBloques.put(b.nombre(), b);
    }

    dibujarModelo();
}

 @FXML
private void onCargarImagen() {
    FileChooser fc = new FileChooser();
    fc.setTitle("Seleccionar imagen");
    fc.getExtensionFilters().add(
        new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
    );

    File file = fc.showOpenDialog(canvasPane.getScene().getWindow());
    if (file == null) return;

    Image img = new Image(file.toURI().toString());
    imageView.setImage(img);

    realImageWidth = img.getWidth();
    realImageHeight = img.getHeight();

    // Escalado vertical
    imageView.fitHeightProperty().bind(scrollPane.heightProperty());

    // Cuando la imagen se escale, ajustamos el ancho de la ventana
    imageView.boundsInParentProperty().addListener((obs, oldB, newB) -> {
        if (realImageWidth > 0) {
            scale = newB.getWidth() / realImageWidth;
            redibujarModelo();

            // Ajustar ancho de la ventana al ancho de la imagen escalada
            Stage stage = (Stage) canvasPane.getScene().getWindow();
            stage.setWidth(newB.getWidth() + 40); // margen para scrollbars
        }
    });
}

    private void dibujarModelo() {
        ModeloOCR modelo = modeloService.getModelo();

        for (Bloque b : modelo.getBloques()) {
            dibujarBloque(b);

            for (Campo c : b.campos()) {
                dibujarCampo(b, c);
            }
        }
    }

  private void dibujarBloque(Bloque b) {

    // Coordenadas escaladas
    double x = b.zona().x() * scale;
    double y = b.zona().y() * scale;
    double w = b.zona().w() * scale;
    double h = b.zona().h() * scale;

    // Rectángulo del bloque
    Rectangle r = new Rectangle(x, y, w, h);
    r.setStroke(Color.RED);
    r.setStrokeWidth(2);
    r.setFill(Color.TRANSPARENT);
    canvasPane.getChildren().add(r);

    // Nombre del bloque
    Text label = new Text(b.nombre());
    label.setFill(Color.RED);
    label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
    label.setX(x + 4);
    label.setY(y - 6);
    canvasPane.getChildren().add(label);

    // === Anchor Point ===
    Point2D anchor = calcularAnchor(b);
    dibujarAnchor(anchor);

    // === Línea punteada hacia el parent (si offset es ASI y parent != imagen) ===
    if (b.offset().tipo() == _OffsetTipo.DEBAJO_FINAL
        && b.parentNombre() != null
        && !b.parentNombre().equals("Imagen (root)")) {

        Bloque parent = mapaBloques.get(b.parentNombre());
        if (!mapaBloques.containsKey(b.parentNombre())) {
        printError(
            "[FxCntrlModeloOCRVisualizer>dibujarBloque] WARNING: Parent no encontrado: "
                + b.parentNombre());
        } else{
            double px = parent.zona().x() * scale;
            double py = parent.zona().y() * scale;
            double pw = parent.zona().w() * scale;
            double ph = parent.zona().h() * scale;

            // Punto inferior del parent (centro)
            Point2D parentBottom = new Point2D(px + pw / 2, py + ph);

            // Dibujar X en el parent
            dibujarX(parentBottom);

            // Dibujar línea punteada desde anchor → X
            dibujarLineaPunteada(anchor, parentBottom);
        }
    }

    // === Grow Horizontal (↔) ===
    if (b.growHorizontal()) {
      dibujarFlechaHorizontal(x, y, w);
    }

    // === Grow Vertical (↕) ===
    if (b.growVertical()) {
      dibujarFlechaVertical(x + w, y, h);
    }

    // Dibujar campos del bloque
    for (Campo c : b.campos()) {
      dibujarCampo(b, c);
    }
  }

private void dibujarCampo(Bloque b, Campo c) {

    // Coordenadas absolutas del campo
    double absX = b.zona().x() + c.offsetX();
    double absY = b.zona().y() + c.offsetY();

    // Escaladas
    double x = absX * scale;
    double y = absY * scale;
    double w = c.w() * scale;
    double h = c.h() * scale;

    Rectangle r = new Rectangle(x, y, w, h);
    r.setStroke(Color.GREEN);
    r.setStrokeWidth(1.5);
    r.setFill(Color.TRANSPARENT);

    canvasPane.getChildren().add(r);

    // Nombre del campo (opcional)
    Text label = new Text(c.nombre());
    label.setFill(Color.GREEN);
    label.setStyle("-fx-font-size: 12px;");

    label.setX(x + 3);
    label.setY(y + 14);

    canvasPane.getChildren().add(label);
}

private void redibujarModelo() {
    // Limpiar todo lo dibujado
    canvasPane.getChildren().clear();

    // Volver a añadir la imagen (siempre debe ser el primer nodo)
    canvasPane.getChildren().add(imageView);

    if (modelo == null || modelo.getBloques() == null)
        return;

    // Dibujar cada bloque y sus campos
    for (Bloque b : modelo.getBloques()) {
        dibujarBloque(b);
    }
}

private Point2D calcularAnchor(BloqueOCR b) {
    double x = b.x();
    double y = b.y();
    double w = b.w();
    double h = b.h();

    double ax = switch (b.anchorX()) {
        case LEFT -> x;
        case CENTER -> x + w / 2;
        case RIGHT -> x + w;
    };

    double ay = switch (b.anchorY()) {
        case TOP -> y;
        case CENTER -> y + h / 2;
        case BOTTOM -> y + h;
    };

    return new Point2D(ax, ay);
}

  private Point2D calcularAnchor(Bloque b) {
    double x = b.zona().x() * scale;
    double y = b.zona().y() * scale;
    double w = b.zona().w() * scale;
    double h = b.zona().h() * scale;

    double ax =
        switch (b.anchorX()) {
          case LEFT -> x;
          case CENTER -> x + w / 2;
          case RIGHT -> x + w;
        };

    double ay =
        switch (b.anchorY()) {
          case TOP -> y;
          case CENTER -> y + h / 2;
          case BOTTOM -> y + h;
        };

    return new Point2D(ax, ay);
  }

  private void dibujarAnchor(Point2D p) {
    Circle c = new Circle(p.getX(), p.getY(), 4); // radio 4 px
    c.setFill(Color.BLACK);
    canvasPane.getChildren().add(c);
  }

  private void dibujarFlechaHorizontal(double x, double y, double w) {

      double midY = y - 12;
      double recorte = 4; // recorte en cada extremo

    // Línea central
    Line line = new Line(x+ recorte, midY, x + w - recorte, midY);
    line.setStroke(Color.GREY);
    line.setStrokeWidth(2);

    // Triángulo izquierdo
    Polygon leftArrow = new Polygon(
        x, midY,
        x + 10, midY - 4,
        x + 10, midY + 4
    );
    leftArrow.setFill(Color.GREY);

    // Triángulo derecho
    Polygon rightArrow = new Polygon(
        x + w, midY,
        x + w - 20, midY - 8,
        x + w - 20, midY + 8
    );
    rightArrow.setFill(Color.GREY);

    canvasPane.getChildren().addAll(line, leftArrow, rightArrow);
}

private void dibujarFlechaVertical(double x, double y, double h) {

    double midX = x + 12;
    double recorte = 4; // recorte en cada extremo

    // Línea central
    Line line = new Line(midX, y + recorte, midX, y + h - recorte);
    line.setStroke(Color.GREY);
    line.setStrokeWidth(2);

    // Triángulo superior
    Polygon topArrow = new Polygon(midX, y, midX - 4, y + 10, midX + 4, y + 10);
    topArrow.setFill(Color.GREY);

    // Triángulo inferior
    Polygon bottomArrow = new Polygon(midX, y + h, midX - 4, y + h - 10, midX + 4, y + h - 10);
    bottomArrow.setFill(Color.GREY);

    canvasPane.getChildren().addAll(line, topArrow, bottomArrow);
}

private void dibujarLineaPunteada(Point2D from, Point2D to) {
    Line line = new Line(from.getX(), from.getY(), to.getX(), to.getY());
    line.setStroke(Color.BLACK);
    line.getStrokeDashArray().addAll(6.0, 6.0);
    line.setStrokeWidth(1.5);
    canvasPane.getChildren().add(line);
}

  private void dibujarX(Point2D p) {
    double s = 6; // tamaño de la X (mitad hacia cada lado)

    Line l1 = new Line(p.getX() - s, p.getY() - s, p.getX() + s, p.getY() + s);
    Line l2 = new Line(p.getX() - s, p.getY() + s, p.getX() + s, p.getY() - s);

    l1.setStroke(Color.BLACK);
    l2.setStroke(Color.BLACK);
    l1.setStrokeWidth(2);
    l2.setStrokeWidth(2);

    canvasPane.getChildren().addAll(l1, l2);
  }
}
