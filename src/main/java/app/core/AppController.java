package app.core;

import javafx.scene.Scene;
import javafx.stage.Stage;


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
        st.show();

    }

    public void loginExitoso(String usuario) {
        AppContext.get().setUsuarioActual(usuario);
        Scene sc2 = AppContext.get().nav().crearEscena("FxPanelControl");
    }
}

