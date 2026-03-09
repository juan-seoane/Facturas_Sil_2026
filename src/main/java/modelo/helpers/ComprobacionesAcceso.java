package modelo.helpers;

import modelo.base.Config;
import modelo.base.Fichero;
import modelo.records.Contrasena;
import ui.fxcontrollers.Acceso;

import java.awt.HeadlessException;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class ComprobacionesAcceso {

// REVIEW: 24-07-20 : Queda este atributo, baseUser, para hacer comprobaciones...
//	private String baseUser = "ADMIN";
	public static boolean userOK = false;
	public static boolean passOK = false;
//#region COMPROBAR_CREDENCIALES()
	public synchronized boolean comprobarCredenciales(String user, String pass) throws HeadlessException, NullPointerException, IOException{
		// REVIEW : 24/04/09 - Si no existe carpeta de Usuario, preguntar si quiere crear un Nuevo Usuario.
		// REVIEW : 24/04/12 - Para las comprobaciones tanto usuario como contraseña se pasan a mayúsculas (CASE INSENSITIVE)
		String rutaCreds = "./config/creds.json";
		String rutaDirPers = "./config/"+user.toUpperCase();
		String rutasCFG = "./config/"+user.toUpperCase()+"/"+"rutasconfig.json";


		Acceso.imprimir("\nDatos introducidos : " + user + " - " + pass +"\n(...espere 2 seg...)");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if ( Fichero.dirExists(rutaDirPers) && Fichero.fileExists(rutasCFG) ){
			// REVIEW : Si existe el Subdirectorio y el archivo config, lo lee.. (Cambiar por chequear las credenciales del archivo config base)
			// REVIEW : Otra vez tuve que hacer público el constructor de la clase Config...por lo que...¿Singleton...?
			// REVIEW - 24-04-11 : Escribir un método estático para leer las credenciales del archivo config base
			for (Contrasena contr : Config.leerCredenciales(rutaCreds).creds){
			// REVIEW : Revisar el modo de comprobación de credenciales    
					//System.out.println("[ComprobacionesAcceso.java>comprobarCredenciales()] Datos obtenidos de Config: " + contr.usuario +" - " + contr.contra);
					if (user.equals(contr.usuario)){
						ComprobacionesAcceso.userOK = true;
						if(pass.equals(contr.contra)){
							ComprobacionesAcceso.passOK = true;		
					}					
				}
			}
		}else{
			// Si no existe el fichero, preguntar si quiere crear un Nuevo Usuario
			JFrame jf = new JFrame();
			jf.setAlwaysOnTop(true);
			int resp = JOptionPane.showConfirmDialog(jf, "El usuario no existe. ¿Quiere crear un Nuevo Usuario??", "ATENCIÓN!", JOptionPane.YES_NO_OPTION);
			if (resp == JOptionPane.YES_OPTION) {
				Config.getConfig(user);
				crearNuevoUsuario(user);
				//System.out.println("[ComprobacionesAcceso.java] No existen Usuario, Subdirectorio y archivo Config personal..Creándose...\nLa contrasena será su nombre de Usuario, hasta que la cambie...");
				JOptionPane.showMessageDialog(null,"La contrasena del nuevo usuario será su nombre de Usuario, hasta que la cambie...");
				// REVIEW 09-04-2024: Incluir las nuevas credenciales en el archivo config.json base
				return true; // Le damos acceso (ya están creados usuario y contrasena)
			} else {
				//System.out.println("[ComprobacionesAcceso.java] No existe el Usuario...");

			}
		}
		if(userOK && passOK)
			return true;
		else return false;
	}
//#endregion

//#region CREAR_NUEVO_USUARIO()
	private void crearNuevoUsuario(String user) {

		Config.guardarCredenciales(user);
	
	}
//#endregion
}

