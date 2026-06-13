package com.sil.facturas.presentationgui.fxcontrollers;

import com.sil.facturas.app.core.AppContext;
import com.sil.facturas.domain.interfaces.IDebugService;
import com.sil.facturas.domain.pojos.Factura;
import com.sil.facturas.domain.records.Fecha;
import com.sil.facturas.infrastructure.config.UIDataConfig;
import com.sil.facturas.presentationgui.helpers.TableScaler;
import com.sil.facturas.presentationgui.viewmodels.ExtractoFX;
import com.sil.facturas.presentationgui.viewmodels.FacturaFX;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.layout.HBox;
import javafx.util.converter.NumberStringConverter;

public class FxCntrlTablaFCT implements Initializable {

  @FXML private TreeTableView<Object> treeFct;

  @FXML private TreeTableColumn<FacturaFX, Number> colID;
  @FXML private TreeTableColumn<FacturaFX, String> colNumFact;
  @FXML private TreeTableColumn<FacturaFX, String> colFecha;
  @FXML private TreeTableColumn<FacturaFX, String> colRS;
  @FXML private TreeTableColumn<Object, String> colConcepto;
  @FXML private TreeTableColumn<FacturaFX, Boolean> colDev;
  @FXML private TreeTableColumn<FacturaFX, Number> colCantidad;

  @FXML private TreeTableColumn<Object, Number> colBase;
  @FXML private TreeTableColumn<Object, Number> colTipoIVA;
  @FXML private TreeTableColumn<Object, Number> colIVA;
  @FXML private TreeTableColumn<Object, Number> colST;
  @FXML private TreeTableColumn<FacturaFX, Number> colBaseNI;
  @FXML private TreeTableColumn<FacturaFX, Number> colTipoRet;
  @FXML private TreeTableColumn<FacturaFX, Number> colRetenc;
  @FXML private TreeTableColumn<FacturaFX, Number> colTotal;

  @FXML private TreeTableColumn<FacturaFX, String> colNota;

  @FXML private TreeTableColumn<Object, Void> colAcciones;

  @FXML private Label lblBase;
  @FXML private Label lblIVA;
  @FXML private Label lblST;
  @FXML private Label lblBaseNI;
  @FXML private Label lblRetenc;
  @FXML private Label lblTotal;
  @FXML private Label lblNumFact;
  @FXML private Label lblIndexFCT;

  private UIDataConfig uiCfg;
  private Path cfgPath;
  private FacturaFX facturaVaciaEnEdicion = FacturaFX.filaVacia();

  @Override
  public void initialize(URL url, ResourceBundle rb) {
      //IDebugService.print("[FxCntrlTablaFCT] Entrando en initialize()");
    if (AppContext.getUsuarioActual() == null) {
      IDebugService.printError(
          "[FxCntrlTablaFCT>initialize()] Usuario null, no cargo config todavía");
      return;
    }
    IDebugService.print(
        "FxCntrlTablaFCT>initialize()] usuarioActual = " + AppContext.getUsuarioActual());

    cargarConfigUIAsync(() -> {

        configurarColumnas();
        configurarColumnasEditables();

        cargarDatos();          // ya es async
        configurarAcciones();
        configurarEscalado();

        treeFct.setEditable(true);
     });

    treeFct.setEditable(true);

    treeFct
        .sceneProperty()
        .addListener(
            (obs, oldScene, newScene) -> {
              if (newScene != null) {
                Platform.runLater(
                    () -> {
                      aplicarAnchosDesdeJSON(); // ← aquí se aplican
                      recalcularColumnas(); // ← aquí se ajustan
                      // verificarColumnas(); // ← aquí SÍ tienen tamaño

                    });
              }
            });

    treeFct
        .widthProperty()
        .addListener(
            (o, ov, nv) -> {
              Platform.runLater(
                  () -> {
                    recalcularColumnas(); // ← aquí se ajustan
                    // verificarColumnas(); // ← aquí SÍ tienen tamaño
                  });
            });
  }

