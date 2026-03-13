package modelo.base;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import modelo.records.Anho;
import modelo.records.ConfigData;
import modelo.records.Contrasena;
import modelo.records.Credenciales;
import modelo.records.MisDatos;
import modelo.records.NIF;
import modelo.records.RutasConfig;
import modelo.records.RutasTrabajo;
import modelo.records.UIData;

// REVIEW - 24-04-22 : configdata.json y misdatos.json deberían contener un JsonArray de sus respectivos objetos

public class Config {

    //#region CAMPOS DE LA CLASE
    public String usuario;
    public ConfigData configData;
    public MisDatos misDatos;
    public UIData uiData;
    public RutasConfig rutasconfig;

    public static ArrayList<Config> configuraciones;
    public static Config configActual = null;

    //#endregion

    // REVIEW - 24-06-29 : Repasar el constructor y los getConfig... parece que el usuario tiene que designarse en otro lado...
    //#region CONSTRUCTOR_NC_DE_CONFIG
    private Config(String user) {
        this.usuario = user;
        String dirCFGpers = _Ruta.CONFIG.getRuta() + "/" + user.toUpperCase();
        // Carpeta de Config personal
        try {
            if (!Fichero.dirExists(dirCFGpers)) {
                Fichero.crearCarpeta(dirCFGpers);
                System.out.println(
                    "[Config>Config(user)] No existe carpeta personal para la Config del usuario " +
                    user +
                    "...Ha sido creada!"
                );
            }
        } catch (NullPointerException | IOException e) {
            System.out.println("[Config>Config(user)] Excepcion Comprobando el directorio de la config");
            e.printStackTrace();
        }
        // Archivo 'rutasconfig.json'
        String rutaCFG = _Ruta.CONFIG.getRuta() + "/" + user.toUpperCase() + "/rutasconfig.json";
        File rutascfg = new File(rutaCFG);
        if (rutascfg.exists()) {
            String rutasCFG = Fichero.leerJSON(rutaCFG);
            this.rutasconfig = new Gson().fromJson(rutasCFG, RutasConfig.class);
            if (this.rutasconfig == null) {
                //System.out.println("[Config>Config(user)] No existen Rutas para la Config del usuario " + user);
                this.rutasconfig = getRutasConfigStd(user);
                Fichero.guardarJSON(this.rutasconfig.toJSON(), rutaCFG);
            }
        } else {
            System.out.println(
                "[Config>Config(user)] El fichero rutasconfig.json del usuario " +
                user +
                " no existe!!!\nSe creará una configuración estándar"
            );
            this.rutasconfig = getRutasConfigStd(user);
            Fichero.guardarJSON(this.rutasconfig.toJSON(), rutaCFG);
        }
        // Archivo 'configdata.json'
        String rutaconfigdata = _Ruta.CONFIG.getRuta() + "/" + user.toUpperCase() + "/configdata.json";
        File f_configdata = new File(rutaconfigdata);
        if (!f_configdata.exists()) {
            this.configData = getConfigDataStd();
            Fichero.guardarJSON(this.configData.toJSON(), rutaconfigdata);
        } else this.configData = getConfigData();
        // Archivo 'misdatos.json'
        // REVIEW: ¿Porqué tienen que estar los datos fiscales del usuario todo el tiempo en memoria? No deberían cargarse hasta que sean necesarios...
        String rutamisdatos = "./data/config/" + user.toUpperCase() + "/misdatos.json";
        File f_misdatos = new File(rutamisdatos);
        if (!(f_misdatos.exists()) || (f_misdatos == null)) {
            this.misDatos = getMisDatosStd();
            Fichero.guardarJSON(this.misDatos.toJSON(), rutamisdatos);
        } else this.misDatos = getMisDatos();
        // Archivo 'uidata.json'
        String rutauidata = "./data/config/" + user.toUpperCase() + "/uidata.json";
        File f_uidata = new File(rutauidata);
        if (!(f_uidata.exists()) || (f_uidata == null)) {
            this.uiData = getUiDataStd();
            Fichero.guardarJSON(this.uiData.toJSON(), rutauidata);
        } else {
            this.uiData = getUiData();
        }

        // Archivos de trabajo
        //System.out.println("[Config>Config(user)] ...Vamos a supervisar y/o crear los archivos de trabajo del usuario " + user );
        String rutaDirTrab = _Ruta.DATOS.getRuta() + "/" + user.toUpperCase();
        Fichero.crearCarpeta(rutaDirTrab); //ya comprueba si existe o no...
        String trab1 = "/FCT" + this.configData.getAnho().getAnho() + this.configData.getAnho().getTrimestre() + ".csv";
        String trab2 = "/RS" + this.configData.getAnho().getAnho() + ".csv";
        String trab3 = "/CJA" + this.configData.getAnho().getAnho() + this.configData.getAnho().getTrimestre() + ".csv";
        Fichero.crearFichero(rutaDirTrab, trab1); //ya comprueba si existe o no...
        Fichero.crearFichero(rutaDirTrab, trab2); //ya comprueba si existe o no...
        Fichero.crearFichero(rutaDirTrab, trab3); //ya comprueba si existe o no...
        // REVIEW - 24-06-16 : Cambiar la extension de los archivos de trabajo a .csv
        // REVIEW - 24-05-06 : Hay que hacer un método para borrar MisDatos (no tiene sentido que se guarden todo el tiempo en memoria)

        //Se borra el objeto 'MisDatos'
        this.misDatos = null;
        //System.out.println("[Config>Config(user)] Creada la Configuracion del Usuario " + user );
    }

