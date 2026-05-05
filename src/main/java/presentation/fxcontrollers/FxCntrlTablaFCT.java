package presentation.fxcontrollers;

import app.core.AppContext;
import app.core.AppController;
import domain.records.Factura;
import domain.records.Fecha;
import infraestructure.servicios.config.Config;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.util.converter.DoubleStringConverter;
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

    @FXML
    private TreeTableColumn<FacturaFX, Void> colAcciones;

    @FXML private Label lblBase;
    @FXML private Label lblIVA;
    @FXML private Label lblST;
    @FXML private Label lblBaseNI;
    @FXML private Label lblRetenc;
    @FXML private Label lblTotal;
    @FXML private Label lblNumFact;
    @FXML
    private Label lblIndexFCT;

    private UIDataConfig uiCfg;
    private Path cfgPath;

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
                        }
                    );
                }
            }
        );

        treeFct
        .widthProperty()
        .addListener(
            (o, ov, nv) -> {
                Platform.runLater(
                        () -> {
                            recalcularColumnas(); // ← aquí se ajustan
                            verificarColumnas(); // ← aquí SÍ tienen tamaño
                        });
            }
        );
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
        colNumFact.setOnEditCommit(event -> {
            FacturaFX fx = event.getRowValue().getValue();
            fx.setNumero(event.getNewValue());
            actualizarFacturaDesdeTabla(fx);
        });

        // FECHA
        colFecha.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
        colFecha.setOnEditCommit(event -> {
            FacturaFX fx = event.getRowValue().getValue();
            fx.setFecha(Fecha.fromString(event.getNewValue()));
            actualizarFacturaDesdeTabla(fx);
        });

        // RAZÓN SOCIAL
        colRS.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
        colRS.setOnEditCommit(event -> {
            FacturaFX fx = event.getRowValue().getValue();
            fx.setRazonSocial(event.getNewValue());
            actualizarFacturaDesdeTabla(fx);
        });

        // CONCEPTO
        colConcepto.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
        colConcepto.setOnEditCommit(event -> {
            FacturaFX fx = (FacturaFX) event.getRowValue().getValue();
            fx.setConcepto(event.getNewValue());
            actualizarFacturaDesdeTabla(fx);
        });

        // DEVOLUCIÓN (boolean)
        colDev.setCellFactory(CheckBoxTreeTableCell.forTreeTableColumn(colDev));
        colDev.setOnEditCommit(event -> {
            FacturaFX fx = event.getRowValue().getValue();
            fx.setDevolucion(event.getNewValue());
            actualizarFacturaDesdeTabla(fx);
        });

        // BASE NI
        colBaseNI.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn(new NumberStringConverter()));
        colBaseNI.setOnEditCommit(event -> {
            FacturaFX fx = event.getRowValue().getValue();
            fx.setBaseNI((double) event.getNewValue());
            actualizarFacturaDesdeTabla(fx);
        });

        // TIPO RET
        colTipoRet.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn(new NumberStringConverter()));
        colTipoRet.setOnEditCommit(event -> {
            FacturaFX fx = event.getRowValue().getValue();
            fx.setRet((int) event.getNewValue());
            actualizarFacturaDesdeTabla(fx);
        });

        // RETENCIONES
        colRetenc.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn(new NumberStringConverter()));
        colRetenc.setOnEditCommit(event -> {
            FacturaFX fx = event.getRowValue().getValue();
            fx.setRetenciones((double) event.getNewValue());
            actualizarFacturaDesdeTabla(fx);
        });

        // TOTAL
        colTotal.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn(new NumberStringConverter()));
        colTotal.setOnEditCommit(event -> {
            FacturaFX fx = event.getRowValue().getValue();
            fx.setTotal(event.getNewValue());
            actualizarFacturaDesdeTabla(fx);
        });

        // NOTA
        colNota.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
        colNota.setOnEditCommit(event -> {
            FacturaFX fx = event.getRowValue().getValue();
            fx.setNota(event.getNewValue());
            actualizarFacturaDesdeTabla(fx);
        });

        // BASE (solo FacturaFX)
        colBase.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn(new NumberStringConverter()));
        colBase.setOnEditCommit(event -> {
            Object v = event.getRowValue().getValue();
            if (v instanceof FacturaFX fx) {
                fx.setBase((double) event.getNewValue());
                actualizarFacturaDesdeTabla(fx);
            }
        });
    }


    public void cargarDatos() {

        List<FacturaFX> facturas = AppContext.get().fact().leerFacturasFX();

        TreeItem<Object> root = new TreeItem<>();
        root.setExpanded(true);

        for (FacturaFX f : facturas) {

        TreeItem<Object> nodoFactura = new TreeItem<>(f);

        for (ExtractoFX extr : f.getExtractos()) {
            nodoFactura.getChildren().add(new TreeItem<>(extr));
        }

        root.getChildren().add(nodoFactura);
        }

        treeFct.setRoot(root);
        treeFct.setShowRoot(false);

        actualizarTotales(facturas);
    }

    private void configurarAcciones() {

    colAcciones.setCellFactory(
        col ->
            new TreeTableCell<>() {

              private final Button btn = new Button("Borrar");

              {
                btn.setOnAction(
                    e -> {
                      // TODO : 26-04-16 : Ver qué hacer con este método obsoleto
                      FacturaFX f = getTreeTableRow().getItem();
                      // TODO : 26-04-16 : Completar AQUÍ la funcionalidad real de borrado
                      // AppContext.get().fact().borrarFacturaFX(f);
                      cargarDatos();
                    });
              }

              @Override
              protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
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

    private void actualizarFacturaDesdeTabla(FacturaFX fx) {
        // Aquí conviertes FacturaFX → Factura (usa el método que ya tengas)
        Factura factura = fx.toDomain();   // si no lo tienes, habrá que crearlo en FacturaFX

        boolean ok = AppContext.get().fact().editarFactura(factura);

        if (!ok) {
            System.err.println("No se pudo actualizar la factura " + factura.getID());
        }
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


}
