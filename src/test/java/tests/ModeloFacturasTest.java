package tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import modelo.ModeloFacturas;
import modelo.base.Config;
import modelo.records.Factura;

public class ModeloFacturasTest {
 ModeloFacturas m;
 Config cfg;

@Before
public void setUp(){

	try {
		//Automáticamente se genera la Config para el usuario 'admin'
		this.cfg = Config.getConfig();
	} catch (NullPointerException e) {
		System.out.println("[ModeloFacturasTest>setUp] Error al ejecutar Config.getConfig(): " + e);
		e.printStackTrace();
	}
	this.m = ModeloFacturas.getModelo();

}

@Test
public void listaFxOK(){
	System.out.println("[ModeloFacturaTest>listaFxOK] Recogiendo la lista de facturas FX");
	var listaFxFCT = this.m.getListaFXFacturas();

	assertTrue(listaFxFCT.get(0) instanceof Factura);

}
 


}
