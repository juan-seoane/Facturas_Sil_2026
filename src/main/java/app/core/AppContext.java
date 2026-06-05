package app.core;

import app.services.FacturasService;
import app.services.NavService;
import infrastructure.servicios.config.Config;
import infrastructure.servicios.AuthService;
import javafx.stage.Stage;

public class AppContext {

  private static AppContext instance;

  public static String usuarioActual;

  private final AuthService authService = new AuthService();
  public static FacturasService facturasService;
  public static Stage tablaFCT;
  private final NavService navService = new NavService();
  public static Config configActual;

  private AppContext() {}

  public static synchronized AppContext get() {
    if (instance == null) instance = new AppContext();
    return instance;
  }

  public AuthService auth() {
    return this.authService;
  }

  public FacturasService fact() {
    return facturasService;
  }

  public NavService nav() {
    return this.navService;
  }

  public static void setUsuarioActual(String user) {
    usuarioActual = user;
  }

  public static void setFacturasService(FacturasService fService) {
    facturasService = fService;
  }

  public static Stage getTablaFCT() {
    // System.out.println(
    //     "[AppContext>getTablaFCT] tablaFCT devuelta con hashCode " + tablaFCT.hashCode());
    return tablaFCT;
  }

  public static void setTablaFCT(Stage tabla) {
    tablaFCT = tabla;
    // System.out.println(
    //     "[AppContext>setTablaFCT] tablaFCT guardada "
    //         + (tablaFCT == null ? "NULL" : String.valueOf(tablaFCT.hashCode())));
  }
}
