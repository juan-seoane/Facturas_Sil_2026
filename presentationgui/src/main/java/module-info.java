module com.sil.facturas.presentationgui {
  requires com.sil.facturas.infrastructure;
  requires javafx.controls;
  requires javafx.fxml;
  requires javafx.graphics;
  requires javafx.base;
  requires com.sil.facturas.app;

  exports com.sil.facturas.presentationgui;
  exports com.sil.facturas.presentationgui.viewmodels;

  opens com.sil.facturas.presentationgui.controllers to
      javafx.fxml;
  opens com.sil.facturas.presentationgui to
      javafx.fxml;
}
