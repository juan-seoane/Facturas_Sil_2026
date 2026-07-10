package com.sil.facturas.presentationgui.services.ocr;

import static com.sil.facturas.domain.interfaces.IDebugService.*;

import com.sil.facturas.domain.ocr.*;
import java.util.Objects;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

public class ModeloOCRRenderer {

  private Group group;
  private ImageView imageView;
  private Pane overlayPane;
  private Pane rootPane;

  private ModeloOCR modelo;
  private InfoImagen infoImagen;
  private double currentScale = 1.0;

  private double imgW;
  private double imgH;
  private double imgRatio;

  private double stageDecorW = -1;
  private double stageDecorH = -1;

  private final PauseTransition resizeDebounce = new PauseTransition(Duration.millis(60));
  private boolean resizingByCode = false;
  private double imageAspect = 0.70710678118;
  private Stage attachedStage;

  //------------------------------------------------------------------------------
  // #region METODOS_GENERALES
  //------------------------------------------------------------------------------

  public ModeloOCRRenderer() {
    resizeDebounce.setOnFinished(
        e -> {
          if (imageView != null && imageView.getImage() != null) {
            ajustarSegunStage(imageView.getImage());
          }
        });
  }

  public void attachTo(Group group, ImageView imageView, Pane overlayPane, Pane rootPane) {
    printWarning("[ModeloOCRRenderer>attachTo] group=" + group);
    printWarning("[ModeloOCRRenderer>attachTo] imageView=" + imageView);
    printWarning("[ModeloOCRRenderer>attachTo] overlayPane=" + overlayPane);
    printWarning("[ModeloOCRRenderer>attachTo] rootPane=" + rootPane);

    this.group = Objects.requireNonNull(group);
    this.imageView = Objects.requireNonNull(imageView);
    this.overlayPane = Objects.requireNonNull(overlayPane);
    this.rootPane = Objects.requireNonNull(rootPane);

    this.overlayPane.setManaged(false);


    // Helper que instala listeners en el Stage cuando esté disponible
    final java.util.function.Consumer<Stage> installOnStage =
        stage -> {
          printWarning("[ModeloOCRRenderer>attachTo] installing setOnShown on stage: " + stage);
          stage.setOnShown(
              ev -> {
                printWarning("[ModeloOCRRenderer>attachTo] stage.onShown fired, adjusting to A4");
                ajustarStageA4(stage);
                if (imageView.getImage() != null) {
                  printWarning(
                      "[ModeloOCRRenderer>attachTo] stage.onShown fired, adjusting to image");
                  ajustarInicial(imageView.getImage());
                }
              });
          instalarResizeListener(stage);
        };

    // Caso 1: la Scene ya está presente en rootPane
    // if (rootPane.getScene() != null) {
    //   printWarning("[ModeloOCRRenderer] attachTo: rootPane already has a Scene");
    //   if (rootPane.getScene().getWindow() != null) {
    //     // Stage ya disponible
    //     installOnStage.accept((Stage) rootPane.getScene().getWindow());
    //     return;
    //   } else {
    //     // Scene presente pero window aún no: escuchar windowProperty
    //     rootPane
    //         .getScene()
    //         .windowProperty()
    //         .addListener(
    //             (obsW, oldW, newW) -> {
    //               if (newW != null) {
    //                 installOnStage.accept((Stage) newW);
    //               }
    //             });
    //     return;
    //   }
    // }

    // Caso 2: la Scene no está presente aún: escuchar sceneProperty

    rootPane
        .sceneProperty()
        .addListener(
            (obs, oldScene, newScene) -> {
              if (newScene != null) {
                printWarning(
                    "[ModeloOCRRenderer] attachTo: sceneProperty changed, newScene"
                        + " available");
                if (newScene.getWindow() != null) {
                  installOnStage.accept((Stage) newScene.getWindow());
                } else {
                  newScene
                      .windowProperty()
                      .addListener(
                          (obs2, oldW, newW) -> {
                            if (newW != null) {
                              installOnStage.accept((Stage) newW);
                            }
                          });
                }
              }
            });
  }

  private void instalarResizeListener(Stage stage) {
    print(
        "[ModeloOCRRenderer>instalarResizeListener] installing resize listener on stage: " + stage);
    // Scene scene = stage.getScene();

    // scene.widthProperty().addListener((obs, oldV, newV) -> {
    //     if (!resizingByCode) enforceAspectRatio(stage, true);
    //     recomputeScaleOnResize();
    // });

    // scene.heightProperty().addListener((obs, oldV, newV) -> {
    //     if (!resizingByCode) enforceAspectRatio(stage, false);
    //     recomputeScaleOnResize();
    // });
  }

