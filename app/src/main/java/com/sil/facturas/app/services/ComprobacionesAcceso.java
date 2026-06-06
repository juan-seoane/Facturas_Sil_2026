package com.sil.facturas.app.services;

import java.awt.HeadlessException;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.sil.facturas.app.core.AppContext;
import com.sil.facturas.domain.records.Credenciales;
import com.sil.facturas.domain.records.Creds;
import com.sil.facturas.infrastructure.helpers._Ruta;
import com.sil.facturas.infrastructure.servicios.AuthService;
import com.sil.facturas.infrastructure.servicios.config.Config;


public class ComprobacionesAcceso {

    // REVIEW: 24-07-20 : Queda este atributo, baseUser, para hacer comprobaciones...
    //	private String baseUser = "ADMIN";
    public static boolean userOK = false;
    public static boolean passOK = false;

    //#region COMPROBAR_CREDENCIALES()
    public synchronized boolean comprobarCredenciales(String user, String pass)
        throws HeadlessException, NullPointerException, IOException {
        // REVIEW : 24/04/09 - Si no existe carpeta de Usuario, preguntar si quiere crear un Nuevo Usuario.
        // REVIEW : 24/04/12 - Para las comprobaciones tanto usuario como contraseña se pasan a mayúsculas (CASE INSENSITIVE)
        String rutaCreds = _Ruta.CONFIG.getRuta() + "/creds.json";
        //String rutaDirPers = _Ruta.DATOS.getRuta() + "/" + user.toUpperCase();
        //String rutasCFG = _Ruta.CONFIG.getRuta() + "/" + user.toUpperCase() + "/" + "rutasconfig.json";

        AppContext.get().getAccesoUI().imprimirMensaje("\nDatos introducidos : " + user + " - " + pass + "\n(...espere..)");

        // STUB : 26-03-11 : [ComprobarCredenciales.java > comprobarCredenciales(String user, String pass)] Arreglar este boolean... Ahora mismo no es exacto... solamente chequea creds
        boolean existen = (getClass().getResource(rutaCreds) != null);

        if (existen) {
            // REVIEW : Si existe el Subdirectorio y el archivo config, lo lee.. (Cambiar por chequear las credenciales del archivo config base)
            // REVIEW : Otra vez tuve que hacer público el constructor de la clase Config...por lo que...¿Singleton...?
            // REVIEW - 24-04-11 : Escribir un método estático para leer las credenciales del archivo config base
            for (Creds contr : ((Credenciales)(AuthService.leerCredenciales(rutaCreds))).getCreds()){
                // REVIEW : Revisar el modo de comprobación de credenciales
                AppContext.get().getAccesoUI().imprimirMensaje(
                    "[ComprobacionesAcceso.java>comprobarCredenciales()]\nDatos obtenidos de Config: " +
                    contr.usuario() +
                    " - " +
                    contr.pass());

                if (user.equals(contr.usuario())) {
                    ComprobacionesAcceso.userOK = true;
                    if (pass.equals(contr.pass())) {
                        ComprobacionesAcceso.passOK = true;
                    }
                }
            }
        } else {
            // Si no existe el fichero, preguntar si quiere crear un Nuevo Usuario
            JFrame jf = new JFrame();
            jf.setAlwaysOnTop(true);
            int resp = JOptionPane.showConfirmDialog(
                jf,
                "El usuario no existe. ¿Quiere crear un Nuevo Usuario??",
                "ATENCIÓN!",
                JOptionPane.YES_NO_OPTION
            );
            if (resp == JOptionPane.YES_OPTION) {
                Config.getConfig(user);
                //crearNuevoUsuario(user);
                //System.out.println("[ComprobacionesAcceso.java] No existen Usuario, Subdirectorio y archivo Config personal..Creándose...\nLa contrasena será su nombre de Usuario, hasta que la cambie...");
                JOptionPane.showMessageDialog(
                    null,
                    "La contrasena del nuevo usuario será su nombre de Usuario, hasta que la cambie..."
                );
                // REVIEW 09-04-2024: Incluir las nuevas credenciales en el archivo config.json base
                return true; // Le damos acceso (ya están creados usuario y contrasena)
            } else {
                //System.out.println("[ComprobacionesAcceso.java] No existe el Usuario...");

            }
        }
        return (userOK && passOK);
    }

    //#endregion

    //STUB: 26-03-19 : Escribir el método crearNuevoUsuario() ... puede que deba estar en otra clase...
    //#region CREAR_NUEVO_USUARIO()
    // private void crearNuevoUsuario(String user) {
    //    // Config.guardarCredenciales(user);
    // }
    //#endregion
}
