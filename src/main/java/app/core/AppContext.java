package app.core;

import app.services.FacturasService;
import app.services.NavService;
import infraestructure.servicios.AuthService;
import infraestructure.servicios.config.Config;

public class AppContext {

    private static AppContext instance;

    public static String usuarioActual;
    private final AuthService authService = new AuthService();
    public static FacturasService facturasService;
    private final NavService navService = new NavService();
    public static Config configActual;
    
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

    public FacturasService facturas() {
        return facturasService;
    }

    public NavService nav() {
        return navService;
    }

    public static void setUsuarioActual(String user) {
        usuarioActual = user;
    }

    public static FacturasService getFacturasService() {
        return AppContext.facturasService;
    }

    public static void setFacturasService(FacturasService facturasService) {
        AppContext.facturasService = facturasService;
    }
}