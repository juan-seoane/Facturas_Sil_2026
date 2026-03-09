package tests;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle.Control;
import java.util.concurrent.BrokenBarrierException;

import org.junit.jupiter.api.Test;

import controladores.Controlador;
import javafx.collections.FXCollections;
import modelo.ModeloFacturas;
import modelo.base.Config;
import modelo.base.Fichero;
import modelo.records.Factura;

public class FicheroTest {

	@Test 
	void fileCredsExists(){

		String ruta="./config/creds.json";
		var f = new File(ruta);
		assertTrue(f.exists());

	}
	
	@Test
	void OKcredsJSON(){
    
		String ruta="./config/creds.json";
        String fichero = Fichero.leerJSON(ruta);
 			
		//System.out.println("[FicheroTest>OKcredsJSON] fichero:\n" + fichero);
		//Además informa de que el fichero no está vacío
		assertNotEquals("", fichero);

    }

	@Test
	void OKMisDatosJSON(){
		String usuario = "admin";
    
		String ruta="./config/" + usuario.toUpperCase() + "/misdatos.json";
        String fichero = Fichero.leerJSON(ruta);
 			
		//System.out.println("[FicheroTest>OKMisDatosJSON] fichero:\n" + fichero);
		//Además informa de que el fichero no está vacío
		assertNotEquals("", fichero);

    }

	@Test
	void OKleerCSV() throws InterruptedException, BrokenBarrierException, NullPointerException, IOException{
		String usuario = "admin";
		String rutaYnombre = "./datos/" + usuario.toUpperCase() + "/FCT242.csv";

		var ctrlPpal = Controlador.getControlador();
		Controlador.setUsuario(usuario);

		var fichero = new Fichero(rutaYnombre);
		var datosCSV = fichero.leerCSV(rutaYnombre);
	
		assertNotNull(datosCSV);
	
		assertNotNull(ModeloFacturas.getModelo().leerFacturasSinFiltrar());

		//System.out.println("\n*****[FicheroTest]********\nFacturas en la lista final:");
		for (Factura f : ModeloFacturas.getModelo().leerFacturasSinFiltrar()){
			//System.out.println(f.toString());
		}
		
	}
// REVIEW - 24-06-14 : Repasar este test, y comprobar el proceso de creacion de nuevos archivos de config y de trabajo, parece que se crean ficheros que no deberían existir en el directorio raiz, en vez de en el de cada usuario...
	@Test
	void OKguardarCSV() throws NullPointerException, IOException, InterruptedException, BrokenBarrierException{
		OKleerCSV();
		String rutaYnombre = "./datos/" + Controlador.getUsuario().toUpperCase()+"/FCT242.csv";
		Fichero<Factura> fichero = new Fichero<Factura>(rutaYnombre);
		ModeloFacturas modeloFCT;

		modeloFCT = ModeloFacturas.getModelo();

		List<Factura> listaFCT = ModeloFacturas.getModelo().leerFacturasSinFiltrar();
	 	System.out.print("\n*****[FicheroTest]******\nDatos a guardar en CSV:");

		ArrayList<String[]>datosCSV = modeloFCT.ConvertirListaFCTaCSV((ArrayList<Factura>)listaFCT);
		Boolean ok = fichero.guardarCSV(datosCSV);
		
		assertTrue(ok);
	}
}
