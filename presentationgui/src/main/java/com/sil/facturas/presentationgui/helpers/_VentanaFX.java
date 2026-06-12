package com.sil.facturas.presentationgui.helpers;

import javafx.stage.StageStyle;
import com.sil.facturas.app.api.IVentanaID;

public enum _VentanaFX implements IVentanaID{
  TABLA_FCT("FxTablaFCT", "Tabla FCT", StageStyle.DECORATED, true, false),
  PANEL_CONTROL("FxPanelControl", "Panel de Control", StageStyle.DECORATED, false, true),
  LOGIN("FxAcceso", "LOGIN", StageStyle.UNDECORATED, false, true),
  SPLASH("FxSplash", "Facturas SIL 2026", StageStyle.UNDECORATED, false, true);

  private final String fxml;
  private final String titulo;
  private final StageStyle style;
  private final boolean resizable;
  private final boolean alwaysOnTop;

  _VentanaFX(String fxml, String titulo, StageStyle style, boolean resizable, boolean alwaysOnTop) {
    this.fxml = fxml;
    this.titulo = titulo;
    this.style = style;
    this.resizable = resizable;
    this.alwaysOnTop = alwaysOnTop;
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

  public boolean alwaysOnTop() {
    return alwaysOnTop;
  }
}
