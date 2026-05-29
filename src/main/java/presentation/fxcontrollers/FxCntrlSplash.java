package presentation.fxcontrollers;

import app.core.AppContext;
import app.helpers.VentanaID;
import infrastructure.filesystem._Ruta;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import presentation.helpers.Debug;


public class FxCntrlSplash {
    @FXML
    private ImageView imgSplash;

    @FXML
    public void initialize() {
        Platform.runLater(() -> iniciarSplash());

    }

    private void iniciarSplash() {

        // Debug.print("[FxCntrlSplash.initialize()] → INICIO");

        int i = (int) (Math.floor(Math.random() * 4) + 1);
        String ruta = _Ruta.IMG.getRuta() + "/splash" + i + ".jpg";

        this.imgSplash.setImage(new Image(getClass().getResourceAsStream(ruta)));

        // Debug.print("[FxCntrlSplash.initialize()] - imgSplash = " + this.imgSplash);
        // Debug.print("[FxCntrlSplash.initialize()] - Ruta calculada = " + ruta);
        // Debug.print("[FxCntrlSplash.initialize()] - Stream = " + getClass().getResourceAsStream(ruta));

        FadeTransition ft = new FadeTransition(Duration.seconds(2), this.imgSplash);
        ft.setFromValue(1.0);
        ft.setToValue(0.5);
        ft.setDelay(Duration.seconds(2)); // tiempo visible

        ft.setOnFinished(e -> {
        // System.out.println("FADE TERMINADO → iniciando aplicación");

        // Cerrar el Stage del Splash
        Stage splashStage = (Stage) imgSplash.getScene().getWindow();
        splashStage.close();

        // Abrir el Stage de Acceso sin marco
        Stage acceso = AppContext.get().nav().crearVentana(
            VentanaID.LOGIN,
            controller -> {} // si no necesitas inicializar nada
        );

        acceso.initStyle(StageStyle.UNDECORATED);
        acceso.show();
    });
    ft.play();
    }
}
