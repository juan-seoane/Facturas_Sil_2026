package infrastructure.servicios.ocr.editor;

import infrastructure.filesystem._Ruta;
import java.io.File;
import java.io.FileInputStream;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import presentation.fxcontrollers.FxROIEditorController;

public class ROIEditor extends Application {

  private String rutaImagen;
  private Image imagenOriginal;

  @Override
  public void start(Stage stage) throws Exception {

    // ============================================================
    // 1) Cargar imagen desde disco
    // ============================================================
    String usuario = "admin";
    String nombreImagen = "test1.png";

    rutaImagen = _Ruta.DATOS.getRuta() + "/" + usuario.toUpperCase() + "/scans/" + nombreImagen;

    File f = new File(rutaImagen);
    if (!f.exists()) {
      new Alert(Alert.AlertType.ERROR, "[ROIEditor] La imagen NO existe:\n" + rutaImagen)
          .showAndWait();
      return;
    }

    imagenOriginal = new Image(new FileInputStream(f));
    FXMLLoader loader = new FXMLLoader(getClass().getResource(_Ruta.FXML.getRuta() + "/FxROIEditor.fxml"));
    Parent root = loader.load();

    FxROIEditorController ctrl = loader.getController();
    ctrl.cargarImagen(imagenOriginal);

    // Crear Stage temporal para mostrar el editor  
        stage.setTitle("Editor de ROIs");
        stage.setScene(new Scene(root));
        stage.show();

  }

  public static void main(String[] args) {
    launch(args);
  }
}
