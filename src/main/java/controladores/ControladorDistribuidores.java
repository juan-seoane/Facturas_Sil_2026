package controladores;

import modelo.*;
import modelo.base.Config;
import modelo.base.Fichero;
import modelo.datasources.DistribuidoresDataSource;
import modelo.records.NIF;
import modelo.records.Nota;
import modelo.records.RazonSocial;
import ui.*;
import ui.formularios.FormularioFact;
import ui.formularios.FormularioRS;
import ui.tablas.TablaDistribuidores;
import ui.ventanas.VentanaFiltrosRS;

import java.awt.Frame;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

public class ControladorDistribuidores extends Thread {

    private static ControladorDistribuidores instancia = null;
    private TablaDistribuidores tabla;
    private ModeloDistribuidores m;
    private FormularioRS form;
    private int indexactual;
    public static VentanaFiltrosRS filtros;
    /**
     * ESTADO 0 : TABLA DE DISTRIBUIDORES APAGADA, SÓLO VENTANA EMERGENTE EN EL
     * FORMULARIO (POR AHORA) ESTADO 1 : TABLA DE DISTRIBUIDORES ENCENDIDA                              
        *
     */
    private static int estado = 0;

    private ControladorDistribuidores() {
        m = ModeloDistribuidores.getModelo();
        tabla = new TablaDistribuidores(m.generarVectorRS(), m.getColumnas());
        actualizarTabla();
        form = FormularioRS.getFormulario();
        filtros = VentanaFiltrosRS.getVentana();
        filtros.setVisible(false);
        

    }

    public static ControladorDistribuidores getControlador() {
        if (instancia == null) {
            instancia = new ControladorDistribuidores();
        }
        return instancia;
    }

    /**
     * setEstado(): params: e es un integer que puede tomar los siguientes
     * valores: 0 : tabla de distribuidores apagada, solo ventana emegente en el
     * formulario 1 : tabla de distribuidores encendida, venttana emergente
     * apagada
     *
     */
    public static void setEstado(int e) {
        ControladorDistribuidores.estado = e;
    }

    public void visible(boolean bool) {
        actualizarTabla();
        tabla.setVisible(bool);
        form.setVisible(false);
    }

    public void run() {
        while (true) {
            if (estado == 0) {
                tabla.setVisible(false);
                if (!FormularioFact.getFormulario().comprobarNIF() && !FormularioFact.getFormulario().comprobarRS() && !FormularioFact.getFormulario().emergenteRS()) {
                    System.out.print("");
                }
                /**
                 * TODO : REPASAR EL ALGORITMO DE COMPROBACIONES DE
                 * RAZONESSOCIALES EN EL FORMULARIO DE FACTURAS....
                 */
                if (FormularioFact.getFormulario().comprobarNIF()) {
                    boolean isCIF;
                    NIF nuevoNIF;
                    if (FormularioFact.getFormulario().getLetraNIFRS().equals("")){
                        isCIF = true;
                        nuevoNIF = new NIF(FormularioFact.getFormulario().getNumNIFRS(),FormularioFact.getFormulario().getLetraCIFRS().toUpperCase(),isCIF);
                        m.comprobacionNIF(nuevoNIF);
                    }
                    else{
                        isCIF = false;
                        nuevoNIF = new NIF(FormularioFact.getFormulario().getNumNIFRS(),FormularioFact.getFormulario().getLetraNIFRS().toUpperCase(),isCIF);
                        m.comprobacionNIF(nuevoNIF);
                    }
                    FormularioFact.getFormulario().resetRS();
                }
                if (FormularioFact.getFormulario().comprobarRS()) {
                    m.comprobacionRS();
                    FormularioFact.getFormulario().resetRS();
                    tabla.reset();
                    tabla.rellenarTabla(m.generarVectorRS(),m.getColumnas());
                }
                if (FormularioFact.getFormulario().emergenteRS()) {
                    m.activarTablaEmergente();
                }
                
            }
            if (estado == 1) {
                tabla.setVisible(true);
                if (tabla.haCambiado()) {
                    switch (tabla.pulsado) {
                        case 1:
//                            JOptionPane.showMessageDialog(null,"Ha pulsado el boton 1 : nueva RS ");
                            nuevaRS();
                            break;
                        case 2:
//                            JOptionPane.showMessageDialog(null,"Ha pulsado el boton 1 : ver RS ");
                            verRS(tabla.getIndice());
                            break;
                        case 3:
                            imprimirTabla();
                            break;
                        case 4:
                            borrarRS(tabla.getIndice());
                            break;
                        case 5:
                            editarRS(tabla.getIndice());
                            break;
                        case 6:
                            verFiltros();
                            break;
                        case 7:
                            
                            break;
                        default: 
                            break;
                    }
                    tabla.reset();
                    actualizarTabla();
                }
            }
            if (form.enviado() || form.cancelado()){
                    if (form.enviado()){
                        form.reset();
                        editarRS(indexactual);
                    }else
                        form.reset();
                }
        }
    }
    public boolean verFiltros(){
         filtros = VentanaFiltrosRS.getVentana();
         filtros.setVisible(true);
         filtros.setAlwaysOnTop(true);
        return true;   
    }
    
