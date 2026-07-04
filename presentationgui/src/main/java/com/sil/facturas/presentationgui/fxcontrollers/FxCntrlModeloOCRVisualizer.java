package com.sil.facturas.presentationgui.fxcontrollers;

import static com.sil.facturas.domain.interfaces.IDebugService.*;

import com.sil.facturas.domain.ocr.ModeloOCR;
import com.sil.facturas.infrastructure.services.ocr.ModeloOCRService;
import com.sil.facturas.presentationgui.services.ocr.ModeloOCRRenderer;
import java.io.File;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;

public class FxCntrlModeloOCRVisualizer {

  @FXML private Pane rootPane;
  @FXML private Group group;
  @FXML private ImageView imageView;
  @FXML private Pane overlayPane;

  private ModeloOCRRenderer renderer;
  private ModeloOCRService modeloService = new ModeloOCRService();

  @FXML
  public void initialize() {
    // NO LLAMAR attachTo() AQUÍ
    renderer = new ModeloOCRRenderer();
  }

  public void postInit() {
    printWarning("[FxCntrlModeloOCRVisualizer>postInit] postInit() ejecutado");
    renderer.attachTo(group, imageView, overlayPane, rootPane);// overlayPane después de imageView
  }

  @FXML
  private void onCargarImagen() {
    FileChooser fc = new FileChooser();
    fc.setTitle("Seleccionar imagen Factura");
    File file = fc.showOpenDialog(rootPane.getScene().getWindow());
    if (file == null) return;

    Image img = new Image(file.toURI().toString());
    renderer.setImage(img);
    Platform.runLater(() -> renderer.recomputeScale());

  }

  @FXML
  private void onCargarModelo() {
    FileChooser fc = new FileChooser();
    fc.setTitle("Seleccionar modelo OCR");
    fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Modelo OCR (*.json)", "*.json"));

    File file = fc.showOpenDialog(rootPane.getScene().getWindow());
    if (file == null) return;

    ModeloOCR modelo = modeloService.cargarModelo(file);
    renderer.setModelo(modelo);
  }
}
