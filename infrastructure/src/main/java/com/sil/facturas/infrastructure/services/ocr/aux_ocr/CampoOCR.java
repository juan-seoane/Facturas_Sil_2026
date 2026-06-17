package com.sil.facturas.infrastructure.services.ocr.aux_ocr;

import com.sil.facturas.domain.ocr.enums._TipoContenido;

public class CampoOCR extends OCRItem {

  private String nombre;
  private String parent;
  private String valorSemantico;
  private _TipoContenido tipoContenido;

  public CampoOCR(
      String nombre,
      String parent,
      String valorSemantico,
      double x,
      double y,
      double w,
      double h,
      _TipoContenido tipoContenido) {
    super(x, y, w, h);
    this.nombre = nombre;
    this.parent = parent;
    this.valorSemantico = valorSemantico;
    this.tipoContenido = tipoContenido;
  }

  public String nombre() {
    return nombre;
  }

  public String parent() {
    return parent;
  }

  public String valorSemantico() {
    return valorSemantico;
  }

  public _TipoContenido tipoContenido() {
    return tipoContenido;
  }

  public void setNombre(String n) {
    this.nombre = n;
  }

  public void setParent(String p) {
    this.parent = p;
  }

  public void setValorSemantico(String v) {
    this.valorSemantico = v;
  }

  public void setTipoContenido(_TipoContenido t) {
    this.tipoContenido = t;
  }
}
