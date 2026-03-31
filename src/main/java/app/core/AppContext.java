package app.core;

import app.services.FacturaService;
import app.services.NavService;
import infraestructure.servicios.AuthService;
import javafx.stage.Stage;

public class AppContext {

    private static AppContext instance;

    public static String usuarioActual;
    private final AuthService authService = new AuthService();
    private final FacturaService facturaService = new FacturaService();
    private final NavService navService = new NavService();

    private AppContext() {
    }

    public static synchronized AppContext get() {
        if (instance == null)
            instance = new AppContext();
        return instance;
    }

    public AuthService auth() {
        return authService;
    }

    public FacturaService facturas() {
        return facturaService;
    }

    public NavService nav() {
        return navService;
    }

    public static void setUsuarioActual(String user) {
        usuarioActual = user;
    }
}