  private void cargarConfigUIAsync(Runnable onReady) {

    Task<UIDataConfig> task =
        new Task<>() {
          @Override
          protected UIDataConfig call() throws Exception {
            Path cfgPath =
                Path.of(
                    AppContext.getConfigService()
                        .getRutasConfig(AppContext.getUsuarioActual())
                        .getRutaUIData());
            return UIDataConfig.fromJson(cfgPath);
          }
        };

    task.setOnSucceeded(
        e -> {
          uiCfg = task.getValue();
          onReady.run();
        });

    task.setOnFailed(e -> task.getException().printStackTrace());

    new Thread(task).start();
  }

  private void recalcularColumnas() {
    if (uiCfg == null) {
      //IDebugService.printError("[FxCntrlTablaFCT] uiCfg null → no recalculo columnas");
      return;
    }
    Map<String, Double> nuevos = TableScaler.resizeColumns(treeFct, uiCfg);
    nuevos.forEach(
        (colId, ancho) ->
            uiCfg.updateAncho(colId, (int) Double.parseDouble(String.valueOf(ancho))));
  }

  private void aplicarAnchosDesdeJSON() {
    if (uiCfg == null) {
      //IDebugService.printError("[FxCntrlTablaFCT] uiCfg aún es null, no aplico anchos");
      return;
    }
    treeFct
        .getColumns()
        .forEach(
            col -> {
              double ancho = uiCfg.getAncho(col.getId());
              if (ancho > 0) {
                col.setPrefWidth(ancho);
              }
            });
  }

  private void verificarColumnas() {
    IDebugService.print("=== VERIFICACIÓN COLUMNAS FACTURA ===");
    treeFct
        .getColumns()
        .forEach(
            col -> {
              String id = col.getId();
              double pref = col.getPrefWidth();
              double cfg = uiCfg.getAncho(id);
              IDebugService.print(id + " | Actual=" + pref + " | json=" + cfg);
            });
    IDebugService.print("=====================================");
  }

  private void actualizarDatosPanelControl() {
    TreeItem<Object> root = treeFct.getRoot();
    if (root == null) {
      //IDebugService.printError("[FxCntrlTablaFCT] Root null → no actualizo PanelControl");
      return;
    }
    int numFacturas =
    (int)
    root.getChildren().stream()
    .filter(item -> item.getValue() instanceof FacturaFX)
    .count();
    IDebugService.print("[FxCntrlTablaFCT>actualizarDatosPanelControl] numEntradas: " + numFacturas);
    FxCntrlPanelControl.getPanelControl().setNumFacturasLbl(" " + (numFacturas-1));
  }

