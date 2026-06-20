package com.sil.facturas.domain.pojos;

import com.sil.facturas.domain.ocr.OCRItem;
import java.util.List;
import java.util.Map;

public class UIDataJson {
  private Map<String, Map<String, Integer>> tablaFCT;
  private List<OCRItem> ocr;

  public Map<String, Map<String, Integer>> getTablaFCT() {
    return tablaFCT;
  }

  public List<OCRItem> getOcr() {
    return ocr;
  }
}
