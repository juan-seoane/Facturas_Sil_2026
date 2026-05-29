package infrastructure.servicios;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.google.gson.Gson;

import domain.records.Credenciales;
import domain.records.Creds;
import infrastructure.filesystem._Ruta;
import infrastructure.helpers._Auth;
import presentation.helpers.Debug;

/* CONTROLA EL PROCESO DE AUTENTICACIÓN */
public class AuthService {

    public int autenticar(String user, String pass, int intentos) {
        // DONE : 26-03-18 // STUB : 26-03-16 : completar el método autenticar
        int resp = 3;
        boolean valido = false;
        String rutaCreds = _Ruta.CONFIG.getRuta() + "/creds.json";
        // Debug.print("[AuthService>autenticar] Chequeando la existencia de archivo de credenciales en ruta: " + rutaCreds);
        boolean existenCreds = Files.exists(Path.of(rutaCreds));
        // Debug.print("existe el archivo: " + existenCreds);
        if (existenCreds) {
            Credenciales creds = leerCredenciales(rutaCreds);

            List<Creds> listaCreds = creds.getCreds();
            valido = listaCreds.stream().anyMatch(c -> c.usuario().equals(user) && c.pass().equals(pass));
            // Debug.print("Credenciales válidas: " + valido);
        }
        if (valido)
            resp = _Auth.AUTH_OK.getCode();
        else if (!valido && (intentos >= 5))
            resp = _Auth.AUTH_FAIL.getCode();
        return resp;
    }

    //#region LEER_CREDS()
    public static synchronized Credenciales leerCredenciales(String ruta) {

        try {
            Path p = Path.of(ruta);
            String json = Files.readString(p);

            Gson gson = new Gson();

            Credenciales c = gson.fromJson(json, Credenciales.class);

            return c;

        } catch (Exception e) {
            Debug.printError("[AuthService] Excepc " + e.getClass() + " al leer Credenciales");
            return null;
        }
    }
    //#endregion
}
