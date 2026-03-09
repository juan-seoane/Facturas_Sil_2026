package modelo.records;

import modelo.ModeloFacturas;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

public class Factura implements Comparable<Factura> {

//#region CAMPOS
    private Integer ID;
    private String numeroFactura;
    private Fecha fecha;
    private RazonSocial RS;
    private TipoGasto categoria;
    public boolean esDevolucion;
    private ArrayList<Extracto> extractos;
    private Totales totales;
    private Nota nota;
//#endregion

//#region CAMPOS_FX
    private ObservableValue<Double> FxRetenciones;
    private ObservableValue<Integer> FxTipoRet;
    private ObservableValue<Double> FxTotal;
    private SimpleStringProperty FxNota;
    private ObservableValue<Double> FxBaseNI;
    private ObservableValue<Double> FxST;
    private ObjectProperty<Double> FxIVA;
    private ObservableValue<Integer> FxTipoIVA;
    private ObservableValue<Double> FxBase;
    private ObservableValue<Integer> FxNumExtr;
    private SimpleStringProperty FxRS;
    private SimpleStringProperty FxCat;
    private SimpleStringProperty FxEsDev;
    private SimpleStringProperty FxFecha;
    private SimpleStringProperty FxNumFact;
    private ObservableValue<Integer> FxID;
//#endregion
    
//#region CONSTR
    public Factura(Integer ID, String numeroFactura, Fecha fecha, RazonSocial RS, TipoGasto categoria, boolean esDevolucion, ArrayList<Extracto> extractos, Totales totales, Nota nota) {
        this.ID = ID;
        this.numeroFactura = numeroFactura;
        this.fecha = fecha;
        this.RS = RS;
        this.categoria = categoria;
        this.esDevolucion = esDevolucion;
        this.extractos = extractos;
        this.totales = totales;
        this.nota = nota;
    }

    public Factura() {
		this(0,"000000-OOO",new Fecha(17,03,24),new RazonSocial(), new TipoGasto("tipoGasto_generico","descripción"),false,new ArrayList<Extracto>(),new Totales(),null);
	}
//#endregion

//#region GETTERS/SETTERS
	public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getNumeroFactura() {
        return numeroFactura;
    }

    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    public Fecha getFecha() {
        return fecha;
    }

    public void setFecha(Fecha fecha) {
        this.fecha = fecha;
    }

    public RazonSocial getRS() {
        return RS;
    }

    public void setRS(RazonSocial RS) {
        this.RS = RS;
    }

    public TipoGasto getCategoria() {
        return categoria;
    }

    public void setCategoria(TipoGasto categoria) {
        this.categoria = categoria;
    }

    public boolean isEsDevolucion() {
        return esDevolucion;
    }

    public void setEsDevolucion(boolean esDevolucion) {
        this.esDevolucion = esDevolucion;
    }

    public ArrayList<Extracto> getExtractos() {
        return extractos;
    }

    public void setExtractos(ArrayList<Extracto> extractos) {
        this.extractos = extractos;
    }

    public Totales getTotales() {
        return totales;
    }

    public void setTotales(Totales totales) {
        this.totales = totales;
    }

    public Nota getNota() {
        return nota;
    }

    public void setNota(Nota nota) {
        this.nota = nota;
    }
//#endregion

//#region OVERRIDES
    @Override
    public String toString(){
        String cadenaResp = this.ID + "," + this.numeroFactura + "," +  this.fecha.toString() + "," + this.RS.getID() + "," + this.RS.getNif() + "," + this.RS.getNombre() + "," + this.categoria.getTipo() + "," + (this.esDevolucion?"S":"N") + "," + this.extractos.size() + ", TOTS-> " + this.totales.getBase() + "," + ((this.totales.isVariosIVAs())?"S":"N,"+this.totales.getTipoIVA()) + "," + this.totales.getIVA() + "," + this.totales.getRet() + "," + this.totales.getRetenciones() + "," + this.totales.getTotal() + "," + ((this.nota!=null)?this.nota.getTexto():"sinNOTA");
        // ojo que sólo puede haber una Nota...
        if (this.extractos.size()>1){
            for (int i=0; i<this.extractos.size(); i++){
                cadenaResp += "\nEXTR->" + this.extractos.get(i).getBase() + "," + this.extractos.get(i).getTipoIVA() + "," + this.extractos.get(i).getIVA() + "," + this.extractos.get(i).getSubtotal()+ "#";
            }
        }

        return cadenaResp;
    }

// Revisar la forma de comparar facturas - las facturas se ordenan por fechas con el método sort()
// Las facturas se ordenan por fechas. Si las fechas son iguales, se ordenan por el NIF de la RazonSocial
    @Override
    public int compareTo(Factura b){
        if ((this.fecha.compareTo(b.getFecha()))<0) 
            return -1;
        if ((this.fecha.compareTo(b.getFecha()))>0) 
            return 1;
        return (this.RS.getNif().compareTo(b.getRS().getNif()));
   }
//#endregion

//#region TOVECTOR()   
   public static Vector<Factura> toVector(Factura f){
     Vector<Factura> vector = new Vector<Factura>();
     vector.add(f);
 
     //System.out.println("[Factura.java>toVector] transformando Factura a Vector: "+vector.toString());
     return vector;
   }
//#endregion   

//#region FXgetters
    public ObservableValue<Integer> getFxID() {
        this.FxID = new SimpleIntegerProperty(this.ID).asObject();
        return this.FxID;
    }