    //#endregion

    //#region GET_CONFIG
    // REVIEW - 24-04-11 : Parece que, ahora mismo, no tiene sentido llamar a un constructor sin definir el usuario...
    // REVIEW - 24-04-11 : Pensar cómo meter usuario
    // REVIEW - 24-04-18 : Hacer un Diagrama de Flujo de todo el proceso de Config, para ajustar
    // REVIEW - 24-05-02 : Estoy probando con 'synchronized' intentando que no use la config mientras se genera...
    public static Config getConfig(String user) {
        if (Config.configActual == null || !Config.configActual.getUsuario().equals(user)) configActual =
            new Config(user);

        return configActual;
    }

    public static Config getConfig() {
        //TODO- 24-06-15 : Hacer un método para que te pregunte qué usuario quieres inicializar...
        if (Config.configActual == null) {
            Config.configActual = new Config("admin");
        }
        return Config.configActual;
    }

    //#endregion

    //#region NUEVOS GETTERS
    private synchronized RutasConfig getRutasConfigStd(String user) {
        String ruta2 = _Ruta.CONFIG.getRuta() + "/" + user.toUpperCase() + "/configdata.json";
        String ruta3 = _Ruta.CONFIG.getRuta() + "/" + user.toUpperCase() + "/misdatos.json";
        String ruta4 = _Ruta.CONFIG.getRuta() + "/" + user.toUpperCase() + "/uidata.json";
        RutasConfig resp = new RutasConfig(user, ruta2, ruta3, ruta4);
        return resp;
    }

    public synchronized ConfigData getConfigDataStd() {
        var tiposIVAprueba = new ArrayList<Integer>();
        tiposIVAprueba.add(0);
        tiposIVAprueba.add(21);
        var origenesPrueba = new ArrayList<String>();
        origenesPrueba.add("caja");
        origenesPrueba.add("otros");
        // TODO- 24-04-28 : Falta asignar rutas estándar para esta config (usuario ya definido)
        var rutas = new RutasTrabajo(
            _Ruta.DATOS.getRuta() + "/" + this.usuario.toUpperCase() + "/FCT242.csv",
            _Ruta.DATOS.getRuta() + "/" + this.usuario.toUpperCase() + "/RS24.csv",
            _Ruta.DATOS.getRuta() + "/" + this.usuario.toUpperCase() + "/CJA242.csv"
        );

        Anho año = new Anho(24, 2);

        ConfigData cfDataPrueba = new ConfigData("TESTuSER", año, rutas, tiposIVAprueba, origenesPrueba);
        //    System.out.println("[Config.java] Asignando ConfigData Estándar");
        return cfDataPrueba;
    }

