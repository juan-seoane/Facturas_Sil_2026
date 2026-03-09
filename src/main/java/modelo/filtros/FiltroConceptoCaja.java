/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo.filtros;

import java.util.*;

import modelo.base.Filtro;

/**
 *
 * @author Juan Seoane
 */
public class FiltroConceptoCaja implements Filtro{
    
    static String origen;
    
    public FiltroConceptoCaja(String origen){
       this.origen = origen;
    }
    
    public List filtrar(List lista){
        List<Caja> lista2 = new ArrayList<Caja>();
        
        for (Object entrada : lista){
            if ((((Caja)entrada).getOrigen().toUpperCase().trim()).contains(this.origen.toUpperCase().trim()))
                lista2.add((Caja)entrada);
        }
        return lista2;
    }
    
    public boolean filtrar(int index,List lista){
        Caja entrada = (Caja)lista.get(index);
            if ((entrada.getOrigen().toUpperCase().trim()).contains(this.origen.toUpperCase().trim()))
                return true;
        return false;
    }
    
    public Object filtrar(Object f){
        if ((((Caja)f).getOrigen().toUpperCase().trim()).contains(this.origen.toUpperCase().trim()))
            return f;
        return null;
    }
}
