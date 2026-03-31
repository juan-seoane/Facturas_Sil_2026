package app.core;

import infraestructure.servicios.config.Config;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class AppController {

    private static AppController instance;

    private AppController() {}

    public static synchronized AppController get() {
        if (instance == null) instance = new AppController();
        return instance;
    }

    public void iniciarAplicacion() {
   
        String ruta = "FxAcceso";
        Scene escena = AppContext.get().nav().crearEscena(ruta);
        Stage st = AppContext.get().nav().crearStage(escena);
        AppContext.get().nav().cambiarEscena(st,escena);
        AppContext.get().nav().mostrarStage(st);

    }

    public static void loginExitoso(String usuario) {
        AppContext.setUsuarioActual(usuario);
        System.out.println("[AppController>loginExitoso] Usuario '" + usuario + "' ha iniciado sesión exitosamente.");
        //1. Cargar Configuración del Usuario
        Config.getConfig(usuario);
        //2. Abrir Panel de Control
        Stage pc = AppContext.get().nav().crearCambiarEscena(new Stage(), "FxPanelControl", StageStyle.DECORATED);
        AppContext.get().nav().mostrarStage(pc);
    }
}

