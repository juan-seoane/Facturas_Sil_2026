package tests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import modelo.base.Config;


public class ComprobacionesAccesoTest {

	String rutaCreds;

	@Test
	void fileCredsIsNotNull(){

		rutaCreds = "./config/creds.json";
 		File f = new File(rutaCreds);
		assertTrue(f.exists());

	}

	@Test
	void CredsNotNull(){

		//ComprobacionesAcceso test_check = new ComprobacionesAcceso();
		rutaCreds = "./config/creds.json";
		File f = new File(rutaCreds);
		assertNotNull(Config.leerCredenciales(rutaCreds).creds);

	}
	@Test
	void fileCredsLeidaOK(){
		rutaCreds = "./config/creds.json";
		File f = new File(rutaCreds);
		var listaContr = Config.leerCredenciales(rutaCreds).creds;
		if (listaContr.size()>1)
			assertNotEquals(listaContr.get(0).usuario,listaContr.get(1).usuario);
		else
			assertEquals("admin",listaContr.get(0).usuario);
	}
}
