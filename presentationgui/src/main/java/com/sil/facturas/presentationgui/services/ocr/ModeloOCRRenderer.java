package com.sil.facturas.presentationgui.services.ocr;

import com.sil.facturas.domain.ocr.Bloque;
import com.sil.facturas.domain.ocr.Campo;
import com.sil.facturas.domain.ocr.Escala;
import com.sil.facturas.domain.ocr.InfoImagen;
import com.sil.facturas.domain.ocr.enums._OffsetTipo;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;

import static com.sil.facturas.domain.interfaces.IDebugService.*;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ModeloOCRRenderer {

 public void cargarImagenYCalcularEscala(
        ImageView imageView,
        ScrollPane scrollPane,
        File file,
        Consumer<InfoImagen> onReady) {

    Image img = new Image(file.toURI().toString());
    imageView.setImage(img);

    double realW = img.getWidth();
    double realH = img.getHeight();

    Platform.runLater(() -> {

        double viewW = imageView.getBoundsInParent().getWidth();

        if (viewW <= 0) {
            // fallback si aún no hay layout
            viewW = scrollPane.getViewportBounds().getWidth();
        }

        double scale = viewW / realW;

        Escala escala = new Escala(scale, scale);
        InfoImagen info = new InfoImagen(realW, realH, escala);

        onReady.accept(info);
    });
}


    public void dibujarBloques(Pane canvas,
                               List<Bloque> bloques,
                               InfoImagen infoImagen,
                               Map<String, Bloque> mapaBloques) {

        for (Bloque b : bloques) {
            dibujarBloque(canvas, b, infoImagen, mapaBloques);
        }
    }

    private void dibujarBloque(Pane canvas,
                               Bloque b,
                               InfoImagen infoImagen,
                               Map<String, Bloque> mapaBloques) {

        double s = infoImagen.escala().x();

        double x = b.zona().x() * s;
        double y = b.zona().y() * s;
        double w = b.zona().w() * s;
        double h = b.zona().h() * s;

        Rectangle r = new Rectangle(x, y, w, h);
        r.setStroke(Color.RED);
        r.setStrokeWidth(2);
        r.setFill(Color.TRANSPARENT);
        canvas.getChildren().add(r);

        Text label = new Text(b.nombre());
        label.setFill(Color.RED);
        label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        label.setX(x + 4);
        label.setY(y - 6);
        canvas.getChildren().add(label);

        // Anchor escalado
        Point2D anchor = new Point2D(
            x + w / 2,
            y + h / 2
        );
        dibujarAnchor(canvas, anchor, s);

        // Campos
        for (Campo c : b.campos()) {
            dibujarCampo(canvas, b, c, infoImagen);
        }
    }

    private void dibujarCampo(Pane canvas, Bloque b, Campo c, InfoImagen infoImagen) {

        double s = infoImagen.escala().x();

        double x = (b.zona().x() + c.offsetX()) * s;
        double y = (b.zona().y() + c.offsetY()) * s;
        double w = c.w() * s;
        double h = c.h() * s;

        Rectangle r = new Rectangle(x, y, w, h);
        r.setStroke(Color.GREEN);
        r.setStrokeWidth(1.5);
        r.setFill(Color.TRANSPARENT);
        canvas.getChildren().add(r);

        Text label = new Text(c.nombre());
        label.setFill(Color.GREEN);
        label.setStyle("-fx-font-size: 12px;");
        label.setX(x + 3);
        label.setY(y + 14);
        canvas.getChildren().add(label);
    }

    private void dibujarAnchor(Pane canvas, Point2D p, double s) {
        Circle c = new Circle(p.getX(), p.getY(), 4 * s);
        c.setFill(Color.BLACK);
        canvas.getChildren().add(c);
    }
}
