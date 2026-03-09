package ui.fxcontrollers;

import modelo.ModeloFacturas;
import modelo.records.Factura;
import controladores.Controlador;
import controladores.ControladorFacturas;

import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.ResourceBundle;
import java.util.concurrent.BrokenBarrierException;

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
import javafx.stage.Stage;

public class FxCntrlTablaFCT implements Initializable{

//#region CAMPOS_FXML
//ANCHOR - tableView
	@FXML public TableView<Factura> tblvwfct ;
	@FXML private TableColumn<Factura, Integer> colID;
	@FXML private TableColumn<Factura, String> colNumFact;
	@FXML private TableColumn<Factura, String> colFecha;
	@FXML private TableColumn<Factura, String> colRS;
	@FXML private TableColumn<Factura, String> colCat;
	@FXML private TableColumn<Factura, String> colDev;
	@FXML private TableColumn<Factura, Integer> colNumExtr;
	@FXML private TableColumn<Factura, Double> colBase;
	@FXML private TableColumn<Factura, Integer> colTipoIVA;
	@FXML private TableColumn<Factura, Double> colIVA;
	@FXML private TableColumn<Factura, Double> colST;
	@FXML private TableColumn<Factura, Double> colBaseNI;
	@FXML private TableColumn<Factura, Integer> colTipoRet;
	@FXML private TableColumn<Factura, Double> colRetenc;
	@FXML private TableColumn<Factura, Double> colTotal;
	@FXML private TableColumn<Factura, String> colNota;

	@FXML private Button btnImprimirFct;
	@FXML private Button btnVisorFct;
	@FXML private Button btnNuevaFct;
    @FXML private Button btnBorrarFct;
    @FXML private Button btnEditarFct;
    @FXML private Button btnFiltrosFct;
	@FXML private CheckBox chkbxFiltrosActivosFct;

	@FXML Label lblInfoTblVw;
	@FXML Label lblInfoStage;
	@FXML Label lblInfoListaFXfacturas;
	@FXML Label lblInfoCntrFXtabla;

	@FXML Label lblIndexT;	
	@FXML Label lblNumFact;
	@FXML Label lblBase;
	@FXML Label lblIVA;
	@FXML Label lblST;
	@FXML Label lblBaseNI;
	@FXML Label lblRetenc;
	@FXML Label lblTotal;
//#endregion

//#region OTROS_CAMPOS

//NOTE - 14-07-24 : Singleton
	static FxCntrlTablaFCT instancia;

	boolean haCambiado = false;
	int pulsado = 0;

