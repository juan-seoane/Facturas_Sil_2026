package app.services;

import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import modeloNegocio.Infraestructura.filesystem._Ruta;
import vista.helpers.FxmlHelper;

/* CONTROLA LA CREACIÓN DE ESCENAS Y DEMÁS ASPECTOS DE LA NAVEGACIÓN POR VENTANAS */
public class NavService {


    public static void cambiarEscena(Stage stage, Scene es, EventHandler<KeyEvent> handlerTeclas) {

        Stage ventana = stage;
        ventana.setScene(es);

    ventana.getScene().addEventFilter(KeyEvent.KEY_PRESSED, handlerTeclas);

    // --- ELIMINAR EL LISTENER AL CERRAR LA VENTANA ---
    ventana.setOnHidden(e -> {
        if (handlerTeclas != null) {
            ventana.getScene().removeEventFilter(KeyEvent.KEY_PRESSED, handlerTeclas);
        }
    });
}

public static Scene crearEscena(String nombreArchivoFxmlSinExt) {
        String ruta = _Ruta.FXML.getRuta() + "/" + nombreArchivoFxmlSinExt + ".fxml";
        FxmlHelper loader = new FxmlHelper(ruta.trim());
        System.out.print("[NavService>crearEscena] ruta del FXML: " + ruta.trim());
        Parent parent = loader.cargarFXML();
        Scene esc = new Scene(parent);
        System.out.println("[NavService>crearEscena] escena creada : " + nombreArchivoFxmlSinExt + " : " + esc.hashCode() );
        return esc;
    }

    public static Stage crearStage(Scene sc) {
        Stage st = new Stage();
        st.setScene(sc);
        return st;
    }
}
