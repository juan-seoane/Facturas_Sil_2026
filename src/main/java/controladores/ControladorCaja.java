package controladores;

//import ui.ventanas.VentanaFiltrosCaja;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import modelo.*;
import modelo.base.Config;
import modelo.base.Fichero;
import modelo.datasources.CajasDataSource;
import modelo.records.EntradaCaja;
import net.sf.jasperreports.engine.JRException;
//import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
//import net.sf.jasperreports.engine.export.JRPdfExporterParameter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import ui.formularios.FormularioCaja;
import ui.tablas.TablaCaja;

/**
 *
 * @author Juan Seoane
 */
public class ControladorCaja extends Thread {

    private static ControladorCaja instancia = null;
    TablaCaja tabla;
    FormularioCaja form;
    ModeloCaja m;

    private ControladorCaja() {
        m = ModeloCaja.getModelo();
        /*
        tabla = new TablaCaja(m.generarVectorCaja(),m.getColumnas());
        form = FormularioCaja.getFormulario();
        tabla.setVisible(false);
        form.setVisible(false);
        */
    }

    public static ControladorCaja getControlador() {
        if (instancia == null) instancia = new ControladorCaja();
        return instancia;
    }

    public void visible(boolean bool) {
        tabla.setVisible(bool);
        form.setVisible(bool);
    }

    public void run() {
        while (true) {
            /*
            while(!form.cambiado() && !tabla.cambiado()){
                System.out.print("");
            }
                if (form.hecho()){
                    if (form.getEstado() == form.INSERTAR){
                        EntradaCaja cajains = form.recogerFormulario();
                        if (cajains != null){
                            m.guardarCaja(cajains);
                            tabla.actualizarDatos(m.generarVectorCaja(),m.getColumnas());
                        }
                        form.reset();
                    }else if (form.getEstado() == form.EDITAR){
                        EntradaCaja cajaed = form.recogerFormulario();
                        if (cajaed != null){
                            m.editarCaja(m.getCaja(tabla.filaSeleccionada()),cajaed,tabla.filaSeleccionada());
                            tabla.actualizarDatos(m.generarVectorCaja(),m.getColumnas());
                        }
                        form.reset();
                    }
                 }
                 if (form.cerrar()){
                     form.reset();
                     form.dispose();
                }
                 if (tabla.cambiado()){
                     switch(tabla.pulsado()){
                         case 1:
                             form.setEstado(form.INSERTAR);
                             form = FormularioCaja.getFormulario();
                             form.setVisible(true);
                             tabla.reset();
                             break;
                         case 2:
                             form = FormularioCaja.getFormulario();
                             form.setVisible(true);
                             form.setEstado(form.EDITAR);
                             form.rellenarFormulario(m.getCaja(tabla.filaSeleccionada()));
                             tabla.reset();
                             break;
                         case 3:
                             int respuesta = JOptionPane.showConfirmDialog(null, "Seguro que desea borrar la entrada "+(tabla.filaSeleccionada()+1), "ATENCION!", JOptionPane.YES_NO_OPTION);
                             if (respuesta == JOptionPane.YES_OPTION){
                                m.borrarEntrada(m.getCaja(tabla.filaSeleccionada()));
                                tabla.actualizarDatos(m.generarVectorCaja(),m.getColumnas());
                                tabla.reset();
                             }
                             break;
                         case 4:
                             if (tabla.getChbxFiltros().isSelected())
                                m.setFiltros(true);
                             else m.setFiltros(false);
                             tabla.actualizarDatos(m.generarVectorCaja(),m.getColumnas());
                             tabla.reset();
                             break;
                         case 5:
                             tabla.dispose();
                             tabla.reset();
                             break;
                         case 6:
                             VentanaFiltrosCaja ventanafiltros = VentanaFiltrosCaja.getVentana();
                             ventanafiltros.setVisible(true);
                             tabla.actualizarDatos(m.generarVectorCaja(),m.getColumnas());
                             tabla.reset();
                             tabla.reset();
                             break;
                         case 7:
                             imprimirTabla();
                             tabla.reset();
                             break;
                         default:
                             tabla.reset();
                             break;
                     }
                 }
             */
        }
    }