    public synchronized MisDatos getMisDatosStd() {
        MisDatos msDtsPrueba = new MisDatos("admin", new NIF(12345678, "X", false), "nombreEmpresaPrueba");
        //    System.out.println(" [Config.java] Asignando MisDatos Estándar");
        return msDtsPrueba;
    }

    public synchronized UIData getUiDataStd() {
        var nombresColprueba = getNombresColumnasStandard();
        var anchosColprueba = getAnchoColumnasStandard();

        UIData uiDataPrueba = new UIData(nombresColprueba, anchosColprueba);
        //    System.out.println(" [Config.java] Asignando UIData Estándar");
        return uiDataPrueba;
    }

    public synchronized String[] getNombresColumnasStandard() {
        String[] nCols = {
            "ID",
            "#Fact",
            "fecha",
            "#RS",
            "NifRS",
            "RS",
            "concepto",
            "esDev",
            "#extr",
            "base",
            "varIVA",
            "tipoIVA",
            "IVA",
            "SubTotal",
            "base N.I.",
            "t ret",
            "Retenc",
            "Total",
            "#Nota",
            "Nota"
        };
        //    System.out.println(" [Config.java] Asignando Nombres de Columna Estándar");
        return nCols;
    }

    public synchronized Integer[] getAnchoColumnasStandard() {
        Integer[] aCols = { 10, 50, 40, 10, 50, 70, 50, 10, 10, 80, 10, 20, 50, 70, 60, 10, 30, 80, 10, 60 };
        //    System.out.println(" [Config.java] Asignando Anchos de Columna Estándar");
        return aCols;
    }

    public synchronized UIData getUiData() {
        String ruta = this.rutasconfig.getRutaUIData();
        String datos = Fichero.leerJSON(ruta);
        UIData resp = new Gson().fromJson(datos, UIData.class);
        //    System.out.println("[Config.java] Asignando UIData del consiguiente archivo\n");

        return resp;
    }

    public synchronized MisDatos getMisDatos() {
        String ruta = this.rutasconfig.getRutaMisDatos();
        String datos = Fichero.leerJSON(ruta);
        MisDatos resp = new Gson().fromJson(datos, MisDatos.class);
        //    System.out.println("[Config.java] Asignando MisDatos del consiguiente archivo:\n");

        return resp;
    }

    public synchronized ConfigData getConfigData() {
        String ruta = this.rutasconfig.getRutaConfigData();
        String datos = Fichero.leerJSON(ruta);
        ConfigData resp = new Gson().fromJson(datos, ConfigData.class);
        //    System.out.print("[Config.java>getConfigData()] : Datos leídos en " + this.rutasconfig.getRutaConfigData());

        return resp;
    }

    //#endregion

    //#region OTROS GETTERS
    // REVIEW - 24-05-13 : Acabo de quitar getConfigActual()
    // REVIEW - 24-04-28 : Controlar que esté todo bien después de quitar el static de algunos métodos get
    public String getUsuario() {
        return this.usuario;
    }

    public String getRutaRS() {
        String rs = getConfigData().getRutas().getRS();
        //    System.out.println(" [Config.java] Asignando Ruta RS desde la config");
        return rs;
    }

    public String getRutaFCT() {
        String fct = getConfigData().getRutas().getFCT();
        //    System.out.println(" [Config.java] Asignando Ruta FCT desde la config");
        return fct;
    }

    public String getRutaCJA() {
        String cja = getConfigData().getRutas().getCJA();
        //    System.out.println(" [Config.java] Asignando Ruta CJA desde la config");
        return cja;
    }

    //#endregion

    // TODO- 24-04-10 : Ver cómo buscar la lista de contrasenas de un usuario...
    // TODO- 24-04-19 : Sopesar si debería generar un archivo 'std_config.json' con la configuración inicial (la de admin:admin)
    // TODO- 24-04-19 : RecConfig cuándo se llama?

