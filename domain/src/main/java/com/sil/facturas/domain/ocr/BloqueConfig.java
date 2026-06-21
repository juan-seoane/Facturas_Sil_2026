package com.sil.facturas.domain.ocr;

import com.sil.facturas.domain.ocr.enums._AnchorX;
import com.sil.facturas.domain.ocr.enums._AnchorY;
import com.sil.facturas.domain.ocr.enums._OffsetTipo;

public class BloqueConfig {
    public _OffsetTipo offsetTipo;
    public boolean growHoriz;
    public boolean growVert;
    public _AnchorX anchorX;
    public _AnchorY anchorY;
    @Override
    public String toString() {
        return "BloqueConfig [offsetTipo=" + offsetTipo + ", growHoriz=" + growHoriz + ", growVert=" + growVert
                + ", anchorX=" + anchorX + ", anchorY=" + anchorY + "]";
    }
}
