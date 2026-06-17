package com.sil.facturas.infrastructure.json.ocr.dtos;

public class OffsetDTO {
  public String tipo; // OffsetTipo en texto
  public int dx;
  public int dy;

  public OffsetDTO() {
  }
  
  public String getTipo() {
    return tipo;
  }
  public void setTipo(String tipo) {
    this.tipo = tipo;
  }
  public int getDx() {
    return dx;
  }
  public void setDx(int dx) {
    this.dx = dx;
  }
  public int getDy() {
    return dy;
  }
  public void setDy(int dy) {
    this.dy = dy;
  }
}
