/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo.filtros;   

import java.text.SimpleDateFormat;
import java.util.*;

import modelo.base.Filtro;

/**
 *
 * @author Juan Seoane
 */
public class FiltroFechaCaja implements Filtro{
    static Date fechainicio;
    static Date fechafinal;
    public FiltroFechaCaja(String fechainicio, String fechafinal){
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        try{
        this.fechainicio = formato.parse(fechainicio);
        this.fechafinal = formato.parse(fechafinal);
        }catch(Exception e){
            
        }
    }
    public List filtrar(List lista){
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        List<Caja> lista2 = new ArrayList<Caja>();
        
        for (Object entrada : lista){
            try{
            if ((formato.parse(((Caja)entrada).getFecha().format())).after(fechainicio) && (formato.parse(((Caja)entrada).getFecha().format())).before(fechafinal))
                lista2.add((Caja)entrada);
            else if ((formato.parse(((Caja)entrada).getFecha().format())).equals(fechainicio) || (formato.parse(((Caja)entrada).getFecha().format())).equals(fechafinal))
                lista2.add((Caja)entrada);
            }catch(Exception e){
                
            }
        }
        return lista2;
    }
    
    public boolean filtrar(int index,List lista){
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        int cuenta = 0;
            try{
            Caja entrada = (Caja)lista.get(0);
            if ((formato.parse(entrada.getFecha().format())).after(fechainicio) && (formato.parse(entrada.getFecha().format())).before(fechafinal))
                return true;
            else if ((formato.parse(entrada.getFecha().format())).equals(fechainicio) && (formato.parse(entrada.getFecha().format())).equals(fechafinal))
                return true;
            else return false;
            }catch(Exception e){
               return false; 
            }
    }
    
    public Object filtrar(Object f){
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        try{
        if ((formato.parse(((Caja)f).getFecha().format())).after(fechainicio) && (formato.parse(((Caja)f).getFecha().format())).before(fechafinal))
            return f;
        else if ((formato.parse(((Caja)f).getFecha().format())).equals(fechainicio) && (formato.parse(((Caja)f).getFecha().format())).equals(fechafinal))
            return f;
        }catch (Exception e){
            
        }
        return null;
    }
}
