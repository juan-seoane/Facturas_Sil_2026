package app.core;

import app.helpers.VentanaID;
import app.services.FacturasService;
import infrastructure.servicios.config.Config;
import javafx.stage.Stage;
import presentation.gui.fxcontrollers.FxCntrlTablaFCT;
import presentation.helpers.Debug;

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

        Stage acceso = AppContext.get().nav().crearVentana(
                VentanaID.LOGIN,
                controller -> {
                } // si no necesitas inicializar nada
        );

        acceso.show();
    }

    public static void loginExitoso(String usuario) {
        AppContext.setUsuarioActual(usuario);
        // Debug.print(
                // "[AppController>loginExitoso] Usuario '" + usuario + "' ha iniciado sesión exitosamente.");
        // 1. Cargar Configuración del Usuario
        AppContext.configActual = Config.getConfig(usuario);
        // Debug.print(
        //         "[AppController>loginExitoso] Configuración cargada para el usuario '"
        //                 + usuario
        //                 + "': "
        //                 + AppContext.configActual.getConfigData().toJSON());
        // 2. Inicializar servicio de facturas con la configuración personal
        FacturasService facturas = new FacturasService(AppContext.configActual.getConfigData());
        AppContext.setFacturasService(facturas);
        // 3. Crear la tablaFCT
        Stage tablaFCT = AppContext.get().nav().crearVentana(
        VentanaID.TABLA_FCT,
        controller -> ((FxCntrlTablaFCT) controller).cargarDatos()
    );

    AppContext.setTablaFCT(tablaFCT);

        //   Debug.print("AppContext en loginExitoso: " + AppContext.getTablaFCT().hashCode());
        // 4. Cargar el Panel de Control
        Stage pc = AppContext.get().nav().crearVentana(
                VentanaID.PANEL_CONTROL,
                controller -> {
                } // si no necesitas inicializar nada
        );

        pc.show();
    }
}
