package com.sil.facturas.presentationgui.fxcontrollers;

import com.sil.facturas.domain.ocr.Bloque;
import com.sil.facturas.domain.ocr.Campo;
import com.sil.facturas.domain.ocr.ModeloOCR;
import com.sil.facturas.infrastructure.services.ocr.ModeloOCRService;
import java.io.File;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class FxCntrlModeloOCRVisualizer {
    @FXML private ScrollPane scrollPane;
    @FXML private Pane canvasPane;

    private ModeloOCRService modeloService;
    private ImageView imageView;

    public void initialize() {
        modeloService = new ModeloOCRService();
    }

    @FXML
    private void onCargarModelo() {
        modeloService.cargarModelo(new File("D:/facturas/modeloOCR.json"));
        dibujarModelo();
    }

    @FXML
    private void onCargarImagen() {
        Image img = new Image(new File("D:/facturas/ejemplo.png").toURI().toString());
        imageView = new ImageView(img);
        canvasPane.getChildren().add(imageView);
        canvasPane.setPrefSize(img.getWidth(), img.getHeight());
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
        var r = new javafx.scene.shape.Rectangle(
                b.zona().x(), b.zona().y(),
                b.zona().w(), b.zona().h()
        );
        r.setStroke(javafx.scene.paint.Color.RED);
        r.setFill(javafx.scene.paint.Color.TRANSPARENT);
        canvasPane.getChildren().add(r);
    }

    private void dibujarCampo(Bloque b, Campo c) {
        var r = new javafx.scene.shape.Rectangle(
                b.zona().x() + c.offsetX(),
                b.zona().y() + c.offsetY(),
                c.w(), c.h()
        );
        r.setStroke(javafx.scene.paint.Color.GREEN);
        r.setFill(javafx.scene.paint.Color.TRANSPARENT);
        canvasPane.getChildren().add(r);
    }
}
