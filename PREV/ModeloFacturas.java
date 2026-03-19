package app.services;

import app.controladores.*;
import java.io.IOException;
import java.util.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javax.swing.*;
import modeloDominio.dtos.records.Factura;
import modeloDominio.dtos.records.RazonSocial;
import modeloNegocio.Infraestructura.filesystem.Fichero;
import modeloNegocio.servicios.FacturaService;
import controladores.fxcontrollers.*;

public class ModeloFacturas {

    //#region CAMPOS
    static ModeloFacturas instancia;
    public static ArrayList<Factura> listaFacturas;
    // En algún momento tendré que arreglar esto... facturas_prev puede no hacer falta...
    public static ArrayList<Factura> facturas_prev;
    private ArrayList<String[]> arrayFacturas;
    static Fichero<Factura> ficheroFacturas;
    // Repasar los vectores de Facturas y demás...
//    static Stack<Factura> pilafacturasant = new Stack<Factura>();
 //   static Stack<Factura> pilafacturassig = new Stack<Factura>();
//    static Vector<Factura> vectorfacturas;
    //static Vector vectorcolumnas;
    static int ultimaID;
    static int numeroFacturas = 0;

    //#endregion

    //#region constructor
    private ModeloFacturas() throws NullPointerException, IOException {
        //System.out.println("[ModeloFacturas>Constructor] Creando nuevo ModeloFacturas");
        ficheroFacturas = new Fichero<Factura>(Config.getConfig(Controlador_prev.getUsuario()).getRutaFCT());
        //System.out.println("[ModeloFacturas>Constructor] Leyendo fichero FCT -> " + Config.getConfig(Controlador.usuario).getRutaFCT() + " de Facturas del usuario "+ Controlador.usuario);
        this.arrayFacturas = ficheroFacturas.leerCSV(Config.getConfig(Controlador_prev.getUsuario()).getRutaFCT());
        ModeloFacturas.facturas = ConvertirArrayCSVenListaFCT(this.arrayFacturas);
        numeroFacturas = ModeloFacturas.facturas.size();
        //System.out.println("[ModeloFacturas>Constructor] Fichero FCT de Facturas leido. Numero de Facturas = "+ numeroFacturas);
        ultimaID = ModeloFacturas.facturas.size();
        //System.out.println("[ModeloFacturas.java>Constructor] Última ID : " + ultimaID);
 //       vectorfacturas = generarVectorFacturas();
        //System.out.println("[ModeloFacturas.java>Constructor] Vector de Facturas generado!\n********************");

        //System.out.println(" [ModeloFacturas>Constructor] Tamano del array de facturas: " + facturas.size());
    }

    //#endregion

    //#region getModelo
    public static synchronized ModeloFacturas getModelo() {
        if (instancia == null) try {
            instancia = new ModeloFacturas();
        } catch (NullPointerException | IOException e) {
            System.out.println("[ModeloFacturas>getModelo] Exc creando el modeloFCT: " + e.getMessage());
        }
        return instancia;
    }

    //#endregion

    //#region fct_getters
    public static synchronized int getUltimaID() {
        return ultimaID;
    }

    public static synchronized int getNumeroFacturas() {
        facturas = (ArrayList<Factura>) leerFacturasSinFiltrar();
        numeroFacturas = facturas.size();
        return numeroFacturas;
    }

    public Factura getFactura(int index) throws NullPointerException, IOException {
        List<Factura> listafacturas = leerFacturasSinFiltrar();
        if (listafacturas.size() > 0) {
            return listafacturas.get(index);
        } else {
            return new Factura();
        }
    }

    public Stack<Factura> getPilaFacturasAnt() {
        return null; //pilafacturasant;
    }

    public Stack<Factura> getPilaFacturasSig() {
        return null; //pilafacturassig;
    }

    public int getIndexOfFactura(Factura f) throws NullPointerException, IOException {
        int index;

        ModeloFacturas.listaFacturas = (ArrayList<Factura>)leerFacturasSinFiltrar();
        for (Factura fact : listaFacturas) if (f.equals(fact)) {
            index = fact.ID - 1;
            return index;
        }
        return 0;
    }

    //#endregion

    //#region (AUTOSAVE)
    public boolean autosave(String ruta) {
        /*         Fichero<Factura> auto = new Fichero<Factura>(ruta);
        if (auto.escribir((ArrayList<Factura>)facturas))
            return true;
        else */return false;
    }

