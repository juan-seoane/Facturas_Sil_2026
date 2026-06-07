package com.sil.facturas.presentationgui.services;

import com.sil.facturas.app.api.INavService;
import com.sil.facturas.app.api.IaccesoUI;
import com.sil.facturas.app.core.AppContext;
import com.sil.facturas.infrastructure.helpers._Ruta;
import com.sil.facturas.presentationgui.fxcontrollers.FxCntrlTablaFCT;
import com.sil.facturas.presentationgui.helpers.FxmlHelper;
import com.sil.facturas.presentationgui.helpers.TablaFCTFX;
import com.sil.facturas.presentationgui.helpers._VentanaFX;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/* CONTROLA LA CREACIÓN DE ESCENAS Y DEMÁS ASPECTOS DE LA NAVEGACIÓN POR VENTANAS */
public class NavSerFX  implements INavService{

  private final Map<_VentanaFX, Stage> ventanas = new HashMap<>();

  public void cambiarEscena(Stage stage, Scene scene, EventHandler<KeyEvent> handlerTeclas) {

    stage.setScene(scene);

    if (handlerTeclas != null) {
      scene.addEventFilter(KeyEvent.KEY_PRESSED, handlerTeclas);

      stage.setOnHidden(
          e -> {
            scene.removeEventFilter(KeyEvent.KEY_PRESSED, handlerTeclas);
          });
    }
  }

  public void crearCambiarEscena(
      Stage stage, String fxml, StageStyle style, Consumer<Object> initController) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + fxml + ".fxml"));
      Parent root = loader.load();

      Object controller = loader.getController();
      if (initController != null) {
        initController.accept(controller);
      }

      Scene scene = new Scene(root);
      stage.setScene(scene);
      stage.initStyle(style);

    } catch (Exception e) {
      System.out.println(
          "[NavService>crearCambiarEscena] Excepcion "
              + e.getClass()
              + " al crear y cambiar de escena");
    }
  }

  public Scene crearEscena(String nombreArchivoFxmlSinExt) {
    String ruta = _Ruta.FXML.getRuta() + "/" + nombreArchivoFxmlSinExt + ".fxml";
    FxmlHelper loader = new FxmlHelper(ruta);
    // System.out.println("[NavService>crearEscena] ruta del FXML: " + ruta);
    Parent parent = loader.cargarFXML();
    Scene esc = new Scene(parent);
    // System.out.println("[NavService>crearEscena] escena creada : " + nombreArchivoFxmlSinExt + "
    // : " + esc.hashCode() );
    return esc;
  }

  public Stage crearStage(Scene sc) {
    Stage st = new Stage();
    st.setScene(sc);
    return st;
  }

  public Stage cambiarEscena(Stage st, Scene es) {
    st.setScene(es);
    return st;
  }

  public Stage crearVentana(_VentanaFX id, Consumer<Object> initController) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + id.fxml() + ".fxml"));
      Parent root = loader.load();

      Object controller = loader.getController();
      if (initController != null) {
        initController.accept(controller);
      }

      Stage stage = new Stage();
      stage.setTitle(id.titulo());
      stage.setScene(new Scene(root));

      ventanas.put(id, stage);
      return stage;

    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public Stage getVentana(_VentanaFX id) {
    return ventanas.get(id);
  }

  public void mostrar(_VentanaFX id) {
    Stage s = ventanas.get(id);
    if (s != null) s.show();
  }

  public void ocultar(_VentanaFX id) {
    Stage s = ventanas.get(id);
    if (s != null) s.hide();
  }

  public void cerrar(_VentanaFX id) {
    Stage s = ventanas.remove(id);
    if (s != null)
      s.close();
  }
  
  @Override
  public void mostrarTablaFacturas() {

      Stage ventana = obtenerOVentana(_VentanaFX.TABLA_FCT, controller -> {
          ((FxCntrlTablaFCT) controller).cargarDatos();
      });

      // Crear wrapper ItablaFCT si no existe
      if (AppContext.get().getTablaFCT() == null) {
          AppContext.get().setTablaFCT(new TablaFCTFX(ventana));
      }

      ventana.show();
      ventana.toFront();
  }

  @Override
  public void mostrarPanelControl() {
    Stage ventana = ventanas.get(_VentanaFX.PANEL_CONTROL);

    if (ventana == null) {
        // Crear la ventana si no existe
        ventana = crearVentana(_VentanaFX.PANEL_CONTROL, controller -> {});
    }

    ventana.show();
    ventana.toFront();
  }

  @Override
  public void mostrarLogin() {

      Stage ventana = obtenerOVentana(_VentanaFX.LOGIN, controller -> {
          AppContext.get().setAccesoUI((IaccesoUI) controller);
      });

      ventana.show();
      ventana.toFront();
  }

  @Override
  public void mostrarSplash() {
      Stage ventana = crearVentana("FxSplash.fxml", "Cargando...");

      if (ventana == null) {
          System.err.println("ERROR: No se pudo crear la ventana Splash");
          return;
      }

      ventana.show();
  }

  private Stage crearVentana(String fxml, String titulo) {
    try {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/fxml/" + fxml)
        );

        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setTitle(titulo);
        stage.setScene(new Scene(root));
        return stage;

    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }
  }

  private Stage obtenerOVentana(_VentanaFX id, Consumer<Object> initController) {
    Stage ventana = ventanas.get(id);

    if (ventana == null) {
        ventana = crearVentana(id, initController);
        ventanas.put(id, ventana);
    }

    return ventana;
  }

  @Override
  public void ocultarTablaFacturas() {
    Stage ventana = ventanas.get(_VentanaFX.TABLA_FCT);

    ventana.hide();
  }

  @Override
  public void loginCorrecto(String usuario) {

      // 1. Guardar usuario
      AppContext.setUsuarioActual(usuario);

      // 2. Cerrar ventana de acceso
      Stage login = ventanas.get(_VentanaFX.LOGIN);
      if (login != null) {
          login.close();
      }

      // 3. Mostrar ventanas principales
      mostrarTablaFacturas();
      mostrarPanelControl();
  }

  @Override
  public void cerrarLoginPorFallo() {
      Stage login = ventanas.get(_VentanaFX.LOGIN);
      if (login != null) {
          login.close();
      }
      System.exit(0);
  }

}
