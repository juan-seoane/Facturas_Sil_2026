package infraestructure.servicios.config;

import java.util.ArrayList;

import domain.records.ConfigData;
import domain.records.MisDatos;
import domain.records.RutasConfig;
import domain.records.UIData;
import infraestructure.filesystem._Ruta;

// REVIEW - 24-04-22 : configdata.json y misdatos.json deberían contener un JsonArray de sus respectivos objetos

public class Config {

    //#region CAMPOS DE LA CLASE
    private static Config configActual;
    public static String usuario;
    public ConfigData configData;
    public MisDatos misDatos;
    public UIData uiData;
    public RutasConfig rutasconfig;
// TODO : 26-03-15 : El plan era guardar un ArrayList<Config> con todas las configuraciones guardadas, para hacerlas intercambiables
    public static ArrayList<Config> configuraciones;
    //#endregion

    private Config(String user) {
        usuario = user;

        // TODO : chequear la existencia de todas estas rutas o su creación (en un archivo aparte?)
        String dirCFGpers = _Ruta.CONFIG.getRuta() + "/" + usuario.toUpperCase();
        String rutaCFG = _Ruta.CONFIG.getRuta() + "/" + usuario.toUpperCase() + "/rutasconfig.json";
        String rutaconfigdata = _Ruta.CONFIG.getRuta() + "/" + usuario.toUpperCase() + "/configdata.json";
        String rutamisdatos = _Ruta.CONFIG.getRuta() + usuario.toUpperCase() + "/misdatos.json";
        String rutauidata = _Ruta.CONFIG.getRuta() + usuario.toUpperCase() + "/uidata.json";
        String rutaDirTrab = _Ruta.DATOS.getRuta() + "/" + usuario.toUpperCase();

        System.out.println("[Config>Config(user)] Creada la Configuracion del Usuario " + usuario );

    }

    public static Config getConfig(String user) {
        if ((configActual == null)||(!(usuario.equals(user)))) {
            configActual = new Config(user);
        }
        return configActual;
    }

    public String getUsuario() {
        return usuario;
    }
        //#region TOSTR()
    @Override
    public String toString() {
        // REVIEW - 24-06-29 : Hay que hacer los toString de uidata, configdata, rutasCongig
        String resp = "Config del usuario " +
                usuario +
                " :\n ConfigData :\n" +
                this.configData.toString() +
                "\nmisDatos:\n" +
                ((this.misDatos != null) ? this.misDatos.toString() : " - NULL -") +
                "\nuiData:\n" +
                this.uiData.toString() +
                "\nrutasConfig:\n" +
                this.rutasconfig.toString() +
                /*"\nElementos en Lista static de configuraciones: " +
                ((Config.configuraciones != null) ? Config.configuraciones.size() : " - NULL -") + */
                "\nstatic configActual not NULL: " +
                ((Config.configActual != null) ? "S" : "N");
        return resp;
    }

    //#endregion

    public Object getConfigData() {
        // STUB : 26-03-19 : Escribir este método getConfigData()
        //leerConfigData(rutaconfigdata);
        return null;
    }
}
