module com.sil.facturas.domain {
  requires transitive java.desktop;

  exports com.sil.facturas.domain.records;
  exports com.sil.facturas.domain.interfaces;
  exports com.sil.facturas.domain.pojos;
  exports com.sil.facturas.domain.enums;
  exports com.sil.facturas.domain.ocr;
  exports com.sil.facturas.domain.ocr.enums;

  opens com.sil.facturas.domain.records to com.google.gson;
  opens com.sil.facturas.domain.pojos to com.google.gson;
  opens com.sil.facturas.domain.ocr to com.google.gson;
  opens com.sil.facturas.domain.ocr.enums to com.google.gson;
}
