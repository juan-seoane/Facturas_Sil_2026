package com.sil.facturas.infrastructure.json.ocr.dtos;

public class RectDTO {
  public double x;
  public double y;
  public double w;
  public double h;

  public RectDTO() {
  }

  public double getX() {
    return x;
  }

  public void setX(double x) {
    this.x = x;
  }

  public double getY() {
    return y;
  }

  public void setY(double y) {
    this.y = y;
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
}