  // ------------------------------------------------------------------------------
  // #endregion
  // ------------------------------------------------------------------------------

  // ------------------------------------------------------------------------------
  // #region ESCALA Y ASPECT RATIO
  // ------------------------------------------------------------------------------
  private void cacheStageDeco(Stage stage) {
    if (stageDecorW >= 0 && stageDecorH >= 0) return;
    Scene scene = stage.getScene();
    if (scene == null) return;

    stageDecorW = stage.getWidth() - scene.getWidth();
    stageDecorH = stage.getHeight() - scene.getHeight();
  }

  public void recomputeScale() {
    print("[ModeloOCRRenderer>recomputeScale] recomputing scale");
  //   Scene scene = rootPane.getScene();
  //   double sceneW = scene.getWidth();
  //   double sceneH = scene.getHeight();

  //   double scale = Math.min(sceneW / imgW, sceneH / imgH);
  //   applyScale(scale);
  // }

  //   private void recomputeScaleOnResize() {
  //   if (rootPane == null
  //       || rootPane.getScene() == null
  //       || imageView == null
  //       || imageView.getImage() == null)
  //     return;

  //   // // Forzar layout para obtener medidas fiables
  //   // rootPane.getScene().getRoot().requestLayout();

  //   double sceneW = rootPane.getScene().getWidth();
  //   double sceneH = rootPane.getScene().getHeight();

  //   // Si quieres que la imagen siempre llene el alto usa sceneH/imgH
  //   double scale = Math.min(sceneW / imgW, sceneH / imgH);

  //   applyScale(scale);
  }

  private void applyScale(double scale) {
    print("[ModeloOCRRenderer>applyScale] applying scale: " + scale);
    // currentScale = scale;

    // group.setScaleX(scale);
    // group.setScaleY(scale);

    // double imgScaledW = imgW * scale;
    // double imgScaledH = imgH * scale;

    // Scene scene = rootPane.getScene();
    // double sceneW = scene.getWidth();
    // double sceneH = scene.getHeight();

    // double tx = (sceneW - imgScaledW) / 2;
    // double ty = (sceneH - imgScaledH) / 2;

    // group.setTranslateX(tx);
    // group.setTranslateY(ty);

    // printWarning(
    //   "[ModeloOCRRenderer>applyScale] group layoutX,Y = "
    //   + group.getLayoutX()
    //   + ","
    //   + group.getLayoutY());
    // printWarning(
    //   "[ModeloOCRRenderer>applyScale] group translateX,Y = "
    //   + group.getTranslateX()
    //   + ","
    //   + group.getTranslateY());
    // printWarning(
    //   "[ModeloOCRRenderer>applyScale] group boundsInParent = " + group.getBoundsInParent());
    // printWarning(
    //   "[ModeloOCRRenderer>applyScale] overlay boundsInLocal = " + overlayPane.getBoundsInLocal());
    // printWarning(
    //   "[ModeloOCRRenderer>applyScale] overlay boundsInParent = "
    //   + overlayPane.getBoundsInParent());
    // printWarning(
    //   "[ModeloOCRRenderer>applyScale] overlay pref = "
    //   + overlayPane.getPrefWidth()
    //   + "x"
    //   + overlayPane.getPrefHeight());
    //   printWarning(
    //     "[ModeloOCRRenderer>applyScale] imageView bounds = " + imageView.getBoundsInParent());

    //   redibujarModelo();
    }

  private void ajustarInicial(Image img) {
    if (rootPane == null || rootPane.getScene() == null || imageView == null) return;

    Stage stage = (Stage) rootPane.getScene().getWindow();
    Scene scene = rootPane.getScene();
    Window window = scene.getWindow();
    if (window == null) return;

    scene.getRoot().applyCss();
    scene.getRoot().layout();

    double sceneW = scene.getWidth();
    double sceneH = scene.getHeight();
    if (sceneW <= 0 || sceneH <= 0 || imgW <= 0 || imgH <= 0) return;

    double toolbarH = 0;
    var toolbar = scene.lookup(".tool-bar");
    if (toolbar != null) toolbarH = toolbar.getBoundsInParent().getHeight();

    double availableH = sceneH - toolbarH;
    if (availableH <= 0) return;

    imageView.setPreserveRatio(true);
    imageView.setSmooth(true);
    imageView.setFitHeight(availableH);
    imageView.setFitWidth(0);

    currentScale = availableH / imgH;

    double targetSceneW = availableH * imgRatio;
    double decoW = (stageDecorW >= 0) ? stageDecorW : (stage.getWidth() - sceneW);
    double decoH = (stageDecorH >= 0) ? stageDecorH : (stage.getHeight() - sceneH);

    stageLikeResize(window, targetSceneW + decoW, availableH + toolbarH + decoH);
  }

