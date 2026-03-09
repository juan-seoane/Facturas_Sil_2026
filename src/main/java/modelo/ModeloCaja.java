/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import ui.*;
import ui.ventanas.VentanaFiltrosCaja;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import modelo.base.Config;
import modelo.base.Fichero;
import modelo.filtros.FiltroConceptoCaja;
import modelo.filtros.FiltroDebeHaberCaja;
import modelo.filtros.FiltroFechaCaja;
import modelo.records.EntradaCaja;
import modelo.records.Fecha;

/**
 *
 * @author Juan Seoane
 */
public class ModeloCaja {
    private static ModeloCaja instancia = null;
    List<EntradaCaja> caja;
    static Fichero<EntradaCaja> ficheroCJA;
    Vector<Vector> vectorcaja;
//    Stack<Factura> pilafacturasant = new Stack<Factura>();
//    Stack<Factura> pilafacturassig = new Stack<Factura>();
    static Vector vectorcolumnas;
    static int ultimaID;
    public static int numeroEntradas = 0;
    public boolean filtros  = false;
    
    private ModeloCaja(){
// TODO - 2024-05-06 : - Estudiar el cambio en la clase 'Caja' que ha pasado a un Java record 'EntradaCaja'
//        caja = leerCaja();
        numeroEntradas = caja.size();
        vectorcaja = generarVectorCaja();
        vectorcolumnas = getColumnas();
        ultimaID = caja.size();
        numeroEntradas = caja.size();
//        //System.out.println("tamaño de la list de caja : "+caja.size());
    }
    
    public static ModeloCaja getModelo(){ 
        if (instancia == null)
            instancia = new ModeloCaja();
        return instancia;
    }
// TODO - 2024-05-06 : - Estudiar el cambio en la clase 'Caja' que ha pasado a un Java record 'EntradaCaja'
// TODO - 2024-05-06 : - En la clase 'Caja' había setters que no pueden estar en un Java record (inmutable por definición)

//    public EntradaCaja getCaja(int index){
//        EntradaCaja c = leerCaja().get(index);
//        return c;
//    }
    
    public boolean autosave(String ruta){
        Fichero<EntradaCaja> auto = new Fichero<EntradaCaja>(ruta);
        if (auto.escribir((ArrayList<EntradaCaja>)caja))
            return true;
        else return false;   
    }
    
    public boolean guardarListaCajas(ArrayList<EntradaCaja> lista){
        if (ficheroCJA.escribir(lista))
            return true;
        else return false;
    }
    
    public boolean guardarCaja(EntradaCaja c){
        caja.add(c);
        Collections.sort(caja);
        int cuenta = 0;
        for (EntradaCaja cj : caja){
            cuenta++;
//            cj.setID(cuenta);
        }
// TODO - 2024-05-06 : - Estudiar el cambio en la clase 'Caja' que ha pasado a un Java record 'EntradaCaja'
        if (guardarListaCajas((ArrayList<EntradaCaja>)caja))
            return true;
        else return false;
    }
    public void setFiltros(boolean bool){
        this.filtros = bool;
    }
    public boolean filtrosActivados(){
        return this.filtros;
    }
    public boolean editarCaja(EntradaCaja c1,EntradaCaja c2, int index){
        caja.remove(c1);
        caja.add(index,c2);
        Collections.sort(caja);
        int cuenta = 0;
        for (EntradaCaja cj : caja){
            cuenta++;
// TODO - 2024-05-06 : - Estudiar el cambio en la clase 'Caja' que ha pasado a un Java record 'EntradaCaja'
//            cj.setID(cuenta);
        }
        if (guardarListaCajas((ArrayList<EntradaCaja>)caja))
            return true;
        else return false;
    }
    public boolean borrarEntrada(EntradaCaja c){
        caja.remove(c);
        Collections.sort(caja);
        int cuenta = 0;
        for (EntradaCaja cj : caja){
            cuenta++;
//            cj.setID(cuenta);
        }
        if (guardarListaCajas((ArrayList<EntradaCaja>)caja))
            return true;
        else return false;
    }
    public Vector<Vector> generarVectorCaja(){
        List<EntradaCaja> cajafiltrada = leerCaja();
        Vector<Vector> vectorcaja = new Vector<Vector>();
        if (cajafiltrada.size()>0){
            for (EntradaCaja c : cajafiltrada){
                Vector temp = new Vector();
                temp.add(c.ID());
                temp.add(c.fecha().toString());
                if (!c.haber()){
                    temp.add(c.caja());
                    temp.add(" - ");
                }
                else if (c.haber()){
                    temp.add(" - ");
                    temp.add(c.caja());
                } 
                temp.add(c.origen());
                
                vectorcaja.add(temp);
            }
        }
        else{
            Vector temp = new Vector();
            temp.add(0);
            temp.add(new Fecha(1,1,2000));
            temp.add(" - ");
            temp.add(" - ");
            temp.add(" - ");
         
          
            vectorcaja.add(temp);
        }
            
    return vectorcaja;
    }

    public Vector getColumnas(){
        Vector columnas = new Vector();
        columnas.add("ID");
        columnas.add("FECHA");
        columnas.add("DEBE");
        columnas.add("HABER");
        columnas.add("ORIGEN");
        
        return columnas;
    }
    public List<EntradaCaja> filtrar (List<EntradaCaja> lista){       
        if (this.filtrosActivados()){
        List<EntradaCaja> lista1 = new ArrayList<EntradaCaja>();
        List<EntradaCaja> lista2 = new ArrayList<EntradaCaja>();
        List<EntradaCaja> lista3 = new ArrayList<EntradaCaja>();
        
        VentanaFiltrosCaja v = VentanaFiltrosCaja.getVentana();
        if (v.getChbxFecha().isSelected()){
            FiltroFechaCaja filtro1 = new FiltroFechaCaja(v.getTxtDesde().getText(), v.getTxtHasta().getText());
            lista1 = filtro1.filtrar(lista);
        }
        else lista1 = lista;
        if (v.getChbxOrigen().isSelected()){
            FiltroConceptoCaja filtro2 = new FiltroConceptoCaja(v.getTxtOrigen().getText());
            lista2 = filtro2.filtrar(lista1);
        }
        else lista2 = lista1;
        if (v.getChbxBalance().isSelected()){
            FiltroDebeHaberCaja filtro3 = new FiltroDebeHaberCaja(v.getBtnHaber().isSelected());
            lista3 = filtro3.filtrar(lista2);
        }
        else lista3 = lista2;
        
        return lista3;
        }
        else return lista;
    }
    public List<EntradaCaja> leerCaja(){
        ficheroCJA = new Fichero<EntradaCaja>(Config.getConfigActual().getRutaCJA().toString());
        caja = ficheroCJA.leer();
        this.numeroEntradas = caja.size();
        Collections.sort(caja);
        int i = 0;
        for (EntradaCaja c : caja){
           i++;
//           c.setID(i);
        }
        return filtrar(caja);
    }   
}
