package com.sil.facturas.domain.ocr;

import com.sil.facturas.domain.ocr.enums._AnchorX;
import com.sil.facturas.domain.ocr.enums._AnchorY;
import com.sil.facturas.domain.ocr.enums._OffsetTipo;

public class BloqueConfig {
  public String nombreBloque; // ← NUEVO
  public String referencia; // ← NUEVO
  public _OffsetTipo offsetTipo;
  public boolean growHoriz;
  public boolean growVert;
  public _AnchorX anchorX;
  public _AnchorY anchorY;
  public String parentNombre; // ← NUEVO

  @Override
  public String toString() {
    return "BloqueConfig [nombreBloque="
        + nombreBloque
        + ", referencia="
        + referencia
        + ", offsetTipo="
        + offsetTipo
        + ", growHoriz="
        + growHoriz
        + ", growVert="
        + growVert
        + ", anchorX="
        + anchorX
        + ", anchorY="
        + anchorY
        + ", parent="
        + parentNombre
        + "]";
  }
}
