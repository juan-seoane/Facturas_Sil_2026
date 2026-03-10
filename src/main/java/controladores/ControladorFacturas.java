package controladores;

import modelo.*;
import modelo.records.Factura;
import ui.helpers.FxmlHelper;
import ui.ventanas.VentanaFiltros;
import ui.fxcontrollers.FxCntrlTablaFCT;
import ui.fxcontrollers.FxCntrlVisorFCT;
import ui.fxcontrollers.PanelControl;

import java.util.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.Component;
import java.awt.HeadlessException;
import java.io.IOException;

public class ControladorFacturas extends Thread {
//NOTE - 24-07-14 : Singleton
    static ControladorFacturas instancia;
//#region CAMPOS_CFCT
//NOTE - 24-07-14 : Se intenta que los campos sean privados y que sólo se acceda a ellos mediante getters ... sobre todo los que pueden llevar a conflictos, como los contrFX
    Controlador ctrlPpal;
    public PanelControl pc;
    //static VentanaFiltros filtros;
    public ModeloFacturas m;
    static FxCntrlTablaFCT FXcontrlTablaFCT;
    static FxCntrlVisorFCT FXcontrlVisorFCT;
    public static Stage tablaFCT;
    //ANCHOR - visor FCT
    public static Stage visorFCT;
    //ANCHOR - tableView
    TableView<Factura> tableViewFCT;
    public static Factura facturaActual = null;
    static boolean GUIon = false;
    //public static CyclicBarrier  barreraTablaFCT;
    public static CyclicBarrier barreraVisor;
//#endregion

//#region CFCT_constr
    private ControladorFacturas() throws InterruptedException, BrokenBarrierException {
        //System.out.println("[ControladorFactuCras>Constructor] Creando el ControladorFacturas...");

        /* barreraVisor = new CyclicBarrier(2,() -> {
            //System.out.println( "[ControladorFactuCras>Constructor]  El hilo " + Thread.currentThread().getName() + " entra ahora en la barrera Visor");
        }); */

        m = ModeloFacturas.getModelo();
        //ANCHOR - 24-07-14 : FxCntrlTablaFCT
        if(GUIon){
            FXcontrlTablaFCT = FxCntrlTablaFCT.getFxController();
            //System.out.println("[ControladorFacturas>constructor] El ControladorFX de la tablaFCT fue asignado correctamente");
        }else{
            //System.out.println("[ControladorFacturas>constructor] El ControladorFX de la tablaFCT sigue siendo NULL");
        }
        //System.out.println("[ControladorFacturas>Constructor] Saliendo del constructor de ControladorFacturas...");
    }
//#endregion

//#region GETTERS_CFCT
    public static synchronized ControladorFacturas getControlador(){

        if (instancia == null) {
            //System.out.println("[ControladorFacturas>getControlador()] Instancia vacia, creando una nueva instancia generica (sin GUI asociada)");
            try {
                instancia = new ControladorFacturas();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        }

        return instancia;
    }

    public static synchronized ControladorFacturas getControlador(FxCntrlTablaFCT fxcntrfct){

        if (instancia == null) {
            //System.out.println("[ControladorFacturas>getControlador(fxcontr)] Instancia vacia, creando una nueva instancia generica con GUI asociada!");
            try {
                instancia = new ControladorFacturas();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
        GUIon = true;
        //ANCHOR - 24-07-14 : FxCntrlTablaFCT
        instancia.setFXcontrlTablaFCT(FXcontrlTablaFCT);
        return instancia;
    }
//#endregion

//#region get/set_ContrFX
    //ANCHOR - 24-07-14 : FxCntrlTablaFCT
    public synchronized FxCntrlTablaFCT getFXcontrlTablaFCT() {
        return FXcontrlTablaFCT;
    }
    //ANCHOR - 24-07-14 : FxCntrlVisorFCT
    public synchronized FxCntrlVisorFCT getFXcontrlVisorFCT(){
        return FXcontrlVisorFCT;
    }
    public synchronized Stage getVisorFCT(){
        return visorFCT;
    }

    public synchronized TableView<Factura> getTableViewFCT(){
        if (this.tableViewFCT==null)
            this.tableViewFCT = new TableView<Factura>();
        return this.tableViewFCT;
    }

    //ANCHOR - 24-07-14 : FxCntrlTablaFCT
    public synchronized void setFXcontrlTablaFCT(FxCntrlTablaFCT contr){
        FXcontrlTablaFCT = contr;
        FxCntrlTablaFCT.setFXcontr(contr);
    }

    public synchronized void setFXcontrlVisorFCT(FxCntrlVisorFCT contr){
        FXcontrlVisorFCT = contr;
        FxCntrlVisorFCT.setFXcontr(contr);

    }

    public synchronized void setVisorFCT(Stage v){
        visorFCT = v;
        FxCntrlVisorFCT.setVisorFCT(v);
    }
    //ANCHOR - tableView
    public synchronized void setTableViewFCT(TableView<Factura> tvfct){
        this.tableViewFCT = tvfct;
    }

    public static void setFacturaActual(Factura f) {
        facturaActual = f;
    }

    private void colocarListenerEnTablaFCT(TableView<Factura> tabla){
        setTableViewFCT(tabla);
        //REVIEW - 24-07-13 : Aquí la TableView 'tabla', al principio, es NULL, y el valor que se le va a asignar, también....
        tabla.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
				if (newSelection != null) {
					int index = tabla.getSelectionModel().getSelectedIndex();
                    ControladorFacturas.facturaActual =  tabla.getSelectionModel().getSelectedItem();
                    //Colocar index en el lblIndex de la tablaFCT
                    FxCntrlTablaFCT.getFxController().setIndiceActual(index);
					System.out.println("[FxCntrlTablaFCT>listener1] Has seleccionado el INDEX: " + FxCntrlTablaFCT.getIndiceActual() + " en la TABLAFCT");
					System.out.println("[FxCntrlTablaFCT>listener1] ...desde el hilo " + Thread.currentThread().getName());
                        ((TableView<Factura>)(getFXcontrlTablaFCT().getTableView())).getSelectionModel().selectedIndexProperty().addListener(
                            (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
                                    int ind = newValue.intValue();
                                    ControladorFacturas.facturaActual = ((TableView<Factura>)(getFXcontrlTablaFCT().getTableView())).getSelectionModel().getSelectedItem();
                                    //Colocar index en el lblIndex de la tablaFCT
                                    FxCntrlTablaFCT.getFxController().setIndiceActual(ind);
                                    System.out.println("[FxCntrlTablaFCT>listener2] Has seleccionado el INDEX: " + FxCntrlTablaFCT.getIndiceActual() + " en la TABLAFCT");
                                    System.out.println("[FxCntrlTablaFCT>listener2] ...desde el hilo " + Thread.currentThread().getName());
                    }
                        );
					// NOTE - 24-07-03 - Quito el reset() al controlador de la tablaFX, para que no se arme un bucle...
                }else{
					System.out.println("[FxCntrlTablaFCT>initialize listener] Parece que no se ha detectado la factura seleccionada, a pesar de haber recogido el evento");
				}
			});
    }
//#endregion

//#region RUN_CFCT_ppio
    @Override
    public void run() {
//REVIEW - 24-07-05 : Asignaciones cuando empieza a ejecutarse el hilo

        this.pc = Controlador.getPanelControl();
        this.ctrlPpal = Controlador.getControlador();
        //NOTE - 24-07-07 : Saco la barrera para los tests de JavaFX
        //Controlador.barreraControladores.await();
        /* if (GUIon) this.tableViewFCT = FXcontrlTablaFCT.getTableView(); */

        while (true) {
//#endregion

//#region FORM_SWITCH_PREV
            /*if (form.seHaEnviado()) {
                try {
                    if (form.getEstado().equals("editando")) {
                        recogerFormyEditar(m.leerFacturas().get(tabla.getIndice()));
                    } else {
                        recogerFormyAnexar();
                    }

                    form.reset();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "[ControladorFacturas] Se ha producido un error en la carga del formulario!");
                    form.enCasodeError(false);
                }
                visible(true);
            }
            if (form.pasoatras()){
                try{
                rellenarFormyActualizar(ModeloFacturas.getModelo().getPilaFacturasSig().push(ModeloFacturas.getModelo().getPilaFacturasAnt().pop()));
                form.borrarDatosNumericos();
               }catch(Exception ex){
                   JOptionPane.showMessageDialog(null,"[ControladorFacturas] No es posible dar más pasos atrás!");
               }
                form.getFormulario().setPasoAtras(false);
            }
            if (form.pasoadelante()){
                 try{
                rellenarFormyActualizar(ModeloFacturas.getModelo().getPilaFacturasAnt().push(ModeloFacturas.getModelo().getPilaFacturasSig().pop()));
                form.borrarDatosNumericos();
               }catch(Exception ex){
                   JOptionPane.showMessageDialog(null,"[ControladorFacturas] No es posible dar más pasos adelante!");
               }
            }
            if (visor.haCambiado()) {
                JOptionPane.showMessageDialog(null, "[ControladorFacturas] Ha pulsado un boton en el visor!");
                switch (visor.getPulsado()) {
                    case 1:	//anterior
                        anteriorFacturaVisor();
                        visor.reset();
                        visible(true);
                        break;
                    case 2:	//siguiente
                        siguienteFacturaVisor();
                        visor.reset();
                        visible(true)            PanelControl.getPanelControl().setNumfacturas(numeroFacturas);;
                        break;
                    case 3: //editar factura
                        editarFacturaVisor();
                        visor.reset();
                        break;
                    case 5: //borrar factura
                        try {
                            borrarFacturaVisor();
                        } catch (NumberFormatException | IOException e) {
                            e.printStackTrace();
                        }
                        visible(true);
                        break;
                    case 6:
                        try {
                            guardarNotaVisor();
                        } catch (NumberFormatException | HeadlessException | IOException e) {
                            e.printStackTrace();
                        }
                        visor.reset();
                        visible(true);
                        break;
                    default:
                        //System.out.println(" [ControladorFacturas] Ninguna opcion seleccionada!");
                }
            }*/
//#endregion

//#region TABLA_SWITCH
//REVIEW - 24-07-03 : OJO!!! El valor de FXcontrlTablaFCT no es el mismo que en cargarTablaFacturas()!!!!
            if(FXcontrlTablaFCT!=null && FXcontrlTablaFCT.HaCambiado()) {
				//System.out.println("[ControladorFacturas>run] recogiendo evento del Controlador de Facturas - pulsado caso " + FXcontrlTablaFCT.getPulsado());
                //REVIEW:  HAY QUE PONER UN CASO CERO DONDE RECOJA EL INDICE DE LA FACTURA ACTUAL

                switch(FXcontrlTablaFCT.getPulsado()){
                    case 1:
                    //System.out.println("[ControladorFacturas>run] Se muestra el Visor de Facturas");
                    mostrarVisorFCT(FxCntrlTablaFCT.getIndiceActual(), ControladorFacturas.facturaActual);
                    //actualizarVisor(tabla.getIndice());
                    break;
                case 2:
                    //System.out.println("[ControladorFacturas>run] Se muestra el Formulario de Nueva Facturas");
                    break;
                case 3:
                    System.out.println("[ControladorFacturas>run] Se comienza a editar la factura seleccionada. TableView->" + tableViewFCT.hashCode());
                    int index = FXcontrlTablaFCT.getIndiceSeleccionadoTabla();
                    Factura f = FXcontrlTablaFCT.getFacturaSeleccionadaTabla();
                    if(mostrarVisorFCT(index, f))
                        editarFacturaVisor();
                    break;
                case 4:
                    System.out.println("[ControladorFacturas>run] Se borra la Factura actual -> " + FxCntrlTablaFCT.getFxController().getIndiceSeleccionadoTabla() + " - TableView->" + tableViewFCT.hashCode());
                    borrarFacturaTabla();
                    break;
                case 5:
                    System.out.println("[ControladorFacturas>run] Se muestra la Ventana de Filtros. TableView->" + tableViewFCT.hashCode());
                    break;
                case 6:
                    System.out.println("[ControladorFacturas>run] Se imprime la TablaFCT. TableView->" + tableViewFCT.hashCode());
                    break;
                }

                FXcontrlTablaFCT.reset();
            }
//#endregion

//#region VISOR_SWITCH
            if(FXcontrlVisorFCT!=null && FXcontrlVisorFCT.HaCambiado()) {
				//System.out.println("[ControladorFacturas>run] recogiendo evento del visorFCT en el Controlador de Facturas - pulsado caso " + FXcontrlVisorFCT.getPulsado());
                //System.out.println("[ControladorFacturas>visorSwitch] indice seleccionado en tablaFCT " + FXcontrlTablaFCT.tblvwFct.hashCode() +  " : " + elem);
                switch(FXcontrlVisorFCT.getPulsado()){
                    case 1:
                        if (FxCntrlVisorFCT.modo == FxCntrlVisorFCT.VISOR){
                            try {
                                ocultarVisorFCT();
                                System.out.println("[ControladorFacturas>run>visorSwitch] Cerrando el Visor de Facturas");
                            } catch (InterruptedException | BrokenBarrierException e) {
                                e.printStackTrace();
                            }
                        }else if(FxCntrlVisorFCT.modo == FxCntrlVisorFCT.EDITAR){
                            if(acabarEdicionFacturaVisor())
                                System.out.println("[ControladorFacturas>run>visorSwitch] Factura Editada!");
                        }
                        break;
                    case 2:
                        //System.out.println("[ControladorFacturas>run] Se activará el Formulario de Nueva Factura");
                        break;
                    case 3:
                        System.out.println("[ControladorFacturas>run] Se activa la Edicion del VisorFCT para la Factura actual");
                        editarFacturaVisor();
                        break;
                    case 4:
                        System.out.println("[ControladorFacturas>run] Se borra la Factura actual -> " + FxCntrlTablaFCT.getFxController().getIndiceSeleccionadoTabla());
                        borrarFacturaVisor();
                        break;
                    case 5:
                        if(anteriorFacturaVisor())
                            System.out.println("[ControladorFacturas>run] La seleccion de la tablaFCT se movera hacia arriba");
                        break;
                    case 6:
                        if(siguienteFacturaVisor())
                            System.out.println("[ControladorFacturas>run] La seleccion de la tablaFCT se movera hacia abajo");
                        break;
                }
                FXcontrlVisorFCT.reset();
            }
//#endregion

//#region LATENCIA_SW
            try {
                //System.out.println("[controladorFacturas] En el sleep");
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
    }
//#endregion

//#region (VisorOps)
    public boolean anteriorFacturaVisor(){
		System.out.println("[ControladorFacturas] Ha pulsado en el visorFCT el boton atras. ");
        Platform.runLater(new Runnable(){
            @Override
            public void run(){
                int i = FXcontrlTablaFCT.getIndiceSeleccionadoTabla();
                System.out.println("[ControladorFacturas>anteriorFacturaVisor] Desde el indice " + i);
                List<Factura> lista = ModeloFacturas.leerFacturasSinFiltrar();
                System.out.println("[ControladorFacturas>anteriorFacturaVisor] Leida Lista de Facturas de tamaño " + lista.size());
                if (i > 0) {
                    Platform.runLater(new Runnable(){
                        @Override
                        public void run(){
                            // HAY QUE AJUSTAR EL VISOR PARA QUE SELECCIONE LA FACTURA ADECUADA DESDPUES DE ACTIVAR EL FILTRO
                            int nuevoindice = i-1;
                            TableView<Factura> tv = FxCntrlTablaFCT.getFxController().getTableView();
                            System.out.println(" [ControladorFacturas] Actualizando tabla - tableView " + tv.hashCode() + " - nuevo index: " + nuevoindice);
                            FxCntrlTablaFCT.getFxController().seleccionarIndiceTabla(nuevoindice);
                            Factura f = FxCntrlTablaFCT.getFxController().getFacturaSeleccionadaTabla();
                            facturaActual = f;
                            Stage vs = getVisorFCT();
                            System.out.println(" [ControladorFacturas] Actualizando Visor " + vs.hashCode());
                            FxCntrlVisorFCT.getFxController().actualizarDatosVisor(nuevoindice, f);
                        }
                    });
                }else{
                    Platform.runLater(new Runnable(){
                        @Override
                        public void run(){
                            int nuevoindice = lista.size()-1;
                            TableView<Factura> tv = FxCntrlTablaFCT.getFxController().getTableView();
                            System.out.println(" [ControladorFacturas] Actualizando tabla - tableView " + tv.hashCode() + " - nuevo index: " + nuevoindice);
                            FxCntrlTablaFCT.getFxController().seleccionarIndiceTabla(nuevoindice);
                            Factura f = FXcontrlTablaFCT.getFacturaSeleccionadaTabla();
                            facturaActual = f;
                            Stage vs = getVisorFCT();
                            System.out.println(" [ControladorFacturas] Actualizando Visor " + vs.hashCode());
                            FxCntrlVisorFCT.getFxController().actualizarDatosVisor(nuevoindice, f);
                        }
                    });
                }
            }
        });
        System.out.println("[ControladorFacturas] Index TablaFCT: " + FXcontrlTablaFCT.getIndiceSeleccionadoTabla());
        FXcontrlTablaFCT.reset();
        FXcontrlVisorFCT.reset();
        return true;
    }

    public boolean siguienteFacturaVisor() {
		System.out.println("[ControladorFacturas] Ha pulsado en el visorFCT el boton adelante.");
        Platform.runLater(new Runnable(){
            @Override
            public void run(){
                int i = FXcontrlTablaFCT.getIndiceSeleccionadoTabla();
                System.out.println("[ControladorFacturas>siguienteFacturaVisor] Desde el indice " + i);
                List<Factura> lista = ModeloFacturas.leerFacturasSinFiltrar();
                System.out.println("[ControladorFacturas>siguienteFacturaVisor] Leida Lista de Facturas de tamaño " + lista.size());

                if (i < (lista.size() - 1)) {
                    Platform.runLater(new Runnable(){
                        @Override
                        public void run(){
                            int nuevoindice = i+1;
                            TableView<Factura> tv = FxCntrlTablaFCT.getFxController().getTableView();
                            System.out.println(" [ControladorFacturas] Actualizando tabla - tableView " + tv.hashCode() + " - nuevo index: " + nuevoindice);
                            FxCntrlTablaFCT.getFxController().seleccionarIndiceTabla(nuevoindice);
                            Factura f = FxCntrlTablaFCT.getFxController().getFacturaSeleccionadaTabla();
                            facturaActual = f;
                            Stage vs = getVisorFCT();
                            System.out.println(" [ControladorFacturas] Actualizando Visor " + vs.hashCode());
                            FxCntrlVisorFCT.getFxController().actualizarDatosVisor(nuevoindice, f);
                        }
                    });
                } else{
                    Platform.runLater(new Runnable(){
                        @Override
                        public void run(){
                            int nuevoindice = 0;
                            TableView<Factura> tv = FxCntrlTablaFCT.getFxController().getTableView();
                            System.out.println(" [ControladorFacturas] Actualizando tabla - tableView " + tv.hashCode() + " - nuevo index: " + nuevoindice);
                            FxCntrlTablaFCT.getFxController().seleccionarIndiceTabla(nuevoindice);
                            Factura f = FXcontrlTablaFCT.getFacturaSeleccionadaTabla();
                            facturaActual = f;
                            Stage vs = getVisorFCT();
                            System.out.println(" [ControladorFacturas] Actualizando Visor " + vs.hashCode());
                            FxCntrlVisorFCT.getFxController().actualizarDatosVisor(nuevoindice, f);
                        }
                    });

                }
            }
        });
        System.out.println("[ControladorFacturas] Index TablaFCT: " + FXcontrlTablaFCT.getIndiceSeleccionadoTabla());
        FXcontrlTablaFCT.reset();
        FXcontrlVisorFCT.reset();
        return true;
    }

    public boolean editarFacturaVisor() {
		FxCntrlVisorFCT.modo = FxCntrlVisorFCT.EDITAR;
		//cambiamos el texto del boton F1 a OK
        Platform.runLater(new Runnable(){
            @Override
            public void run(){
                FXcontrlVisorFCT.setTextoBotonF1("OK");
                try {
                    FXcontrlVisorFCT.setTFEditables(true);
                    // Hacemos los botones del visor que no sirven no visibles
                    FXcontrlVisorFCT.btnVNueva.setVisible(false);
                    FXcontrlVisorFCT.btnVEditar.setVisible(false);
                    FXcontrlVisorFCT.btnVBorrar.setVisible(false);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });
        return true;
    }

    public boolean acabarEdicionFacturaVisor(){

        Factura f = FXcontrlVisorFCT.recogerDatosVisor();
        var listafact = ((ArrayList<Factura>)(ModeloFacturas.leerFacturasSinFiltrar()));
        // STUB - Arreglar la asignación de index, filtradas las facturas no son necesariamente correlativos, ID de la factura e index de la tabla
        int index = f.getID()-1;
        try {
            m.editarFactura(listafact, f, index);
        } catch (NumberFormatException | IOException e) {
            e.printStackTrace();
            return false;
        }
        if (f!=null){
            System.out.println("[ControladorFacturas>run>visorSwitch] Se cambia la factura " + f.getID());
            Platform.runLater(new Runnable(){
                @Override
                public void run(){
                    FXcontrlVisorFCT.setTFEditables(false);
                    FXcontrlVisorFCT.setTextoBotonF1("CERRAR");
                    // volvemos a hacer los botones visiblEs
                    FXcontrlVisorFCT.btnVNueva.setVisible(true);
                    FXcontrlVisorFCT.btnVEditar.setVisible(true);
                    FXcontrlVisorFCT.btnVBorrar.setVisible(true);
                    FxCntrlVisorFCT.modo = FxCntrlVisorFCT.VISOR;
                    //recargar la lista de facturas y refrescar la tabla
                    ObservableList<Factura> listaFXnueva = ModeloFacturas.getModelo().getListaFXFacturas();
                    FXCollections.sort(listaFXnueva, Comparator.comparing(Factura::getFecha));
                    FXcontrlTablaFCT.setListaFXFacturas(listaFXnueva);
                    FXcontrlTablaFCT.getTableView().setItems(listaFXnueva);
                    FXcontrlTablaFCT.getTableView().refresh();
                    FXcontrlTablaFCT.actualizarInfoTabla();
                    String[] nuevosTotales = ModeloFacturas.getModelo().calcularTotales();
                    FXcontrlTablaFCT.actualizarTotales(nuevosTotales);
                    FXcontrlTablaFCT.seleccionarIndiceTabla((index));
        // REVIEW - 24-07-29 : actualizar los datos de la tabla         <- Line 558
        // REVIEW - 24-07-29 : volver a colocar el boton F1 como cerrar <- Line 559
        // REVIEW - 24-07-29 : volver al modo VISOR                     <- Line 560
        // REVIEW - 24-07-29 : TFs no editables en visor                <- Line 561
                }
            });
        }
        return true;
    }

    public boolean borrarFacturaVisor(){
        System.out.println("[ControladorFacturas>borrarFacturaVisor()] Ha pulsado el boton borrar factura");
        Platform.runLater(new Runnable(){
            @Override
            public void run(){
                // restea el visor
                FxCntrlVisorFCT.getFxController().reset();
                // y borra la Factura seleccionada
                int i = FxCntrlTablaFCT.getFxController().getIndiceSeleccionadoTabla();
                Factura fact = FxCntrlTablaFCT.getFxController().getFacturaSeleccionadaTabla();
                // Crear una alerta de tipo información
                // Hacer que los Alert estén siempre en primer plano (AOF)
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Atención!!");
                alert.setHeaderText("[ControladorFacturas]");
                alert.setContentText("Recogiendo los datos de la factura");
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.initOwner(visorFCT);
                // Mostrar la alerta y esperar a que el usuario la cierre
                alert.showAndWait();
                m.borrarFactura(fact);
                // colocar en la misma posicion de la tabla
                if (i > ModeloFacturas.getNumeroFacturas()){
                    i = i -1;
                }
                FxCntrlTablaFCT.getFxController().seleccionarIndiceTabla(i);
                // actualizar los datos de la tabla
                i = FxCntrlTablaFCT.getFxController().getIndiceSeleccionadoTabla();
                actualizarTabla(i);
                // Actualizar aquí las ID de las facturas siguientes
                // actualizar los datos del visor
                Factura f = FxCntrlTablaFCT.getFxController().getFacturaSeleccionadaTabla();
                FxCntrlVisorFCT.getFxController().actualizarDatosVisor(i, f);
                // actualizar los datos del PanelControl
                int num = ModeloFacturas.getNumeroFacturas();
                PanelControl.getPanelControl().setNumfacturas(num);
            }
        });
        return true;
    }

    public boolean guardarNotaVisor() throws NumberFormatException, HeadlessException, IOException{
        /*
                Factura f = m.getFactura(visor.getIndex());
                f.setNota(visor.getNota());
                if (m.editarFactura((ArrayList<Factura>)m.leerFacturas(),f,visor.getIndex()))
                    JOptionPane.showMessageDialog(null,"[ControladorFacturas] Nota guardada!");
        */
                //System.out.println("[ControladorFacturas>guardarNotaVisor()] ");
                return true;
            }

//#endregion

//#region (TablaOps)
    public boolean actualizarTabla(int sel){
        // Crear una alerta de tipo información
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Atención!!");
        alert.setHeaderText("[ControladorFacturas]");
        alert.setContentText("Actualizando Tabla!");
        alert.initModality(Modality.APPLICATION_MODAL);
        if (visorFCT.isShowing())
            alert.initOwner(visorFCT);
        else
            alert.initOwner(tablaFCT);
        // Mostrar la alerta y esperar a que el usuario la cierre
        alert.showAndWait();
        //List<Factura> facturas = m.leerFacturasSinFiltrar();
        var listaFXfact = ModeloFacturas.getModelo().getListaFXFacturas();
        FxCntrlTablaFCT.getFxController().getTableView().setItems(listaFXfact);
        String[] totales = m.calcularTotales();
        /*
        Vector<Factura> vectorFacturas = new Vector<Factura>();
        if (ModeloFacturas.numeroFacturas>0){
            for (Factura f : facturas) {
                vectorFacturas.add(f);
            }
        }
        else vectorFacturas.add(new Factura());

        try {
            tabla.actualizarModelo(vectorFacturas, sel);
        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
        }
        */
        FXcontrlTablaFCT.seleccionarIndiceTabla(sel);
        FXcontrlTablaFCT.actualizarTotales(totales);


        //System.out.println("[ControladorFacturas>actualizarTabla] Actualizar tabla  de hashCode " + tableViewFCT.hashCode() + " a index " + sel);
        return true;
    }

    public boolean borrarFacturaTabla(){
        System.out.println("[ControladorFacturas>borrarFacturaVisor()] Ha pulsado el boton borrar factura");
        Platform.runLater(new Runnable(){
            @Override
            public void run(){
                // restea el visor
                FxCntrlVisorFCT.getFxController().reset();
                // y borra la Factura seleccionada
                int i = FxCntrlTablaFCT.getFxController().getIndiceSeleccionadoTabla();
                Factura fact = FxCntrlTablaFCT.getFxController().getFacturaSeleccionadaTabla();
                // Crear una alerta de tipo información
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Atención!!");
                alert.setHeaderText("[ControladorFacturas]");
                alert.setContentText("Recogiendo los datos de la factura");
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.initOwner(tablaFCT);
                // Mostrar la alerta y esperar a que el usuario la cierre
                alert.showAndWait();
                m.borrarFactura(fact);
                // colocar en la posicion anterior de la tabla
                int index = 0;
                if((i-1)<0){
                    index = (ModeloFacturas.getNumeroFacturas())-1;
                }
                FxCntrlTablaFCT.getFxController().seleccionarIndiceTabla(index);
                // actualizar los datos de la tabla
                i = FxCntrlTablaFCT.getFxController().getIndiceSeleccionadoTabla();
                actualizarTabla(i);
                // actualizar los datos del PanelControl
                int num = ModeloFacturas.getNumeroFacturas();
                PanelControl.getPanelControl().setNumfacturas(num);
            }
        });

        return true;
    }
//#endregion

//#region (TABLA_PDF)
/*
    public boolean imprimirTabla() {
        String titulo = JOptionPane.showInputDialog("[ControladorFacturas] Escriba el título del informe, por favor: ");
        JOptionPane.showMessageDialog(null, "[ControladorFacturas] pulse OK y espere a que se genere el informe!");

        List<Factura> facturas = m.leerFacturas();
        FacturasDataSource datasource = new FacturasDataSource();

        for (Factura f : facturas) {
            datasource.addFactura(f);
        }
        JOptionPane.showMessageDialog(null,"[ControladorFacturas] facturas añadidas a la cola de impresion!");
        try {
            Fichero directoriopersonal = new Fichero("informes/"+Config.getConfig().getUsuario()+"/");

            JasperReport reporte = (JasperReport) JRLoader.loadObjectFromLocation("adjuntos/informe01.jasper");

            //mapa de parámetros para enviar al informe
                HashMap hm = new HashMap();
                hm.put("titulo",titulo);
                hm.put("anho", Config.getConfig().getAnho().getAnho()+" trimestre "+Config.getConfig().getAnho().getTrimestre());
                hm.put("empresa", Config.getConfig().getMiRS().getNombre());
                hm.put("razonsocial", Config.getConfig().getMiRS().getRazon());
                hm.put("nif", Config.getConfig().getMiRS().getNIF().toString());
                hm.put("telefono", Config.getConfig().getMiRS().getTelefono());
                hm.put("direccion", Config.getConfig().getMiRS().getDireccion());
                hm.put("codigopostal", Config.getConfig().getMiRS().getCP());
                hm.put("poblacion", Config.getConfig().getMiRS().getPoblacion());

                try{
                    BufferedImage bi = ImageIO.read(new File("adjuntos/coffee.png"));

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(bi, "png", baos );
                    baos.flush();

                    byte[] imageInByte = baos.toByteArray();
                    baos.close();
                    hm.put("logo",new ByteArrayInputStream(imageInByte));
                }catch(IOException ioe){
                    JOptionPane.showMessageDialog(null, "[ControladorFacturas] El logo no ha sido cargado");
                    hm.put("logo",null);
                }
//                InputStream is = new ByteArrayInputStream(imageInByte);


            JasperPrint jasperPrint = JasperFillManager.fillReport(reporte, hm, datasource);

            JRExporter exporter = new JRPdfExporter();
            //parameters used for the destined file.
            String rutaInforme = "informes/"+Config.getConfig().getUsuario()+"/"+"IVA"+ Config.getConfig().getAnho().getAnho() + "_" + Config.getConfig().getAnho().getTrimestre() + ".pdf";

            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, rutaInforme);
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            //export to .pdf
            exporter.exportReport();
            JOptionPane.showMessageDialog(null, "[ControladorFacturas] informe guardado en FACTURASv20/" + rutaInforme);

            JasperViewer viewer = new JasperViewer(jasperPrint, false);
            viewer.setTitle("Informe de Facturacion");
            viewer.setVisible(true);
            viewer.setAlwaysOnTop(true);
            viewer.setAutoRequestFocus(true);

        } catch (JRException jrex) {
            JOptionPane.showMessageDialog(null, "[ControladorFacturas] Error " + jrex + " al imprimir el informe");
            return false;
        }
        return true;

    }
*/
//#endregion

//#region CARG_T/FCT
    public synchronized boolean cargarTablaFacturas() throws InterruptedException, BrokenBarrierException {

        if(FXcontrlTablaFCT == null){
            Platform.runLater(new Runnable(){

                Stage tabla;

                @Override
                public void run() {
                    FxmlHelper loader = new FxmlHelper("..//ui/resources/fxmltablaFCT.fxml");
                    Parent root;
                    root = loader.cargarFXML();
                    FxCntrlTablaFCT contrFxtablaTemp = (FxCntrlTablaFCT)loader.getFXcontr();
                    FxCntrlTablaFCT.setFXcontr(contrFxtablaTemp);
                    setFXcontrlTablaFCT(contrFxtablaTemp);
                    System.out.println("[ControladorFacturas] Controlador FX para tabla de FCT asignado: cntrFXtabla->" + FXcontrlTablaFCT.hashCode());
                    Scene escena = new Scene(root);
                    tabla = new Stage();
                    tabla.setScene(escena);
                    tabla.setResizable(true);
                    // REVIEW - 24-05-30 : Aquí se ajusta el modo de la ventana de la TablaFCT
                    //this.tablaFCT.initModality(Modality.NONE);
                    //ANCHOR - Asignar Stage T/FCT a CFCT
                    System.out.println("[CFCT>cargarTablaFacturas] Stage de Tabla con hashcode :" + tabla.hashCode());
                    ControladorFacturas.tablaFCT = tabla;

                    System.out.println("[CFCT>cargarTablaFacturas] Stage de Tabla asignado a ControladorFacturas.tablaFCT con valor : StageTabla->" + tablaFCT.hashCode() + " - tblVw->" + contrFxtablaTemp.getTableView().hashCode());
                    //ANCHOR - 24-07-14 : Asignar contrFX de T/FCT a CFCT
                    if(FXcontrlTablaFCT==null){
                        System.out.println("[CFCT>cargarTablaFacturas] El contrlFX de la tablaFCT es NULL. El programa se cierra!!!");
                        System.exit(0);
                    }
                    //ANCHOR - tableView
                    //REVIEW - 24-07-13 : Aquí la TableView, al principio, es NULL, y el valor que se le va a asignar, también....


                    //System.out.println("[ControladorFacturas>cargarTablaFacturas] Cargada y asignada la tabla FCT con hashCode " + tableViewFCT.hashCode());
                    }
                });
                System.out.println("[ControladorFacturas>cargarTablaFacturas>runLater]...LLAMANDO A INFOTABLA");
                imprimirInfoTabla();
            return true;
        }
        else {
            System.out.println("[ControladorFacturas>cargarTablaFacturas]...LLAMANDO A INFOTABLA");
            imprimirInfoTabla();
            //Falta algo aquí... arreglar - Falta comprobar si es el controladorFX correcto
            return false;
        }
    }
//#endregion

//#region MOSTR_T/FCT
public synchronized void mostrarTablaFacturas() {

    Platform.runLater(new Runnable(){
        @Override
        public void run(){
            // Tendré que SINCRONIZAR MEDIANTE BARRERAS, para controlar que los hilos esperen hasta que esté activo el modeloFCT, o los elementos de la GUI correspondiente
            //ANCHOR - Mostrar T/FCT
            //System.out.println("[controladorFacturas>mostrarTablaFacturas] tableView con hashCode " + tableViewFCT.hashCode());
            ControladorFacturas.tablaFCT.setTitle("Tabla de Facturas");
            ControladorFacturas.tablaFCT.show();
            colocarListenerEnTablaFCT(FxCntrlTablaFCT.getFxController().getTableView());
            System.out.println("[controladorFacturas>mostrarTablaFacturas] Mostrando Stage de tablaFCt " + tablaFCT.hashCode());

        }
    });
    System.out.println("[ControladorFacturas>mostrarTablaFacturas]...LLAMANDO A INFOTABLA");
    imprimirInfoTabla();
    //REVIEW - 24-06-09 : Hay que cargar el modeloFCT para que no sea NULL eventualmente (en los test)
    m = ModeloFacturas.getModelo();
    }

    public synchronized void ocultarTablaFacturas() {
        System.out.println("[ControladorFacturas>ocultarTablaFacturas] Se oculta  el StageTabla->" + tablaFCT.hashCode());
        System.out.println("[ControladorFacturas>ocultararTablaFacturas]...LLAMANDO A INFOTABLA");
        imprimirInfoTabla();
        Platform.runLater(new Runnable(){
            @Override
            public void run(){
                System.out.println("[ControladorFacturas>ocultarTablaFacturas>runLater] Oculto el StageTabla->" + tablaFCT.hashCode());
                ControladorFacturas.tablaFCT.hide();
            }
        }
        );

        //this.tableViewFCT = null;

    }
//#endregion

//#region CARG_V/FCT
public synchronized boolean cargarVisorFacturas(){
    if(visorFCT == null){
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                Stage visor;
                FxmlHelper loader = new FxmlHelper("..//ui/resources/visorFormFCT.fxml");
                Parent root;
                root = loader.cargarFXML();
                FXcontrlVisorFCT = (FxCntrlVisorFCT)loader.getFXcontr();
                System.out.println("[ControladorFacturas] FXcontrlVisorFCT: " + FXcontrlVisorFCT.hashCode());
                Scene escena = new Scene(root);
                visor = new Stage();
                visor.setScene(escena);
                visor.setResizable(false);
                //visor.initStyle(StageStyle.UNDECORATED);
                visor.setAlwaysOnTop(true);
                visor.setOnCloseRequest((ev)->{
                    FxCntrlVisorFCT.getFxController().getVisorFCT().hide();
                });

                //ANCHOR - Asignar V/FCT y contrFX/V/FCT
                Controlador.getControladorFacturas().setVisorFCT(visor);
                Controlador.getControladorFacturas().setFXcontrlVisorFCT((FxCntrlVisorFCT)(loader.getFXcontr()));
                System.out.println("[ControladorFacturas>cargarVisorFacturas] asignado ContrlFact.visorFCT: " + ControladorFacturas.visorFCT.hashCode());
                }
            });
            return true;
        }else{
            return false;
        }
    }
//#endregion

//#region MOSTR_V/FCT
public synchronized boolean mostrarVisorFCT(int index, Factura f){
        //System.out.println("[ControladorFacturas>mostrarVisorFCT] entrando en la barreraVisor desde el hilo " + Thread.currentThread().getName());
        //NOTE - 24-07-13 : Ojo, hay que actualizar la facturta Actual del Controlador de Facturas manualmente...
        ControladorFacturas.facturaActual = f;
        FxCntrlTablaFCT.getFxController().setIndiceActual(index);
        if (f==null){
            System.out.println("[ControladorFacturas>mostrarVisorFCT] No hay factura seleccionada. No se muestra el visor");
            return false;
        }
        //NOTE - 24-07-13 : Ojo, hay que actualizar el index Actual del ControladorFX de la TablaFCT manualmente...
        cargarVisorFacturas();

        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                ControladorFacturas.visorFCT.setTitle("Visor de Facturas");
                ControladorFacturas.visorFCT.show();
                setVisorFCT(visorFCT);
                System.out.println("[ControladorFacturas>mostrarVisorFCT>runLater] Llamando a actualizarDatosVisor: ");
                System.out.println("[ControladorFacturas>mostrarVisorFCT>runLater] index: "+index + "\nfactura: \n" + ((f==null)?"NULL":f.toString()));
                //NOTE - 24-07-26 : Aquí iba un Thread.sleep en el hilo CntrFCT
                FXcontrlVisorFCT.setTFEditables(false);
                System.out.println("[ControladorFacturas>mostrarVisorFCT>runLater] TextFields no editables");
                FXcontrlVisorFCT.actualizarDatosVisor(index, f);
                System.out.println("[ControladorFacturas>mostrarVisorFCT>runLater] se muestra el visorFCT del contrFX con hashCode: " + FxCntrlVisorFCT.getFxController().getVisorFCT().hashCode());

            }
        });
        return true;
        //NOTE - 24-07-07 : Hacemos una pausa en el hilo del CFCT, no en el de la FXApplication (para ver si se inicializa el visor)
        /*Thread.sleep(1000);
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                            }
        });*/
    //System.out.println("[ControladorFacturas] Se muestra el VisorFCT cuya instancia en el CFCT tiene el valor " + visorFCT.hashCode());
    }

