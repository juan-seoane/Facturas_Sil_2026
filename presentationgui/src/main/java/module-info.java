module com.sil.facturas.presentationgui {
  requires transitive com.sil.facturas.infrastructure;
  requires transitive javafx.controls;
  requires transitive javafx.fxml;
  requires transitive javafx.graphics;
  requires javafx.base;
  requires com.sil.facturas.app;
  requires com.sil.facturas.domain;
  requires com.google.gson;

  exports com.sil.facturas.presentationgui;
  exports com.sil.facturas.presentationgui.viewmodels;

  opens com.sil.facturas.presentationgui.fxcontrollers to
      javafx.fxml;
  opens com.sil.facturas.presentationgui to
      javafx.fxml;
}
