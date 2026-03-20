package app.core;

import app.services.FacturaService;
import app.services.NavService;
import infraestructure.servicios.AuthService;

public class AppContext {

    private static AppContext instance;

    private String usuarioActual;
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

    public String getUsuarioActual() {
        return this.usuarioActual;
    }

    public void setUsuarioActual(String user) {
        this.usuarioActual = user;
    }
}