	static int indiceActual;
	ControladorFacturas cfct;
	public ObservableList<Factura> listaFxFacturas;
//#endregion

//#region CONSTR
// REVIEW - 24-06-22 : - En el constructor inicializamos los campos que necesitamos listos antes de nada...
	public FxCntrlTablaFCT(){
		//System.out.println("[FxControladorFacturas>constructor] Comenzando el constructor de FxCntrlTablaFCT");
		// REVIEW - 02-07-24 : Vamos a ver qué ocurre si el constructor de FxCntrlTablaFCT no crea una nueva tabla... parece que nada ...
		this.cfct = Controlador.getControladorFacturas();
		this.listaFxFacturas = ModeloFacturas.getModelo().getListaFXFacturas();
		this.tblvwfct  = Controlador.getControladorFacturas().getTableViewFCT();
		System.out.println("[FxControladorFacturas>constructor] Acabando el constructor de FxCntrlTablaFCT->" + this.hashCode());
	}
//#endregion

//#region INI_FCT/T
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		//Adjudicar valores para las Columnas de la Tabla de Facturas
		//System.out.println("[FxControladorFacturas.java>initialize()] Comenzando initialize - Adjudicando valores para las columnas de la Tabla de Facturas");
		colID.setCellValueFactory(cellData -> cellData.getValue().getFxID());
		colNumFact.setCellValueFactory(cellData -> cellData.getValue().getFxNumFact());
		colFecha.setCellValueFactory(cellData -> cellData.getValue().getFxFecha());
		colRS.setCellValueFactory(cellData -> cellData.getValue().getFxRS());
		colCat.setCellValueFactory(cellData -> cellData.getValue().getFxCat());
		colDev.setCellValueFactory(cellData -> cellData.getValue().getFxDev());
		colNumExtr.setCellValueFactory(cellData -> cellData.getValue().getFxNumExtr());
		colBase.setCellValueFactory(cellData -> cellData.getValue().getFxBase());
		colTipoIVA.setCellValueFactory(cellData -> cellData.getValue().getFxTipoIVA());
		colIVA.setCellValueFactory(cellData -> cellData.getValue().getFxIVA());
		colST.setCellValueFactory(cellData -> cellData.getValue().getFxST());
		colBaseNI.setCellValueFactory(cellData -> cellData.getValue().getFxBaseNI());
		colTipoRet.setCellValueFactory(cellData -> cellData.getValue().getFxTipoRet());
		colRetenc.setCellValueFactory(cellData -> cellData.getValue().getFxRetenc());
		colTotal.setCellValueFactory(cellData -> cellData.getValue().getFxTotal());
		colNota.setCellValueFactory(cellData -> cellData.getValue().getFxNota());

//addColums2table
/*
		this.tblvwfct .getColumns().add(colID);
		tblvwfct .getColumns().add(colNumFact);
		tblvwfct .getColumns().add(colFecha);
		tblvwfct .getColumns().add(colRS);
		tblvwfct .getColumns().add(colCat);
		tblvwfct .getColumns().add(colDev);
		tblvwfct .getColumns().add(colNumExtr);
		tblvwfct .getColumns().add(colBase);
		tblvwfct .getColumns().add(colTipoIVA);
		tblvwfct .getColumns().add(colIVA);
		tblvwfct .getColumns().add(colST);
		tblvwfct .getColumns().add(colTipoRet);
		tblvwfct .getColumns().add(colRetenc);
		tblvwfct .getColumns().add(colTotal);
		tblvwfct .getColumns().add(colNota);
*/
		for (TableColumn<Factura, ?> column : tblvwfct.getColumns()) {
			column.setSortable(false);
		}

		ObservableList<Factura> listaFXtemp = ModeloFacturas.getModelo().getListaFXFacturas();
		FXCollections.sort(listaFXtemp, Comparator.comparing(Factura::getFecha));
		tblvwfct.setItems(listaFXtemp);
		tblvwfct.refresh();
		setListaFXFacturas(listaFXtemp);
		String[] datosResumen = ModeloFacturas.getModelo().calcularTotales();
		actualizarTotales(datosResumen);
		actualizarInfoTabla();	

		//NOTE - 02-07-24 : generar listener en el TableView para que informe del cambio de elemento seleccionad
		//Arrancar el hilo del ControladorFacturas
		//System.out.println("[FxCntrlTablaFCT>initialize] Comenzando el initialize del ControladorFX de Facturas");
		//NOTE - 07-07-24 : Saco la barrera para los tests de JavaFX 
		//Controlador.barreraControladores.await();
		cfct = Controlador.getControladorFacturas(this);
		//System.out.println("[FxCntrlTablaFCT>initialize] Saliendo del initialize del ControladorFX de Facturas");
	}
//#endregion

