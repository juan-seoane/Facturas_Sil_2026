package com.sil.facturas.app.core;

import com.sil.facturas.app.api.INavService;
import com.sil.facturas.app.api.IaccesoUI;
import com.sil.facturas.app.api.ItablaFCT;
import com.sil.facturas.app.services.FacturasService;
import com.sil.facturas.domain.interfaces.IAuthService;
import com.sil.facturas.domain.interfaces.IConfigService;

public class AppContext {

  private static AppContext instance;

  public static String usuarioActual;

  private static IAuthService authService;
  public static IAuthService getAuthService() {
    return authService;
}

  public static FacturasService facturasService;
  private static ItablaFCT tablaFCT;
  private INavService navService;
  public static IConfigService configService;
  private IaccesoUI accesoUI;

  private AppContext() {}

  public static synchronized AppContext get() {
    if (instance == null)
      instance = new AppContext();
    return instance;
  }

  public static void setAuthService(IAuthService service) {
    authService = service;
  }

  public static IAuthService auth() {
    return authService;
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

    public static void setConfigService(IConfigService s) {
        configService = s;
    }

    public static IConfigService getConfigService() {
        return configService;
    }

}
