module com.sil.facturas.app {

  // Módulos propios
  // Paquetes propios
  requires transitive com.sil.facturas.domain;

  // JavaFX
  requires javafx.controls;
  requires javafx.fxml;
  requires javafx.graphics;
  requires javafx.base;

  // Librerías externas
  requires com.google.gson;
  requires com.opencsv;
  requires itext;
  requires jasperreports;
  requires tess4j;
  requires org.slf4j;
  requires com.sil.facturas.infrastructure;

  // Exporta los paquetes del módulo app
  exports com.sil.facturas.app.core;
  exports com.sil.facturas.app.services;
  exports com.sil.facturas.app.api;
}
