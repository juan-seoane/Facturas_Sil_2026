module com.sil.facturas.presentationgui {
  requires transitive com.sil.facturas.infrastructure;
  requires transitive javafx.controls;
  requires transitive javafx.fxml;
  requires transitive javafx.graphics;
  requires transitive javafx.base;
  requires transitive javafx.swing;
  requires com.sil.facturas.app;
  requires com.sil.facturas.domain;
  requires com.google.gson;
  requires java.desktop;

  exports com.sil.facturas.presentationgui;
  exports com.sil.facturas.presentationgui.viewmodels;
  exports com.sil.facturas.presentationgui.services.ocr;

  opens com.sil.facturas.presentationgui.services.ocr to
      javafx.graphics,
      javafx.fxml;
  opens com.sil.facturas.presentationgui.fxcontrollers to
      javafx.fxml;
  opens com.sil.facturas.presentationgui to
          javafx.fxml;
}
