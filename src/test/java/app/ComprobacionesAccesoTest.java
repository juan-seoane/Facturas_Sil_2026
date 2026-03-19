package app;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import infraestructure.filesystem._Ruta;
import infraestructure.servicios.AuthService;


public class ComprobacionesAccesoTest {

	String rutaCreds;

	@Test
	void fileCredsIsNotNull(){

		rutaCreds = _Ruta.CONFIG.getRuta() + "/creds.json";
 		File f = new File(rutaCreds);
		assertTrue(f.exists());

	}

	@Test
	void CredsNotNull(){

		//ComprobacionesAcceso test_check = new ComprobacionesAcceso();
		rutaCreds = _Ruta.CONFIG.getRuta() + "/creds.json";
		File f = new File(rutaCreds);
		assertNotNull(AuthService.leerCredenciales(rutaCreds).getCreds());

	}
	@Test
	void fileCredsLeidaOK(){
		rutaCreds = "./config/creds.json";
		File f = new File(rutaCreds);
		var listaContr = AuthService.leerCredenciales(rutaCreds).getCreds();
		if (listaContr.size()>1)
			assertNotEquals(listaContr.get(0).usuario(),listaContr.get(1).usuario());
		else
			assertEquals("admin",listaContr.get(0).usuario());
	}
}
