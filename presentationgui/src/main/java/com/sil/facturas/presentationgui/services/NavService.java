package com.sil.facturas.presentationgui.services;

import com.sil.facturas.app.helpers.VentanaID;
import com.sil.facturas.infrastructure.filesystem._Ruta;
import com.sil.facturas.presentationgui.helpers.FxmlHelper;
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
public class NavService {

  private final Map<VentanaID, Stage> ventanas = new HashMap<>();

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

  public Stage crearVentana(VentanaID id, Consumer<Object> initController) {
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

  public Stage getVentana(VentanaID id) {
    return ventanas.get(id);
  }

  public void mostrar(VentanaID id) {
    Stage s = ventanas.get(id);
    if (s != null) s.show();
  }

  public void ocultar(VentanaID id) {
    Stage s = ventanas.get(id);
    if (s != null) s.hide();
  }

  public void cerrar(VentanaID id) {
    Stage s = ventanas.remove(id);
    if (s != null) s.close();
  }
}