    public StringProperty getFxNumFact() {
        this.FxNumFact = new SimpleStringProperty(this.numeroFactura);
        return this.FxNumFact;
    }

    public StringProperty getFxFecha() {
        this.FxFecha = new SimpleStringProperty(this.fecha.toString());
        return this.FxFecha;
    }

    public StringProperty getFxRS() {
        this.FxRS = new SimpleStringProperty(this.RS.getNombre());
        return this.FxRS;
    }

	public StringProperty getFxCat() {
		this.FxCat = new SimpleStringProperty(this.getCategoria().getTipo());
        return this.FxCat;
	}

	public StringProperty getFxDev() {
		this.FxEsDev = new SimpleStringProperty(this.esDevolucion?"X":"");
        return this.FxEsDev;
	}

	public ObservableValue<Integer> getFxNumExtr() {
		this.FxNumExtr = new SimpleIntegerProperty(this.extractos.size()).asObject();
        return this.FxNumExtr;
	}

	public ObservableValue<Double> getFxBase() {
		this.FxBase = new SimpleDoubleProperty(this.getTotales().getBase()).asObject();
        return this.FxBase;
	}

	public ObservableValue<Integer> getFxTipoIVA() {
		this.FxTipoIVA = new SimpleIntegerProperty(this.getTotales().getTipoIVA()).asObject();
        return this.FxTipoIVA;
	}

	public ObservableValue<Double> getFxIVA() {
		this.FxIVA = new SimpleDoubleProperty(this.getTotales().getIVA()).asObject();
        return this.FxIVA;
	}
    public ObservableValue<Double> getFxST() {
        this.FxST = new SimpleDoubleProperty(this.getTotales().getSubtotal()).asObject();
        return this.FxST;
    }

    public ObservableValue<Double> getFxBaseNI() {
        this.FxBaseNI = new SimpleDoubleProperty(this.getTotales().getBaseNI()).asObject();
        return this.FxBaseNI;
    }

	public ObservableValue<Integer> getFxTipoRet() {
		this.FxTipoRet = new SimpleIntegerProperty(this.getTotales().getRet()).asObject();
        return this.FxTipoRet;
	}

	public ObservableValue<Double> getFxRetenc() {
		this.FxRetenciones = new SimpleDoubleProperty(this.getTotales().getRetenciones()).asObject();
        return this.FxRetenciones;
	}

	public ObservableValue<Double> getFxTotal() {
		this.FxTotal = new SimpleDoubleProperty(this.getTotales().getTotal()).asObject();
        return this.FxTotal;
	}

