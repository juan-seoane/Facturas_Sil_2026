package com.sil.facturas.presentationgui.services.ocr;

import com.sil.facturas.infrastructure.helpers._Ruta;
import com.sil.facturas.presentationgui.fxcontrollers.FxROIEditorController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ROIEditor extends Application {

  private String rutaImagen;

  @Override
  public void start(Stage stage) throws Exception {

    // ============================================================
    // 1) Cargar imagen desde disco
    // ============================================================
    String usuario = "admin";
    String nombreImagen = "test1.png";

    rutaImagen = _Ruta.DATOS.getRuta() + "/" + usuario.toUpperCase() + "/scans/" + nombreImagen;

    FXMLLoader loader =
        new FXMLLoader(getClass().getResource(_Ruta.FXML.getRuta() + "/FxROIEditor.fxml"));
    Parent root = loader.load();

    FxROIEditorController fxCtrl = loader.getController();
    fxCtrl.cargarImagen(rutaImagen);
    fxCtrl
        .cargarBloquesDespuesDeImagen(); // Cargar bloques después de cargar la imagen, para que se
                                         // normalicen correctamente

    // Crear Stage temporal para mostrar el editor
    stage.setTitle("Editor de ROIs");
    stage.setScene(new Scene(root));
    stage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
