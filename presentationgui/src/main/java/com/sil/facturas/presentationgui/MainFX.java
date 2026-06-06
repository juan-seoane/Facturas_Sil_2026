package com.sil.facturas.presentationgui;


import com.sil.facturas.app.core.AppContext;

import javafx.application.Application;
import javafx.stage.Stage;



public class MainFX extends Application {

   @Override
    public void start(Stage stageSplash) {

        AppContext.get().nav().mostrarSplash();
        
    }

    public static void main(String[] args) {
        launch(args);
    }
}

