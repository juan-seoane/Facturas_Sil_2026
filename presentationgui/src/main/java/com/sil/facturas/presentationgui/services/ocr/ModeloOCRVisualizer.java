package com.sil.facturas.presentationgui.services.ocr;

import com.sil.facturas.presentationgui.fxcontrollers.FxCntrlModeloOCRVisualizer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class ModeloOCRVisualizer extends Application {
    
@Override
public void start(Stage stage) throws Exception {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/FxModeloOCRVisualizer.fxml"));
    Parent root = loader.load();
    Scene scene = new Scene(root);

    stage.setTitle("Modelo OCR Visualizer");
    stage.setScene(scene);

    Rectangle2D vb = Screen.getPrimary().getVisualBounds();
    double a4Ratio = 1.0 / Math.sqrt(2.0);

    double targetH = vb.getHeight();
    double targetW = targetH * a4Ratio;

    stage.setWidth(targetW);
    stage.setHeight(targetH);

    stage.show();
    stage.centerOnScreen();

    FxCntrlModeloOCRVisualizer controller = loader.getController();
    javafx.application.Platform.runLater(controller::postInit);
}

  public static void main(String[] args) {
    launch(args);
  }
}
