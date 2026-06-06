package com.sil.facturas.app.core;

import com.sil.facturas.app.services.FacturasService;
import com.sil.facturas.infrastructure.servicios.config.Config;

import javafx.stage.Stage;


public class AppController {

    private static AppController instance;

    private AppController() {
    }

    public static synchronized AppController get() {
        if (instance == null)
            instance = new AppController();
        return instance;
    }

    public void iniciarAplicacion() {

        AppContext.get().nav().mostrarLogin();

    }

    public static void loginExitoso(String usuario) {

        AppContext.setUsuarioActual(usuario);

        AppContext.configActual = Config.getConfig(usuario);

        FacturasService facturas = new FacturasService(AppContext.configActual.getConfigData());
        AppContext.setFacturasService(facturas);

        AppContext.get().nav().mostrarTablaFacturas();
        AppContext.get().nav().mostrarPanelControl();
    }

}