  private void stageLikeResize(Window window, double w, double h) {
    if (window instanceof Stage stage) {
      stage.setWidth(w);
      stage.setHeight(h);
    }
  }

  private void ajustarSegunStage(Image img) {
    print("[ModeloOCRRenderer>ajustarSegunStage] adjusting according to stage");
    // Scene scene = rootPane.getScene();
    // double w = scene.getWidth();
    // double h = scene.getHeight();

    // double scale = Math.min(w / imgW, h / imgH);
    // applyScale(scale);

    // infoImagen = new InfoImagen(imgW, imgH, new Escala(scale, scale));
  }

private void instalarAspectRatioListener(Stage stage) {
  stage.heightProperty().addListener((obs, oldV, newV) -> {
    if (resizingByCode)
      return;
    if (imageView == null || imageView.getImage() == null)
      return;

    resizingByCode = true;
    try {
      reajustarImagenYVentana(stage);
    } finally {
      Platform.runLater(() -> resizingByCode = false);
    }
  });
}

  private void ajustarStageA4(Stage stage) {
    print("[ModeloOCRRenderer>ajustarStageA4] adjusting stage to A4 aspect ratio");

    if (stage == null || stage.getScene() == null) return;

    Scene scene = stage.getScene();
    Parent root = scene.getRoot();

    Rectangle2D visual = Screen.getPrimary().getVisualBounds();
    double a4Ratio = 0.70710678118;

    root.applyCss();
    root.layout();

    double decoW = stage.getWidth() - scene.getWidth();
    double decoH = stage.getHeight() - scene.getHeight();

    double targetSceneH = visual.getHeight();
    double targetSceneW = targetSceneH * a4Ratio;

    stage.setX(visual.getMinX());
    stage.setY(visual.getMinY());
    stage.setWidth(targetSceneW + decoW);
    stage.setHeight(targetSceneH + decoH);

    root.requestLayout();
}

  //------------------------------------------------------------------------------
  // #endregion
  //------------------------------------------------------------------------------

  //------------------------------------------------------------------------------
  // #region IMAGEN
  //------------------------------------------------------------------------------

private boolean resizeListenersInstalled = false;
private boolean initialAdjustDone = false;

public void setImage(Image img) {
    imageView.setPreserveRatio(true);
    imageView.setSmooth(true);
    imageView.setImage(img);

    if (img == null) return;

    if (img.getWidth() > 0 && img.getHeight() > 0) {
        onImageReady(img);
        return;
    }

    final ChangeListener<Number>[] hListenerRef = new ChangeListener[1];

    ChangeListener<Number> wListener = new ChangeListener<>() {
        @Override
        public void changed(ObservableValue<? extends Number> obs, Number oldV, Number newV) {
            if (newV.doubleValue() > 0 && img.getHeight() > 0) {
                img.widthProperty().removeListener(this);
                if (hListenerRef[0] != null) img.heightProperty().removeListener(hListenerRef[0]);
                onImageReady(img);
            }
        }
    };

    hListenerRef[0] = new ChangeListener<>() {
        @Override
        public void changed(ObservableValue<? extends Number> obs, Number oldV, Number newV) {
            if (newV.doubleValue() > 0 && img.getWidth() > 0) {
                img.heightProperty().removeListener(this);
                img.widthProperty().removeListener(wListener);
                onImageReady(img);
            }
        }
    };

    img.widthProperty().addListener(wListener);
    img.heightProperty().addListener(hListenerRef[0]);
}

private void onImageReady(Image img) {
    print(
        "[ModeloOCRRenderer>onImageReady] image ready: " + img.getWidth() + "x" + img.getHeight());

    imgW = img.getWidth();
    imgH = img.getHeight();
    if (imgH <= 0)
        return;
    imgRatio = imgW / imgH;

    overlayPane.setManaged(false);
    overlayPane.setPrefWidth(imgW);
    overlayPane.setPrefHeight(imgH);
    overlayPane.resizeRelocate(0, 0, imgW, imgH);
    overlayPane.setMouseTransparent(true);

    Platform.runLater(
        () -> {
          if (rootPane == null || rootPane.getScene() == null) return;
          Stage stage = (Stage) rootPane.getScene().getWindow();
          if (stage == null) return;

          cacheStageDeco(stage);
          instalarListenersDeResize(stage);

          if (!initialAdjustDone) {
            ajustarStageAlRatioDeImagen(stage);
            initialAdjustDone = true;
          } else {
            reajustarImagenYVentana(stage);
          }
        });
}

