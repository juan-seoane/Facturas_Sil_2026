package presentation.fxcontrollers;

import app.core.AppContext;
import app.core.AppController;
import infraestructure.filesystem._Ruta;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;


public class FxCntrlSplash {
    @FXML
    private ImageView imgSplash;

    @FXML
    public void initialize() {
        Platform.runLater(() -> iniciarSplash());

    }

    private void iniciarSplash() {
        
        System.out.println("[FxCntrlSplash.initialize()] → INICIO");

        int i = (int) (Math.floor(Math.random() * 4) + 1);
        String ruta = _Ruta.IMG.getRuta() + "/splash" + i + ".jpg";

        this.imgSplash.setImage(new Image(getClass().getResourceAsStream(ruta)));
        
        System.out.println("[FxCntrlSplash.initialize()] - imgSplash = " + this.imgSplash);
        System.out.println("[FxCntrlSplash.initialize()] - Ruta calculada = " + ruta);
        System.out.println("[FxCntrlSplash.initialize()] - Stream = " + getClass().getResourceAsStream(ruta));

        FadeTransition ft = new FadeTransition(Duration.seconds(2), this.imgSplash);
        ft.setFromValue(1.0);
        ft.setToValue(0.5);
        ft.setDelay(Duration.seconds(2)); // tiempo visible

        ft.setOnFinished(e -> {
        System.out.println("FADE TERMINADO → iniciando aplicación");

        // Cerrar el Stage del Splash
        Stage splashStage = (Stage) imgSplash.getScene().getWindow();
        splashStage.close();
            
        // Abrir el Stage de Acceso sin marco
        Stage stageAcceso = new Stage();
        stageAcceso.initStyle(StageStyle.UNDECORATED); // ← con marco
        AppContext.get().nav().setStage(stageAcceso);
        Scene acceso = AppContext.get().nav().crearEscena("FxAcceso");
        AppContext.get().nav().cambiarEscena(stageAcceso, acceso);
        stageAcceso.show();
        });

        ft.play();   
    }
}