package com.sil.facturas.domain.ocr;

import com.sil.facturas.domain.ocr.enums._OffsetTipo;

public record Offset(_OffsetTipo tipo, int dx, int dy) {}
