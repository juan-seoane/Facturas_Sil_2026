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
    print("[ModeloOCRRenderer>instalarResizeListener] installing resize listener on stage: " + stage);
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
  
  //------------------------------------------------------------------------------
  // #endregion
  //------------------------------------------------------------------------------
  
  //------------------------------------------------------------------------------
  // #region ESCALA Y ASPECT RATIO
  //------------------------------------------------------------------------------

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
    Scene scene = rootPane.getScene();
    
    double w = scene.getWidth();
    double h = scene.getHeight();
    
    double scale = Math.min(w / imgW, h / imgH);
  
    double tx = (scene.getWidth() - imgW * scale) / 2;
    double ty = (scene.getHeight() - imgH * scale) / 2;
  
    group.setTranslateX(tx);
    group.setTranslateY(ty);
    
    applyScale(scale);
  
    infoImagen = new InfoImagen(imgW, imgH, new Escala(scale, scale));
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
    scene.getRoot().applyCss();
    scene.getRoot().layout();

    double a4Ratio = 0.70710678118;
    double decoW = stage.getWidth() - scene.getWidth();
    double decoH = stage.getHeight() - scene.getHeight();

    double screenH = Screen.getPrimary().getVisualBounds().getHeight();

    double toolbarH = 0;
    if (rootPane != null && rootPane.getScene() != null) {
        toolbarH = rootPane.getScene().lookup(".tool-bar") != null
                ? rootPane.getScene().lookup(".tool-bar").getBoundsInParent().getHeight()
                : 0;
    }

    double targetSceneH = screenH - toolbarH;
    double targetSceneW = targetSceneH * a4Ratio;

    stage.setWidth(targetSceneW + decoW);
    stage.setHeight(targetSceneH + decoH);
    stage.centerOnScreen();

    rootPane.setPrefWidth(targetSceneW);
    rootPane.setPrefHeight(targetSceneH);
    rootPane.setMinWidth(targetSceneW);
    rootPane.setMinHeight(targetSceneH);
    rootPane.setMaxWidth(targetSceneW);
    rootPane.setMaxHeight(targetSceneH);
}
  
  //------------------------------------------------------------------------------
  // #endregion
  //------------------------------------------------------------------------------

  //------------------------------------------------------------------------------
  // #region IMAGEN
  //------------------------------------------------------------------------------

public void setImage(Image img) {
    imageView.setPreserveRatio(true);
    imageView.setSmooth(true);

    imageView.setImage(img);

    if (img.getWidth() > 0 && img.getHeight() > 0) {
        onImageReady(img);
        return;
    }
    final ChangeListener<Number>[] hListenerRef = new ChangeListener[1];

    ChangeListener<Number> wListener = new ChangeListener<>() {
        @Override
        public void changed(ObservableValue<? extends Number> obs, Number oldV, Number newV) {
            if (newV.doubleValue() > 0) {
                img.widthProperty().removeListener(this);
                img.heightProperty().removeListener(hListenerRef[0]);
                onImageReady(img);
            }
        }
    };

    hListenerRef[0] = new ChangeListener<>() {
        @Override
        public void changed(ObservableValue<? extends Number> obs, Number oldV, Number newV) {
            if (newV.doubleValue() > 0) {
                img.widthProperty().removeListener(wListener);
                img.heightProperty().removeListener(this);
                onImageReady(img);
            }
        }
    };

    img.widthProperty().addListener(wListener);
    img.heightProperty().addListener(hListenerRef[0]);
}

private void onImageReady(Image img) {
  print("[ModeloOCRRenderer>onImageReady] image ready: " + img.getWidth() + "x" + img.getHeight());

  imgW = img.getWidth();
  imgH = img.getHeight();
  imgRatio = imgW / imgH;

  overlayPane.setManaged(false);
  overlayPane.setPrefWidth(imgW);
  overlayPane.setPrefHeight(imgH);
  overlayPane.resizeRelocate(0, 0, imgW, imgH);
  overlayPane.setMouseTransparent(true);

  Platform.runLater(() -> {
      if (rootPane == null || rootPane.getScene() == null) return;
      Stage stage = (Stage) rootPane.getScene().getWindow();
      ajustarImagenSegunAltura();
      instalarAspectRatioListener(stage);
  });
}

private void ajustarImagenSegunAltura() {
  if (rootPane == null || rootPane.getScene() == null || imageView == null || imageView.getImage() == null)
    return;

  Scene scene = rootPane.getScene();
  double sceneH = scene.getHeight();
  double sceneW = scene.getWidth();

  double toolbarH = 0;
  if (scene.lookup(".tool-bar") != null) {
    toolbarH = scene.lookup(".tool-bar").getBoundsInParent().getHeight();
  }

  double availableH = sceneH - toolbarH;

  imageView.setPreserveRatio(true);
  imageView.setFitHeight(availableH);
  imageView.setFitWidth(0);

  currentScale = availableH / imgH;

  double scaledW = imgW * currentScale;
  double decoW = rootPane.getScene().getWindow().getWidth() - sceneW;
  double decoH = rootPane.getScene().getWindow().getHeight() - sceneH;

  rootPane.getScene().getWindow().setWidth(scaledW + decoW);
  rootPane.getScene().getWindow().setHeight(availableH + toolbarH + decoH);
}

private void reajustarImagenYVentana(Stage stage) {
    if (stage == null || stage.getScene() == null || imageView == null || imageView.getImage() == null) return;

    Scene scene = stage.getScene();
    double decoW = stage.getWidth() - scene.getWidth();
    double decoH = stage.getHeight() - scene.getHeight();

    double toolbarH = 0;
    var toolbar = scene.lookup(".tool-bar");
    if (toolbar != null) toolbarH = toolbar.getBoundsInParent().getHeight();

    double availableH = scene.getHeight() - toolbarH;

    imageView.setPreserveRatio(true);
    imageView.setSmooth(true);
    imageView.setFitHeight(availableH);
    imageView.setFitWidth(0);

    currentScale = availableH / imgH;

    double targetSceneW = availableH * imageAspect;
    stage.setWidth(targetSceneW + decoW);
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


  private void dibujarBloque(Bloque b) {
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
