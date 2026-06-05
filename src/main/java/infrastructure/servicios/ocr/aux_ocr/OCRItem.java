package infrastructure.servicios.ocr.aux_ocr;

import java.util.Map;

public class OCRItem {

  private String id;
  private String entity; // factura, extracto, totales o null
  private String type; // campo, bloque, parametro
  private Map<String, Object> meta; // alturaLinea, default, etc.

  public String getId() {
    return id;
  }

  public String getEntity() {
    return entity;
  }

  public String getType() {
    return type;
  }

  public Map<String, Object> getMeta() {
    return meta;
  }

  @Override
  public String toString() {
    return id + " (" + type + ")";
  }
}
