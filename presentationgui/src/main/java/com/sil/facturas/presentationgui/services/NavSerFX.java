package com.sil.facturas.presentationgui.services;

import com.sil.facturas.app.api.INavService;
import com.sil.facturas.app.api.IVentanaID;
import com.sil.facturas.app.api._VentanaID;
import com.sil.facturas.app.core.AppContext;
import com.sil.facturas.domain.interfaces.IDebugService;
import com.sil.facturas.presentationgui.fxcontrollers.FxCntrlPanelControl;
import com.sil.facturas.presentationgui.helpers._VentanaFX;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/* CONTROLA LA CREACIÓN DE ESCENAS Y DEMÁS ASPECTOS DE LA NAVEGACIÓN POR VENTANAS */
public class NavSerFX  implements INavService{

  private final Map<_VentanaFX, Stage> ventanas = new HashMap<>();
  private final Map<_VentanaFX, Object> controladores = new HashMap<>();

  /*
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


  public Stage cambiarEscena(Stage st, Scene es) {
    st.setScene(es);
    return st;
    }
    */

  public Stage crearVentana(_VentanaFX v, Consumer<Object> initController) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + v.fxml() + ".fxml"));
      Parent root = loader.load();

      Object controller = loader.getController();
      controladores.put(v, controller); // ← GUARDAR CONTROLLER

      if (initController != null) {
        initController.accept(controller);
      }

      Stage stage = new Stage();
      stage.setTitle(v.titulo());
      stage.setScene(new Scene(root));

      ventanas.put(v, stage);
      return stage;

    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

    public Stage getVentana(_VentanaFX id) {
        return ventanas.get(id);
    }

    public Stage crearStage(Scene sc) {
        Stage st = new Stage();
        st.setScene(sc);
        return st;
    }


    private Stage obtenerOVentana(IVentanaID id, Consumer<Object> initController) {
    _VentanaFX v = ((_VentanaFX) id);
        Stage ventana = ventanas.get(v);

        if (ventana == null) {
            ventana = crearVentana(v, initController);
            ventanas.put(v, ventana);
        }

        return ventana;
    }
  @Override
  public void mostrar(IVentanaID id, Consumer<Object> initController) {

    _VentanaFX v = resolverFX(id); // ← SIEMPRE resuelve correctamente

    IDebugService.print("[NavSerFX>mostrar] mostrando ventana " + v);

    Stage s = ventanas.get(v);

    if (s == null) {
      s = crearVentana(v, initController);
      ventanas.put(v, s);
    } else if (initController != null) {
      initController.accept(getControllerDe(s));
    }

    // ALWAYS_ON_TOP siempre aplicado
    s.setAlwaysOnTop(v.alwaysOnTop());

    // Handler de cierre si es PanelControl
    if (v == _VentanaFX.PANEL_CONTROL) {
      s.setOnCloseRequest(
          e -> {
            IDebugService.print("[NavSerFX] Cerrando PanelControl → salir de la aplicación");
            Platform.exit();
            System.exit(0);
          });
    }

    s.show();
  }

  private _VentanaFX resolverFX(IVentanaID id) {
    if (id instanceof _VentanaFX fx) {
      return fx;
    }

    // mapping desde _VentanaID
    switch (id.toString()) {
      case "PANEL_CONTROL":
        return _VentanaFX.PANEL_CONTROL;
      case "TABLA_FCT":
        return _VentanaFX.TABLA_FCT;
      case "LOGIN":
        return _VentanaFX.LOGIN;
      case "SPLASH":
        return _VentanaFX.SPLASH;
    }

    throw new IllegalArgumentException("No se puede resolver ventana FX para: " + id);
  }

  private Object getControllerDe(Stage s) {
    for (var entry : ventanas.entrySet()) {
      if (entry.getValue() == s) {
        return controladores.get(entry.getKey());
      }
    }
    return null;
  }

    @Override
    public void ocultar(IVentanaID v) {
        Stage ventana = ventanas.get((_VentanaFX)v);

        ventana.hide();
    }
    @Override
    public void cerrarPorFallo(IVentanaID v) {
        Stage s = ventanas.get((_VentanaFX)v);
        if (s != null) {
            s.close();
        }
        System.exit(0);
    }

    @Override
    public void cerrar(IVentanaID id) {
        if (id instanceof _VentanaID) {
            _VentanaFX v = null;
            switch (id.toString()) {
                case "PANEL_CONTROL":
                    IDebugService.print("cerrando PANEL_CONTROL");
                    v = _VentanaFX.PANEL_CONTROL;
                    break;
                case "TABLA_FCT":
                    IDebugService.print("cerrando TABLA_FCT");
                    v = _VentanaFX.TABLA_FCT;
                    break;
                case "LOGIN":
                    IDebugService.print("[NavSerFX] cerrando ventana LOGIN");
                    v = _VentanaFX.LOGIN;
                    break;
            }
            Stage s = ventanas.get(v);
            if (s == null) {
                IDebugService.print("[NavSerFX] ventana ya cerrada " + (v).fxml());
            }
            ventanas.remove(v);
            s.close();
        } else {
            Stage s = ventanas.get((_VentanaFX) id);
            if (s != null) {
                s.close();
                ventanas.remove(id);
            }
        }
    }
}
