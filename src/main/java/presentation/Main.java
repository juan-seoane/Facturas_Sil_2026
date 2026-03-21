package presentation;

import app.core.AppContext;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class Main extends Application {

   @Override
public void start(Stage stageSplash) {

    stageSplash.initStyle(StageStyle.UNDECORATED);
    AppContext.get().nav().setStage(stageSplash);
    Scene splash = AppContext.get().nav().crearEscena("FxSplash");

    AppContext.get().nav().cambiarEscena(splash);

    stageSplash.show();
}

    public static void main(String[] args) {
        launch(args);
    }
}

