package com.sil.facturas.domain.ocr;

import java.util.List;

import com.sil.facturas.domain.ocr.enums._AnchorX;
import com.sil.facturas.domain.ocr.enums._AnchorY;

public record Bloque(
    String nombre,
    String parentNombre,
    String referencia,
    Offset offset,
    Rect zona,
    boolean growVertical,
    boolean growHorizontal,
    _AnchorX anchorX,
    _AnchorY anchorY,
    java.util.List<Campo> campos,
    List<String> cabeceraColumnas) {}
