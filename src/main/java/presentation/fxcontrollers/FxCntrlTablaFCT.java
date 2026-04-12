package presentation.fxcontrollers;

import app.core.AppContext;
import infraestructure.servicios.config.Config;
import java.net.URL;
import java.nio.file.Path;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import presentation.config.UIDataConfig;
import presentation.helpers.TableScaler;
import presentation.viewmodels.FacturaFX;

public class FxCntrlTablaFCT implements Initializable {

  // #region CAMPOS_FXML
  @FXML private TableView<FacturaFX> tblvwfct;

  @FXML private TableColumn<FacturaFX, Number> colID;
  @FXML private TableColumn<FacturaFX, Number> colNumExtr;
  @FXML private TableColumn<FacturaFX, Number> colBase;
  @FXML private TableColumn<FacturaFX, Number> colTipoIVA;
  @FXML private TableColumn<FacturaFX, Number> colIVA;
  @FXML private TableColumn<FacturaFX, Number> colST;
  @FXML private TableColumn<FacturaFX, Number> colBaseNI;
  @FXML private TableColumn<FacturaFX, Number> colTipoRet;
  @FXML private TableColumn<FacturaFX, Number> colRetenc;
  @FXML private TableColumn<FacturaFX, Number> colTotal;

  @FXML private TableColumn<FacturaFX, Boolean> colDev;

  @FXML private TableColumn<FacturaFX, String> colNumFact;
  @FXML private TableColumn<FacturaFX, String> colFecha;
  @FXML private TableColumn<FacturaFX, String> colRS;
  @FXML private TableColumn<FacturaFX, String> colCat;
  @FXML private TableColumn<FacturaFX, String> colNota;

  @FXML private Button btnImprimirFct;
  @FXML private Button btnVisorFct;
  @FXML private Button btnScanFct;
  @FXML private Button btnNuevaFct;
  @FXML private Button btnBorrarFct;
  @FXML private Button btnEditarFct;
  @FXML private Button btnFiltrosFct;
  @FXML private CheckBox chkbxFiltrosActivosFct;

  @FXML Label lblInfoTblVw;
  @FXML Label lblInfoStage;
  @FXML Label lblInfoListaFXfacturas;
  @FXML Label lblInfoCntrFXtabla;

  @FXML Label lblIndexFCT;
  @FXML Label lblNumFact;
  @FXML Label lblBase;
  @FXML Label lblIVA;
  @FXML Label lblST;
  @FXML Label lblBaseNI;
  @FXML Label lblRetenc;
  @FXML Label lblTotal;
  // #endregion

  // #region OTROS_CAMPOS

  static int indiceActual;
  private UIDataConfig uiCfg;
  public ObservableList<FacturaFX> listaFxFacturas;
  private Path cfgPath;

  // #endregion

  // #region CONSTR
  // REVIEW - 24-06-22 : - En el constructor inicializamos los campos que necesitamos listos antes
  // de nada...
  public FxCntrlTablaFCT() {}

  // #endregion

  @Override
  @FXML
  public void initialize(URL location, ResourceBundle resources) {
    System.out.println("[FxCntrlTablaFCT>initialize] Controlador FCT hash = " + this.hashCode());
    System.out.println("[FxCntrlTablaFCT>initialize] TableView inyectada = " + tblvwfct.hashCode());

    cfgPath = Path.of(Config.getConfig(Config.usuario).getRutasconfig().getRutaUIData());

    try {
      uiCfg = UIDataConfig.fromJson(cfgPath);
    } catch (Exception e) {
      System.out.println("Error leyendo UIDataConfig: " + e);
    }

    Platform.runLater(
        () -> {

          // columnas ya inyectadas
          tblvwfct.getColumns().forEach(col -> col.setSortable(false));

          // escalado inicial
          TableScaler.resizeColumns(tblvwfct, uiCfg);

          // listener único
          tblvwfct
              .widthProperty()
              .addListener(
                  (obs, o, n) -> {
                    Platform.runLater(
                        () -> {
                          if (uiCfg == null) return;

                          Map<String, Double> nuevos = TableScaler.resizeColumns(tblvwfct, uiCfg);

                          nuevos.forEach((colId, ancho) -> uiCfg.updateAncho(colId, ancho));
                          TableScaler.resizeColumns(tblvwfct, uiCfg);
                          System.out.println(
                              "[FxCntrlTablaFCT>initialize>RunLater] En teoría está actualizando"
                                  + " tamaños de columna...");

                          try {
                            // uiCfg.saveToJson(cfgPath);
                            System.out.println(
                                "[FxCntrlTablaFCT>initialize>RunLater] Aquí debería guardar el JSON"
                                    + " uiCfg");
                          } catch (Exception e) {
                            System.out.println(
                                "[FxCntrlTablaFCT>initialize>RunLater] Error guardando JSON: " + e);
                          }
                        });
                  });
        });
  }

