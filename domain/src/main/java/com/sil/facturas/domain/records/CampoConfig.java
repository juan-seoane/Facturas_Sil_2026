package com.sil.facturas.domain.records;

public class CampoConfig {

  private String NOTA; // opcional
  private String ocr; // obligatorio
  private TablaConfig tabla; // opcional

  public CampoConfig() {
  }
  
  public String getNOTA() {
    return NOTA;
  }

  public void setNOTA(String NOTA) {
    this.NOTA = NOTA;
  }

  public String getOcr() {
    return ocr;
  }

  public void setOcr(String ocr) {
    this.ocr = ocr;
  }

  public TablaConfig getTabla() {
    return tabla;
  }

  public void setTabla(TablaConfig tabla) {
    this.tabla = tabla;
  }
}
