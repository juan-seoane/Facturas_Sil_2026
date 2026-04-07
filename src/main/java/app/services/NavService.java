package app.services;

import infraestructure.filesystem._Ruta;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import presentation.helpers.FxmlHelper;

/* CONTROLA LA CREACIÓN DE ESCENAS Y DEMÁS ASPECTOS DE LA NAVEGACIÓN POR VENTANAS */
public class NavService {

    private Stage ventana;

    public void cambiarEscena( Scene es) {

        this.ventana.setScene(es);

    }

    public void cambiarEscena(Stage stage, Scene es) {

        this.ventana = stage;
        this.ventana.setScene(es);

    }

    public void cambiarEscena(Stage stage, Scene es, EventHandler<KeyEvent> handlerTeclas) {

        this.ventana = stage;
        this.ventana.setScene(es);

        this.ventana.getScene().addEventFilter(KeyEvent.KEY_PRESSED, handlerTeclas);

        // --- ELIMINAR EL LISTENER AL CERRAR LA VENTANA ---
        this.ventana.setOnHidden(e -> {
            if (handlerTeclas != null) {
                this.ventana.getScene().removeEventFilter(KeyEvent.KEY_PRESSED, handlerTeclas);
            }
        });
    }

    public Scene crearEscena(String nombreArchivoFxmlSinExt) {
        String ruta = _Ruta.FXML.getRuta() + "/" + nombreArchivoFxmlSinExt + ".fxml";
        FxmlHelper loader = new FxmlHelper(ruta);
        //System.out.println("[NavService>crearEscena] ruta del FXML: " + ruta);
        Parent parent = loader.cargarFXML();
        Scene esc = new Scene(parent);
        //System.out.println("[NavService>crearEscena] escena creada : " + nombreArchivoFxmlSinExt + " : " + esc.hashCode() );
        return esc;
    }

    public Stage crearCambiarEscena(Stage stage, String nombreArchivoFxmlSinExt, StageStyle style) {
        Scene es = crearEscena(nombreArchivoFxmlSinExt);
        this.ventana = stage;
        if (es != null) {
            cambiarEscena(this.ventana, es);
            this.ventana.initStyle(style);
            return this.ventana;
        } else {
            System.out.println("[NavService>crearYCambiarEscena] Error al crear la escena: " + nombreArchivoFxmlSinExt);
            return null;
        }
    }

    public Stage crearStage(Scene sc) {
        Stage st = new Stage();
        st.setScene(sc);
        this.ventana = st;
        return this.ventana;
    }

    public void setStage(Stage stage) {
        this.ventana = stage;
    }

    public void mostrarStage(Stage st) {
        this.ventana = st;
        this.ventana.show();
    }
}
