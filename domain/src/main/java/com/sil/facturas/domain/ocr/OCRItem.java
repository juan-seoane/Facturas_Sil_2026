package com.sil.facturas.domain.ocr;

import java.util.Map;

public class OCRItem {
  private String id;
  private String entity;
  private String type;
  private Map<String, Object> meta;

  public OCRItem(double x, double y, double w, double h) {
    //TODO Auto-generated constructor stub
}

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
}
