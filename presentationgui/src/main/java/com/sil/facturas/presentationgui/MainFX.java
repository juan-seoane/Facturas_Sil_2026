package com.sil.facturas.presentationgui;


import com.sil.facturas.app.api.INavService;
import com.sil.facturas.app.core.AppContext;
import com.sil.facturas.presentationgui.services.NavSerFX;

import javafx.application.Application;
import javafx.stage.Stage;



public class MainFX extends Application {

   @Override
    public void start(Stage stageSplash) {

        // 1) Crear el servicio de navegación JavaFX
        INavService nav = new NavSerFX();

        // 2) Registrar en AppContext
        AppContext.get().setNav(nav);

        // 3) Ahora sí puedes llamar a mostrarSplash()
        nav.mostrarSplash();
        
    }

    public static void main(String[] args) {
        launch(args);
    }
}

