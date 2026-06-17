package com.sil.facturas.domain.ocr;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ModeloOCR {

  private String nombre;
  private String version;
  private String rutaImagen;
  private int dpi;

  private Map<String, ROI> zonasSueltas;
  private List<Bloque> bloques;

  public ModeloOCR() {
  }

  public ModeloOCR(String nombre, String version, List<Bloque> bloques) {
      this.nombre = nombre;
      this.version = version;
      this.bloques = bloques;

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
  public java.util.List<Bloque> getBloques() {
    return bloques;
  }
  public void setBloques(java.util.List<Bloque> bloques) {
    this.bloques = bloques;
  }


}