    //#endregion
    // TODO : 26-03-13 : Cambiar a LeerFacturas() (con filtros, a base de streams)
    //#region leerFacturas
    public ObservableList<Factura> getListaFXFacturas() {
        List<Factura> fact_prev = null;
        try {
            fact_prev = leerFacturasSinFiltrar();
        } catch (NullPointerException e) {
            System.out.println("[ModeloFacturas>getListaFXFacturas] Error al recoger la lista observable (para JFX) de facturas : " + e.getMessage());
        }
        var facturasFX = FXCollections.observableList(fact_prev);
        return facturasFX;
    }

    // public List<Factura> leerFacturas() throws NullPointerException, IOException {
    //     ficheroFacturas = new Fichero<>(Config.getConfig(Controlador.getUsuario()).getRutaFCT());
    //     this.arrayFacturas = ficheroFacturas.leerCSV(ficheroFacturas.rutaArchivo);
    //     ModeloFacturas.facturas = ConvertirArrayCSVenListaFCT(this.arrayFacturas);
    //     numeroFacturas = ModeloFacturas.facturas.size();
    //     if (numeroFacturas == 0) {
    //         ModeloFacturas.facturas.add(new Factura());
    //     }

    //     ultimaID = ModeloFacturas.facturas.size();

    //     return filtrar(ModeloFacturas.facturas);
    // }

    public static List<Factura> leerFacturasSinFiltrar() {
        ficheroFacturas = new Fichero<>(Config.getConfig(Controlador_prev.getUsuario()).getRutaFCT());
        ArrayList<String[]> arrayFct = ficheroFacturas.leerCSV(ficheroFacturas.rutaArchivo);
        ArrayList<Factura> listaFct = null;
        listaFct = ConvertirArrayCSVenListaFCT(arrayFct);
        numeroFacturas = listaFct.size();
        if (numeroFacturas == 0) {
            listaFct.add(new Factura());
            numeroFacturas = listaFct.size();
        }

        return listaFct;
    }

    public static List<Factura> getFacturas_prev() {
        return facturas_prev;
    }

    public static void setFacturas_prev(ArrayList<Factura> f_p) {
        facturas_prev = f_p;
    }

    //#endregion

    //#region listaFCTaCSVar
    public synchronized ArrayList<String[]> ConvertirListaFCTaCSV(ArrayList<Factura> lista) {
        var arrayCSV = new ArrayList<String[]>();
        try {
            arrayCSV.add(getColumnas());
        } catch (NullPointerException | IOException e) {
            //System.out.println("Error al trasvasar los nombres de columnas al CSV");
            e.printStackTrace();
        }
        int i = 0;
        for (Factura f : lista) {
            arrayCSV.addAll(Factura.convertirFCTaCSV(f));
            i++;
        }
        return arrayCSV;
    }

    //#endregion

    //#region ArrayCSVaFCT
    private static synchronized ArrayList<Factura> ConvertirArrayCSVenListaFCT(ArrayList<String[]> arrayFacturas) {
        facturas_prev = new ArrayList<Factura>();
        if (arrayFacturas.size() > 0) {
            for (String[] linea : arrayFacturas) {
                Factura resp = null;
                try {
                    resp = Factura.convertirCSVaFCT(linea);
                } catch (NullPointerException | IOException e) {
                    e.printStackTrace();
                }
                if (resp != null) {
                    facturas_prev.add(resp);
                }
            }
        } else {
            facturas_prev.add(new Factura());
        }
        //System.out.println("\n[ModeloFacturas>ConvertirArrayCSVenListaFCT] Numero de facturas convertidas desde el CSV: " + facturas_prev.size() + "\n");
        return facturas_prev;
    }

    //#endregion
    // TODO : 26-03-13 : Convertir la función 'filtrar' en una función que se base en streams
    //#region filtrar
    public List<Factura> filtrar(List<Factura> lista) {
        // REVIEW - 24-06-14 : - Habría que convertir este ArrayList<String[]> a un ArrayList<Factura>...

        for (int i = 0; i < lista.size(); i++) {
            Factura f1 = lista.get(i);
            for (int j = 0; j < lista.size(); j++) {
                Factura f2 = lista.get(j);
                if (i == j) continue; else if (f1.equals(f2)) {
                    lista.remove(f2);
                }
            }
        }

        int ultID = 0;
        int i = 0;
        for (Factura f : lista) {
            i++;
            // REVIEW - 24-05-06 : - Hay que reemplazar estas operaciones con setters... No existen en un Java record...
            f.setID(i);
            ultID = i;
        }
        if (ultID != 0) {
            ultimaID = ultID;
        }

        /*
        List<Factura> lista2, lista3, lista4;
        if (TablaFacturas.filtrosActivos()){
            if (ControladorFacturas.filtros.getChbFiltroFecha().isSelected())
            {
            String año = Config.getConfigActual().configData.año().año()+"";
            FiltroFecha filtro1 = new FiltroFecha(ControladorFacturas.filtros.getFechaInicio(),ControladorFacturas.filtros.getFechaFinal());
            lista2 = filtro1.filtrar(lista);
            }
            else lista2 = lista;
            if (ControladorFacturas.filtros.getChbFiltroCategoria().isSelected())
            {
            FiltroCategoria filtro2 = new FiltroCategoria(ControladorFacturas.filtros.getCmbCategoriasFiltros().getSelectedItem().toString());
            lista3 = filtro2.filtrar(lista2);
            }
            else lista3 = lista2;
            if (ControladorFacturas.filtros.getChbFiltroDistribuidor().isSelected())
            {
            FiltroDistribuidor filtro3 = new FiltroDistribuidor(ControladorFacturas.filtros.getFiltroDist());
            lista4 = filtro3.filtrar(lista3);
            }
            else lista4 = lista3;

//ACORDARSE DE ACTUALIZAR LOS TOTALES DESPUES DE FILTRAR!
            return lista4;
        }
*/
        return lista;
    }

