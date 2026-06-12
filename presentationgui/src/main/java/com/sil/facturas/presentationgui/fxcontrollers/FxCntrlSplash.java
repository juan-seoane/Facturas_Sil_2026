package com.sil.facturas.presentationgui.fxcontrollers;

import com.sil.facturas.app.core.AppContext;
import com.sil.facturas.domain.enums._Ruta;
import com.sil.facturas.domain.interfaces.IDebugService;
import com.sil.facturas.presentationgui.helpers._VentanaFX;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class FxCntrlSplash {
  @FXML private ImageView imgSplash;

  @FXML
  public void initialize() {
    Platform.runLater(() -> iniciarSplash());
  }

  private void iniciarSplash() {

    IDebugService.print("[FxCntrlSplash.initialize()] → INICIO");

    int i = (int) (Math.floor(Math.random() * 4) + 1);
    String ruta = _Ruta.IMG.getRuta() + "/splash" + i + ".jpg";

    this.imgSplash.setImage(new Image(getClass().getResourceAsStream(ruta)));

    IDebugService.print("[FxCntrlSplash.initialize()] - imgSplash = " + this.imgSplash);
    IDebugService.print("[FxCntrlSplash.initialize()] - Ruta calculada = " + ruta);
    IDebugService.print("[FxCntrlSplash.initialize()] - Stream = " +
    getClass().getResourceAsStream(ruta));

    FadeTransition ft = new FadeTransition(Duration.seconds(2), this.imgSplash);
    ft.setFromValue(1.0);
    ft.setToValue(0.5);
    ft.setDelay(Duration.seconds(2)); // tiempo visible

    ft.setOnFinished(
        e -> {
          IDebugService.print("FADE TERMINADO → iniciando aplicación");

          // Cerrar el Stage del Splash
          AppContext.get().nav().cerrar(_VentanaFX.SPLASH);

          // Abrir el Stage de Acceso sin marco
          AppContext.get().nav().mostrar(_VentanaFX.LOGIN, null);
        });
    ft.play();
  }
}
