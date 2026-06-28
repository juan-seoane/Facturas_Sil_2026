package com.sil.facturas.presentationgui.fxcontrollers;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.sil.facturas.domain.ocr.Bloque;
import com.sil.facturas.domain.ocr.InfoImagen;
import com.sil.facturas.domain.ocr.ModeloOCR;
import com.sil.facturas.infrastructure.services.ocr.ModeloOCRService;
import com.sil.facturas.presentationgui.services.ocr.ModeloOCRRenderer;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;

public class FxCntrlModeloOCRVisualizer {

    @FXML private ScrollPane scrollPane;
    @FXML private Pane canvasPane;
    @FXML private ImageView imageView;

    private ModeloOCRService modeloService;
    private ModeloOCR modelo;

    private ModeloOCRRenderer renderer;

    private InfoImagen infoImagen;
    private Map<String, Bloque> mapaBloques = new HashMap<>();

    public void initialize() {

        this.modeloService = new ModeloOCRService();
        this.renderer = new ModeloOCRRenderer();

        // Imagen escalada SOLO por ancho
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.fitWidthProperty().bind(scrollPane.widthProperty());
        
        // ScrollPane crece correctamente
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPannable(false);
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

        this.modelo = this.modeloService.cargarModelo(file);
        this.mapaBloques.clear();
        for (Bloque b : this.modelo.getBloques()) {
            this.mapaBloques.put(b.nombre(), b);
        }

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

        this.renderer.cargarImagenYCalcularEscala(
            this.imageView,
            this.scrollPane,
            file,
            info -> {
                this.infoImagen = info;
                redibujarModelo();
            }
        );
    }

    private void redibujarModelo() {

        canvasPane.getChildren().clear();
        canvasPane.getChildren().add(imageView);

        if (modelo == null || infoImagen == null) return;

        renderer.dibujarBloques(
            canvasPane,
            modelo.getBloques(),
            infoImagen,
            mapaBloques
        );
    }
}
