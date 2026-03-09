/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo.filtros;   

import java.text.SimpleDateFormat;
import java.util.*;

import modelo.base.Filtro;
import modelo.records.Factura;

/**
 *
 * @author Juan Seoane
 */
public class FiltroFecha implements Filtro{
    static Date fechainicio;
    static Date fechafinal;
    public FiltroFecha(String fechainicio, String fechafinal){
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        try{
        this.fechainicio = formato.parse(fechainicio);
        this.fechafinal = formato.parse(fechafinal);
        }catch(Exception e){
            
        }
    }
    public List filtrar(List lista){
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        List<Factura> lista2 = new ArrayList<Factura>();
        
        for (Object entrada : lista){
            try{
            if ((formato.parse(((Factura)entrada).getFecha())).after(fechainicio) && (formato.parse(((Factura)entrada).getFecha())).before(fechafinal))
                lista2.add((Factura)entrada);
            else if ((formato.parse(((Factura)entrada).getFecha())).equals(fechainicio) || (formato.parse(((Factura)entrada).getFecha())).equals(fechafinal))
                lista2.add((Factura)entrada);
            }catch(Exception e){
                
            }
        }
        return lista2;
    }
    
    public boolean filtrar(int index,List lista){
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        int cuenta = 0;
            try{
            Factura entrada = (Factura)lista.get(0);
            if ((formato.parse(entrada.getFecha())).after(fechainicio) && (formato.parse(entrada.getFecha())).before(fechafinal))
                return true;
            else if ((formato.parse(entrada.getFecha())).equals(fechainicio) && (formato.parse(entrada.getFecha())).equals(fechafinal))
                return true;
            else return false;
            }catch(Exception e){
               return false; 
            }
    }
    
    public Object filtrar(Object f){
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        try{
        if ((formato.parse(((Factura)f).getFecha())).after(fechainicio) && (formato.parse(((Factura)f).getFecha())).before(fechafinal))
            return f;
        else if ((formato.parse(((Factura)f).getFecha())).equals(fechainicio) && (formato.parse(((Factura)f).getFecha())).equals(fechafinal))
            return f;
        }catch (Exception e){
            
        }
        return null;
    }
}
