package com.sil.facturas.presentationgui.services.ocr;

import com.sil.facturas.app.core.AppContext;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;


public class ROIEditor extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/FxROIEditor.fxml"));

        Parent root = loader.load();
        Scene scene = new Scene(root);

        stage.setTitle("ROI Editor");
        // stage.setMaximized(true);
        stage.setScene(scene);
        AppContext.setUsuarioActual("admin");

        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        stage.setY(screen.getMinY());
        stage.setHeight(screen.getHeight());

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

