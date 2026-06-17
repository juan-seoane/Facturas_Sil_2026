package com.sil.facturas.infrastructure.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class UIDataConfig {

  private TablaFCTConfig tablaFCT;

  public TablaFCTConfig getTablaFCT() {
    return tablaFCT;
  }

  // ============================================================
  //  CARGAR JSON
  // ============================================================

  public static UIDataConfig fromJson(Path path) throws IOException {
    String json = Files.readString(path);
    Gson gson = new Gson();
    return gson.fromJson(json, UIDataConfig.class);
  }

  // ============================================================
  //  GUARDAR JSON
  // ============================================================

  public void saveToJson(Path path) throws IOException {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    Files.writeString(path, gson.toJson(this));
  }

  // ============================================================
  //  MÉTODOS ÚTILES
  // ============================================================

  /** Devuelve el ancho de una columna por su ID */
  public int getAncho(String colId) {
    Integer ancho = tablaFCT.getAncho(colId);
    return ancho != null ? ancho : 0;
  }

  /** Actualiza el ancho de una columna */
  public void updateAncho(String colId, int nuevoAncho) {
    tablaFCT.updateAncho(colId, nuevoAncho);
  }

  @Override
  public String toString() {
    return "UIDataConfig{\n" + "tablaFCT=" + tablaFCT + "\n}";
  }
}
