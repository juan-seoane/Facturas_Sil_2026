package com.sil.facturas.domain.ocr;

import com.sil.facturas.domain.ocr.enums._TipoContenido;

public record Campo(
    String nombre,
    String parent,
    String valorSemantico,
    double offsetX,
    double offsetY,
    double w,
    double h,
    _TipoContenido tipoContenido) {}
