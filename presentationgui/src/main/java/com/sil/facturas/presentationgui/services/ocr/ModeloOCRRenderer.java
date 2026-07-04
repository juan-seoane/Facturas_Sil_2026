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
  private boolean lockAspectToImage = true; // controla si el Stage debe mantener aspect ratio de la imagen
  private double imageAspect = 1.0; // ancho/alto de la imagen cargada

  public ModeloOCRRenderer() {
    resizeDebounce.setOnFinished(
        e -> {
          if (imageView != null && imageView.getImage() != null) {
            ajustarSegunStage(imageView.getImage());
          }
        });
  }

  public void attachTo(Group group, ImageView imageView, Pane overlayPane, Pane rootPane) {
    this.group = Objects.requireNonNull(group);
    this.imageView = Objects.requireNonNull(imageView);
    this.overlayPane = Objects.requireNonNull(overlayPane);
    this.rootPane = Objects.requireNonNull(rootPane);

    overlayPane.setManaged(false);

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
    // Debounce para evitar recalculos continuos
    resizeDebounce.setOnFinished(
        e -> {
          if (imageView != null && imageView.getImage() != null) {
            recomputeScaleOnResize();
            enforceAspectRatioOnStage(stage, true);
          }
        });

    // Escuchar cambios de tamaño del Scene

    Scene scene = stage.getScene();

    scene
        .widthProperty()
        .addListener(
            (obs, oldV, newV) -> {
              recomputeScaleOnResize();
              enforceAspectRatioOnStage(stage, true);
            });

    scene
        .heightProperty()
        .addListener(
            (obs, oldV, newV) -> {
              recomputeScaleOnResize();
              enforceAspectRatioOnStage(stage, false);
            });

    // stage
    //     .heightProperty()
    //     .addListener(
    //         (obs, oldV, newV) -> {
    //           if (resizingByCode) return;
    //           resizeDebounce.playFromStart();
    //           if (lockAspectToImage && imageView != null && imageView.getImage() != null) {
    //             enforceAspectRatioOnStage(stage, false); // alto cambió, ajustar ancho
    //           }
    //         });

    // // También escuchar cambios directos en la Scene (por si el usuario redimensiona desde la Scene)
    // if (rootPane != null) {
    //   rootPane
    //       .sceneProperty()
    //       .addListener(
    //           (obs, oldS, newS) -> {
    //             if (newS != null) {
    //               newS.widthProperty()
    //                   .addListener(
    //                       (o, ov, nv) -> {
    //                         if (resizingByCode) return;
    //                         resizeDebounce.playFromStart();
    //                       });
    //               newS.heightProperty()
    //                   .addListener(
    //                       (o, ov, nv) -> {
    //                         if (resizingByCode) return;
    //                         resizeDebounce.playFromStart();
    //                       });
    //             }
    //           });
    // }
  }

  private void enforceAspectRatioOnStage(Stage stage, boolean widthChanged) {
    if (imageAspect <= 0) return;

    // No forzar si está maximizado
    if (stage.isMaximized()) return;

    // Calcula la diferencia entre stage y scene (decoraciones)
    double decoW = stage.getWidth() - (stage.getScene() != null ? stage.getScene().getWidth() : 0);
    double decoH =
        stage.getHeight() - (stage.getScene() != null ? stage.getScene().getHeight() : 0);

    resizingByCode = true;
    try {
      if (widthChanged) {
        double targetSceneW = stage.getWidth() - decoW;
        double targetSceneH = Math.round(targetSceneW / imageAspect);
        stage.setHeight(targetSceneH + decoH);
      } else {
        double targetSceneH = stage.getHeight() - decoH;
        double targetSceneW = Math.round(targetSceneH * imageAspect);
        stage.setWidth(targetSceneW + decoW);
      }
    } finally {
      // Pequeño delay para evitar que el mismo cambio vuelva a disparar listeners inmediatamente
      Platform.runLater(() -> resizingByCode = false);
    }
  }

  private void recomputeScaleOnResize() {
    if (rootPane == null
        || rootPane.getScene() == null
        || imageView == null
        || imageView.getImage() == null) return;

    // Forzar layout para obtener medidas fiables
    rootPane.getScene().getRoot().requestLayout();

    double sceneW = rootPane.getScene().getWidth();
    double sceneH = rootPane.getScene().getHeight();

    // Si quieres que la imagen siempre llene el alto usa sceneH/imgH
    double scale = Math.min(sceneW / imgW, sceneH / imgH);

    applyScale(scale);
  }

  private void ajustarStageA4(Stage stage) {
    Rectangle2D visual = Screen.getPrimary().getVisualBounds();
    double targetSceneH = visual.getHeight();
    double targetSceneW = targetSceneH * 0.7;

    Platform.runLater(
        () -> {
          if (stage.getScene() == null) return;

          // 1) Forzar layout para obtener medidas reales
          stage.getScene().getRoot().applyCss();
          stage.getScene().getRoot().layout();

          // 2) Calcular decoraciones reales
          double decoW = stage.getWidth() - stage.getScene().getWidth();
          double decoH = stage.getHeight() - stage.getScene().getHeight();

          // 3) Ajustar Stage para que la Scene tenga EXACTAMENTE el tamaño A4
          stage.setWidth(targetSceneW);
          stage.setHeight(targetSceneH + decoH);

          stage.centerOnScreen();

          // 4) Ajustar rootPane al tamaño de la Scene
          rootPane.setPrefWidth(targetSceneW);
          rootPane.setPrefHeight(targetSceneH);

          rootPane.setClip(new Rectangle(0, 0, targetSceneW, targetSceneH));

          System.out.println(
              "Scene final: " + stage.getScene().getWidth() + "x" + stage.getScene().getHeight());
        });
  }

 public void setImage(Image img) {
    // Evitar fitWidth/fitHeight que interfieran
    imageView.setPreserveRatio(true);
    imageView.setSmooth(true);
    imageView.setFitWidth(0);
    imageView.setFitHeight(0);

    imageView.setImage(img);

    if (img.getWidth() > 0 && img.getHeight() > 0) {
        onImageReady(img);
        return;
    }

    // listeners reutilizables guardados en campos (ver mensajes anteriores)
    ChangeListener<Number> wListener = new ChangeListener<>() {
        @Override
        public void changed(ObservableValue<? extends Number> obs, Number oldV, Number newV) {
            if (newV.doubleValue() > 0) {
                img.widthProperty().removeListener(this);
                img.heightProperty().removeListener(this);
                onImageReady(img);
            }
        }
    };
    ChangeListener<Number> hListener = new ChangeListener<>() {
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
    img.heightProperty().addListener(hListener);
}

private void onImageReady(Image img) {
    imgW = img.getWidth();
    imgH = img.getHeight();
    imgRatio = imgW / imgH;

    // Preparar overlay en coordenadas de imagen
    overlayPane.setManaged(false);
    overlayPane.setPrefWidth(imgW);
    overlayPane.setPrefHeight(imgH);
    overlayPane.resizeRelocate(0, 0, imgW, imgH);
    overlayPane.setMouseTransparent(true);

    // Forzar layout y luego ajustar la imagen
    Platform.runLater(() -> {
        if (rootPane == null || rootPane.getScene() == null) return;
        rootPane.getScene().getRoot().requestLayout();
        ajustarInicial(img);
    });
}

  private void ajustarInicial(Image img) {
    Scene scene = rootPane.getScene();
    double w = scene.getWidth();
    double h = scene.getHeight();

    double scale = Math.min(w / imgW, h / imgH);
    applyScale(scale);

    infoImagen = new InfoImagen(imgW, imgH, new Escala(scale, scale));
  }

  private void ajustarSegunStage(Image img) {
    Scene scene = rootPane.getScene();
    double w = scene.getWidth();
    double h = scene.getHeight();

    double scale = Math.min(w / imgW, h / imgH);
    applyScale(scale);

    infoImagen = new InfoImagen(imgW, imgH, new Escala(scale, scale));
  }

  private void applyScale(double scale) {
    currentScale = scale;
    // group.setScaleX(scale);
    // group.setScaleY(scale);

    rootPane.setPrefWidth(imgW * scale);
    rootPane.setPrefHeight(imgH * scale);

    printWarning(
        "[ModeloOCRRenderer>applyScale] group layoutX,Y = "
            + group.getLayoutX()
            + ","
            + group.getLayoutY());
    printWarning(
        "[ModeloOCRRenderer>applyScale] group translateX,Y = "
            + group.getTranslateX()
            + ","
            + group.getTranslateY());
    printWarning(
        "[ModeloOCRRenderer>applyScale] group boundsInParent = " + group.getBoundsInParent());
    printWarning(
        "[ModeloOCRRenderer>applyScale] overlay boundsInLocal = " + overlayPane.getBoundsInLocal());
    printWarning(
        "[ModeloOCRRenderer>applyScale] overlay boundsInParent = "
            + overlayPane.getBoundsInParent());
    printWarning(
        "[ModeloOCRRenderer>applyScale] overlay pref = "
            + overlayPane.getPrefWidth()
            + "x"
            + overlayPane.getPrefHeight());
    printWarning(
        "[ModeloOCRRenderer>applyScale] imageView bounds = " + imageView.getBoundsInParent());

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

  public void setModelo(ModeloOCR modelo) {
    this.modelo = modelo;
    redibujarModelo();
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
}
