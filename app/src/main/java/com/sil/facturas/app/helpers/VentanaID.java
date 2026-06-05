package com.sil.facturas.app.helpers;

import javafx.stage.StageStyle;

public enum VentanaID {

    TABLA_FCT("FxTablaFCT", "Tabla FCT", StageStyle.DECORATED, true),
    PANEL_CONTROL("FxPanelControl", "Panel de Control", StageStyle.DECORATED, false),
    LOGIN("FxAcceso", "LOGIN", StageStyle.UNDECORATED, false),
    SPLASH("FxSplash", "Facturas SIL 2026", StageStyle.UNDECORATED, false);

  private final String fxml;
  private final String titulo;
  private final StageStyle style;
  private final boolean resizable;

  VentanaID(String fxml, String titulo, StageStyle style, boolean resizable) {
    this.fxml = fxml;
    this.titulo = titulo;
    this.style = style;
    this.resizable = resizable;
  }

  public String fxml() {
    return fxml;
  }

  public String titulo() {
    return titulo;
  }

  public StageStyle style() {
    return style;
  }

  public boolean resizable() {
    return resizable;
  }
}