public synchronized void ocultarVisorFCT() throws InterruptedException, BrokenBarrierException{
    Platform.runLater(new Runnable(){
        @Override
        public void run() {
                visorFCT.hide();
        }
    });
    //System.out.println("[ControladorFacturas>ocultarVisorFCT] Se oculta el VisorFCT " + visorFCT.hashCode());
}
//#endregion

//#region HELPERS
    public void imprimirInfoTabla(){
        System.out.println("[ControladorFacturas]****Info Tabla****");
        System.out.println("ControladorFacturas]   tableview->"+((tableViewFCT==null)?"NULL":tableViewFCT.hashCode()));
        System.out.println("ControladorFacturas]  Stagetabla->"+((tablaFCT==null)?"NULL":tablaFCT.hashCode()));
        System.out.println("ControladorFacturas] cntrFxtabla->"+((FXcontrlTablaFCT==null)?"NULL":FXcontrlTablaFCT.hashCode()));
        System.out.println("ControladorFacturas] listaFXfact->"+((ModeloFacturas.getModelo().getListaFXFacturas()==null)?"NULL":ModeloFacturas.getModelo().getListaFXFacturas().hashCode()));
        if (FXcontrlTablaFCT!=null)
            FXcontrlTablaFCT.imprimirInfoTabla();
        else
            System.out.println("----No hay datos del cntFXtabla*---");
        System.out.println("[ControladorFacturas]*****Fin Info*****");

    }
//#endregion

//#region (VISIB)
//#endregion

//#region (FILTRAR)
//#endregion

//#region (AUTOSAVE)
//#endregion

}
