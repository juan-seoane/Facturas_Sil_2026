package com.sil.facturas.infrastructure.servicios.config;

import com.sil.facturas.domain.enums._Ruta;
import com.sil.facturas.domain.interfaces.IConfigService;
import com.sil.facturas.domain.pojos.ConfigData;
import com.sil.facturas.domain.pojos.RutasConfig;
import com.sil.facturas.infrastructure.json.JsonParser;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigService implements IConfigService {

    private String baseDir(String usuario) {
        return _Ruta.CONFIG.getRuta() + "/" + usuario.toUpperCase();
    }

    private String rutaConfigData(String usuario) {
        return baseDir(usuario) + "/configdata.json";
    }

    private String rutaRutasConfig(String usuario) {
        return baseDir(usuario) + "/rutasconfig.json";
    }

    private void crearArchivoSiNoExiste(File f) {
        try {
            if (!f.exists()) {
                f.getParentFile().mkdirs();
                try (FileWriter fw = new FileWriter(f)) {
                    fw.write("{}");
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void asegurarArchivos(String usuario) {
        crearArchivoSiNoExiste(new File(rutaConfigData(usuario)));
        crearArchivoSiNoExiste(new File(rutaRutasConfig(usuario)));
    }

    @Override
    public ConfigData loadConfig(String usuario) {
        asegurarArchivos(usuario);
        return JsonParser.leerJson(rutaConfigData(usuario), ConfigData.class);
    }

    @Override
    public void saveConfig(String usuario, ConfigData data) {
        asegurarArchivos(usuario);
        JsonParser.guardarJson(rutaConfigData(usuario), data);
    }

    @Override
    public ConfigData getConfigData(String usuario) {
        return loadConfig(usuario);
    }

    @Override
    public RutasConfig getRutasConfig(String usuario) {
        asegurarArchivos(usuario);
        return JsonParser.leerJson(rutaRutasConfig(usuario), RutasConfig.class);
    }
}
