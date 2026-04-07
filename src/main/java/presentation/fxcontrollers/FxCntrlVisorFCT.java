package presentation.fxcontrollers;
/*
import domain.records.Extracto;
import domain.records.Factura;
import domain.records.Fecha;
import domain.records.NIF;
import domain.records.Nota;
import domain.records.RazonSocial;
import domain.records.TipoGasto;
import domain.records.Totales;
import controladores.Controlador;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.BrokenBarrierException;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FxCntrlVisorFCT implements Initializable{

//#region CAMPOS FXML
// REVIEW - 24-07-02 : Falta cambiar algunas Label por TextField
	@FXML Label lblVID;

	@FXML TextField tfNumFactura;
	@FXML TextField tfFecha;
	@FXML TextField tfCategoria;

	@FXML Label lblVIDRS;

	@FXML TextField tfNombreEmpresa;
	@FXML TextField tfNIF;
	@FXML TextField tfRS;

	@FXML TextField tfVBase1;
	@FXML TextField tfVTipoIVA1;
	@FXML TextField tfVIVA1;
	@FXML TextField tfVST1;
	@FXML TextField tfVCon1;
	@FXML TextField tfVBase2;
	@FXML TextField tfVTipoIVA2;
	@FXML TextField tfVIVA2;
	@FXML TextField tfVST2;
	@FXML TextField tfVCon2;
	@FXML TextField tfVBase3;
	@FXML TextField tfVTipoIVA3;
	@FXML TextField tfVIVA3;
	@FXML TextField tfVST3;
	@FXML TextField tfVCon3;
	@FXML TextField tfVBase4;
	@FXML TextField tfVTipoIVA4;
	@FXML TextField tfVIVA4;
	@FXML TextField tfVST4;
	@FXML TextField tfVCon4;

	@FXML Button btnVcalcIVA1;
	@FXML Button btnVcalcIVA2;
	@FXML Button btnVcalcIVA3;
	@FXML Button btnVcalcIVA4;

	@FXML Button btnVcalc1;
	@FXML Button btnVcalc2;
	@FXML Button btnVcalc3;
	@FXML Button btnVcalc4;
	@FXML Button btnVsumar;

	@FXML TextField tfVTotalesBase;
	@FXML TextField tfVTotalesTipoIVA;
	@FXML TextField tfVTotalesIVA;
	@FXML TextField tfVTotalesST;
	@FXML TextField tfVTotalesBaseNI;
	@FXML TextField tfVTotalesTipoRet;
	@FXML TextField tfVTotalesRetenciones;
	@FXML TextField tfVTotal;

	@FXML Button btnVcalcTOT;

	@FXML TextArea txtAreaVNota;

	@FXML Button btnVizda;
	@FXML
	public Button btnVNueva;
	@FXML
	public Button btnVEditar;
	@FXML
	public Button btnVBorrar;
	// @FXML Label lblVTitulo;
	@FXML Button btnVF1;
	@FXML Button btnVdcha;
//#endregion

//#region CAMPOS_CLASE

	public static final int VISOR  = 1;
	public static final int EDITAR = 2;
	public static final int BORRAR = 3;
	public static final int NUEVAF = 4;

	ControladorFacturas cfct;
	boolean haCambiado = false;
	int pulsado = 0;
	public static int modo = VISOR;
	ArrayList<TextField> todoslostextfields;
	ArrayList<Label> todaslasetiq;
	static Stage visorFct;
	static FxCntrlVisorFCT instancia;
	static int indexActual = 0;
//#endregion

//#region CONSTR
// REVIEW - 24-06-22 : En el constructor inicializamos los campos que necesitamos listos antes de nada...
	public FxCntrlVisorFCT() throws InterruptedException, BrokenBarrierException{

		System.out.println("[FxCntrlVisorFCT>constructor] Arrancando el constructor del controlador FX del visorFCT");

		if(this.cfct==null) this.cfct = Controlador_prev.getControladorFacturas();

		ControladorFacturas.visorFCT = getVisorFCT();
		visorFct = ControladorFacturas.visorFCT;
		if (visorFct!=null)
			System.out.println("[FxCntrlVisorFCT>constructor] Asignado el VisorFCT con hashCode: "+ visorFct.hashCode());
		else System.out.println("[FxCntrlVisorFCT>constructor] El VisorFCT sigue siendo NULL");
	}
//#endregion

//#region GETTERS/SETTERSthis.tfVBase1.getText()
	public static FxCntrlVisorFCT getFxController() {
		if (instancia==null){
			try {
				instancia = new FxCntrlVisorFCT();
			} catch (InterruptedException | BrokenBarrierException e) {
				e.printStackTrace();
			}
		}
		return instancia;
	}

	public static void setFXcontr(FxCntrlVisorFCT contr) {
       instancia = contr;
    }

	public synchronized Stage getVisorFCT(){
		if (ControladorFacturas.visorFCT==null){
			System.out.println("[FxCntrlVisorFCT>getVisorFCT] El valor del visor era NULL.Se crea un visor nuevo");
			Stage visor = getVisorFacturas();
				if(visor!=null){
					visorFct = visor;
					System.out.println("[FxCntrlVisorFCT>getVisorFCT] Se devuelve un visor nuevo con hashCode "+ visorFct.hashCode());
					return visorFct;
				}
		}else{
			if (visorFct!=null)
				return visorFct;
		}
		return null;
	}

	public static void setVisorFCT(Stage v) {
		visorFct = v;
	}
	private Stage getVisorFacturas() {
		return visorFct;
	}

	public synchronized TextArea getAreaNota(){
		return this.txtAreaVNota;
	}
	public void reset(){
		this.haCambiado = false;
		this.pulsado = 0;
	}

	public void setTextoBotonF1(String str) {
		this.btnVF1.setText(str);
	}

//#endregion

//#region INI FCT/V
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.out.println("[FxCntrlVisorFCT>Initialize] Empezando la inicializacion del controlador FX del visorFCT");
		//ANCHOR - tableView
		if (cfct.getFXcontrlTablaFCT().getTableView()!=null){
			indexActual = cfct.getFXcontrlTablaFCT().getIndiceSeleccionadoTabla();
			System.out.println("[FxCntrlVisorFCT>initialize] Index actual en tabla " + cfct.getFXcontrlTablaFCT().getTableView().hashCode() + " : " + indexActual);
			if (FxCntrlVisorFCT.getFxController()!=null){
				if((FxCntrlVisorFCT.visorFct = getVisorFCT())!=null){
					System.out.println("[FxCntrlVisorFCT>initialize] Llamando a actualizarDatosVisor - visor con hashCode: " + FxCntrlVisorFCT.visorFct.hashCode());
					actualizarDatosVisor(indexActual, ControladorFacturas.facturaActual);
				}
			}
		}else{
			System.out.println("[FxCntrlVisorFCT>initialize] tabla de valor NULL. El programa se cerrará");
			System.exit(0);
		}
		System.out.println("[FxCntrlVisorFCT>Initialize] Acabando la inicializacion del controlador FX del visorFCT ");
	}
//#endregion

//#region EventosBTN
	@FXML
	public void btnVF1Pulsado(){

		this.haCambiado = true;
		this.pulsado = 1;
	}

	@FXML
	public void btnNuevaFctVPulsado(){

		this.haCambiado = true;
		this.pulsado = 2;
		//modo = NUEVAF;
	}

	@FXML
	public void btnEditarFctVPulsado(){

		this.haCambiado = true;
		this.pulsado = 3;
	}

	@FXML
	public void btnBorrarFctPulsado(){
		this.haCambiado = true;
		this.pulsado = 4;
		//modo = BORRAR;
	}

	@FXML
	public void btnIzdaVPulsado(){
		if(modo == VISOR){
			this.haCambiado = true;
			this.pulsado = 5;
		} else
			System.out.println("[FxCntrlVisorFCT>botonIzdaPulsado] El modo no permite moverse ewntre facturas");
	}

	@FXML
	public void btnDchaVPulsado(){
		if(modo == VISOR){
			this.haCambiado = true;
			this.pulsado = 6;
		} else
			System.out.println("[FxCntrlVisorFCT>botonDchaPulsado] El modo no permite moverse ewntre facturas");
	}

	@FXML
	public void btnVcalc1Pulsado(){
		sumar(1);
	}
	@FXML
	public void btnVcalc2Pulsado(){
		sumar(2);
	}
	@FXML
	public void btnVcalc3Pulsado(){
		sumar(3);
	}
	@FXML
	public void btnVcalc4Pulsado(){
		sumar(4);
	}
	@FXML
	public void btnVsumarPulsado(){
		sumar(5);
	}
	@FXML
	public void btnVcalcTOTPulsado(){
		sumar(0);
	}

	@FXML
	public void btnVcalcIVA1pulsado(){
		calcIVA(1);
	}
	@FXML
	public void btnVcalcIVA2pulsado(){
		calcIVA(2);
	}
	@FXML
	public void btnVcalcIVA3pulsado(){
		calcIVA(3);
	}
	@FXML
	public void btnVcalcIVA4pulsado(){
		calcIVA(4);
	}

	public boolean HaCambiado(){
		return this.haCambiado;
	}

	public int getPulsado(){
		this.haCambiado = false;
		return this.pulsado;
	}
//#endregion

//#region Act_VISOR
	public synchronized void actualizarDatosVisor(int index, Factura f){
		Stage prueba = getVisorFCT();
		borrarCampos();
        System.out.println("[FxCntrlVisorFCT>actualizarDatosVisor] TextFields borrados");
		if (prueba!=null){
			visorFct = prueba;
			System.out.println("[FxCntrlVisorFCT>actualizarDatosVisor] visorFct de hashCode: " + visorFct.hashCode());
		}
		//Thread.sleep(500);
// REVIEW - 24-07-12 : Aquí lo dejo, parece que el visor no está inicializado, o lblVID es siempre null por otro motivo...
		if(!ControladorFacturas.visorFCT.isShowing())
		ControladorFacturas.visorFCT.show();
		if(f!=null){
			//ANCHOR - tableView
			Platform.runLater(new Runnable(){
				@Override
				public void run(){
					actualizarDatosFacturaVisor(f);
					actualizarDatosEmpresaVisor(f);
					actualizarExtractosVisor(f);
					actualizarTotalesVisor(f);
					actualizarNotaVisor(f);
					System.out.println("[FxCntrlVisorFCT>actualizarDatosVisor] Se muestra la Factura num " + index + " :\n" + f.toString());
				}
			});
		}else{
			System.out.println("[FxCntrlVisorFCT>actualizarDatosVisor] No se muestra ninguna factura");
		}
	}

	private void actualizarDatosFacturaVisor(Factura f) {
		this.lblVID.setText(f.getID()+"");
		this.tfNumFactura.setText(f.getNumeroFactura()+"");
		this.tfFecha.setText(f.getFecha().toString());
		this.tfCategoria.setText(f.getCategoria().getTipo());
	}

	private void actualizarDatosEmpresaVisor(Factura f){
		this.lblVIDRS.setText(f.getRS().getID()+"");
		this.tfNIF.setText(f.getRS().getNif().toString());
		this.tfNombreEmpresa.setText(f.getRS().getNombre());
		this.tfRS.setText(f.getRS().getRazon());
	}

	private void actualizarExtractosVisor(Factura f){
		switch(f.getExtractos().size()){
			case 4:
				this.tfVBase4.setText(f.getExtractos().get(3).getBase()+"");
				this.tfVTipoIVA4.setText(f.getExtractos().get(3).getTipoIVA()+"");
				this.tfVIVA4.setText(f.getExtractos().get(3).getIVA()+"");
				this.tfVST4.setText(f.getExtractos().get(3).getSubtotal()+"");
				this.tfVCon4.setText(f.getExtractos().get(3).getConcepto());
			case 3:
				this.tfVBase3.setText(f.getExtractos().get(2).getBase()+"");
				this.tfVTipoIVA3.setText(f.getExtractos().get(2).getTipoIVA()+"");
				this.tfVIVA3.setText(f.getExtractos().get(2).getIVA()+"");
				this.tfVST3.setText(f.getExtractos().get(2).getSubtotal()+"");
				this.tfVCon3.setText(f.getExtractos().get(2).getConcepto());
			case 2:
				this.tfVBase2.setText(f.getExtractos().get(1).getBase()+"");
				this.tfVTipoIVA2.setText(f.getExtractos().get(1).getTipoIVA()+"");
				this.tfVIVA2.setText(f.getExtractos().get(1).getIVA()+"");
				this.tfVST2.setText(f.getExtractos().get(1).getSubtotal()+"");
				this.tfVCon2.setText(f.getExtractos().get(1).getConcepto());
			case 1:
				this.tfVBase1.setText(f.getExtractos().get(0).getBase()+"");
				this.tfVTipoIVA1.setText(f.getExtractos().get(0).getTipoIVA()+"");
				this.tfVIVA1.setText(f.getExtractos().get(0).getIVA()+"");
				this.tfVST1.setText(f.getExtractos().get(0).getSubtotal()+"");
				this.tfVCon1.setText(f.getExtractos().get(0).getConcepto());
				break;
			case 0:
				borrarCampos();
			default:
				System.out.println("[FxCntrlVisorFCT>actualizarExtractosVisor] Error en el num de extractos de la factura seleccionada");
				break;
		}
	}

	private void actualizarTotalesVisor(Factura f) throws NullPointerException{
		this.tfVTotalesBase.setText(f.getTotales().getBase()+"");
		this.tfVTotalesIVA.setText(f.getTotales().getIVA()+"");
		this.tfVTotalesST.setText(f.getTotales().getSubtotal()+"");
		this.tfVTotalesBaseNI.setText(f.getTotales().getBaseNI()+"");
		this.tfVTotalesTipoRet.setText(f.getTotales().getRet()+"");
		this.tfVTotalesRetenciones.setText(f.getTotales().getRetenciones()+"");
		this.tfVTotal.setText(f.getTotales().getTotal()+"");
		//lblVTitulo.setText("num de fact: " + datos[6]);

	}

	public void actualizarTotalesVisor(String[] datos) throws NullPointerException{
		tfVTotalesBase.setText(datos[0]);
		tfVTotalesIVA.setText(datos[1]);
		tfVTotalesST.setText(datos[2]);
		tfVTotalesBaseNI.setText(datos[3]);
		tfVTotalesRetenciones.setText(datos[4]);

	}

	private void actualizarNotaVisor(Factura f){
		if (f.getNota()!=null)
			this.txtAreaVNota.setText(f.getNota().getTexto());
		else this.txtAreaVNota.setText("--SIN NOTA--");
	}
//#endregion

//#region HELPERS
    private void addTextFields(Node node, ArrayList<TextField> textFields) {
        if (node instanceof TextField) {
            textFields.add((TextField) node);
        } else if (node instanceof Parent) {
            for (Node child : ((Parent) node).getChildrenUnmodifiable()) {
                addTextFields(child, textFields);
            }
        }
    }

    private void addLabels(Node node, ArrayList<Label> labels) {
        if (node instanceof Label && node.getId()!=null) {
            labels.add((Label) node);
        } else if (node instanceof Parent) {
            for (Node child : ((Parent) node).getChildrenUnmodifiable()) {
                addLabels(child, labels);
            }
        }
    }

	public void borrarCampos(){
		todoslostextfields = new ArrayList<>();
		todaslasetiq = new ArrayList<>();
        Scene scene = getVisorFCT().getScene();
        if (scene != null) {
            addTextFields(scene.getRoot(), todoslostextfields);
			addLabels(scene.getRoot(), todaslasetiq);
        }
		for(TextField tf : todoslostextfields){
			tf.setText("");
		}
		for(Label l : todaslasetiq){
			l.setText("");
		}
		System.out.println("[FxCntrlVisorFCT>borrarTextfields] borrados los tf y lbl en Visor->" + visorFct.hashCode());
		if (this.txtAreaVNota!=null){
			this.txtAreaVNota.setText("");
			System.out.println("[FxCntrlVisorFCT>borrarTextfields] borrada tb la Nota en Visor->" + visorFct.hashCode());
		}
	}

	public void setTFEditables(boolean editable){
		todoslostextfields = new ArrayList<>();
        Scene scene = getVisorFCT().getScene();
        if (scene != null) {
            addTextFields(scene.getRoot(), todoslostextfields);
        }
		for (TextField tf : todoslostextfields){
			tf.setEditable(editable);
		}
		System.out.println("[FxCntrlVisorFCT>setTFEditables] actualizados los tf a ed:" + editable + " en Visor " + visorFct.hashCode());
		if (this.txtAreaVNota!=null){
			this.txtAreaVNota.setEditable(editable);
			System.out.println("[FxCntrlVisorFCT>setTFEditables] actualizada tb la Nota a ed:" + editable + " en Visor " + visorFct.hashCode());
		}
	}
//#endregion

//#region REC_DATOS
	public Factura recogerDatosVisor(){
		Factura f = new Factura ();
		//recoger todos los campos del visor
		//leer Datos Factura
		f.setID(Integer.parseInt(this.lblVID.getText()));
		f.setNumeroFactura(tfNumFactura.getText());
		f.setCategoria(new TipoGasto(tfCategoria.getText(), tfCategoria.getText()));
		String fechaStr = tfFecha.getText();
		String[] fechaArr = fechaStr.split("/");
		int dia = Integer.parseInt(fechaArr[0]);
		int mes = Integer.parseInt(fechaArr[1]);
		int anho = Integer.parseInt(fechaArr[2]);
		f.setFecha(new Fecha(dia, mes, anho));
		f.esDevolucion = ((tfVTotal.getText().contains("-"))?true:false);
		//leer RS
		int idrs = Integer.parseInt(lblVIDRS.getText());
		String nifstr = tfNIF.getText();
		String[] nifarr = nifstr.split("-");
		boolean isCIF = ((nifarr[0].length()<nifarr[1].length())?true:false);
		String letranif;
		int numnif;
		if (isCIF){
			letranif = nifarr[0];
			numnif = Integer.parseInt(nifarr[1]);
		} else {
			numnif = Integer.parseInt(nifarr[0]);
			letranif = nifarr[1];
		}
		NIF nif = new NIF(numnif, letranif, isCIF);
		String nombre = tfNombreEmpresa.getText();
		RazonSocial rs = new RazonSocial(idrs, nif, nombre);
		f.setRS(rs);
		//leer Extractos
		ArrayList<Extracto> extr = new ArrayList<Extracto>();
		if (!tfVST1.getText().equals("")){
			Extracto ex1 = new Extracto(Double.parseDouble(tfVBase1.getText()), Integer.parseInt(tfVTipoIVA1.getText()),Double.parseDouble(tfVIVA1.getText()),Double.parseDouble(tfVST1.getText()), tfVCon1.getText());
			extr.add(ex1);
		}
		if (!tfVST2.getText().equals("")){
			Extracto ex2 = new Extracto(Double.parseDouble(tfVBase2.getText()), Integer.parseInt(tfVTipoIVA2.getText()),Double.parseDouble(tfVIVA2.getText()),Double.parseDouble(tfVST2.getText()), tfVCon2.getText());
			extr.add(ex2);
		}
		if (!tfVST3.getText().equals("")){
			Extracto ex3 = new Extracto(Double.parseDouble(tfVBase3.getText()), Integer.parseInt(tfVTipoIVA3.getText()),Double.parseDouble(tfVIVA3.getText()),Double.parseDouble(tfVST3.getText()), tfVCon3.getText());
			extr.add(ex3);
		}
		if (!tfVST4.getText().equals("")){
			Extracto ex4 = new Extracto(Double.parseDouble(tfVBase4.getText()), Integer.parseInt(tfVTipoIVA4.getText()),Double.parseDouble(tfVIVA4.getText()),Double.parseDouble(tfVST4.getText()), tfVCon4.getText());
			extr.add(ex4);
		}
		f.setExtractos(extr);
		//leer Totales
		// STUB - 24-07-29 : Revisar lo de variosIVAs... todavía no está
		boolean variosIvas = (f.getExtractos().size()>1);
		Totales tots = new Totales(Double.parseDouble(tfVTotalesBase.getText()), variosIvas, (variosIvas?0:Integer.parseInt(tfVTipoIVA1.getText())),Double.parseDouble(tfVTotalesIVA.getText()),Double.parseDouble(tfVTotalesST.getText()), Double.parseDouble(tfVTotalesBaseNI.getText()),Integer.parseInt(tfVTotalesTipoRet.getText()),Double.parseDouble(tfVTotalesRetenciones.getText()),Double.parseDouble(tfVTotal.getText()),tfCategoria.getText());
		f.setTotales(tots);
		//leer Nota
		if (txtAreaVNota.getText().equals("") || txtAreaVNota.getText().contains("-SIN NOTA-"))
			f.setNota(null);
		else f.setNota(new Nota(txtAreaVNota.getText()));
		System.out.println("[FxCntrlVisorFCT>recogerDatosVisor] Factura registrada : " + f.toString());
		return f;
	}
//#endregion

//#region CALCS
	public void sumar(int i) {
		if (modo == EDITAR){
			if(i ==1){
				if(!(this.tfVBase1.getText().equals("")) && !(this.tfVTipoIVA1.getText().equals("0"))){
					double st1 = Double.parseDouble(this.tfVBase1.getText()) + Double.parseDouble(this.tfVIVA1.getText());
					this.tfVST1.setText(""+st1);
				} else if (this.tfVTipoIVA1.getText().equals("0")){
					double baseNI = 0.0;
					if (!(this.tfVTotalesBaseNI.getText().equals(""))){
						baseNI = Double.parseDouble(this.tfVTotalesBaseNI.getText());
					}
					baseNI += Double.parseDouble(this.tfVBase1.getText());
					this.tfVTipoIVA1.setText("0");
					this.tfVIVA1.setText("0.0");
					this.tfVST1.setText(this.tfVBase1.getText());
					this.tfVTotalesBaseNI.setText(""+baseNI);
				}
			}
			if(i ==2){
				if(!(this.tfVBase2.getText().equals("")) && !(this.tfVTipoIVA2.getText().equals("0"))){
					double st2 = Double.parseDouble(this.tfVBase2.getText()) + Double.parseDouble(this.tfVIVA2.getText());
					this.tfVST2.setText(""+st2);
				} else if (this.tfVTipoIVA2.getText().equals("0")){
					double baseNI = 0.0;
					if (!(this.tfVTotalesBaseNI.getText().equals(""))){
						baseNI = Double.parseDouble(this.tfVTotalesBaseNI.getText());
					}
					baseNI += Double.parseDouble(this.tfVBase2.getText());
					this.tfVTipoIVA2.setText("0");
					this.tfVIVA2.setText("0.0");
					this.tfVST2.setText(this.tfVBase2.getText());
					this.tfVTotalesBaseNI.setText(""+baseNI);
				}
			}
			if(i == 3){
				if(!(this.tfVBase3.getText().equals("")) && !(this.tfVTipoIVA3.getText().equals("0"))){
					double st3 = Double.parseDouble(this.tfVBase3.getText()) + Double.parseDouble(this.tfVIVA3.getText());
					this.tfVST3.setText(""+st3);
				} else if (this.tfVTipoIVA3.getText().equals("0")){
					double baseNI = 0.0;
					if (!(this.tfVTotalesBaseNI.getText().equals(""))){
						baseNI = Double.parseDouble(this.tfVTotalesBaseNI.getText());
					}
					baseNI += Double.parseDouble(this.tfVBase3.getText());
					this.tfVTipoIVA3.setText("0");
					this.tfVIVA3.setText("0.0");
					this.tfVST3.setText(this.tfVBase3.getText());
					this.tfVTotalesBaseNI.setText(""+baseNI);
				}
			}
			if (i == 4){
				if(!(this.tfVBase4.getText().equals("")) && !(this.tfVTipoIVA4.getText().equals("0"))){
					double st4 = Double.parseDouble(this.tfVBase4.getText()) + Double.parseDouble(this.tfVIVA4.getText());
					this.tfVST4.setText(""+st4);
				} else if (this.tfVTipoIVA4.getText().equals("0")){
					double baseNI = 0.0;
					if (!(this.tfVTotalesBaseNI.getText().equals(""))){
						baseNI = Double.parseDouble(this.tfVTotalesBaseNI.getText());
					}
					baseNI += Double.parseDouble(this.tfVBase4.getText());
					this.tfVTipoIVA4.setText("0");
					this.tfVIVA4.setText("0.0");
					this.tfVST4.setText(this.tfVBase4.getText());
					this.tfVTotalesBaseNI.setText(""+baseNI);
				}
			}
			if (i == 5){
				sumarSubtotales();
			}
			if (i == 0){
				sumarTotales();
			}
		}
	}

	private void sumarSubtotales() {
		if (modo == EDITAR){
			//bases
			double sumabases = 0.0;
			if (!(this.tfVBase1.getText().equals(""))){
				sumabases += Double.parseDouble(this.tfVBase1.getText());
			}
			if (!(this.tfVBase2.getText().equals(""))){
				sumabases += Double.parseDouble(this.tfVBase2.getText());
			}
			if (!(this.tfVBase3.getText().equals(""))){
				sumabases += Double.parseDouble(this.tfVBase3.getText());
			}
			if (!(this.tfVBase4.getText().equals(""))){
				sumabases += Double.parseDouble(this.tfVBase4.getText());
			}
			this.tfVTotalesBase.setText("" + sumabases);
			//tiposIVA
			//IVAs
			double sumaivas = 0.0;
			if (!(this.tfVIVA1.getText().equals("")) && !(this.tfVIVA1.getText().equals("0"))){
				sumaivas += Double.parseDouble(this.tfVIVA1.getText());
			}
			if (!(this.tfVIVA2.getText().equals("")) && (this.tfVIVA2.getText().equals("0"))){
				sumaivas += Double.parseDouble(this.tfVIVA2.getText());
			}
			if (!(this.tfVIVA3.getText().equals("")) && !(this.tfVIVA3.getText().equals("0"))){
				sumaivas += Double.parseDouble(this.tfVIVA3.getText());
			}
			if (!(this.tfVIVA4.getText().equals("")) && !(this.tfVIVA4.getText().equals("0"))){
				sumaivas += Double.parseDouble(this.tfVIVA4.getText());
			}
			this.tfVTotalesIVA.setText("" + sumaivas);
			//Subtotales
			double sumasts = 0.0;
			if (!(this.tfVST1.getText().equals(""))){
				sumasts += Double.parseDouble(this.tfVST1.getText());
			}
			if (!(this.tfVST2.getText().equals(""))){
				sumasts += Double.parseDouble(this.tfVST2.getText());
			}
			if (!(this.tfVST3.getText().equals(""))){
				sumasts += Double.parseDouble(this.tfVST3.getText());
			}
			if (!(this.tfVST4.getText().equals(""))){
				sumasts += Double.parseDouble(this.tfVST4.getText());
			}
			this.tfVTotalesST.setText("" + sumasts);
		}
	}

	private void sumarTotales() {
		if (modo == EDITAR){
			double total = 0.0;
			double subtotal = 0.0;
			double baseNI = 0.0;
			double retenciones = 0.0;
			if (!(this.tfVTotalesST.getText()).equals(""))
				subtotal += Double.parseDouble(this.tfVTotalesST.getText());
			if (!(this.tfVTotalesBaseNI.getText()).equals(""))
				baseNI = Double.parseDouble(this.tfVTotalesBaseNI.getText());
			if (!(this.tfVTotalesRetenciones.getText()).equals(""))
				retenciones = Double.parseDouble(this.tfVTotalesRetenciones.getText());

			total = subtotal + baseNI - retenciones;
			this.tfVTotal.setText(""+total);
		}
	}

	public void calcIVA(int i) {
		if (modo == EDITAR){
			if (i == 1 && !(this.tfVBase1.getText().equals("")) && !(this.tfVTipoIVA1.getText().equals("")) && !(this.tfVTipoIVA1.getText().equals("0"))){
				double base1 = Double.parseDouble(this.tfVBase1.getText());
				int tipoiva1 = Integer.parseInt(this.tfVTipoIVA1.getText());
				double IVA1 = (base1 * tipoiva1) / 100.00;
				this.tfVIVA1.setText(""+IVA1);
			} else if (this.tfVTipoIVA1.getText().equals("0")){
				double baseNI = Double.parseDouble(this.tfVTotalesBaseNI.getText());
				double asumar = Double.parseDouble(this.tfVBase1.getText());
				this.tfVBase1.setText("");
				this.tfVTipoIVA1.setText("");
				this.tfVIVA1.setText("");
				this.tfVST1.setText("");
				baseNI += asumar;
				this.tfVTotalesBaseNI.setText(""+baseNI);
			}
			if (i == 2 && !(this.tfVBase2.getText().equals("")) && !(this.tfVTipoIVA2.getText().equals("")) && !(this.tfVTipoIVA2.getText().equals("0"))){
				double base2 = Double.parseDouble(this.tfVBase2.getText());
				int tipoiva2 = Integer.parseInt(this.tfVTipoIVA2.getText());
				double IVA2 = (base2 * tipoiva2) / 100.00;
				this.tfVIVA2.setText(""+IVA2);
			} else if (this.tfVTipoIVA2.getText().equals("0")){
				double baseNI = Double.parseDouble(this.tfVTotalesBaseNI.getText());
				double asumar = Double.parseDouble(this.tfVBase2.getText());
				this.tfVBase2.setText("");
				this.tfVTipoIVA2.setText("");
				this.tfVIVA2.setText("");
				this.tfVST2.setText("");
				baseNI += asumar;
				this.tfVTotalesBaseNI.setText(""+baseNI);
			}
			if (i == 3 && !(this.tfVBase3.getText().equals("")) && !(this.tfVTipoIVA3.getText().equals("")) && !(this.tfVTipoIVA3.getText().equals("0"))){
				double base3 = Double.parseDouble(this.tfVBase3.getText());
				int tipoiva3 = Integer.parseInt(this.tfVTipoIVA3.getText());
				double IVA3 = (base3 * tipoiva3) / 100.00;
				this.tfVIVA3.setText(""+IVA3);
			} else if (this.tfVTipoIVA3.getText().equals("0")){
				double baseNI = Double.parseDouble(this.tfVTotalesBaseNI.getText());
				double asumar = Double.parseDouble(this.tfVBase3.getText());
				this.tfVBase3.setText("");
				this.tfVTipoIVA3.setText("");
				this.tfVIVA3.setText("");
				this.tfVST3.setText("");
				baseNI += asumar;
				this.tfVTotalesBaseNI.setText(""+baseNI);
			}
			if (i == 4 && !(this.tfVBase4.getText().equals("")) && !(this.tfVTipoIVA4.getText().equals(""))&& !(this.tfVTipoIVA4.getText().equals("0"))){
				double base4 = Double.parseDouble(this.tfVBase4.getText());
				int tipoiva4 = Integer.parseInt(this.tfVTipoIVA4.getText());
				double IVA4 = (base4 * tipoiva4) / 100.00;
				this.tfVIVA4.setText(""+IVA4);
			} else if (this.tfVTipoIVA4.getText().equals("0")){
				double baseNI = Double.parseDouble(this.tfVTotalesBaseNI.getText());
				double asumar = Double.parseDouble(this.tfVBase4.getText());
				this.tfVBase4.setText("");
				this.tfVTipoIVA4.setText("");
				this.tfVIVA4.setText("");
				this.tfVST4.setText("");
				baseNI += asumar;
				this.tfVTotalesBaseNI.setText(""+baseNI);
				}
		}
	}


//#endregion

}
*/