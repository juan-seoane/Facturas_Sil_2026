module com.sil.facturas.app {

  // Módulos propios
  requires com.sil.facturas.domain;
  requires com.sil.facturas.infrastructure;


  // JavaFX
  requires javafx.controls;
  requires javafx.fxml;
  requires javafx.graphics;
  requires javafx.base;

  // Swing / AWT
  requires java.desktop;

  // Librerías externas
  requires com.google.gson;
  requires com.opencsv;
  requires itext;
  requires jasperreports;
  requires tess4j;
  requires org.slf4j;

  // Exporta los paquetes del módulo app
  exports com.sil.facturas.app.core;
  exports com.sil.facturas.app.services;
  exports com.sil.facturas.app.api;

}