  private void ajustarStageAlRatioDeImagen(Stage stage) {
    if (stage == null
        || stage.getScene() == null
        || imageView == null
        || imageView.getImage() == null) return;

    Scene scene = stage.getScene();
    scene.getRoot().applyCss();
    scene.getRoot().layout();

    double sceneW = scene.getWidth();
    double sceneH = scene.getHeight();
    if (sceneW <= 0 || sceneH <= 0 || imgW <= 0 || imgH <= 0) return;

    double toolbarH = 0;
    var toolbar = scene.lookup(".tool-bar");
    if (toolbar != null) toolbarH = toolbar.getBoundsInParent().getHeight();

    double availableH = sceneH - toolbarH;
    if (availableH <= 0) return;

    imageView.setPreserveRatio(true);
    imageView.setSmooth(true);
    imageView.setFitHeight(availableH);
    imageView.setFitWidth(0);

    currentScale = availableH / imgH;

    double targetSceneW = availableH * imgRatio;
    double decoW = (stageDecorW >= 0) ? stageDecorW : (stage.getWidth() - sceneW);
    double decoH = (stageDecorH >= 0) ? stageDecorH : (stage.getHeight() - sceneH);

    resizingByCode = true;
    try {
      stage.setWidth(targetSceneW + decoW);
      stage.setHeight(sceneH + decoH);
    } finally {
      Platform.runLater(() -> resizingByCode = false);
    }

    scene.getRoot().requestLayout();
  }

private void ajustarImagenSegunAltura() {
    if (rootPane == null || rootPane.getScene() == null || imageView == null || imageView.getImage() == null) return;

    Stage stage = (Stage) rootPane.getScene().getWindow();
    Scene scene = rootPane.getScene();
    Window window = scene.getWindow();
    if (window == null) return;

    scene.getRoot().applyCss();
    scene.getRoot().layout();

    double sceneH = scene.getHeight();
    double sceneW = scene.getWidth();
    if (sceneH <= 0 || sceneW <= 0 || imgH <= 0) return;

    double toolbarH = 0;
    var toolbar = scene.lookup(".tool-bar");
    if (toolbar != null) toolbarH = toolbar.getBoundsInParent().getHeight();

    double availableH = sceneH - toolbarH;
    if (availableH <= 0) return;

    imageView.setPreserveRatio(true);
    imageView.setSmooth(true);
    imageView.setFitHeight(availableH);
    imageView.setFitWidth(0);

    currentScale = availableH / imgH;

    double scaledW = imgW * currentScale;
    double decoW = (stageDecorW >= 0) ? stageDecorW : (stage.getWidth() - sceneW);
    double decoH = (stageDecorH >= 0) ? stageDecorH : (stage.getHeight() - sceneH);

    double targetSceneW = scaledW;
    double targetSceneH = availableH + toolbarH;

    window.setWidth(targetSceneW + decoW);
    window.setHeight(targetSceneH + decoH);

    scene.getRoot().requestLayout();
}