	public StringProperty getFxNota() {
		this.FxNota = new SimpleStringProperty(this.nota!=null?"*":"");
        return this.FxNota ;
	}
//#endregion

//#region CSVaFCT
	public static synchronized Factura convertirCSVaFCT(String[] linea) throws NullPointerException, IOException {
        Factura f = null;
        var extractos = new ArrayList<Extracto>();
        //System.out.println("[Factura>convertirCSVaFCT] Primer campo en linea a leer: " + linea[0]);
        if(linea[0].contains("#")){
            //Se salta el comentario
            //System.out.println("[Factura>ConvertirCSVaFCT] --- comentario detectado ---");
            return null;
        }else if (!(linea[0].equals(""))){
                //Se lee la factura
                //System.out.println("[Factura>ConvertirCSVaFCT] --- leyendo factura num " + Integer.parseInt(linea[0]) + " ---");
                String[] fecha = linea[2].split("/");
                int dia = Integer.parseInt(fecha[0]);
                int mes = Integer.parseInt(fecha[1]);
                int año = Integer.parseInt(fecha[2]);
                int rsid = Integer.parseInt(linea[3]);
                // Revisar esto, sólo vale para NIF, no para CIF... Habrá que hacer un método nuevo que convierta un array de cadenas de texto en un objeto NIF
                // Además habría que ver si existe ya la RS (según el ID) 
                String[] rsnif = linea[4].split("-");
                NIF nifRS = NIF.array2nif(rsnif);
                String rsnombre = linea[5];
                Nota nota;
                if (linea[18].equals("0")){
                        nota = null;
                }else{
                    nota = new Nota(/*Integer.parseInt(linea[18]),*/ linea[19]);
                }
                // Revisar esto, sólo vale para cuando hay notas... Hacer un método para leer la nota si existe, y si no, hacerla null
                // Se podría poner el TipoIVA como un Integer en vez de una clase TipoIVA...
                f = new Factura(Integer.parseInt(linea[0]),linea[1],new Fecha(dia, mes, año),new RazonSocial(rsid,nifRS,rsnombre),new TipoGasto(linea[6],linea[6]),(linea[7].equals("S"))?true:false,extractos, new Totales(Double.parseDouble(linea[9]),(linea[10].equals("S"))?true:false,Integer.parseInt(linea[11]),Double.parseDouble(linea[12]),Double.parseDouble(linea[13]),Double.parseDouble(linea[14]),Integer.parseInt(linea[15]),Double.parseDouble(linea[16]),Double.parseDouble(linea[17]),linea[6]),((nota==null)?null:nota));            
        }else{
            //se lee el extracto
            //System.out.println("[Factura>ConvertirCSVaFCT] --- leyendo extracto num " + linea[1] + "---");
            f = ModeloFacturas.facturas_prev.getLast();
            extractos = f.getExtractos();
            extractos.add(new Extracto(Double.parseDouble(linea[9]),Integer.parseInt(linea[11]),Double.parseDouble(linea[12]),Double.parseDouble(linea[13]), linea[6]));
            f.setExtractos(extractos);
        // REVIEW - 24-06-15 : - ojo con este paso: se quita la última entrada de la lista de facturas...
            ModeloFacturas.facturas_prev.removeLast();
            
        }
        return f;
    }
//#endregion  

//#region FCTaCSV
    public static synchronized ArrayList<String[]> convertirFCTaCSV(Factura f){
        var lista = new ArrayList<String[]>();
        String[] arrayCSV = new String[20];
        arrayCSV[0] = "" + f.getID();
        arrayCSV[1] = f.getNumeroFactura();
        arrayCSV[2] = f.getFecha().toString();
        arrayCSV[3] = "" + f.getRS().getID();
        arrayCSV[4] = f.getRS().getNif().toString();
        arrayCSV[5] = f.getRS().getNombre();
        arrayCSV[6] = f.getCategoria().getTipo();
        arrayCSV[7] = ((f.esDevolucion)?"S":"N");
        arrayCSV[8] = "" + f.extractos.size();
        arrayCSV[9] =  "" + f.getTotales().getBase();
        arrayCSV[10] = ((f.getTotales().isVariosIVAs())?"S":"N");
        arrayCSV[11] = "" + f.getTotales().getTipoIVA();
        arrayCSV[12] = "" + f.getTotales().getIVA();
        arrayCSV[13] = "" + f.getTotales().getSubtotal();
        arrayCSV[14] = "" + f.getTotales().getBaseNI();
        arrayCSV[15] = "" + f.getTotales().getRet();
        arrayCSV[16] = "" + f.getTotales().getRetenciones();
        // NOTE - 24-07-24 : El Total se marca negativo si es Devolución
        arrayCSV[17] = (f.esDevolucion?(((f.getTotales().getTotal())>0)?"-":""):"") + f.getTotales().getTotal();
        // REVIEW : 24-06-24 - Sólo debería haber 0 o 1 notas... no tiene sentido el Objeto Nota con un Número y un Texto, llega un String con valor 'null' o el 'Texto'
        // REVIEW : 24-06-24 - En la tabla puede marcarse el campo Nota con un '*'' si tiene Nota, y cuando el cursor esté encima del asterisco, que se lea la nota como un texto de ayuda al lado del cursor....
        if (f.getNota()!=null){
            //arrayCSV[18] = ""+f.getNota().getNumero();
        // REVIEW - 24-07-31 : Así que el número de la Nota siempre es 0 o 1...
            arrayCSV[18] = "1";
            arrayCSV[19] = f.getNota().getTexto();
        }else if(f.getNota()==null){
            arrayCSV[18] = "0";
        }
        lista.add(arrayCSV);
        
        if (f.extractos.size()>0){
            int i = 1;    
            for (Extracto ex : f.extractos){
                arrayCSV = new String[20];
                arrayCSV[1] = ""+i;
                arrayCSV[6] = ex.getConcepto();
                arrayCSV[9] = "" + ex.getBase();
                arrayCSV[11] = "" + ex.getTipoIVA();
                arrayCSV[12] = "" + ex.getIVA();
                arrayCSV[13] = "" + ex.getSubtotal();
        
                lista.add(arrayCSV);
                i++;
            }
        }
        //System.out.println("[Factura>convertirFCTaCSV] Devolviendo " + lista.size() + " linea(s)");

        return lista;
    }
//#endregion

}