  private void configurarColumnas() {

    // =========================
    // COLUMNAS SOLO FACTURA
    // =========================

    colID.setCellValueFactory(
        c -> {
          Object v = c.getValue().getValue();
          if (v instanceof FacturaFX f) return f.idProperty();
          return null;
        });

    colNumFact.setCellValueFactory(
        c -> {
          Object v = c.getValue().getValue();
          if (v instanceof FacturaFX f) return f.numeroProperty();
          return null;
        });

    colFecha.setCellValueFactory(
        c -> {
          Object v = c.getValue().getValue();
          if (v instanceof FacturaFX f) return new SimpleStringProperty(f.getFecha().toString());
          return null;
        });

    colRS.setCellValueFactory(
        c -> {
          Object v = c.getValue().getValue();
          if (v instanceof FacturaFX f) return f.razonSocialProperty();
          return null;
        });

    colConcepto.setCellValueFactory(
        c -> {
          Object v = c.getValue().getValue();
          if (v instanceof FacturaFX f) return f.conceptoProperty();
          return null;
        });

    colDev.setCellValueFactory(
        c -> {
          Object v = c.getValue().getValue();

          if (v instanceof FacturaFX f) return f.devolucionProperty();

          // ExtractoFX → devolver un BooleanProperty falso (pero observable)
          return new SimpleBooleanProperty(false);
        });

    colCantidad.setCellValueFactory(
        c -> {
          Object v = c.getValue().getValue();

          if (v instanceof FacturaFX f) return f.numExtractosProperty();

          if (v instanceof ExtractoFX e) return e.cantidadProperty();

          return null;
        });

    colBaseNI.setCellValueFactory(
        c -> {
          Object v = c.getValue().getValue();
          if (v instanceof FacturaFX f) return f.baseNIProperty();
          return null;
        });

    colTipoRet.setCellValueFactory(
        c -> {
          Object v = c.getValue().getValue();
          if (v instanceof FacturaFX f) return f.retProperty();
          return null;
        });

    colRetenc.setCellValueFactory(
        c -> {
          Object v = c.getValue().getValue();
          if (v instanceof FacturaFX f) return f.retencionesProperty();
          return null;
        });

    colTotal.setCellValueFactory(
        c -> {
          Object v = c.getValue().getValue();
          if (v instanceof FacturaFX f) return f.totalProperty();
          return null;
        });

    colNota.setCellValueFactory(
        param -> {
          Object v = param.getValue().getValue();

          if (v instanceof FacturaFX fx)
            return new SimpleStringProperty(fx.notaExiste() ? "*" : "");

          // ExtractoFX no tiene nota
          return new SimpleStringProperty("");
        });

    // =========================
    // COLUMNAS COMPARTIDAS (FacturaFX + ExtractoFX)
    // =========================

    colBase.setCellValueFactory(
        c -> {
          Object v = c.getValue().getValue();
          if (v instanceof FacturaFX f) return f.baseProperty();
          if (v instanceof ExtractoFX e) return e.baseProperty();
          return null;
        });

    colTipoIVA.setCellValueFactory(
        c -> {
          Object v = c.getValue().getValue();
          if (v instanceof FacturaFX f) return f.tipoIVAProperty();
          if (v instanceof ExtractoFX e) return e.tipoIVAProperty();
          return null;
        });

    colIVA.setCellValueFactory(
        c -> {
          Object v = c.getValue().getValue();
          if (v instanceof FacturaFX f) return f.ivaProperty();
          if (v instanceof ExtractoFX e) return e.ivaProperty();
          return null;
        });

    colST.setCellValueFactory(
        c -> {
          Object v = c.getValue().getValue();
          if (v instanceof FacturaFX f) return f.subtotalProperty();
          if (v instanceof ExtractoFX e) return e.subtotalProperty();
          return null;
        });
  }

