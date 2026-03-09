package tests;

import controladores.Controlador;
import modelo.ModeloFacturas;
import modelo.base.Config;
import modelo.base.Fichero;
import modelo.records.Factura;

import org.junit.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.BrokenBarrierException;

public class FacturaTest {

	@SuppressWarnings("unchecked")
	@Test
	public void convertirCSVok() throws NullPointerException, IOException, InterruptedException, BrokenBarrierException{
		//Se inicializan config y controladores
		String usuario = "admin";
		String rutaYnombre = "datos/" + usuario.toUpperCase() + "/FCT20240.csv";
		var config = Config.getConfig(usuario);
		var contrlPpal = Controlador.getControlador();
		Controlador.setUsuario("admin");
		var modeloFCT = ModeloFacturas.getModelo();
		//Se lee el fichero CSV
		var fichero = new Fichero(rutaYnombre);
		ArrayList<String[]> datosCSV = fichero.leerCSV(rutaYnombre);
		//comprobación
		assertNotNull(datosCSV);
		//Se convierte el CSV a una lista de Facturas
		var listaPruebaFCT = new ArrayList<Factura>();
		for(String[] linea : datosCSV){
			Factura f = Factura.convertirCSVaFCT(linea);
			if(f!=null){
		        listaPruebaFCT.add(f);
			    System.out.println("****[FacturaTest] Factura convertida: ");
			    System.out.println(f.toString());
			}
	    }
		//Se comprueba...
	    assertNotNull(listaPruebaFCT);
	}
}
