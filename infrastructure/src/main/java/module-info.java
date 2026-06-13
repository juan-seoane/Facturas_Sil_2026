module com.sil.facturas.infrastructure {
  requires transitive com.sil.facturas.domain;

  // Librerías externas
  requires tess4j;
  requires com.fasterxml.jackson.databind;
  requires transitive com.google.gson;
  requires com.opencsv;
  requires jasperreports;
  requires java.desktop;
  requires java.logging;
  requires javafx.controls;
  requires javafx.swing;
  requires javafx.fxml;
  requires javafx.graphics;

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
  exports com.sil.facturas.infrastructure.servicios.ocr.editor;

  // Para permitir que Gson lea records
  opens com.sil.facturas.infrastructure.records to
      com.google.gson;
  opens com.sil.facturas.infrastructure.config to
      com.google.gson;
  opens com.sil.facturas.infrastructure.servicios.ocr.aux_ocr to
      com.google.gson;
}