    public void verCaja(int index) {
        form = FormularioCaja.getFormulario();
        form.setVisible(true);
        form.setEstado(form.EDITAR);
        form.rellenarFormulario(m.leerCaja().get(index));
        tabla.reset();
    }

    public boolean autosave(String ruta) {
        if (m.autosave(ruta)) return true; else return false;
    }

    public boolean imprimirTabla() {
        String titulo = JOptionPane.showInputDialog("Escriba el título del informe, por favor: ");
        JOptionPane.showMessageDialog(null, "pulse OK y espere a que se genere el informe!");

        List<EntradaCaja> cajas = m.leerCaja();
        CajasDataSource datasource = new CajasDataSource();

        for (EntradaCaja c : cajas) {
            datasource.addCaja(c);
        }
        //JOptionPane.showMessageDialog(null,"facturas añadidas a la cola de impresion!");
        try {
            Fichero directoriopersonal = new Fichero("informes/" + Config.getConfig().getUsuario() + "/");

            JasperReport reporte = (JasperReport) JRLoader.loadObjectFromFile("adjuntos/informe03.jasper");

            //mapa de parámetros para enviar al informe
            HashMap<String, Object> hm = new HashMap<String, Object>();
            hm.put("titulo", titulo);
            hm.put(
                "anho",
                Config.getConfig().getConfigData().getAnho() +
                " trimestre " +
                Config.getConfig().getConfigData().getAnho().getTrimestre()
            );
            hm.put("empresa", Config.getConfig().getMisDatos().getNombreEmpresa());
            hm.put("razonsocial", Config.getConfig().getMisDatos().getRazon());
            hm.put("nif", Config.getConfig().getMisDatos().getNif().toString());
            hm.put("telefono", Config.getConfig().getMisDatos().getTelefono());
            hm.put("direccion", Config.getConfig().getMisDatos().getDireccion());
            hm.put("codigopostal", Config.getConfig().getMisDatos().getCP());
            hm.put("poblacion", Config.getConfig().getMisDatos().getPoblacion());

            try {
                BufferedImage bi = ImageIO.read(new File("adjuntos/coffee.png"));

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(bi, "png", baos);
                baos.flush();

                byte[] imageInByte = baos.toByteArray();
                baos.close();
                hm.put("logo", new ByteArrayInputStream(imageInByte));
            } catch (IOException ioe) {
                JOptionPane.showMessageDialog(null, "El logo no ha sido cargado");
                hm.put("logo", null);
            }

            JasperPrint jasperPrint = JasperFillManager.fillReport(reporte, hm, datasource);

            JRPdfExporter exporter = new JRPdfExporter();
            //parameters used for the destined file.
            String rutaInforme =
                "informes/" +
                Config.getConfig().getUsuario() +
                "/" +
                "CAJA" +
                Config.getConfig().getConfigData().getAnho() +
                "_" +
                Config.getConfig().getConfigData().getAnho().getTrimestre() +
                ".pdf";

            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, rutaInforme);
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            //export to .pdf
            exporter.exportReport();
            JOptionPane.showMessageDialog(null, "informe guardado en FACTURASv20/" + rutaInforme);

            JasperViewer viewer = new JasperViewer(jasperPrint, false);
            viewer.setTitle("Informe de Caja");
            viewer.setVisible(true);
            viewer.setAlwaysOnTop(true);
            viewer.setAutoRequestFocus(true);
        } catch (JRException jrex) {
            JOptionPane.showMessageDialog(null, "Error " + jrex + " al imprimir el informe");
            return false;
        }
        return true;
    }
}