    //#region REC_CONFIG()
    public static synchronized boolean recConfig(String p_usuario, Config p_config)
        throws NullPointerException, IOException {
        // Crea las carpetas personales (si no existen)
        crearCarpetasPersonales(p_usuario);

        String ruta1 = _Ruta.CONFIG.getRuta() + "/" + p_usuario.toUpperCase() + "/rutasconfig.json";
        String ruta2 = _Ruta.CONFIG.getRuta() + "/" + p_usuario.toUpperCase() + "/configdata.json";
        String ruta3 = _Ruta.CONFIG.getRuta() + "/" + p_usuario.toUpperCase() + "/misdatos.json";
        String ruta4 = _Ruta.CONFIG.getRuta() + "/" + p_usuario.toUpperCase() + "/uidata.json";
        // TODO- 24-04-20 : Rehacer 'datosFormateados', enviar la config a formatearse como JSON, o incluir una std_config
        // TODO- 24-04-23 : Rehacer el método toString() para adaptarlo a la salida en JSON
        String datos1 = p_config.rutasconfig.toJSON();
        //    System.out.println("[Config.java]->\n" + datos1);
        String datos2 = p_config.getConfigData().toJSON();
        //    System.out.println("[Config.java]->\n" + datos2);
        String datos3 = p_config.getMisDatos().toJSON();
        //    System.out.println("[Config.java]->\n" + datos3);
        String datos4 = p_config.getUiData().toJSON();
        //    System.out.println("[Config.java]->\n" + datos4);

        // Crea los ficheros de Configuracion (también si no existen)
        if (!Fichero.fileExists(ruta1)) {
            Fichero.guardarJSON(datos1, ruta1);
        }
        if (!Fichero.fileExists(ruta2)) {
            Fichero.guardarJSON(datos2, ruta2);
        }
        if (!Fichero.fileExists(ruta3)) {
            Fichero.guardarJSON(datos3, ruta3);
        }
        if (!Fichero.fileExists(ruta4)) {
            Fichero.guardarJSON(datos4, ruta4);
        }

        //System.out.println(" [Config.java>recConfig] El nuevo año se ha establecido en  " + p_config.getConfigData().getAnho().getAnho()+" - Trimestre: " + p_config.getConfigData().getAnho().getTrimestre());

        String dirTrab = _Ruta.DATOS.getRuta() + "/" + p_usuario.toUpperCase();
        String fiTrab1 = p_config.getConfigData().getRutas().getFCT();
        String fiTrab2 = p_config.getConfigData().getRutas().getRS();
        String fiTrab3 = p_config.getConfigData().getRutas().getCJA();

        if (
            Fichero.crearCarpeta(dirTrab) &&
            Fichero.crearFichero(fiTrab1) &&
            Fichero.crearFichero(fiTrab2) &&
            Fichero.crearFichero(fiTrab3)
        ) {
            System.out.println(" [Config>recConfig] Archivos de trabajo personales en orden para usuario " + p_usuario);
        }
        return true;
    }

    //#endregion

    //#region CREAR_DIRPERS()
    private static synchronized boolean crearCarpetasPersonales(String p_usuario)
        throws NullPointerException, IOException {
        Boolean rutaConfigOK = false;
        Boolean rutaTrabOK = false;

        String rutadircfg = _Ruta.CONFIG.getRuta() + "/" + p_usuario.toUpperCase() + "/";
        String rutadirtrab = _Ruta.DATOS.getRuta() + "/" + p_usuario.toUpperCase() + "/";
        File dircfg = new File(rutadircfg);
        File dirtrab = new File(rutadirtrab);
        if (dircfg.exists() && Fichero.dirExists(rutadircfg)) {
            //System.out.println(" [Config.java] El subdirectorio de configuracion personal del usuario "+p_usuario+" ya existe!");
            rutaConfigOK = true;
        } else {
            if (Fichero.crearCarpeta(rutadircfg)) rutaConfigOK = true; else rutaConfigOK = false;
        }
        if (dirtrab.exists() && Fichero.dirExists(rutadirtrab)) {
            //System.out.println(" [Config.java] El subdirectorio de trabajo personal del usuario "+p_usuario+" ya existe!");
            rutaTrabOK = true;
        } else {
            if (Fichero.crearCarpeta(rutadirtrab)) rutaTrabOK = true; else rutaTrabOK = false;
        }
        if (rutaConfigOK && rutaTrabOK) return true; else return false;
    }

