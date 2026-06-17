package com.sil.facturas.infrastructure.services.ocr;

import com.sil.facturas.infrastructure.services.ocr.aux_ocr.BloqueOCR;
import com.sil.facturas.infrastructure.services.ocr.aux_ocr.CampoOCR;

public class ROIEditorService {

  public boolean contiene(BloqueOCR b, double x, double y) {
    return x >= b.x() && x <= b.x() + b.w() && y >= b.y() && y <= b.y() + b.h();
  }

  public boolean contiene(CampoOCR c, double x, double y) {
    return x >= c.x() && x <= c.x() + c.w() && y >= c.y() && y <= c.y() + c.h();
  }
}
