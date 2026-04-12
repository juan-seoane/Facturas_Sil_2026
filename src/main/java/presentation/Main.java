package presentation;


import app.core.AppContext;
import app.helpers.VentanaID;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class Main extends Application {

   @Override
public void start(Stage stageSplash) {

    Stage splash = AppContext.get().nav().crearVentana(
        VentanaID.SPLASH,
            controller -> {
            } // no necesita init

    );
    
    splash.initStyle(StageStyle.UNDECORATED);
    splash.show();


}

    public static void main(String[] args) {
        launch(args);
    }
}