//#region EVT_BTN
	@FXML
	public void btnVisorFctPulsado(Event ev){
		this.haCambiado = true;
		this.pulsado = 1;
	}

	@FXML
	public void btnNuevaFctPulsado(Event ev){
		this.haCambiado = true;
		this.pulsado = 2;
	}	

	@FXML
	public void btnEditarFctPulsado(Event ev){
		this.haCambiado = true;
		this.pulsado = 3;
	}

	@FXML
	public void btnBorrarFctPulsado(Event ev){
		this.haCambiado = true;
		this.pulsado = 4;
	}

	@FXML
	public void btnFiltrosFctPulsado(Event ev){
		this.haCambiado = true;
		this.pulsado = 5;
	}
	
	@FXML
	public void btnImprimirFctPulsado(){
		this.haCambiado = true;
		this.pulsado = 6;
	}

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
	public synchronized TableView<Factura> getTableView(){
		//System.out.println("[FxCntrlTablaFCT>getTableView] la tabla no es NULL - devolviendo su valor hashCode de tablaFCT: " + this.tblvwfct .hashCode());
		this.tblvwfct  = cfct.getFXcontrlTablaFCT().tblvwfct ;
		return this.tblvwfct ;
	}
	
	public int getIndiceSeleccionadoTabla() {
		System.out.println("[FxCntrlTablaFCT>getIndiceSeleccionadoTabla] Devolviendo indiceActual desde contrFxtabla->" + this.hashCode());
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
				e.printStackTrace();
				System.exit(0);
			}
			//System.out.println("[FxCntrlTablaFCT>getFacturaSeleccionadaTabla] Como this.tblvwfct .isVisible(): " + this.tblvwfct .isVisible() + " se muestra la *(segunda) factura:" + f.toString());
			return f;
		}
	}
// REVIEW - 24-07-29 : Aquí fallaba y no cogía el index 0 (primera factura)
	public void seleccionarIndiceTabla(int nuevoindice){
		if (this.tblvwfct.isVisible()){
			this.tblvwfct.getSelectionModel().select(nuevoindice);
			indiceActual = nuevoindice;
			this.lblIndexT.setText(""+(indiceActual+1));
			ControladorFacturas.facturaActual = getFacturaSeleccionadaTabla(); 
		}
			setIndiceActual(nuevoindice);
	}

	public static int getIndiceActual() {
		return indiceActual;
	}

	public void setIndiceActual(int index) {
		this.lblIndexT.setText(""+(index+1));
		// ¿¿Refrescar tabla después de actualizar etiqueta??
		indiceActual = index;
	}

	public void setListaFXFacturas(ObservableList<Factura> lista) {
		this.listaFxFacturas = lista;
	}

//#endregion

//#region ACT_TABLA
	public void actualizarInfoTabla(){
		lblInfoTblVw.setText(""+this.tblvwfct .hashCode());
		lblInfoStage.setText(""+((ControladorFacturas.tablaFCT==null)?"NULL":ControladorFacturas.tablaFCT.hashCode()));
		lblInfoCntrFXtabla.setText(""+this.hashCode());
		lblInfoListaFXfacturas.setText(""+ ModeloFacturas.getModelo().getListaFXFacturas().hashCode());
	}

	public void actualizarTotales(String[] datos) throws NullPointerException{
		lblIndexT.setText(""+(indiceActual+1));
		lblBase.setText(datos[0]);
		lblIVA.setText(datos[1]);
		lblST.setText(datos[2]);
		lblBaseNI.setText(datos[3]);
		lblRetenc.setText(datos[4]);
		lblTotal.setText(datos[5]);
		lblNumFact.setText(datos[6]);
		System.out.println("[FxCntrlTablaFCT>actualizarTotales] Totales actualizados en tablaFCT con tblVw->" + tblvwfct .hashCode() + " - contrFx->" + this.hashCode());
	}
//#endregion

//region HELPERS
	public void imprimirInfoTabla() {
		System.out.println("[FxCntrlTablaFCT]   ----info tabla----");
		System.out.println("[FxCntrlTablaFCT]      tableview->"+((this.tblvwfct ==null)?"NULL":this.tblvwfct .hashCode()));
		System.out.println("[FxCntrlTablaFCT]     Stagetabla->"+((ControladorFacturas.tablaFCT==null)?"NULL":ControladorFacturas.tablaFCT.hashCode()));
		System.out.println("[FxCntrlTablaFCT]    cntrFXtabla->"+this.hashCode());
		System.out.println("[FxCntrlTablaFCT]    listaFXfact->"+((this.listaFxFacturas==null)?"NULL":this.listaFxFacturas.hashCode()));
		System.out.println("[FxCntrlTablaFCT]   -----Fin info-----");
	}
//#endregion

}
