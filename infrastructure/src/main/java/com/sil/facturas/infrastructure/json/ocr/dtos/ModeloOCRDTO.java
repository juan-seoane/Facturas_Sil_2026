package com.sil.facturas.infrastructure.json.ocr.dtos;

import com.sil.facturas.domain.ocr.ROI;

public class ModeloOCRDTO {
  public String nombre;
  public String version;
  public String rutaImagen;
  public int dpi;

  public java.util.Map<String, ROI> zonasSueltas;
  public java.util.List<BloqueDTO> bloques;

  public ModeloOCRDTO() {
  }
  
  public String getNombre() {
    return nombre;
  }
  public void setNombre(String nombre) {
    this.nombre = nombre;
  }
  public String getVersion() {
    return version;
  }
  public void setVersion(String version) {
    this.version = version;
  }
  public String getRutaImagen() {
    return rutaImagen;
  }
  public void setRutaImagen(String rutaImagen) {
    this.rutaImagen = rutaImagen;
  }
  public int getDpi() {
    return dpi;
  }
  public void setDpi(int dpi) {
    this.dpi = dpi;
  }
  public java.util.Map<String, ROI> getZonasSueltas() {
    return zonasSueltas;
  }
  public void setZonasSueltas(java.util.Map<String, ROI> zonasSueltas) {
    this.zonasSueltas = zonasSueltas;
  }
  public java.util.List<BloqueDTO> getBloques() {
    return bloques;
  }
  public void setBloques(java.util.List<BloqueDTO> bloques) {
    this.bloques = bloques;
  }
}