  public void cargarDatos() {

    // Configurar columnas SOLO UNA VEZ
    colID.setCellValueFactory(c -> c.getValue().idProperty());
    colNumFact.setCellValueFactory(c -> c.getValue().numeroProperty());
    colFecha.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFecha().toString()));
    colRS.setCellValueFactory(c -> c.getValue().razonSocialProperty());
    colCat.setCellValueFactory(c -> c.getValue().categoriaProperty());
    colDev.setCellValueFactory(c -> c.getValue().devolucionProperty());
    colNumExtr.setCellValueFactory(c -> c.getValue().numExtractosProperty());

    colBase.setCellValueFactory(c -> c.getValue().baseProperty());
    colTipoIVA.setCellValueFactory(c -> c.getValue().tipoIVAProperty());
    colIVA.setCellValueFactory(c -> c.getValue().ivaProperty());
    colST.setCellValueFactory(c -> c.getValue().subtotalProperty());
    colBaseNI.setCellValueFactory(c -> c.getValue().baseNIProperty());
    colTipoRet.setCellValueFactory(c -> c.getValue().retProperty());
    colRetenc.setCellValueFactory(c -> c.getValue().retencionesProperty());
    colTotal.setCellValueFactory(c -> c.getValue().totalProperty());

    colNota.setCellValueFactory(c -> c.getValue().notaProperty());

    // Cargar lista inicial
    listaFxFacturas = FXCollections.observableArrayList(AppContext.get().fact().leerFacturasFX());
    tblvwfct.setItems(listaFxFacturas);

    // Calcular totales iniciales
    actualizarDatos();
  }

  // #endregion

  // #region ACT_TABLA
  public void actualizarDatos() {

    // Releer datos
    listaFxFacturas.setAll(AppContext.get().fact().leerFacturasFX());

    // Calcular totales
    double totalBase = listaFxFacturas.stream().mapToDouble(FacturaFX::getBase).sum();
    double totalIVA = listaFxFacturas.stream().mapToDouble(FacturaFX::getIVA).sum();
    double totalST = listaFxFacturas.stream().mapToDouble(FacturaFX::getSubtotal).sum();
    double totalNI = listaFxFacturas.stream().mapToDouble(FacturaFX::getBaseNI).sum();
    double totalRet = listaFxFacturas.stream().mapToDouble(FacturaFX::getRetenciones).sum();
    double totalTot = listaFxFacturas.stream().mapToDouble(FacturaFX::getTotal).sum();

    String[] datosResumen = {
      String.format("%.2f", totalBase),
      String.format("%.2f", totalIVA),
      String.format("%.2f", totalST),
      String.format("%.2f", totalNI),
      String.format("%.2f", totalRet),
      String.format("%.2f", totalTot),
      String.valueOf(listaFxFacturas.size())
    };

    actualizarTotales(datosResumen);
    actualizarInfoTabla();
  }

  public void actualizarInfoTabla() {

    lblInfoCntrFXtabla.setText("" + this.hashCode());
  }

  public void actualizarTotales(String[] datos) throws NullPointerException {
    lblIndexFCT.setText("" + (indiceActual + 1));
    lblBase.setText(datos[0]);
    lblIVA.setText(datos[1]);
    lblST.setText(datos[2]);
    lblBaseNI.setText(datos[3]);
    lblRetenc.setText(datos[4]);
    lblTotal.setText(datos[5]);
    lblNumFact.setText(datos[6]);
  }

  // #endregion

  // #region EVT_BTN
  @FXML
  public void btnVisorFctPulsado(Event ev) {
    // this.haCambiado = true;
    // this.pulsado = 1;
  }

  @FXML
  public void btnNuevaFctPulsado(Event ev) {
    // this.haCambiado = true;
    // this.pulsado = 2;
  }

  @FXML
  public void btnEditarFctPulsado(Event ev) {
    // this.haCambiado = true;
    // this.pulsado = 3;
  }

  @FXML
  public void btnBorrarFctPulsado(Event ev) {
    // this.haCambiado = true;
    // this.pulsado = 4;
  }

  @FXML
  public void btnFiltrosFctPulsado(Event ev) {
    // this.haCambiado = true;
    // this.pulsado = 5;
  }

  @FXML
  public void btnImprimirFctPulsado() {
    // this.haCambiado = true;
    // this.pulsado = 6;
  }

  @FXML
  public void btnScanFctPulsado() {
    //    this.haCambiado = true;
    //    this.pulsado = 7;
  }

  /*
     public boolean HaCambiado(){
     	return this.haCambiado;
     }

     public int getPulsado(){
     	this.haCambiado = false;
     	return this.pulsado;
     }

     public void reset(){
     	//System.out.println("[FxCntrlTablaFCT>reset] En el reset de contrFX de tablaFCT con hashCode (del contrFX " + this.hashCode());
     	this.haCambiado = false;
     	this.pulsado = 0;
     }
     //#endregion

     //#region GET/SET
     public static FxCntrlTablaFCT getFxController(){
     	if (instancia==null){
     		instancia = new FxCntrlTablaFCT();
     	}
     	return instancia;
     }

     public static void setFXcontr(FxCntrlTablaFCT contr){
     	instancia = contr;
     }

     public Stage getTablaFCT(){
     	return instancia.getTablaFCT();
     }

     //ANCHOR - tableView
     public synchronized TableView<FacturaFX> getTableView(){
     	//System.out.println("[FxCntrlTablaFCT>getTableView] la tabla no es NULL - devolviendo su valor hashCode de tablaFCT: " + this.tblvwfct .hashCode());
     	this.tblvwfct  = cfct.getFXcontrlTablaFCT().tblvwfct ;
     	return this.tblvwfct ;
     }

     public int getIndiceSeleccionadoTabla() {
      System.out.println(
          "[FxCntrlTablaFCT>getIndiceSeleccionadoTabla] Devolviendo indiceActual desde contrFxtabla->"
              + this.hashCode());
     	return  indiceActual;
     }

     public Factura getFacturaSeleccionadaTabla(){
     	if (this.tblvwfct.isVisible()){
     		Factura selectedItem = this.tblvwfct .getSelectionModel().getSelectedItem();
     		//System.out.println("[FxCntrlTablaFCT>getFacturaSeleccionadaTabla] Como this.tblvwfct .isVisible(): " + this.tblvwfct .isVisible() + " se muestra la factura seleccionada:\n"+ selectedItem.toString());
     		return selectedItem;
     	}
     	else{
     		//OJO : Estoy enviando la segunda factura al Visor, al arrancar...
     		Factura f = null;
     		try {
     			f = cfct.m.getFactura(1);
     		} catch (NullPointerException | IOException e) {
          System.out.println(
              "[FxCntrlTablaFCT>getFacturaSeleccionadaTabla] Error al obtener la factura seleccionada"
                  + " de la tabla (posiblemente por falta de datos o porque la tabla no es visible) -"
                  + " "
                  + e);
     			System.exit(0);
     		}
        System.out.println(
            "[FxCntrlTablaFCT>getFacturaSeleccionadaTabla] Como this.tblvwfct.isVisible(): "
                + this.tblvwfct.isVisible()
                + " se muestra la *(segunda) factura:"
                + f.toString());
     		return f;
     	}
     }
     // REVIEW - 24-07-29 : Aquí fallaba y no cogía el index 0 (primera factura)
     public void seleccionarIndiceTabla(int nuevoindice){
     	if (this.tblvwfct.isVisible()){
     		this.tblvwfct.getSelectionModel().select(nuevoindice);
     		indiceActual = nuevoindice;
     		this.lblIndexFCT.setText(""+(indiceActual+1));
     		ControladorFacturas.facturaActual = getFacturaSeleccionadaTabla();
     	}
     		setIndiceActual(nuevoindice);
     }

     public static int getIndiceActual() {
     	return indiceActual;
     }

     public void setIndiceActual(int index) {
     	this.lblIndexFCT.setText(""+(index+1));
     	// ¿¿Refrescar tabla después de actualizar etiqueta??
     	indiceActual = index;
     }

     public void setListaFXFacturas(ObservableList<FacturaFX> lista) {
     	this.listaFxFacturas = lista;
     }

     //#endregion
  */
  /*
     //region HELPERS
     public void imprimirInfoTabla() {
     	System.out.println("[FxCntrlTablaFCT]   ----info tabla----");
      System.out.println(
          "[FxCntrlTablaFCT]      tableview->"
              + ((this.tblvwfct == null) ? "NULL" : this.tblvwfct.hashCode()));
      System.out.println(
          "[FxCntrlTablaFCT]     Stagetabla->"
              + ((ControladorFacturas.tablaFCT == null)
                  ? "NULL"
                  : ControladorFacturas.tablaFCT.hashCode()));
     	System.out.println("[FxCntrlTablaFCT]    cntrFXtabla->"+this.hashCode());
      System.out.println(
          "[FxCntrlTablaFCT]    listaFXfact->"
              + ((this.listaFxFacturas == null) ? "NULL" : this.listaFxFacturas.hashCode()));
     	System.out.println("[FxCntrlTablaFCT]   -----Fin info-----");
     }
     //#endregion

  //    public ObservableList<Factura> getListaFXFacturas() {
  //        List<Factura> fact_prev = null;
  //        try {
  //            fact_prev = FacturaCSVRepo.leerTodasLasFacturas();
  //        } catch (NullPointerException e) {
  //       System.out.println(
  //           "[ModeloFacturas>getListaFXFacturas] Error al recoger la lista observable (para JFX) de"
  //               + " facturas : "
  //               + e.getMessage());
  //        }
  //        var facturasFX = FXCollections.observableList(fact_prev);
  //        return facturasFX;
     }

  private void cargarFacturas() {
      List<Factura> lista = FacturaCSVRepo.leerTodasLasFacturas();

      listaFxFacturas = FXCollections.observableArrayList(lista.stream().map(FacturaFX::fromDomain).toList());

      tblvwfct.setItems(listaFxFacturas);
  }
  */
}