  private void configurarColumnasEditables() {

    // ============================
    // NUMERO FACTURA
    // ============================
    colNumFact.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
    colNumFact.setOnEditCommit(
        event -> {
          Object obj = event.getRowValue().getValue();
          if (obj instanceof FacturaFX fx) {
            fx.setNumero(event.getNewValue());
            if (fx.getId() == 0) {
              facturaVaciaEnEdicion = fx;
              //   LogCacheFacturaVacia();
              return;
            }
            actualizarFacturaDesdeTabla(fx);
          }
        });

    // ============================
    // FECHA
    // ============================
    colFecha.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
    colFecha.setOnEditCommit(
        event -> {
          Object obj = event.getRowValue().getValue();
          if (obj instanceof FacturaFX fx) {
            fx.setFecha(Fecha.fromString(event.getNewValue()));
            if (fx.getId() == 0) {
              facturaVaciaEnEdicion = fx;
              //   LogCacheFacturaVacia();
              return;
            }
            actualizarFacturaDesdeTabla(fx);
          }
        });

    // ============================
    // RAZÓN SOCIAL
    // ============================
    colRS.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
    colRS.setOnEditCommit(
        event -> {
          Object obj = event.getRowValue().getValue();
          if (obj instanceof FacturaFX fx) {
            fx.setRazonSocial(event.getNewValue());
            if (fx.getId() == 0) {
              facturaVaciaEnEdicion = fx;
              //   LogCacheFacturaVacia();
              return;
            }
            actualizarFacturaDesdeTabla(fx);
          }
        });

    // ============================
    // CONCEPTO
    // ============================
    colConcepto.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
    colConcepto.setOnEditCommit(
        event -> {
          Object obj = event.getRowValue().getValue();
          if (obj instanceof FacturaFX fx) {
            fx.setConcepto(event.getNewValue());
            if (fx.getId() == 0) {
              facturaVaciaEnEdicion = fx;
              //   LogCacheFacturaVacia();
              return;
            }
            actualizarFacturaDesdeTabla(fx);
          }
        });

    // ============================
    // DEVOLUCIÓN (boolean)
    // ============================

    colDev.setCellFactory(CheckBoxTreeTableCell.forTreeTableColumn(colDev));
    colDev.setOnEditCommit(
        event -> {
          Object obj = event.getRowValue().getValue();
          if (obj instanceof FacturaFX fx) {
            fx.setDevolucion(event.getNewValue());
            if (fx.getId() == 0) {
              facturaVaciaEnEdicion = fx;
              //   LogCacheFacturaVacia();
              return;
            }
            actualizarFacturaDesdeTabla(fx);
          }
        });

    // ============================
    // NOTA
    // ============================
    colNota.setCellFactory(
        column ->
            new TreeTableCell<>() {
              @Override
              protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null || item.isBlank()) {
                  setText(null);
                  setTooltip(null);
                  setCursor(Cursor.DEFAULT); // ← volver al cursor normal
                  return;
                }

                setText("*");

                Object obj = getTableRow().getItem(); // API moderna

                if (obj instanceof FacturaFX fx && fx.notaExiste()) {
                  setTooltip(new Tooltip(fx.getNota()));
                  setCursor(Cursor.OPEN_HAND); // ← CURSOR DISTINTO
                } else {
                  setTooltip(null);
                  setCursor(Cursor.DEFAULT);
                }
              }
            });

    colNota.setOnEditCommit(
        event -> {
          Object obj = event.getRowValue().getValue();
          if (obj instanceof FacturaFX fx) {
            fx.setNota(event.getNewValue());
            if (fx.getId() == 0) {
              facturaVaciaEnEdicion = fx;
              //   LogCacheFacturaVacia();
              return;
            }
            actualizarFacturaDesdeTabla(fx);
          }
        });

    // ============================
    // BASE (solo FacturaFX)
    // ============================
    colBase.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn(new NumberStringConverter()));
    colBase.setOnEditCommit(
        event -> {
          Object obj = event.getRowValue().getValue();
          if (obj instanceof FacturaFX fx) {
            Number n = event.getNewValue();
            fx.setBase(n.doubleValue());
            if (fx.getId() == 0) {
              facturaVaciaEnEdicion = fx;
              //   LogCacheFacturaVacia();
              return;
            }
            actualizarFacturaDesdeTabla(fx);
          }
        });

    // ============================
    // TIPO IVA
    // ============================
    colTipoIVA.setCellFactory(
        TextFieldTreeTableCell.forTreeTableColumn(new NumberStringConverter()));
    colTipoIVA.setOnEditCommit(
        event -> {
          Object obj = event.getRowValue().getValue();
          if (obj instanceof FacturaFX fx) {
            Number n = event.getNewValue();
            fx.setTipoIVA(n.intValue());
            if (fx.getId() == 0) {
              facturaVaciaEnEdicion = fx;
              //   LogCacheFacturaVacia();
              return;
            }
            actualizarFacturaDesdeTabla(fx);
          }
        });

    // ============================
    // IVA
    // ============================
    colIVA.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn(new NumberStringConverter()));
    colIVA.setOnEditCommit(
        event -> {
          Object obj = event.getRowValue().getValue();
          if (obj instanceof FacturaFX fx) {
            Number n = event.getNewValue();
            fx.setIVA(n.doubleValue());
            if (fx.getId() == 0) {
              facturaVaciaEnEdicion = fx;
              //   LogCacheFacturaVacia();
              return;
            }
            actualizarFacturaDesdeTabla(fx);
          }
        });

    // ============================
    // SUBTOTAL
    // ============================
    colST.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn(new NumberStringConverter()));
    colST.setOnEditCommit(
        event -> {
          Object obj = event.getRowValue().getValue();
          if (obj instanceof FacturaFX fx) {
            Number n = event.getNewValue();
            fx.setSubtotal(n.doubleValue());
            if (fx.getId() == 0) {
              facturaVaciaEnEdicion = fx;
              //   LogCacheFacturaVacia();
              return;
            }
            actualizarFacturaDesdeTabla(fx);
          }
        });

    // ============================
    // BASE NI
    // ============================
    colBaseNI.setCellFactory(
        TextFieldTreeTableCell.forTreeTableColumn(new NumberStringConverter()));
    colBaseNI.setOnEditCommit(
        event -> {
          Object obj = event.getRowValue().getValue();
          if (obj instanceof FacturaFX fx) {
            Number n = event.getNewValue();
            fx.setBaseNI(n.doubleValue());
            if (fx.getId() == 0) {
              facturaVaciaEnEdicion = fx;
              //   LogCacheFacturaVacia();
              return;
            }
            actualizarFacturaDesdeTabla(fx);
          }
        });

    // ============================
    // TIPO RET
    // ============================
    colTipoRet.setCellFactory(
        TextFieldTreeTableCell.forTreeTableColumn(new NumberStringConverter()));
    colTipoRet.setOnEditCommit(
        event -> {
          Object obj = event.getRowValue().getValue();
          if (obj instanceof FacturaFX fx) {
            Number n = event.getNewValue();
            fx.setRet(n.intValue());
            if (fx.getId() == 0) {
              facturaVaciaEnEdicion = fx;
              //   LogCacheFacturaVacia();
              return;
            }
            actualizarFacturaDesdeTabla(fx);
          }
        });

    // ============================
    // RETENCIONES
    // ============================
    colRetenc.setCellFactory(
        TextFieldTreeTableCell.forTreeTableColumn(new NumberStringConverter()));
    colRetenc.setOnEditCommit(
        event -> {
          Object obj = event.getRowValue().getValue();
          if (obj instanceof FacturaFX fx) {
            Number n = event.getNewValue();
            fx.setRetenciones(n.doubleValue());
            if (fx.getId() == 0) {
              facturaVaciaEnEdicion = fx;
              //   LogCacheFacturaVacia();
              return;
            }
            actualizarFacturaDesdeTabla(fx);
          }
        });

    // ============================
    // TOTAL
    // ============================
    colTotal.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn(new NumberStringConverter()));
    colTotal.setOnEditCommit(
        event -> {
          Object obj = event.getRowValue().getValue();
          if (obj instanceof FacturaFX fx) {
            Number n = event.getNewValue();
            fx.setTotal(n.doubleValue());
            if (fx.getId() == 0) {
              facturaVaciaEnEdicion = fx;
              //   LogCacheFacturaVacia();
              return;
            }
            actualizarFacturaDesdeTabla(fx);
          }
        });
  }

  private void configurarAcciones() {

    colAcciones.setCellFactory(
        col ->
            new TreeTableCell<Object, Void>() {

              private final Button btnAddFactura = new Button("+FCT");
              // private final Button btnAddExtracto = new Button("+Extracto");
              private final Button btnBorrar = new Button(" - ");
              private final HBox contFacturaVacia = new HBox(5, btnAddFactura);
              private final HBox contFactura = new HBox(5, btnBorrar);

              // private final HBox contExtracto = new HBox(5, btnBorrar);

              {
                // +Factura → insertar nueva factura
                btnAddFactura.setOnAction(
                    e -> {
                      Object rowItem = getTreeTableView().getTreeItem(getIndex()).getValue();
                      if (!(rowItem instanceof FacturaFX fx)) return;
                      //   LogCacheFacturaVacia();
                      insertarFacturaDesdeTabla(fx);
                      facturaVaciaEnEdicion = FacturaFX.filaVacia();
                      cargarDatos();
                    });

                // +Extracto → insertar extracto en factura
                /*
                btnAddExtracto.setOnAction(
                        e -> {
                            Object rowItem = getTreeTableView().getTreeItem(getIndex()).getValue();
                            if (!(rowItem instanceof FacturaFX fxFactura)) return;
                            ExtractoFX nuevo = crearExtractoVacio();
                            insertarExtractoDesdeTabla(fxFactura, nuevo);
                        });
                    */
                // Borrar → según si es factura o extracto
                btnBorrar.setOnAction(
                    e -> {
                      Object rowItem = getTreeTableView().getTreeItem(getIndex()).getValue();
                      if (rowItem instanceof FacturaFX fxFactura) {
                        borrarFacturaDesdeTabla(fxFactura);
                      } else if (rowItem instanceof ExtractoFX fxExtracto) {
                        borrarExtractoDesdeTabla(fxExtracto);
                      }
                    });
              }

              @Override
              protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                  setGraphic(null);
                  return;
                }

                @SuppressWarnings("deprecation")
                TreeItem<Object> treeItem = getTreeTableRow().getTreeItem();
                if (treeItem == null) {
                  setGraphic(null);
                  return;
                }

                Object value = treeItem.getValue();
                if (value == null) {
                  setGraphic(null);
                  return;
                }

                // 1) Fila vacía → +Factura
                if (value instanceof FacturaFX fx && fx.getId() == 0) {
                  setGraphic(contFacturaVacia); // SOLO +Factura
                  return;
                }

                // 2) Factura normal → Borrar
                if (value instanceof FacturaFX) {
                  setGraphic(contFactura); // SOLO Borrar
                  return;
                }

                // 3) Extracto → nada
                if (value instanceof ExtractoFX) {
                  setGraphic(null);
                  return;
                }

                setGraphic(null);
              }
            });
  }

  private void configurarEscalado() {
    Platform.runLater(
        () -> {
            treeFct.getColumns().forEach(col -> col.setSortable(false));
            if (uiCfg != null) {
                TableScaler.resizeColumns(treeFct, uiCfg);
            }
        }
    );
  }

  private void actualizarTotales(List<FacturaFX> facturas) {

    // ✔ Normalizar TODAS las facturas antes de sumar
    facturas.forEach(FacturaFX::normalizarSignos);

    double totalBase = facturas.stream().mapToDouble(FacturaFX::getBase).sum();
    double totalIVA = facturas.stream().mapToDouble(FacturaFX::getIVA).sum();
    double totalST = facturas.stream().mapToDouble(FacturaFX::getSubtotal).sum();
    double totalNI = facturas.stream().mapToDouble(FacturaFX::getBaseNI).sum();
    double totalRet = facturas.stream().mapToDouble(FacturaFX::getRetenciones).sum();
    double totalTot = facturas.stream().mapToDouble(FacturaFX::getTotal).sum();

    lblBase.setText(String.format("%.2f", totalBase));
    lblIVA.setText(String.format("%.2f", totalIVA));
    lblST.setText(String.format("%.2f", totalST));
    lblBaseNI.setText(String.format("%.2f", totalNI));
    lblRetenc.setText(String.format("%.2f", totalRet));
    lblTotal.setText(String.format("%.2f", totalTot));
    lblNumFact.setText(String.valueOf(facturas.size()));
  }

  public void cargarDatos() {

    Task<List<FacturaFX>> task =
        new Task<>() {
          @Override
          protected List<FacturaFX> call() throws Exception {
            return AppContext.get().fact().leerFacturas().stream()
                .map(FacturaFX::fromDomain)
                .collect(Collectors.toList());
          }
        };

    task.setOnSucceeded(
        e -> {
          List<FacturaFX> facturas = task.getValue();

          // 1) Ordenar
          facturas.sort(Comparator.comparingInt(FacturaFX::getId));

          // 2) Crear raíz
          TreeItem<Object> root = new TreeItem<>();
          root.setExpanded(true);

          // 3) Añadir facturas y extractos
          for (FacturaFX f : facturas) {

              f.devolucionProperty()
              .addListener(
                  (obs, oldVal, newVal) -> {
                      f.aplicarDevolucionEnCascada();
                    });

            TreeItem<Object> nodoFactura = new TreeItem<>(f);

            for (ExtractoFX extr : f.getExtractos()) {
                nodoFactura.getChildren().add(new TreeItem<>(extr));
            }

            root.getChildren().add(nodoFactura);
        }

        // 4) Fila vacía
        TreeItem<Object> filaVacia = new TreeItem<>(FacturaFX.filaVacia());
        root.getChildren().add(filaVacia);

        // 5) Aplicar a la tabla
        treeFct.setRoot(root);
        treeFct.setShowRoot(false);

        // 6) Actualizar los datos en el PanelControl
              IDebugService.print("[FxCntrlTablaFCT>cargarDatos] Actualizando datos en el PanelControl");
                actualizarDatosPanelControl();
              
        // 7) Totales
        actualizarTotales(facturas);
    });

    task.setOnFailed(e -> task.getException().printStackTrace());

    new Thread(task).start();
  }

  // region EVENTOS FXML
  @FXML
  private void btnVisorFctPulsado(ActionEvent ev) {
    IDebugService.print("[FxCntrlTablaFCT] VISOR pulsado");
  }

  @FXML
  private void btnNuevaFctPulsado(ActionEvent ev) {
    IDebugService.print("[FxCntrlTablaFCT] NUEVA FCT pulsado");
  }

  @FXML
  private void btnEditarFctPulsado(ActionEvent ev) {
    IDebugService.print("[FxCntrlTablaFCT] EDITAR pulsado");
  }

  @FXML
  private void btnFiltrosFctPulsado(ActionEvent ev) {
    IDebugService.print("[FxCntrlTablaFCT] FILTROS pulsado");
  }

  @FXML
  private void btnBorrarFctPulsado(ActionEvent ev) {
    IDebugService.print("[FxCntrlTablaFCT] BORRAR pulsado");
  }

  @FXML
  private void btnScanFctPulsado(ActionEvent ev) {
    IDebugService.print("[FxCntrlTablaFCT] SCAN pulsado");
  }

  @FXML
  private void btnImprimirFctPulsado(ActionEvent ev) {
    IDebugService.print("[FxCntrlTablaFCT] IMPRIMIR pulsado");
  }

  // endregion

  // region OPERACIONES DESDE TABLA

  private void actualizarFacturaDesdeTabla(FacturaFX fx) {

    // CASO 1: Es la fila vacía → INSERTAR FACTURA NUEVA
    if (fx.getId() == 0) {

      // 1) Generar ID nueva
      int nuevaID = AppContext.get().fact().generarID();
      fx.setId(nuevaID);

      // 2) Convertir a dominio
      Factura nueva = fx.toDomain();

      // 3) Guardar en CSV
      AppContext.get().fact().introducirFactura(nueva);

      // 4) Crear nueva fila vacía (ID=0)
      facturaVaciaEnEdicion = FacturaFX.filaVacia();

      // 5) Recargar tabla (ya con la nueva fila vacía)
      cargarDatos();

      return;
    }

    // CASO 2: Es una factura real → EDITAR
    Factura factura = fx.toDomain();
    boolean ok = AppContext.get().fact().editarFactura(factura);

    if (!ok) {
      IDebugService.printError(
          "[FxCntrlTablaFCT] No se pudo actualizar la factura " + factura.getID());
    }
  }

  private void insertarFacturaDesdeTabla(FacturaFX fx) {

    // 1. Convertir FX → dominio
    fx.setId(AppContext.get().fact().generarID());
    Factura factura = fx.toDomain();
    // vaciar la Factura en memoria
    // TODO : 26-05-09 : Esto hará que cuando edites otra factura se pierda la edición de la
    // filaVaciaEnEDicion
    facturaVaciaEnEdicion = FacturaFX.filaVacia();

    // 2. Guardar en CSV
    boolean ok = AppContext.get().fact().introducirFactura(factura);

    if (!ok) {
      IDebugService.printError("No se pudo insertar la factura " + factura.getID());
      return;
    }

    // 3. Añadir a la tabla
    TreeItem<Object> item = new TreeItem<>(fx);
    treeFct.getRoot().getChildren().add(item);
  }

  private void borrarFacturaDesdeTabla(FacturaFX fxFactura) {
    // 1) Llamar al servicio de dominio
    boolean ok = AppContext.get().fact().borrarFactura(fxFactura.toDomain());
    if (!ok) {
      IDebugService.printError("No se pudo borrar la factura");
      return;
    }

    // 2) Quitar nodo del árbol
    TreeItem<Object> root = treeFct.getRoot();
    TreeItem<Object> nodoABorrar = null;

    for (TreeItem<Object> item : root.getChildren()) {
      if (item.getValue() == fxFactura) {
        nodoABorrar = item;
        break;
      }
    }

    if (nodoABorrar != null) {
      root.getChildren().remove(nodoABorrar);
    }
  }

  private void insertarExtractoDesdeTabla(FacturaFX fxFactura, ExtractoFX fxExtracto) {

    // 1) Añadir extracto a la factura FX
    fxFactura.getExtractos().add(fxExtracto);

    // 2) Añadir nodo hijo en la tabla
    TreeItem<Object> nodoFactura = buscarNodo(fxFactura);
    if (nodoFactura != null) {
      nodoFactura.getChildren().add(new TreeItem<>(fxExtracto));
    }

    // 3) Guardar factura actualizada en el CSV
    Factura factura = fxFactura.toDomain();
    AppContext.get().fact().introducirFactura(factura);
  }

  private void borrarExtractoDesdeTabla(ExtractoFX fxExtracto) {

    // 1) Encontrar la factura padre
    TreeItem<Object> root = treeFct.getRoot();
    FacturaFX fxFacturaPadre = null;
    TreeItem<Object> nodoExtracto = null;

    for (TreeItem<Object> itemFactura : root.getChildren()) {
      if (!(itemFactura.getValue() instanceof FacturaFX fxF)) continue;

      for (TreeItem<Object> itemExt : itemFactura.getChildren()) {
        if (itemExt.getValue() == fxExtracto) {
          fxFacturaPadre = fxF;
          nodoExtracto = itemExt;
          break;
        }
      }
      if (fxFacturaPadre != null) break;
    }

    if (fxFacturaPadre == null || nodoExtracto == null) return;

    // 2) Quitar de la lista de extractos de la factura FX
    fxFacturaPadre.getExtractos().remove(fxExtracto);

    // 3) Quitar nodo del árbol
    TreeItem<Object> nodoFactura = buscarNodo(fxFacturaPadre);
    if (nodoFactura != null) {
      nodoFactura.getChildren().remove(nodoExtracto);
    }

    // 4) Guardar factura actualizada
    AppContext.get().fact().introducirFactura(fxFacturaPadre.toDomain());
  }

  // endregion

  private ExtractoFX crearExtractoVacio() {
    return new ExtractoFX(
        0.0, // base
        21, // IVA
        0.0, // cuota
        0.0, // total
        "Nuevo extracto");
  }

  private TreeItem<Object> buscarNodo(FacturaFX fx) {
    for (TreeItem<Object> item : treeFct.getRoot().getChildren()) {
      if (item.getValue() == fx) {
        return item;
      }
    }
    return null;
  }

  private void LogCacheFacturaVacia() {
    IDebugService.print("[FxCntrlTablaFCT>LogCacheFacturaVacia] facturaVaciaEnEdicion ahora es:");
    IDebugService.print("  Numero: " + facturaVaciaEnEdicion.getNumero());
    IDebugService.print("  Fecha: " + facturaVaciaEnEdicion.getFecha());
    IDebugService.print("  RS: " + facturaVaciaEnEdicion.getRazonSocial());
    IDebugService.print("  Concepto: " + facturaVaciaEnEdicion.getConcepto());
    IDebugService.print("  Base: " + facturaVaciaEnEdicion.getBase());
    IDebugService.print("  Total: " + facturaVaciaEnEdicion.getTotal());
    IDebugService.print("  Nota: " + facturaVaciaEnEdicion.getNota());
  }
}
