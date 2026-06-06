package com.sil.facturas.infrastructure.servicios.config;

import com.sil.facturas.domain.records.ConfigData;
import com.sil.facturas.domain.records.RutasConfig;
import com.sil.facturas.infrastructure.helpers._Ruta;
import com.sil.facturas.infrastructure.json.JsonParser;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

// REVIEW - 24-04-22 : configdata.json y misdatos.json deberían contener un JsonArray de sus
// respectivos objetos

public class Config {

  // #region CAMPOS DE LA CLASE
  public static String usuario;
  public String rutaConfigData;
  public String rutaMisDatos;
  public String rutaUIData;
  public String rutaRutasConfig;
  public String dirCFGpers;
  public String rutaCFG;
  public String dirPers;
  public static Config configActual;
  private ConfigData configData;
  private RutasConfig rutasconfig;

  // TODO : 26-03-15 : El plan era guardar un ArrayList<Config> con todas las configuraciones
  // guardadas, para hacerlas intercambiables
  public static ArrayList<Config> configuraciones;

  // #endregion

  private Config(String user) {
    usuario = user;

    // TODO : 26-03-15 : chequear la existencia de todas estas rutas o su creación (en un archivo
    // aparte?)
    this.dirCFGpers = _Ruta.CONFIG.getRuta() + "/" + usuario.toUpperCase();
    this.rutaRutasConfig =
        _Ruta.CONFIG.getRuta() + "/" + usuario.toUpperCase() + "/rutasconfig.json";
    this.rutaConfigData = _Ruta.CONFIG.getRuta() + "/" + usuario.toUpperCase() + "/configdata.json";
    this.rutaMisDatos = _Ruta.CONFIG.getRuta() + "/" + usuario.toUpperCase() + "/misdatos.json";
    this.rutaUIData = _Ruta.CONFIG.getRuta() + "/" + usuario.toUpperCase() + "/uidata.json";
    this.dirPers = _Ruta.DATOS.getRuta() + "/" + usuario.toUpperCase();
    // System.out.println("Rutas de Configuración para el usuario " + Config.usuario + " : \n" +
    //         "Directorio Configuración Personal: " + this.dirCFGpers + "\n" +
    //         "Ruta configdata.json: " + this.rutaConfigData + "\n" +
    //         "Ruta misdatos.json: " + this.rutaMisDatos + "\n" +
    //         "Ruta uidata.json: " + this.rutaUIData + "\n" +
    //         "Ruta rutasconfig.json: " + this.rutaRutasConfig + "\n" +
    //         "Ruta Directorio de Trabajo: " + this.dirPers);

    // Cargar objetos
    this.configData = JsonParser.leerJson(this.rutaConfigData, ConfigData.class);
    // NOTE : 26-04-10 : Estos objetos, por ahora, no los cargo en memoria...
    // this.misDatos = JsonParser.leerJson(this.rutaMisDatos, MisDatos.class);
    // this.uiData = JsonParser.leerJson(this.rutaUIData, UIData.class);
    this.rutasconfig = JsonParser.leerJson(this.rutaRutasConfig, RutasConfig.class);

    // STUB: 26-03-30 : Escribir el código para cargar cada uno de estos archivos JSON en sus
    // respectivos objetos ConfigData, MisDatos, UIData, RutasConfig
    // System.out.println("[Config>Config(user)] Creada la Configuracion del Usuario " + usuario);
    // System.out.println("[Config>Config(user)] Config de usuario " + usuario + " : \n" +
    // this.toString());

  }

  public static Config getConfig(String user) {
    if ((configActual == null) || (!(usuario.equals(user)))) {
      configActual = new Config(user);
    }
    return configActual;
  }

  public String getUsuario() {
    return usuario;
  }

  public boolean chequearYRellenarRutas(String usuario) {

    String base = _Ruta.CONFIG.getRuta() + "/" + usuario.toUpperCase();

    File dirPersonal = new File(base);
    File fConfigData = new File(base + "/configdata.json");
    File fMisDatos = new File(base + "/misdatos.json");
    File fUIData = new File(base + "/uidata.json");
    File fRutasConfig = new File(base + "/rutasconfig.json");

    boolean faltaAlgo =
        !dirPersonal.exists()
            || !fConfigData.exists()
            || !fMisDatos.exists()
            || !fUIData.exists()
            || !fRutasConfig.exists();

    if (!faltaAlgo) {
      System.out.println("[Config] Todas las rutas existen para " + usuario);
      return true;
    }

    // --- Confirmación del usuario ---
    boolean crear = confirmarCreacion(usuario);
    if (!crear) {
      System.out.println("[Config] El usuario NO aceptó crear las rutas.");
      return false;
    }

    // --- Crear directorio ---
    if (!dirPersonal.exists()) {
      System.out.println("[Config] Creando directorio: " + dirPersonal.getAbsolutePath());
      dirPersonal.mkdirs();
    }

    // --- Crear archivos vacíos ---
    crearArchivoSiNoExiste(fConfigData);
    crearArchivoSiNoExiste(fMisDatos);
    crearArchivoSiNoExiste(fUIData);
    crearArchivoSiNoExiste(fRutasConfig);

    System.out.println("[Config] Rutas creadas correctamente para " + usuario);

    return true;
  }

  private void crearArchivoSiNoExiste(File f) {
    try {
      if (!f.exists()) {
        System.out.println("[Config] Creando archivo: " + f.getAbsolutePath());
        try (FileWriter fw = new FileWriter(f)) {
          fw.write("{}"); // contenido inicial
        }
      }
    } catch (IOException ex) {
      ex.getMessage();
    }
  }

  private boolean confirmarCreacion(String usuario) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Crear configuración");
    alert.setHeaderText("Faltan archivos de configuración para " + usuario);
    alert.setContentText("¿Desea crearlos ahora?");

    Optional<ButtonType> result = alert.showAndWait();
    return result.isPresent() && result.get() == ButtonType.OK;
  }

  // #region TOSTR()
  @Override
  public String toString() {
    // REVIEW - 24-06-29 : Hay que hacer los toString de uidata, configdata, rutasCongig
    String resp = "Config del usuario " + usuario + " :\n ConfigData:\n" + this.configData.toJSON();
    // "\nmisDatos:\n" +
    // ((this.misDatos != null) ? this.misDatos.toJSON() : " - NULL -") +
    // "\nuiData:\n" +
    // this.uiData.toJSON() +
    // "\nrutasConfig:\n" +
    // this.rutasconfig.toJSON() +
    // "\nElementos en Lista static de configuraciones: " +
    // ((Config.configuraciones != null) ? Config.configuraciones.size() : " - NULL -") +
    // "\nconfigActual not NULL: " +
    // ((Config.configActual != null) ? "S" : "N");
    return resp;
  }

  // #endregion

  public ConfigData getConfigData() {

    return this.configData;
  }

  // public MisDatos getMisDatos() {
  //     return misDatos;
  // }

  // public void setMisDatos(MisDatos misDatos) {
  //     this.misDatos = misDatos;
  // }

  // public UIData getUiData() {
  //     return uiData;
  // }

  // public void setUiData(UIData uiData) {
  //     this.uiData = uiData;
  // }

  public RutasConfig getRutasconfig() {
    return rutasconfig;
  }

  public void setRutasconfig(RutasConfig rutasconfig) {
    this.rutasconfig = rutasconfig;
  }
}
