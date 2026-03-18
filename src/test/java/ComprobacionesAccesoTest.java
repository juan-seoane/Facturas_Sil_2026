


import java.io.File;

import modeloNegocio.Infraestructura.filesystem._Ruta;
import infraestructure.services.config.Config;


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
