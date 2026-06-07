module com.sil.facturas.domain {
  requires java.desktop;

  exports com.sil.facturas.domain.records;
  exports com.sil.facturas.domain.interfaces;
  
  opens com.sil.facturas.domain.records to com.google.gson;

}