    //#endregion

    //#region fct_ops
    public boolean insertarFacturas(ArrayList<Factura> facturas) throws NumberFormatException, IOException {
        return (FacturaService.insertarFacturas(facturas));
    }

    public boolean anexarFactura(Factura factura) throws NumberFormatException, IOException {
        // REVIEW - 24-05-06 : - Hay que reemplazar estas operaciones con setters... No existen en un Java record...
        //        factura.setID(this.ultimaID++);
        numeroFacturas++;
        PanelControl.getPanelControl().setNumfacturas(numeroFacturas);
        return (ficheroFacturas.anexar(factura));
    }

    public boolean editarFactura(ArrayList<Factura> listafacturas, Factura factura, int index)
        throws NumberFormatException, IOException {
        System.out.println(" [ModeloFacturas>editarFactura] Index de factura : " + index);
        listafacturas.set(index, factura);
        Collections.sort(listafacturas);
        int idf = 1;
        for (Factura f : listafacturas) {
            f.ID = idf;
            idf++;
        }
        var datosLista = ConvertirListaFCTaCSV(listafacturas);

        if (ficheroFacturas.guardarCSV(datosLista)) {
// NOTE :26-03-15 : Aquí estoy leyendo facturas SIN FILTROS
            leerFacturasSinFiltrar();
            return true;
        }
        return false;
    }

    public boolean actualizarDistribuidorEnFacturas(RazonSocial antrs, RazonSocial nuevars) {
        return (FacturaService.actualizarDistrihbuidorEnFacturas(antrs, nuevars));
    }

