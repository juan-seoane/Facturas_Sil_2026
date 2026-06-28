package com.sil.facturas.presentationgui.services.ocr;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class ModeloOCRVisualizer extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/FxModeloOCRVisualizer.fxml")
        );

        Parent root = loader.load();
        Scene scene = new Scene(root);

        stage.setTitle("Modelo OCR Visualizer");
        stage.setScene(scene);
        stage.show();

        Platform.runLater(() -> {
            Rectangle2D screen = Screen.getPrimary().getVisualBounds();
            stage.setHeight(screen.getHeight());
        });

    }

    public static void main(String[] args) {
        launch(args);
    }
}

