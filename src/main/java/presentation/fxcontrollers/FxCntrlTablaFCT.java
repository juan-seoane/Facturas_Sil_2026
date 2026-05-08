package presentation.fxcontrollers;

import app.core.AppContext;
import domain.records.Factura;
import domain.records.Fecha;
import infraestructure.servicios.config.Config;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.layout.HBox;
import javafx.util.converter.NumberStringConverter;
import presentation.config.UIDataConfig;
import presentation.helpers.TableScaler;
import presentation.viewmodels.ExtractoFX;
import presentation.viewmodels.FacturaFX;

public class FxCntrlTablaFCT implements Initializable {

    @FXML private TreeTableView<Object> treeFct;

    @FXML private TreeTableColumn<FacturaFX, Number> colID;
    @FXML private TreeTableColumn<FacturaFX, String> colNumFact;
    @FXML private TreeTableColumn<FacturaFX, String> colFecha;
    @FXML private TreeTableColumn<FacturaFX, String> colRS;
    @FXML private TreeTableColumn<Object, String> colConcepto;
    @FXML private TreeTableColumn<FacturaFX, Boolean> colDev;
    @FXML private TreeTableColumn<FacturaFX, Number> colNumExtr;

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

        cargarConfigUI();
        configurarColumnas();
        configurarColumnasEditables(); // ← añadimos edición SIN tocar nada
        cargarDatos();
        configurarAcciones();
        configurarEscalado();


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
                                            verificarColumnas(); // ← aquí SÍ tienen tamaño
                                            actualizarDatosPanelControl();
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
                                        verificarColumnas(); // ← aquí SÍ tienen tamaño
                                    });
                        });
    }

    private void cargarConfigUI() {
        cfgPath = Path.of(Config.getConfig(Config.usuario).getRutasconfig().getRutaUIData());
        try {
            uiCfg = UIDataConfig.fromJson(cfgPath);
            // System.out.println("[FxCntrlTablaFCT>cargarConfigUI] UIDataConfig cargado:");
            // System.out.println(uiCfg);
        } catch (IOException e) {
      System.out.println(
          "[FxCntrlTablaFCT>initialize] Error " + e.getClass() + " al cargar la UIDataConfig");
        }
    }

    private void recalcularColumnas() {
        Map<String, Double> nuevos = TableScaler.resizeColumns(treeFct, uiCfg);
        nuevos.forEach((colId, ancho) -> uiCfg.updateAncho(colId, ancho));
    }

    private void aplicarAnchosDesdeJSON() {
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
        System.out.println("=== VERIFICACIÓN COLUMNAS FACTURA ===");
        treeFct
        .getColumns()
        .forEach(
            col -> {
                String id = col.getId();
                double pref = col.getPrefWidth();
                double cfg = uiCfg.getAncho(id);
                System.out.println(id + " | Actual=" + pref + " | json=" + cfg);
            });
            System.out.println("=====================================");
        }

    private void actualizarDatosPanelControl() {
        int numFacturas = treeFct.getRoot().getChildren().size();

       FxCntrlPanelControl.getPanelControl().setNumFacturasLbl(String.valueOf(numFacturas));

        // si quieres contar extractos:
        // long numExtractos =
        //     treeFct.getRoot().getChildren().stream().flatMap(f -> f.getChildren().stream()).count();

        // si quieres totales:
        // actualizarTotales(); // si ya tienes este método
    }

    private void configurarColumnas() {

        // =========================
        // COLUMNAS SOLO FACTURA
        // =========================

        colID.setCellValueFactory(
                c -> {
                    Object v = c.getValue().getValue();
                    if (v instanceof FacturaFX f)
                        return f.idProperty();
                    return null;
                });

        colNumFact.setCellValueFactory(
                c -> {
                    Object v = c.getValue().getValue();
                    if (v instanceof FacturaFX f)
                        return f.numeroProperty();
                    return null;
                });

        colFecha.setCellValueFactory(
                c -> {
                    Object v = c.getValue().getValue();
                    if (v instanceof FacturaFX f)
                        return new SimpleStringProperty(f.getFecha().toString());
                    return null;
                });

        colRS.setCellValueFactory(
                c -> {
                    Object v = c.getValue().getValue();
                    if (v instanceof FacturaFX f)
                        return f.razonSocialProperty();
                    return null;
                });

        colConcepto.setCellValueFactory(
                c -> {
                    Object v = c.getValue().getValue();
                    if (v instanceof FacturaFX f)
                        return f.conceptoProperty();
                    return null;
                });

        colDev.setCellValueFactory(
                c -> {
                    Object v = c.getValue().getValue();
                    if (v instanceof FacturaFX f)
                        return f.devolucionProperty().asObject();
                    return null;
                });

        colNumExtr.setCellValueFactory(
                c -> {
                    Object v = c.getValue().getValue();
                    if (v instanceof FacturaFX f)
                        return f.numExtractosProperty();
                    return null;
                });

        colBaseNI.setCellValueFactory(
                c -> {
                    Object v = c.getValue().getValue();
                    if (v instanceof FacturaFX f)
                        return f.baseNIProperty();
                    return null;
                });

        colTipoRet.setCellValueFactory(
                c -> {
                    Object v = c.getValue().getValue();
                    if (v instanceof FacturaFX f)
                        return f.retProperty();
                    return null;
                });

        colRetenc.setCellValueFactory(
                c -> {
                    Object v = c.getValue().getValue();
                    if (v instanceof FacturaFX f)
                        return f.retencionesProperty();
                    return null;
                });

        colTotal.setCellValueFactory(
                c -> {
                    Object v = c.getValue().getValue();
                    if (v instanceof FacturaFX f)
                        return f.totalProperty();
                    return null;
                });

        colNota.setCellValueFactory(
                c -> {
                    Object v = c.getValue().getValue();
                    if (v instanceof FacturaFX f)
                        return f.notaProperty();
                    return null;
                });

        // =========================
        // COLUMNAS COMPARTIDAS (FacturaFX + ExtractoFX)
        // =========================

        colBase.setCellValueFactory(
                c -> {
                    Object v = c.getValue().getValue();
                    if (v instanceof FacturaFX f)
                        return f.baseProperty();
                    if (v instanceof ExtractoFX e)
                        return e.baseProperty();
                    return null;
                });

        colTipoIVA.setCellValueFactory(
                c -> {
                    Object v = c.getValue().getValue();
                    if (v instanceof FacturaFX f)
                        return f.tipoIVAProperty();
                    if (v instanceof ExtractoFX e)
                        return e.tipoIVAProperty();
                    return null;
                });

        colIVA.setCellValueFactory(
                c -> {
                    Object v = c.getValue().getValue();
                    if (v instanceof FacturaFX f)
                        return f.ivaProperty();
                    if (v instanceof ExtractoFX e)
                        return e.ivaProperty();
                    return null;
                });

        colST.setCellValueFactory(
                c -> {
                    Object v = c.getValue().getValue();
                    if (v instanceof FacturaFX f)
                        return f.subtotalProperty();
                    if (v instanceof ExtractoFX e)
                        return e.subtotalProperty();
                    return null;
                });
    }

    private void configurarColumnasEditables() {

        // NUMERO FACTURA
        colNumFact.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
        colNumFact.setOnEditCommit(
                event -> {
                    Object obj = event.getRowValue().getValue();
                    if (!(obj instanceof FacturaFX fx))
                        return;
                    fx.setNumero(event.getNewValue());
                    if (fx.getId() == 0) {
                        facturaVaciaEnEdicion = fx;
                        return;
                    }

                    LogCacheFacturaVacia();
                    actualizarFacturaDesdeTabla(fx);
                });

        // FECHA
        colFecha.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
        colFecha.setOnEditCommit(
                event -> {
                    Object obj = event.getRowValue().getValue();
                    if (!(obj instanceof FacturaFX fx))
                        return;

                    fx.setFecha(Fecha.fromString(event.getNewValue()));
                    if (fx.getId() == 0) {
                        facturaVaciaEnEdicion = fx;
                        return;
                    }

                    LogCacheFacturaVacia();
                    actualizarFacturaDesdeTabla(fx);
                });

        // RAZÓN SOCIAL
        colRS.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
        colRS.setOnEditCommit(
                event -> {
                    Object obj = event.getRowValue().getValue();
                    if (!(obj instanceof FacturaFX fx))
                        return;

                    fx.setRazonSocial(event.getNewValue());
                    if (fx.getId() == 0) {
                        facturaVaciaEnEdicion = fx;
                        return;
                    }

                    LogCacheFacturaVacia();
                    actualizarFacturaDesdeTabla(fx);
                });

        // CONCEPTO
        colConcepto.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
    colConcepto.setOnEditCommit(
        event -> {
          Object obj = event.getRowValue().getValue();
          if (!(obj instanceof FacturaFX fx)) return;

          fx.setConcepto(event.getNewValue());
          if (fx.getId() == 0) {
            facturaVaciaEnEdicion = fx;
            return;
          }

          LogCacheFacturaVacia();
          actualizarFacturaDesdeTabla(fx);
        });

    // DEVOLUCIÓN (boolean)
    colDev.setCellValueFactory(param -> param.getValue().getValue().devolucionProperty());

    colDev.setCellFactory(CheckBoxTreeTableCell.forTreeTableColumn(colDev));

        colDev.setOnEditCommit(
                event -> {
                    Object obj = event.getRowValue().getValue();
                    if (!(obj instanceof FacturaFX fx))
                        return;

                    fx.setDevolucion(event.getNewValue());
                    if (fx.getId() == 0) {
                        facturaVaciaEnEdicion = fx;
                        return;
                    }

                    LogCacheFacturaVacia();
                    actualizarFacturaDesdeTabla(fx);
                });

        // NOTA
        colNota.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
        colNota.setOnEditCommit(
                event -> {
                    Object obj = event.getRowValue().getValue();
                    if (!(obj instanceof FacturaFX fx))
                        return;

                    fx.setNota(event.getNewValue());
                    if (fx.getId() == 0) {
                        facturaVaciaEnEdicion = fx;
                        return;
                    }

                    LogCacheFacturaVacia();
                    actualizarFacturaDesdeTabla(fx);
                });

        // BASE (solo FacturaFX)
        colBase.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn(new NumberStringConverter()));
        colBase.setOnEditCommit(
                event -> {
                    Object obj = event.getRowValue().getValue();
                    if (!(obj instanceof FacturaFX fx))
                        return;

                    Number n = event.getNewValue();

                    fx.setBase(n.doubleValue());

                    if (fx.getId() == 0) {
                        facturaVaciaEnEdicion = fx;
                        return;
                    }

                    LogCacheFacturaVacia();
                    actualizarFacturaDesdeTabla(fx);
                });

        // TIPO IVA
        colTipoIVA.setCellFactory(
                TextFieldTreeTableCell.forTreeTableColumn(new NumberStringConverter()));
    colTipoIVA.setOnEditCommit(
        event -> {
          Object obj = event.getRowValue().getValue();
          if (!(obj instanceof FacturaFX fx)) return;

          Number n = event.getNewValue();
          fx.setTipoIVA(n.intValue());

          if (fx.getId() == 0) {
            facturaVaciaEnEdicion = fx;
            return;
          }

          LogCacheFacturaVacia();
          actualizarFacturaDesdeTabla(fx);
        });
        // IVA
        colIVA.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn(new NumberStringConverter()));
        colIVA.setOnEditCommit(
                event -> {
                    Object obj = event.getRowValue().getValue();
                    if (!(obj instanceof FacturaFX fx))
                        return;

                    Number n = event.getNewValue();
                    fx.setIVA(n.doubleValue());

                    if (fx.getId() == 0) {
                        facturaVaciaEnEdicion = fx;
                        return;
                    }

                    LogCacheFacturaVacia();
                    actualizarFacturaDesdeTabla(fx);
                });

        // SUBTOTAL
        colST.setCellFactory(
                TextFieldTreeTableCell.forTreeTableColumn(new NumberStringConverter()));
        colST.setOnEditCommit(
                event -> {
                    Object obj = event.getRowValue().getValue();
                    if (!(obj instanceof FacturaFX fx))
                        return;

                    Number n = event.getNewValue();
                    fx.setSubtotal(n.doubleValue());

                    if (fx.getId() == 0) {
                        facturaVaciaEnEdicion = fx;
                        return;
                    }

                    LogCacheFacturaVacia();
                    actualizarFacturaDesdeTabla(fx);
                });

        // BASE NI
        colBaseNI.setCellFactory(
                TextFieldTreeTableCell.forTreeTableColumn(new NumberStringConverter()));
        colBaseNI.setOnEditCommit(
                event -> {
                    Object obj = event.getRowValue().getValue();
                    if (!(obj instanceof FacturaFX fx))
                        return;

                    Number n = event.getNewValue();
                    fx.setBaseNI(n.doubleValue());

                    if (fx.getId() == 0) {
                        facturaVaciaEnEdicion = fx;
                        return;
                    }

                    LogCacheFacturaVacia();
                    actualizarFacturaDesdeTabla(fx);
                });

        // TIPO RET
        colTipoRet.setCellFactory(
                TextFieldTreeTableCell.forTreeTableColumn(new NumberStringConverter()));
        colTipoRet.setOnEditCommit(
                event -> {
                    Object obj = event.getRowValue().getValue();
                    if (!(obj instanceof FacturaFX fx))
                        return;

                    Number n = event.getNewValue();
                    fx.setRet(n.intValue());

                    if (fx.getId() == 0) {
                        facturaVaciaEnEdicion = fx;
                        return;
                    }

                    LogCacheFacturaVacia();
                    actualizarFacturaDesdeTabla(fx);
                });

        // RETENCIONES
        colRetenc.setCellFactory(
                TextFieldTreeTableCell.forTreeTableColumn(new NumberStringConverter()));
        colRetenc.setOnEditCommit(
                event -> {
                    Object obj = event.getRowValue().getValue();
                    if (!(obj instanceof FacturaFX fx))
                        return;

                    Number n = event.getNewValue();
                    fx.setRetenciones(n.doubleValue());

                    if (fx.getId() == 0) {
                        facturaVaciaEnEdicion = fx;
                        return;
                    }

                    LogCacheFacturaVacia();
                    actualizarFacturaDesdeTabla(fx);
                });

        // TOTAL
        colTotal.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn(new NumberStringConverter()));
        colTotal.setOnEditCommit(event -> {
            Object obj = event.getRowValue().getValue();
            if (!(obj instanceof FacturaFX fx))
                return;

            Number n = event.getNewValue();
            fx.setTotal(n.doubleValue());

            if (fx.getId() == 0) {
                facturaVaciaEnEdicion = fx;
                return;
            }

            LogCacheFacturaVacia();
            actualizarFacturaDesdeTabla(fx);
        });
    }

    private void configurarAcciones() {

    colAcciones.setCellFactory(
        col ->
            new TreeTableCell<Object, Void>() {

              private final Button btnAddFactura = new Button("+Factura");
              // private final Button btnAddExtracto = new Button("+Extracto");
              private final Button btnBorrar = new Button("-Borrar");
              private final HBox contFacturaVacia = new HBox(5, btnAddFactura);
              private final HBox contFactura = new HBox(5, btnBorrar);

              // private final HBox contExtracto = new HBox(5, btnBorrar);

              {
                // +Factura → insertar nueva factura
                btnAddFactura.setOnAction(
                    e -> {
                      Object rowItem = getTreeTableView().getTreeItem(getIndex()).getValue();
                      if (!(rowItem instanceof FacturaFX fx)) return;
                      LogCacheFacturaVacia();
                      insertarFacturaDesdeTabla(fx); // o crearFacturaVacia() y usar servicio
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
                TableScaler.resizeColumns(treeFct, uiCfg);
            });
        }

    private void actualizarTotales(List<FacturaFX> facturas) {

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

    // 1) Leer facturas
    List<FacturaFX> facturas = new ArrayList<>(AppContext.get().fact().leerFacturasFX());

    // 2) Ordenar por ID
    facturas.sort(Comparator.comparingInt(FacturaFX::getId));

    // 3) Crear raíz
    TreeItem<Object> root = new TreeItem<>();
    root.setExpanded(true);

    // 4) Añadir facturas y extractos
    for (FacturaFX f : facturas) {

      TreeItem<Object> nodoFactura = new TreeItem<>(f);

      for (ExtractoFX extr : f.getExtractos()) {
        nodoFactura.getChildren().add(new TreeItem<>(extr));
      }

      root.getChildren().add(nodoFactura);
    }

    // 5) Añadir fila vacía SIEMPRE al final
    TreeItem<Object> filaVacia = new TreeItem<>(FacturaFX.filaVacia());
    root.getChildren().add(filaVacia);

    // 6) Aplicar a la tabla
    treeFct.setRoot(root);
    treeFct.setShowRoot(false);

    // 7) Totales
    actualizarTotales(facturas);
  }

    //region EVENTOS FXML
    @FXML
    private void btnVisorFctPulsado(ActionEvent ev) {
        System.out.println("[FxCntrlTablaFCT] VISOR pulsado");
    }

    @FXML
    private void btnNuevaFctPulsado(ActionEvent ev) {
        System.out.println("[FxCntrlTablaFCT] NUEVA FCT pulsado");
    }

    @FXML
    private void btnEditarFctPulsado(ActionEvent ev) {
        System.out.println("[FxCntrlTablaFCT] EDITAR pulsado");
    }

    @FXML
    private void btnFiltrosFctPulsado(ActionEvent ev) {
        System.out.println("[FxCntrlTablaFCT] FILTROS pulsado");
    }

    @FXML
    private void btnBorrarFctPulsado(ActionEvent ev) {
        System.out.println("[FxCntrlTablaFCT] BORRAR pulsado");
    }

    @FXML
    private void btnScanFctPulsado(ActionEvent ev) {
        System.out.println("[FxCntrlTablaFCT] SCAN pulsado");
    }

    @FXML
    private void btnImprimirFctPulsado(ActionEvent ev) {
        System.out.println("[FxCntrlTablaFCT] IMPRIMIR pulsado");
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
        System.err.println("[FxCntrlTablaFCT] No se pudo actualizar la factura " + factura.getID());
        }
    }

    private void insertarFacturaDesdeTabla(FacturaFX fx) {

        // 1. Convertir FX → dominio
        Factura factura = fx.toDomain();
        fx.setId(AppContext.get().fact().generarID());

        // 2. Guardar en CSV
        boolean ok = AppContext.get().fact().introducirFactura(factura);

        if (!ok) {
            System.err.println("No se pudo insertar la factura " + factura.getID());
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
            System.err.println("No se pudo borrar la factura");
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
            if (!(itemFactura.getValue() instanceof FacturaFX fxF))
                continue;

            for (TreeItem<Object> itemExt : itemFactura.getChildren()) {
                if (itemExt.getValue() == fxExtracto) {
                    fxFacturaPadre = fxF;
                    nodoExtracto = itemExt;
                    break;
                }
            }
            if (fxFacturaPadre != null)
                break;
        }

        if (fxFacturaPadre == null || nodoExtracto == null)
            return;

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

    //endregion

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
        System.out.println("[DEBUG] facturaVaciaEnEdicion ahora es:");
        System.out.println("  Numero: " + facturaVaciaEnEdicion.getNumero());
        System.out.println("  Fecha: " + facturaVaciaEnEdicion.getFecha());
        System.out.println("  RS: " + facturaVaciaEnEdicion.getRazonSocial());
        System.out.println("  Concepto: " + facturaVaciaEnEdicion.getConcepto());
        System.out.println("  Base: " + facturaVaciaEnEdicion.getBase());
        System.out.println("  Total: " + facturaVaciaEnEdicion.getTotal());
        System.out.println("  Nota: " + facturaVaciaEnEdicion.getNota());
    }
}
