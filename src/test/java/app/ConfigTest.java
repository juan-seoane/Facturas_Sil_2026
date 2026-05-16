package app;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import domain.records.Credenciales;
import infrastructure.filesystem._Ruta;
import infrastructure.servicios.AuthService;
import infrastructure.servicios.config.Config;

public class ConfigTest {
    //Declara el usuario del que testear los datos
    String user = "admin";
    String userAelim = "TESTuSER";
/*
    @Test
    void borrarDatosUsuario() {
        userAelim = "TESTuSER";
        Credenciales cred_prev = AppContext.get().auth().leerCredenciales(_Ruta.CONFIG.getRuta() + "/creds.json");
        var listaCredsNueva = new ArrayList<Creds>();
        //Generar nuevo archivo de credenciales sin el usuario declarado
        for (Creds c : cred_prev.getCreds()) {
            if (!c.usuario().equals(userAelim))
                listaCredsNueva.add(c);
        }

        var n_creds = new Credenciales();
        n_creds.setCreds(listaCredsNueva);

        // STUB : 26-03-19 : Falta método para Guardar Credenciales
        //Config.guardarCredenciales(n_creds);
        //Borrar subdirectorios de Config y Trabajo
        borrarSubdirs(userAelim);

    }

    void borrarSubdirs(String usuario) {
        File dir1 = new File("./config/" + usuario.toUpperCase());
        File dir2 = new File("./datos/" + usuario.toUpperCase());

        if (dir1.exists())
            recursiveDelete(dir1);
        if (dir2.exists())
            recursiveDelete(dir2);

        assertTrue(!dir1.exists());
        assertTrue(!dir2.exists());
    }

    static void recursiveDelete(File targetDirectory) {

        File[] data = targetDirectory.listFiles();

        for (File file : data) {
            if (file.isDirectory())
                recursiveDelete(file);

            else
                file.delete();
        }

        targetDirectory.delete();
    }
*/
    @Test
    void CreaConfigOK() throws NullPointerException, IOException {
        Config configPrueba = Config.getConfig(user);
        System.out.println("[FacturaTest>CrearConfig] Config: " + configPrueba.toString());
        assertNotNull(configPrueba);
    }

