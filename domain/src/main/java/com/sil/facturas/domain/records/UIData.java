package com.sil.facturas.domain.records;

import java.util.Map;

public class UIData {

  private Map<String, CampoConfig> factura;
  private Map<String, CampoConfig> extracto;

  public UIData() {
  }
  
  public Map<String, CampoConfig> getFactura() {
    return factura;
  }

  public void setFactura(Map<String, CampoConfig> factura) {
    this.factura = factura;
  }

  public Map<String, CampoConfig> getExtracto() {
    return extracto;
  }

  public void setExtracto(Map<String, CampoConfig> extracto) {
    this.extracto = extracto;
  }
}
