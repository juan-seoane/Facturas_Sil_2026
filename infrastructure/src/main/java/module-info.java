module com.sil.facturas.infrastructure {
  
  // Librerías externas
  requires tess4j;
  requires com.fasterxml.jackson.databind;
  requires com.google.gson;
  requires com.opencsv;
  requires jasperreports;
  requires java.desktop;
  requires java.logging;
  requires com.sil.facturas.domain;
  requires javafx.controls;
  requires javafx.swing;
  requires javafx.fxml;

  // Exporta adaptadores
  exports com.sil.facturas.infrastructure.config;
  exports com.sil.facturas.infrastructure.csv;
  exports com.sil.facturas.infrastructure.debug;
  exports com.sil.facturas.infrastructure.helpers;
  exports com.sil.facturas.infrastructure.records;
  exports com.sil.facturas.infrastructure.servicios;
  exports com.sil.facturas.infrastructure.servicios.config;
  exports com.sil.facturas.infrastructure.servicios.ocr;
  exports com.sil.facturas.infrastructure.servicios.ocr.aux_ocr;
  // Para permitir que Gson lea records
  opens com.sil.facturas.infrastructure.records to
      gson;
}
