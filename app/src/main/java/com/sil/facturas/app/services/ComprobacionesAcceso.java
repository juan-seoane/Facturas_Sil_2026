package com.sil.facturas.app.services;

import com.sil.facturas.app.core.AppContext;

public class ComprobacionesAcceso {

    // REVIEW: 24-07-20 : Queda este atributo, baseUser, para hacer comprobaciones...
    //	private String baseUser = "ADMIN";

    public synchronized boolean comprobarCredenciales(String user, String pass) {

        AppContext.get()
                .getAccesoUI()
                .imprimirMensaje("\nDatos introducidos : " + user + " - " + pass + "\n(...espere..)");

        boolean valido = AppContext.auth().autenticar(user, pass);

        return valido;
    }

}