    @Test
    void leerCredencialesOK() {
        // TODO : 26-04-16 : El método leerCredenciales en AuthService ahora es ESTÁTICO
        String rutaCreds = _Ruta.CONFIG.getRuta() + "/creds.json";
        Credenciales c = AuthService.leerCredenciales(rutaCreds);
        System.out.println(c.toString());
        assertNotNull(c);

    }
}
        /*
    // REVIEW - 24-04-11 : Revisar esto: Si es Arraylist.class o Contrasena.class
	// REVIEW - 24-04-21 : Parece que hay un problema al leer las credenciales... El fichero lo lee bien, pero el Objeto 'Credenciales' lo coge mal...

    	//System.out.println("\n---------------\ncredenciales:\n"+credenciales.toString());
		if(credenciales.getCreds().size()>1)
			assertNotEquals(credenciales.getCreds().get(0).usuario(), credenciales.getCreds().get(1).usuario());
		else
			assertEquals("admin", credenciales.getCreds().get(0).usuario());
	}

	@Test
	void RutasConfigJsonExiste(){

		String ruta= _Ruta.CONFIG.getRuta() + user.toUpperCase() + "/rutasconfig.json";
		File fichero = new File(ruta);

		assertTrue(fichero.exists());
	}

	@Test
	void leerConfigDataJson(){
		String ruta=_Ruta.CONFIG.getRuta() + user.toUpperCase() + "/configdata.json";
		String datos = Fichero.leerJSON(ruta);
		Gson gson = new Gson();
		ConfigData configData = gson.fromJson(datos, ConfigData.class);
		assertEquals( user, configData.getUser());
	}

	@Test
	void leerRutasConfigJson(){

		String ruta = _Ruta.CONFIG.getRuta() + user.toUpperCase() + "/rutasconfig.json";
		String datos = Fichero.leerJSON(ruta);
		Gson gson = new Gson();
		RutasConfig rutas = gson.fromJson(datos, RutasConfig.class);
		String rutaMSDATS = _Ruta.CONFIG.getRuta() + user.toUpperCase() + "/misdatos.json";
		assertEquals( rutaMSDATS, rutas.getRutaMisDatos());
	}

	@Test
	void leerUIDataJson(){

		String ruta = _Ruta.CONFIG.getRuta() + user.toUpperCase() + "/uidata.json";
		String datos = Fichero.leerJSON(ruta);
		Gson gson = new Gson();
		UIData uidata = gson.fromJson(datos, UIData.class);

		assertEquals( 20, uidata.getNombreColsFCT().length);
	}

	@Test
	void leerMisDatosJson(){

		String ruta = _Ruta.CONFIG.getRuta() + user.toUpperCase() + "/misdatos.json";
		String datos = Fichero.leerJSON(ruta);
		Gson gson = new Gson();
		MisDatos misdatos = gson.fromJson(datos, MisDatos.class);
		assertEquals( user , misdatos.getUser());
	}

	@Test
	void configToStringOK() throws NullPointerException, IOException{
		user="admin";
		Config cfgPrueba;
		if((cfgPrueba = Config.getConfig(user))!=null){
			System.out.println("[ConfigTest>configToStringOK] config(user).toString():\n" + cfgPrueba.toString());
			assertEquals( user, cfgPrueba.getUsuario());
		}
	}

	@Test
	void ConfigFilesOK() throws NullPointerException, IOException{
	// REVIEW - 24-04-28 : El problema es pasar a 'final String' un dato que viene del JSON en forma de 'String' (sin 'final')
		Config cfgPrueba = Config.getConfig(user);

		String cfgjson = cfgPrueba.getRutasconfig().toJSON();
		System.out.println("[ConfigTest>configToStringOK] rutasconfig:\n" + cfgjson);

		String cfgdtjson = cfgPrueba.getConfigData().toJSON();
		System.out.println("[ConfigTest>configToStringOK] configdata:\n" + cfgdtjson);

		String msdtsjson = cfgPrueba.getMisDatos().toJSON();
		System.out.println("[ConfigTest>configToStringOK] misdatos:\n" + msdtsjson);

		String uidtjson = cfgPrueba.getUiData().toJSON();
		System.out.println(uidtjson);

		assertEquals(user, cfgPrueba.getUsuario());

		String rutacfg1 = "config/"+cfgPrueba.getUsuario().toUpperCase()+"/rutasconfig.json";
		File fcfg1 = new File(rutacfg1);
		assertTrue(fcfg1.exists());

		String rutacfg2 = "config/"+cfgPrueba.getUsuario().toUpperCase()+"/configdata.json";
		File fcfg2 = new File(rutacfg2);
		assertTrue(fcfg2.exists());

		String rutacfg3 = "config/"+cfgPrueba.getUsuario().toUpperCase()+"/misdatos.json";
		File fcfg3 = new File(rutacfg3);
		assertTrue(fcfg3.exists());

		String rutacfg4 = "config/"+cfgPrueba.getUsuario().toUpperCase()+"/uidata.json";
		File fcfg4 = new File(rutacfg4);
		assertTrue(fcfg4.exists());
// REVIEW - 24-05-02 : Hay que guardar las credenciales y los ficheros de trabajo
	}
// REVIEW - 24-04-24 : Crear método toJSON() en cada record anterior, para luego grabar los ficheros
	@Test
	void WorkingFilesOK() throws NullPointerException, IOException{

	while(Config.getConfig(user)==null){
		System.out.print("");
	}
	Config cfgPrueba = Config.getConfig(user);

	File f1 = new File(cfgPrueba.getConfigData().getRutas().getFCT());
	System.out.println("[ConfigTest] Chequeando el archivo "+ f1.getPath());
	assertTrue(f1.exists());

	File f2 = new File(cfgPrueba.getConfigData().getRutas().getRS());
	System.out.println("[ConfigTest] Chequeando el archivo "+ f2.getPath());
	assertTrue(f2.exists());

	File f3 = new File(cfgPrueba.getConfigData().getRutas().getCJA());
	System.out.println("[ConfigTest] Chequeando el archivo "+ f3.getPath());
	assertTrue(f3.exists());
	}

}
*/
