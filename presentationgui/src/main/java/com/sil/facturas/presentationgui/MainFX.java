package com.sil.facturas.presentationgui;


import com.sil.facturas.app.api.INavService;
import com.sil.facturas.app.core.AppContext;
import com.sil.facturas.domain.interfaces.IDebugService;
import com.sil.facturas.infrastructure.servicios.AuthService;
import com.sil.facturas.presentationgui.helpers._VentanaFX;
import com.sil.facturas.presentationgui.services.NavSerFX;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainFX extends Application {

   @Override
    public void start(Stage stageSplash) {
        IDebugService.print("([MainFX] >>> FX THREAD OK: " + Thread.currentThread());
        // 1a) Crear el servicio de navegación JavaFX
        IDebugService.print("[MainFX] >>> creando NavSerFX");
        INavService nav = new NavSerFX();

    // 1b) Registrar en AppContext
        IDebugService.print("[MainFX] >>> registrando NavSerFX");
        AppContext.get().setNav(nav);

    // 2) Crear un Servicio de Autenticación
        IDebugService.print("[MainFX] >>> creando y registrando AuthService");
        AppContext.setAuthService(new AuthService());

        // 3) Llamar a mostrarSplash()
        IDebugService.print("[MainFX] llamando al Splash");
        nav.mostrar(_VentanaFX.SPLASH, null);
        IDebugService.print("[MainFX] llamada a Splash terminada");

    }

    public static void main(String[] args) {
        launch(args);
    }
}

