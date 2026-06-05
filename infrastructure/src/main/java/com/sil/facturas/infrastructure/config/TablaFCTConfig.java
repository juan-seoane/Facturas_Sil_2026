package com.sil.facturas.infrastructure.config;

import java.util.Map;

public class TablaFCTConfig {

  private Map<String, Integer> factura;
  private Map<String, Integer> extracto;

  public Map<String, Integer> getFactura() {
    return factura;
  }

  public Map<String, Integer> getExtracto() {
    return extracto;
  }

  /** Devuelve el ancho de una columna */
  public Integer getAncho(String colId) {
    if (factura != null && factura.containsKey(colId)) {
      return factura.get(colId);
    }
    if (extracto != null && extracto.containsKey(colId)) {
      return extracto.get(colId);
    }
    return null;
  }

  /** Actualiza el ancho de una columna */
  public void updateAncho(String colId, int nuevoAncho) {
    if (factura != null && factura.containsKey(colId)) {
      factura.put(colId, nuevoAncho);
    }
    if (extracto != null && extracto.containsKey(colId)) {
      extracto.put(colId, nuevoAncho);
    }
  }

  @Override
  public String toString() {
    return "{factura=" + factura.keySet() + ", extracto=" + extracto.keySet() + "}";
  }
}
