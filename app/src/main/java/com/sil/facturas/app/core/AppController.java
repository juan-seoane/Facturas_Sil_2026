package com.sil.facturas.app.core;

import com.sil.facturas.app.api._VentanaID;
import com.sil.facturas.app.services.FacturasService;
import com.sil.facturas.domain.enums._Boton;
import com.sil.facturas.domain.interfaces.IDebugService;
import com.sil.facturas.domain.interfaces.IFacturaRepo;
import com.sil.facturas.infrastructure.csv.FacturaCSVRepo;
import com.sil.facturas.infrastructure.servicios.config.ConfigService;

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

        AppContext.get().nav().mostrar(_VentanaID.LOGIN,null);

    }

    public static void loginExitoso(String usuario) {

        IDebugService.print("[AppController>loginExitoso] usuario : " + usuario);

        // 1) Cargar config del usuario (AQUÍ ES DONDE FALLABA)
        AppContext.setConfigService(new ConfigService());
        var cfg = AppContext.getConfigService().getConfigData(usuario);

        // 2) Guardar usuario
        AppContext.setUsuarioActual(usuario);

        // 3) Crear repo con la ruta correcta
        String rutaFCT = cfg.getRutas().getFCT();
        IFacturaRepo repo = new FacturaCSVRepo(rutaFCT);

        // 4) Crear servicio de facturas
        FacturasService facturas = new FacturasService(repo);
        AppContext.setFacturasService(facturas);

        // 5) Mostrar UI
        IDebugService.print("[AppController>loginExitoso] Mostrando PANEL_CONTROL");
        AppContext.get().nav().mostrar(_VentanaID.PANEL_CONTROL, null);
        IDebugService.print("[AppController>loginExitoso] Mostrando TABLA_FCT");
        AppContext.get().pc().pulsar(_Boton.FCT);

        // 6) Cerrar ventana de acceso
        IDebugService.print("[AppController>loginExitoso] Cerrando LOGIN");
        AppContext.get().nav().cerrar(_VentanaID.LOGIN);
    }
}