    public boolean borrarFactura(Factura factura) {
       // pilafacturasant.push(factura);
        System.out.println(" [ModeloFacturas>borrarFactura] borrando factura:\n" + factura.toString());
        // Crear una alerta de tipo confirmación
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Advertencia!");
        alert.setHeaderText("[ModeloFacturas]");
        alert.setContentText("¿Desea realmente borrar la factura?");
        alert.initModality(Modality.APPLICATION_MODAL);
        if (ControladorFacturas.visorFCT.isShowing()) alert.initOwner(
            ControladorFacturas.visorFCT
        ); else alert.initOwner(ControladorFacturas.tablaFCT);
        // Mostrar la alerta y esperar la respuesta del usuario
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // El usuario seleccionó "Sí"
            facturas.remove(factura);
            // Hay que cambiar las ID de las facturas (no necesariamente ordenarlas)
            Collections.sort(facturas);
            int i = 1;
            for (Factura f : facturas) {
                f.setID(i);
                i++;
            }
            /*// Crear una alerta de tipo información
            Alert alert2 = new Alert(AlertType.INFORMATION);
            alert2.setTitle("Atención!!");
            alert2.setHeaderText("[ModeloFacturas]");
            alert2.setContentText("Borrando factura!");
            // Mostrar la alerta y esperar a que el usuario la cierre
            alert2.showAndWait();*/
            System.out.println("Borrando factura!");
            numeroFacturas--;
            var datosLista = ConvertirListaFCTaCSV(facturas);

            if (ficheroFacturas.guardarCSV(datosLista)) {
                leerFacturasSinFiltrar();
                return true;
            }
            return false;
        } else {
            // El usuario seleccionó "No" o cerró el cuadro de diálogo
            /*// Crear una alerta de tipo información
            Alert alert2 = new Alert(AlertType.INFORMATION);
            alert2.setTitle("Atención!!");
            alert2.setHeaderText("[ModeloFacturas]");
            alert2.setContentText("Operación de borrado de Factura cancelada.");
            // Mostrar la alerta y esperar a que el usuario la cierre
            alert2.showAndWait();*/
            System.out.println("Operacion de borrado de Factura cancelada.");
            return false;
        }
    }

    //#endregion

    //#region RecogerFORM
    /*
    public Factura recogerFormulario(FormularioFact form) {
        boolean isCIF;
        String letra;
        int numero = form.getNumNIFRS();
        if((letra = form.getLetraNIFRS().trim().toUpperCase()).equals("")){
            letra = form.getLetraCIFRS().trim().toUpperCase();
            isCIF = true;
        }else isCIF = false;

        String razon = form.getRS().toUpperCase();
        String textoNota = form.getNota();

        if (textoNota.equals("")) {
            textoNota = "";
        }
        if (form.esDevolucion()){
            textoNota = "Devolucion - "+textoNota;
        }
        // REVIEW  - 24-04-11 : - Cambiar los datos de los formularios por datos obtenidos de la GUI JFX
        int dia = form.dia();
        int mes = form.mes();
        int año = form.año();
        String numeroFactura = form.getNumeroFactura().toUpperCase();
        String tipoGasto = form.getTipoGasto().toUpperCase();
        boolean esDevolucion = form.esDevolucion();
        form.limpiarFormulario();

        Fecha fecha = new Fecha(dia, mes, año);
// Necesito un método (estático, a poder ser) para generar automáticamente el ID de cada Nota
        RazonSocial rs = completarRS(new RazonSocial(1, new NIF(numero, letra, isCIF), razon, new Nota(0,"")));
        // HAY QUE CONSEGUIR INTRODUCIR TODA LA RAZON SOCIAL COMO APARECE EN DISTRIBUIDORES
        ArrayList<Extracto> subfacturas = form.getSubfacturas();

        Totales totales = form.getTotales();

        Nota nota = new Nota(1, textoNota);

        Factura f = new Factura(1, numeroFactura, fecha, rs, new TipoGasto(tipoGasto, ""), esDevolucion, subfacturas, totales, nota);
//        if (form.getEstado().equals("editando")) {
//
//        } else {
////            facturas.add(f);
////            insertarFacturas((ArrayList) facturas);
////            this.facturas = leerFacturas();
//        }
        pilafacturasant.push(f);
        return f;
    }

    public RazonSocial completarRS(RazonSocial razon){
       Fichero<RazonSocial> ficheroRS = new Fichero<RazonSocial>(Config.getConfigActual().getRutaRS());
       ArrayList<RazonSocial> distribuidores = ficheroRS.leer();
       for (RazonSocial rs : distribuidores)
           if (rs.equals(razon))
               return rs;
       JOptionPane.showMessageDialog(null,"El distribuidor no concuerda con ninguno de los registrados!");
       return razon;
    }
*/
    //#endregion

    //#region calcTOTs
    public String[] calcularTotales() {
        List<Factura> listaFacturas = getListaFXFacturas();
        //System.out.println("[ModeloFacturas>calcularTotales] Calculando resumen de datos ");

        int cuenta = 0;
        double base = 0;
        double iva = 0;
        double subtotal = 0;
        double baseNI = 0;
        double retenc = 0;
        double total = 0;

        for (Factura f : listaFacturas) {
            cuenta++;
            base += f.totales.getBase();
            iva += f.totales.getIVA();
            subtotal += f.totales.getSubtotal();
            baseNI += f.totales.getBaseNI();
            retenc += f.totales.getRetenciones();
            total += f.totales.getTotal();
        }
        String[] datos = { "" + base, "" + iva, "" + subtotal, "" + baseNI, "" + retenc, "" + total, "" + cuenta };
        return datos;
    }

    //#endregion

    //#region genVectorFact
    // repasar esta función, puede que no funcione...
    public Vector<Factura> generarVectorFacturas() {
        var vectorfacturas = new Vector<Factura>();

        if (facturas.size() == 0) {
            vectorfacturas.add(new Factura());
        } else {
            for (Factura f : ModeloFacturas.facturas) {
                vectorfacturas.add(f);
            }
        }
        return vectorfacturas;
    }

    //#endregion

    //#region getColumnas
    public static String[] getColumnas() throws NullPointerException, IOException {
        String[] columnas = new String[Config.getConfig(Controlador_prev.getUsuario()).uiData.getNombreColsFCT().length];
        for (int i = 0; i < Config.getConfig(Controlador_prev.getUsuario()).uiData.getNombreColsFCT().length; i++) {
            if (i == 0) {
                columnas[i] = "#";
                columnas[i] += Config.getConfig(Controlador_prev.getUsuario()).uiData.getNombreColsFCT()[i];
            } else {
                columnas[i] = Config.getConfig(Controlador_prev.getUsuario()).uiData.getNombreColsFCT()[i];
            }
        }

        return columnas;
    }
    //#endregion

}
