package com.sil.facturas.infrastructure.json.ocr.dtos;

public class CampoDTO {
  public String nombre;
  public String parent;
  public String valorSemantico;
  public double offsetX;
  public double offsetY;
  public double w;
  public double h;
  public String tipoContenido; // TipoContenido en texto

  public CampoDTO() {
  }

  public String getNombre() {
    return nombre;
  }
  public void setNombre(String nombre) {
    this.nombre = nombre;
  }
  public String getParent() {
    return parent;
  }
  public void setParent(String parent) {
    this.parent = parent;
  }
  public String getValorSemantico() {
    return valorSemantico;
  }
  public void setValorSemantico(String valorSemantico) {
    this.valorSemantico = valorSemantico;
  }
  public double getOffsetX() {
    return offsetX;
  }

  public void setOffsetX(double offsetX) {
    this.offsetX = offsetX;
  }
  public double getOffsetY() {
    return offsetY;
  }

  public void setOffsetY(double offsetY) {
    this.offsetY = offsetY;
  }
  public double getW() {
    return w;
  }

  public void setW(double w) {
    this.w = w;
  }
  public double getH() {
    return h;
  }

  public void setH(double h) {
    this.h = h;
  }
  public String getTipoContenido() {
    return tipoContenido;
  }
  public void setTipoContenido(String tipoContenido) {
    this.tipoContenido = tipoContenido;
  }
}
