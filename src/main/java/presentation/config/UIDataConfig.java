package presentation.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class UIDataConfig {

  private TablaFCTConfig tablaFCT;
  private List<OCRItem> ocr;

  public TablaFCTConfig getTablaFCT() {
    return tablaFCT;
  }

  public List<OCRItem> getOcr() {
    return ocr;
  }

  // ------------------------------
  // CARGAR JSON
  // ------------------------------
  public static UIDataConfig fromJson(Path path) throws IOException {
    String json = Files.readString(path);
    Gson gson = new Gson();
    return gson.fromJson(json, UIDataConfig.class);
  }

  // ------------------------------
  // GUARDAR JSON
  // ------------------------------
  public void saveToJson(Path path) throws IOException {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    Files.writeString(path, gson.toJson(this));
  }

  // ------------------------------
  // MÉTODOS ÚTILES
  // ------------------------------

  /** Devuelve el ancho de una columna por su ID */
  public int getAncho(String colId) {
    Integer ancho = tablaFCT.getAncho(colId);
    return ancho != null ? ancho : 0;
  }

  /** Actualiza el ancho de una columna */
  public void updateAncho(String colId, int nuevoAncho) {
    tablaFCT.updateAncho(colId, nuevoAncho);
  }

  /** Devuelve todos los nombres OCR (solo type="campo") */
  public List<String> getAllOcrNames() {
    List<String> lista = new ArrayList<>();
    for (OCRItem item : ocr) {
      if ("campo".equals(item.getType())) {
        lista.add(item.getId());
      }
    }
    return lista;
  }

  /** Devuelve todos los bloques OCR (type="bloque") */
  public List<String> getAllBloques() {
    List<String> lista = new ArrayList<>();
    for (OCRItem item : ocr) {
      if ("bloque".equals(item.getType())) {
        lista.add(item.getId());
      }
    }
    return lista;
  }

  /** Devuelve parámetros especiales (type="parametro") */
  public List<OCRItem> getParametros() {
    List<OCRItem> lista = new ArrayList<>();
    for (OCRItem item : ocr) {
      if ("parametro".equals(item.getType())) {
        lista.add(item);
      }
    }
    return lista;
  }

  @Override
  public String toString() {
    return "UIDataConfig{\n" + "tablaFCT=" + tablaFCT + ", ocr=" + ocr + "\n}";
  }
}
