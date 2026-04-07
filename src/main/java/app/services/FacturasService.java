package app.services;

import domain.records.ConfigData;
import infraestructure.csv.FacturaCSVRepo;

/* CONTROLA TODAS LAS OPERACIONES SOBRE UNA FACTURA */

// import java.io.IOException;
// import java.util.ArrayList;
// import java.util.Collections;
// import java.util.List;
// import java.util.Optional;
// import java.util.stream.Collector;

// import javax.swing.JOptionPane;

// import controladores.ControladorFacturas;
// import controladores.fxcontrollers.PanelControl;
// import javafx.scene.control.Alert;
// import javafx.scene.control.Alert.AlertType;
// import javafx.scene.control.ButtonType;
// import javafx.stage.Modality;
// import modeloDominio.records.Factura;
// import modeloDominio.records.RazonSocial;
// import modeloDominio.records.Totales;
// import modeloNegocio.Infraestructura.csv.FacturaCSVRepo;


public class FacturasService {

    public static FacturaCSVRepo repo;
    public static ConfigData configdata;
    

    public FacturasService() {
        System.out.println("[FacturasService] Inicializando servicio de facturas...");
    }

    public static ConfigData getConfigdata() {
        return configdata;
    }

    public void setConfigdata(ConfigData configdata) {
        FacturasService.configdata = configdata;
        System.out.println("[FacturasService] Configuración de datos actualizada.");

    }

}
/*
    }

    //#region fct_ops (prov del ModeloFacturas)
    public static int getNumeroFacturas() {

    }

    public static Factura getFactura() {

    }

    public static int getIndexOfFactura(Factura f) {

    }

    public static boolean actualizarDistribuidorEnFacturas() {

    }

    public Totales calcularTotales(List<Factura> listaFCT) {
        return listaFCT.stream().collect(
                Collector.of(
                        Totales::new, // proveedor (identidad)
                        (tot, f) -> {
                            // Campos propios de Totales
                            tot.setBase(tot.getBase() + f.totales.getBase());
                            tot.setIVA(tot.getIVA() + f.totales.getIVA());
                            tot.setBaseNI(tot.getBaseNI() + f.totales.getBaseNI());
                            tot.setRetenciones(tot.getRetenciones() + f.totales.getRetenciones());
                            tot.setTotal(tot.getTotal() + f.totales.getTotal());
                        },
                        (t1, t2) -> {
                            // Combinador (solo se usa si el stream es paralelo)
                            t1.setBase(t1.getBase() + t2.getBase());
                            t1.setIVA(t1.getIVA() + t2.getIVA());
                            t1.setBaseNI(t1.getBaseNI() + t2.getBaseNI());
                            t1.setRetenciones(t1.getRetenciones() + t2.getRetenciones());
                            t1.setTotal(t1.getTotal() + t2.getTotal());
                            return t1;
                        }));
    }

    public static boolean insertarFacturas(ArrayList<Factura> facturas) throws NumberFormatException, IOException {
        int numeroFacturas = facturas.size();
        if(numeroFacturas > 0) {
            Collections.sort(facturas);
        }
        //JOptionPane.showMessageDialog(null, "Espere unos segundos mientras se ordena la lista!");
        // REVIEW - 24-05-06 : - Hay que reemplazar estas operaciones con setters... No existen en un Java record...
        //        for (int i = 0; i < numeroFacturas; i++) {
        //            facturas.get(i).setID(i + 1);
        //        }
        PanelControl.getPanelControl().setNumfacturas(numeroFacturas);
        return (ficheroFacturas.escribir(facturas));
    }

    public static boolean anexarFactura(Factura factura) throws NumberFormatException, IOException {
        // REVIEW - 24-05-06 : - Hay que reemplazar estas operaciones con setters... No existen en un Java record...
        //        factura.setID(this.ultimaID++);
        numeroFacturas++;
        PanelControl.getPanelControl().setNumfacturas(numeroFacturas);
        return (ficheroFacturas.anexar(factura));
    }

    public static boolean editarFactura(ArrayList<Factura> listafacturas, Factura factura, int index)
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

    public static boolean actualizarDistribuidorEnFacturas(RazonSocial antrs, RazonSocial nuevars) {
        // int res = JOptionPane.showConfirmDialog(
            null,
            "Quiere actualizar el Distribuidor en todas las facturas?",
            "ACTUALIZAR DISTRIBUIDOR EN FACTURAS:",
            // JOptionPane.YES_NO_OPTION
        );

        if (res == JOptionPane.YES_OPTION) {
            for (Factura f : ModeloFacturas.listaFacturas) {
                if ((f.RS.equals(antrs))) {
                    f.RS = nuevars;
                }
            // }
            // // JOptionPane.showMessageDialog(null, "Se han actualizado las facturas"); return true;
        } else{
            JOptionPane.showMessageDialog(null, "No se han actualizado las facturas");
        }
        return false;
    }
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
        // // Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // El usuario seleccionó "Sí"
            facturas.remove(factura);
            // Hay que cambiar las ID de las facturas (no necesariamente ordenarlas)
            Collections.sort(facturas);
            // int i = 1;
            for (Factura f : facturas) {
                f.setID(i);
                i++;
            // // }
            // Crear una alerta de tipo información
            Alert alert2 = new Alert(AlertType.INFORMATION);
            alert2.setTitle("Atención!!");
            alert2.setHeaderText("[ModeloFacturas]");
            alert2.setContentText("Borrando factura!");
            // Mostrar la alerta y esperar a que el usuario la cierre
            alert2.showAndWait();*//*
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
            /* Crear una alerta de tipo información
            Alert alert2 = new Alert(AlertType.INFORMATION);
            alert2.setTitle("Atención!!");
            alert2.setHeaderText("[ModeloFacturas]");
            alert2.setContentText("Operación de borrado de Factura cancelada.");
            // Mostrar la alerta y esperar a que el usuario la cierre
            alert2.showAndWait();*//*
            System.out.println("Operacion de borrado de Factura cancelada.");
            return false;
        }
    }

    //#endregion
    */

