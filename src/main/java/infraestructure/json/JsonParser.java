package infraestructure.json;

import com.google.gson.Gson;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;

public class JsonParser {
	public static <T> T leerJson(String ruta, Class<T> clazz) {
		try {
			File f = new File(ruta);

			if (!f.exists()) {
				System.out.println("[Config] Archivo no existe, creando vacío: " + ruta);
				T obj = clazz.getDeclaredConstructor().newInstance();
				guardarJson(ruta, obj);
				return obj;
			}

			try (FileReader fr = new FileReader(f)) {
				return new Gson().fromJson(fr, clazz);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void guardarJson(String ruta, Object obj) {
		try (FileWriter fw = new FileWriter(ruta)) {
			new Gson().toJson(obj, fw);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