    //#endregion

    //#region LEER_CFG_JSON()
    public static synchronized RutasConfig leerRutasCFGjson(String ruta) {
        // REVIEW : Si no existe el fichero, devolver 'false' y crearlo
        RutasConfig resp;
        File fichCFG = new File(ruta);
        if (fichCFG.exists()) {
            // REVIEW - 24-05-04 : Parece que el problema está aquí, cuando intenta leer el archivo 'rutasconfig.json'

            String json = Fichero.leerJSON(ruta);
            //System.out.println("[Config>leerRutasConfigJson] Fichero JSON leído");

            JsonElement rutasEl = new Gson().fromJson(json, JsonElement.class);
            JsonObject rutasObj = rutasEl.getAsJsonObject();

            String user = (rutasObj.get("user")).toString();
            user = user.substring(1, user.length() - 2);
            String ruta1 = (rutasObj.get("rutaconfigdata")).toString();
            ruta1 = ruta1.substring(1, ruta1.length() - 2);
            String ruta2 = (rutasObj.get("rutamisdatos")).toString();
            ruta2 = ruta2.substring(1, ruta2.length() - 2);
            String ruta3 = (rutasObj.get("rutauidata")).toString();
            ruta3 = ruta3.substring(1, ruta3.length() - 2);

            resp = new RutasConfig(user, ruta1, ruta2, ruta3);

            return resp;
        } else {
            return null;
        }
    }

    //#endregion

    //#region LEER_CREDS()
    public static synchronized Credenciales leerCredenciales(String ruta) {
        String ficheroResp = Fichero.leerJSON(ruta);
        System.out.println(" [Config.java] Crenciales leídas: \n" + ficheroResp);
        Gson gson = new Gson();
        Credenciales credenciales = gson.fromJson(ficheroResp, Credenciales.class);
        System.out.println("[Config>leerCredenciales] Credenciales leidas!"/* + credenciales.toString()*/);
        return credenciales;
    }

    //#endregion
// NOTE    - 26-03-11 [Config.java>leerCredenciales(String ruta)] : He cambiado la forma en que manejo los ficheros dentro de este método... posiblemente necesite cambiarla en más...
    //#region GUARDAR_CREDS()
    public static boolean guardarCredenciales(String user) {
        String ruta = _Ruta.CONFIG.getRuta() + "/creds.json";
        Credenciales creds = new Credenciales();

        if (Config.class.getResource(ruta)!=null) {
            creds = leerCredenciales(ruta);
        }
        creds.creds.add(new Contrasena(user, user));
        return (Fichero.guardarJSON(creds.toString(), ruta));
    }

    public static void guardarCredenciales(Credenciales nuevas_creds) {
        String ruta = _Ruta.CONFIG.getRuta() + "/creds.json";
        Fichero.guardarJSON(nuevas_creds.toString(), ruta);
    }

    //#endregion

    //#region TOSTR()
    @Override
    public String toString() {
        // REVIEW - 24-06-29 : Hay que hacer los toString de uidata, configdata, rutasCongig
        String resp =
            "Config del usuario " +
            this.usuario +
            " :\n ConfigData :\n" +
            this.configData.toString() +
            "\nmisDatos:\n" +
            ((this.misDatos != null) ? this.misDatos.toString() : " - NULL -") +
            "\nuiData:\n" +
            this.uiData.toString() +
            "\nrutasConfig:\n" +
            this.rutasconfig.toString() +
            "\nElementos en Lista static de configuraciones: " +
            ((Config.configuraciones != null) ? Config.configuraciones.size() : " - NULL -") +
            "\nstatic configActual not NULL: " +
            ((Config.configActual != null) ? "S" : "N");
        return resp;
    }

    //#endregion

    public static Config getConfigActual() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getConfigActual'");
    }
}
