package com.sil.facturas.infrastructure.servicios;

import com.google.gson.Gson;
import com.sil.facturas.domain.enums._Ruta;
import com.sil.facturas.domain.interfaces.IAuthService;
import com.sil.facturas.domain.interfaces.IDebugService;
import com.sil.facturas.domain.pojos.Credenciales;
import com.sil.facturas.domain.records.Creds;
import com.sil.facturas.infrastructure.helpers._Auth;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/* CONTROLA EL PROCESO DE AUTENTICACIÓN */
public class AuthService implements IAuthService {
  @Override
  public boolean autenticar(String user, String pass) {
    String rutaCreds = _Ruta.CONFIG.getRuta() + "/creds.json";

    if (!Files.exists(Path.of(rutaCreds))) {
      return false;
    }

    Credenciales creds = leerCredenciales(rutaCreds);
    List<Creds> listaCreds = creds.getCreds();

    return listaCreds.stream().anyMatch(c -> c.usuario().equals(user) && c.pass().equals(pass));
  }

  public int autenticar(String user, String pass, int intentos) {

    // Si ya superó el límite → fallo directo
    if (intentos >= 5) {
      return _Auth.AUTH_FAIL.getCode();
    }

    // Llamada al método real
    boolean valido = autenticar(user, pass);

    if (valido) {
      return _Auth.AUTH_OK.getCode();
    }

    // No válido pero aún no superó el límite
    return _Auth.AUTH_RETRY.getCode(); // o el que uses para "incorrecto pero quedan intentos"
  }

    // #region LEER_CREDS()
    public static synchronized Credenciales leerCredenciales(String ruta) {

        try {
            Path p = Path.of(ruta);
            String json = Files.readString(p);

            Gson gson = new Gson();

            Credenciales c = gson.fromJson(json, Credenciales.class);

            return c;

        } catch (Exception e) {
            IDebugService.printError("[AuthService] Excepc " + e.getClass() + " al leer Credenciales");
            return null;
        }
    }
    // #endregion
}
