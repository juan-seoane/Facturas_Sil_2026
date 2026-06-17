package com.sil.facturas.infrastructure.services.ocr.aux_ocr;

public abstract class OCRItem {

  protected double x;
  protected double y;
  protected double w;
  protected double h;

  public OCRItem(double x, double y, double w, double h) {
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
  }

  public double x() {
    return x;
  }

  public double y() {
    return y;
  }

  public double w() {
    return w;
  }

  public double h() {
    return h;
  }

  public void setX(double x) {
    this.x = x;
  }

  public void setY(double y) {
    this.y = y;
  }

  public void setW(double w) {
    this.w = w;
  }

  public void setH(double h) {
    this.h = h;
  }
}
