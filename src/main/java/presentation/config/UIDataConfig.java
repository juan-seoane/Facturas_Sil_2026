package presentation.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UIDataConfig {

  private Map<String, Double> anchoColsFCT;
  private List<String> nombreColsFCT;

  public UIDataConfig(Map<String, Double> anchoColsFCT) {
    this.anchoColsFCT = anchoColsFCT;
  }

  public List<String> getNombreColsFCT() {
    return nombreColsFCT;
  }

  public Map<String, Double> getAnchoColsFCT() {
    return anchoColsFCT;
  }

  public double getAnchoBase(String colId) {
    return anchoColsFCT.getOrDefault(colId, 0.0);
  }

  public Map<String, Double> getAnchos() {
    return anchoColsFCT;
  }

  public static UIDataConfig fromJson(Path path) throws IOException {

      // 1) Leer archivo
      String json = Files.readString(path);

      // 2) Parsear con Gson
      Gson gson = new Gson();
      UIDataJsonDTO dto = gson.fromJson(json, UIDataJsonDTO.class);

      // 3) Validar
      if (dto.nombreColsFCT == null || dto.anchoColsFCT == null) {
          throw new IllegalArgumentException("JSON inválido: faltan campos");
      }
      if (dto.nombreColsFCT.size() != dto.anchoColsFCT.size()) {
          throw new IllegalArgumentException("JSON inválido: tamaños no coinciden");
      }

      // 4) Construir mapa nombre → anchoBase
      Map<String, Double> mapa = new HashMap<>();
      for (int i = 0; i < dto.nombreColsFCT.size(); i++) {
          mapa.put(dto.nombreColsFCT.get(i), dto.anchoColsFCT.get(i));
      }

      // 5) Crear config final
      return new UIDataConfig(mapa);
  }

public void saveToJson(Path path) throws IOException {

    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // reconstruir arrays en el mismo orden que nombreColsFCT
    List<Double> anchos = new ArrayList<>();
    for (String nombre : nombreColsFCT) {
        anchos.add(anchoColsFCT.getOrDefault(nombre, 0.0));
    }

    JsonObject root = new JsonObject();
    root.add("nombreColsFCT", gson.toJsonTree(nombreColsFCT));
    root.add("anchoColsFCT", gson.toJsonTree(anchos));

    Files.writeString(path, gson.toJson(root));
}

public void updateAncho(String colId, double nuevoAncho) {
    System.out.println("[UIDataConfig>updateAncho] en col: " + colId + " : " + nuevoAncho);
    if (colId == null) return;
    anchoColsFCT.put(colId, nuevoAncho);
  }
}
