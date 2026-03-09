package controladores;

import modelo.*;
import modelo.base.Config;
// TODO: 09/04/24 - Crear un ControladorFicheros, Singleton, para controlar el Guardado Automático
public class ControladorFicheros{

	private modelo.base.Fichero<modelo.records.Factura> fct;
	private Controlador controlador;
	
	public ControladorFicheros(Controlador controlador){
		
		this.controlador = controlador;
		//System.out.println("Ruta del fichero FCT : "+(String)(Config.getConfig().getRutas().get("FCT")));
		this.fct = new modelo.base.Fichero<modelo.records.Factura>((String)(Config.getConfig().getRutas().get("FCT")));
	}
	
	public modelo.base.Fichero<modelo.records.Factura> getFCT(){
		return this.fct;
	
	}

}