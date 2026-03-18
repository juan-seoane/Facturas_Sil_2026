package presentation;

import java.io.IOException;

import javafx.application.Application;
import presentation.fxcontrollers.FxAcceso;
import presentation.fxcontrollers.SplashFX;

public class Main {

    //public static Controlador ctr;
    public static FxAcceso acceso;
    public static String usuario;
    public static boolean credsOK;

    public static void main(String[] args) throws IOException {

        Application.launch(SplashFX.class);
    // NOTE : Desde aquí no debería funcionar hasta que se acabe la aplicación JavaFX
        System.out.println("[Main.java] Aplicación finalizada");
        System.exit(0);

    }

}
