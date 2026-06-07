package com.sil.facturas.app.core;

import com.sil.facturas.app.api.INavService;
import com.sil.facturas.app.api.IaccesoUI;
import com.sil.facturas.app.api.ItablaFCT;
import com.sil.facturas.app.services.FacturasService;
import com.sil.facturas.infrastructure.servicios.AuthService;
import com.sil.facturas.infrastructure.servicios.config.Config;





public class AppContext {

  private static AppContext instance;

  public static String usuarioActual;

  private final AuthService authService = new AuthService();
  public static FacturasService facturasService;
  private static ItablaFCT tablaFCT;
  private INavService navService;
  public static Config configActual;
  private IaccesoUI accesoUI;
  
  private AppContext() {}
  
  public static synchronized AppContext get() {
    if (instance == null)
      instance = new AppContext();
    return instance;
  }
  
  public AuthService auth() {
    return this.authService;
  }
  
  public FacturasService fact() {
    return facturasService;
  }
  
  public INavService nav() {
    return this.navService;
  }
  
  public static void setUsuarioActual(String user) {
    usuarioActual = user;
  }
  
  public static void setFacturasService(FacturasService fService) {
    facturasService = fService;
  }
  
  public void setTablaFCT(ItablaFCT tabla) {
    tablaFCT = tabla;
  }
  
  public ItablaFCT getTablaFCT() {
    return tablaFCT;
  }
  
    public void setAccesoUI(IaccesoUI acceso) {
        this.accesoUI = acceso;
    }
  
    public IaccesoUI getAccesoUI() {
        return accesoUI;
    }

    public void setNav(INavService nav) {
        this.navService = nav;
    }

	public static String getUsuarioActual() {
      return usuarioActual;
	}
}
