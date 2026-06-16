package com.sil.facturas.infrastructure.servicios.ocr.aux_ocr;

import com.sil.facturas.infrastructure.records.Rect;

public class Campo {
  public String nombre;
  public String parent;

  // Estos campos se mapean directamente desde el JSON
  public double x1;
  public double y1;
  public double x2;
  public double y2;

  public double offsetX;
  public double offsetY;

  // Convertir a Rect cuando lo necesites
  public Rect zona() {
    return new Rect(x1, y1, x2, y2);
  }
}