  private void reajustarImagenYVentana(Stage stage) {
    if (stage == null
        || stage.getScene() == null
        || imageView == null
        || imageView.getImage() == null) return;

    if (resizingByCode) return;
    resizingByCode = true;
    try {
      Scene scene = stage.getScene();
      Window window = stage;

      scene.getRoot().applyCss();
      scene.getRoot().layout();

      double sceneH = scene.getHeight();
      double sceneW = scene.getWidth();
      if (sceneH <= 0 || sceneW <= 0 || imgH <= 0) return;

      double toolbarH = 0;
      var toolbar = scene.lookup(".tool-bar");
      if (toolbar != null) toolbarH = toolbar.getBoundsInParent().getHeight();

      double availableH = sceneH - toolbarH;
      if (availableH <= 0) return;

      imageView.setPreserveRatio(true);
      imageView.setSmooth(true);
      imageView.setFitHeight(availableH);
      imageView.setFitWidth(0);

      currentScale = availableH / imgH;

      double scaledW = availableH * imgRatio;
      double decoW = (stageDecorW >= 0) ? stageDecorW : (stage.getWidth() - sceneW);
      double decoH = (stageDecorH >= 0) ? stageDecorH : (stage.getHeight() - sceneH);

      stage.setWidth(scaledW + decoW);
      stage.setHeight(availableH + toolbarH + decoH);

      scene.getRoot().requestLayout();
    } finally {
      Platform.runLater(() -> resizingByCode = false);
    }
  }

private void instalarListenersDeResize(Stage stage) {
    if (stage == null || resizeListenersInstalled) return;
    resizeListenersInstalled = true;

    ChangeListener<Number> resizeListener =
        (obs, oldV, newV) -> {
          if (resizingByCode) return;
          if (stage.getScene() == null || imageView == null || imageView.getImage() == null) return;
          reajustarImagenYVentana(stage);
        };

    stage.widthProperty().addListener(resizeListener);
    stage.heightProperty().addListener(resizeListener);

    stage.showingProperty().addListener((obs, oldV, showing) -> {
        if (showing) {
            Platform.runLater(() -> reajustarImagenYVentana(stage));
        }
    });
}

//------------------------------------------------------------------------------
  // #endregion
  //------------------------------------------------------------------------------

  //------------------------------------------------------------------------------
  // #region MODELOOCR
  //------------------------------------------------------------------------------

  public void setModelo(ModeloOCR modelo) {
    this.modelo = modelo;
    redibujarModelo();
  }

  private void redibujarModelo() {
    overlayPane.getChildren().clear();

        //logging
        Scene scene = overlayPane.getScene();
    printWarning(
        "[ModeloOCRRenderer>redibujarModelo] scene: " + scene.getWidth() + "x" + scene.getHeight());
    printWarning(
        "[ModeloOCRRenderer>redibujarModelo] img: " + imgW + "x" + imgH + " scale=" + currentScale);
        double imgScaledW = imgW * currentScale;
        double imgScaledH = imgH * currentScale;
    printWarning("[ModeloOCRRenderer>redibujarModelo] imgScaled: " + imgScaledW + "x" + imgScaledH);
    printWarning(
        "[ModeloOCRRenderer>redibujarModelo] group layout: "
            + group.getLayoutX()
            + ","
            + group.getLayoutY());
    printWarning(
        "[ModeloOCRRenderer>redibujarModelo] group translate: "
            + group.getTranslateX()
            + ","
            + group.getTranslateY());
    printWarning(
        "[ModeloOCRRenderer>redibujarModelo] overlay boundsInParent: "
            + overlayPane.getBoundsInParent());

      if (modelo == null)
          return;

      for (Bloque b : modelo.getBloques()) {
          dibujarBloque(b);
      }
  }


  public void dibujarBloque(Bloque b) {
    double x = b.zona().x();
    double y = b.zona().y();
    double w = b.zona().w();
    double h = b.zona().h();

    Rectangle r = new Rectangle(x, y, w, h);
    r.setStroke(Color.RED);
    r.setStrokeWidth(1.0 / currentScale);
    r.setFill(Color.TRANSPARENT);
    overlayPane.getChildren().add(r);

    Text label = new Text(b.nombre());
    label.setFill(Color.RED);
    label.setX(x + 4);
    label.setY(y - 6);
    overlayPane.getChildren().add(label);

    Circle c = new Circle(x + w / 2, y + h / 2, 4);
    c.setFill(Color.BLACK);
    overlayPane.getChildren().add(c);

    for (Campo campo : b.campos()) {
      dibujarCampo(b, campo);
    }
  }

  private void dibujarCampo(Bloque b, Campo c) {
    double x = b.zona().x() + c.offsetX();
    double y = b.zona().y() + c.offsetY();
    double w = c.w();
    double h = c.h();

    Rectangle r = new Rectangle(x, y, w, h);
    r.setStroke(Color.GREEN);
    r.setStrokeWidth(1.0 / currentScale);
    r.setFill(Color.TRANSPARENT);
    overlayPane.getChildren().add(r);

    Text label = new Text(c.nombre());
    label.setFill(Color.GREEN);
    label.setX(x + 3);
    label.setY(y + 14);
    overlayPane.getChildren().add(label);
  }

  //------------------------------------------------------------------------------
  // #endregion
  //------------------------------------------------------------------------------
}