    public boolean nuevaRS() {
        form.setVisible(true);
        form.setTitle("Nueva Razon Social");
        form.limpiarFormulario();

        while (!form.enviado() && !form.cancelado()) {
            System.out.print("");
        }
        if (form.enviado()) {
            RazonSocial rs = form.recogerRazonForm();
            if (rs != null){
                if (!m.comprobacionNIF(form.recogerRazonForm().getNIF())){
                    form.setVisible(false);
                    m.insertarDistribuidor(form.recogerRazonForm());
                    actualizarTabla();
                    return true;
                }
            }
        }
        form.reset();
        return false;
    }

    public boolean verRS(int index) {
//        JOptionPane.showMessageDialog(null,"Visualizando Distribuidor");
        form = FormularioRS.getFormulario();
        form.setTitle("Visor de Razones Sociales");
        form.setVisible(true);
        
        indexactual = index;
//        System.out.println("nombre razon: "+ m.getListaDistribuidores().get(index).getNombre());
        RazonSocial rs = m.getListaDistribuidores().get(index);
//        System.out.println("ID seleccionada: "+rs.getID());
        if (form.rellenarFormulario(rs))
        {
            form.cambiarBotonoK("EDITAR");
        }      
        return true;
    }

    public boolean editarRS(int index) {
//        JOptionPane.showMessageDialog(null,"Editando Distribuidor");
        form.setVisible(true);
        form.setTitle("Editor de Razones Sociales");
        form.limpiarFormulario();
        RazonSocial rs = m.getListaDistribuidores().get(index);
        int id = rs.getID();
        form.rellenarFormulario(rs);
        form.cambiarBotonoK("OK");
        while (!form.enviado() && !form.cancelado()) {
            System.out.print("");
        }
        if (form.enviado()) {
            RazonSocial antrs = m.getListaDistribuidores().get(index);
           RazonSocial nuevars = form.recogerRazonForm();
           Nota nota = form.getNota();
           if (nuevars!=null)
           {
               nuevars.setID(id);
               nuevars.setNota(nota);
               
                m.editarDistribuidor(nuevars, index);
                form.reset();
                actualizarTabla();
                ModeloFacturas.actualizarDistribuidorEnFacturas(antrs,nuevars);
                return true; 
           }
        }
        form.reset();
        return false;
    }

    public boolean borrarRS(int index) {
        RazonSocial rs = m.getListaDistribuidores().get(index);
        form = FormularioRS.getFormulario();
        form.limpiarFormulario();
        form.reset();
        int respuesta = JOptionPane.showConfirmDialog(null, "Seguro que desea borrar el Distribidor " + index+1 + " ? ", "ATENCIÓN!", JOptionPane.YES_NO_OPTION);
        if (respuesta == JOptionPane.YES_OPTION) {
            m.borrarDistribuidor(rs);
            actualizarTabla();
            return true;
        } else {
            return false;
        }
    }
    
    public boolean actualizarTabla() 
    {

        tabla.actualizarModelo(m.generarVectorRS(), indexactual);
        tabla.setExtendedState(Frame.MAXIMIZED_BOTH);
        tabla.setVisible(true);
        return true;
    }
    
    public boolean autosave(String ruta){
        
        if (m.autosave(ruta))
            return true;
        else return false;     
    }
    
    public boolean imprimirTabla() {
        String titulo = JOptionPane.showInputDialog("Escriba el título del informe, por favor: ");
        JOptionPane.showMessageDialog(null, "pulse OK y espere a que se genere el informe!");

        List<RazonSocial> distribuidores = m.cargarDistribuidores();
        DistribuidoresDataSource datasource = new DistribuidoresDataSource();

        for (RazonSocial rs : distribuidores) {
            datasource.addDistribuidor(rs);
        }
        //JOptionPane.showMessageDialog(null,"facturas añadidas a la cola de impresion!");
        try {
            //TODO - 24-03-26 : He probado a parametrizar de forma genérica el tipo Fichero -> Fichero<Object>

            Fichero<Object> directoriopersonal = new Fichero("informes/"+Config.getConfig().getUsuario()+"/");
            
            JasperReport reporte = (JasperReport) JRLoader.loadObjectFromLocation("adjuntos/informe02.jasper");

            //mapa de parámetros para enviar al informe
            //TODO - 24-03-26 : He probado a parametrizar de forma genérica el tipo HashMap -> HashMap<Object, Object>
                HashMap hm = new HashMap<Object, Object>();
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
                    JOptionPane.showMessageDialog(null, "El logo no ha sido cargado");
                    hm.put("logo",null);
                }
                
            JasperPrint jasperPrint = JasperFillManager.fillReport(reporte, hm, datasource);

            JRExporter exporter = new JRPdfExporter();
            //parameters used for the destined file.
            String rutaInforme = "informes/"+Config.getConfig().getUsuario()+"/"+"DIST"+ Config.getConfig().getAnho().getAnho() + "_" + Config.getConfig().getAnho().getTrimestre() + ".pdf";

            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, rutaInforme);
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            //export to .pdf  
            exporter.exportReport();
            JOptionPane.showMessageDialog(null, "informe guardado en FACTURASv20/" + rutaInforme);

            JasperViewer viewer = new JasperViewer(jasperPrint, false);
            viewer.setTitle("Listado de Distribuidores");
            viewer.setVisible(true);

        } catch (JRException jrex) {
            JOptionPane.showMessageDialog(null, "Error " + jrex + " al imprimir el informe");
            return false;
        }
        return true;

    }
}