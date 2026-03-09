/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo.filtros;

import java.util.*;

import modelo.base.Filtro;
import modelo.records.RazonSocial;

/**
 *
 * @author Juan Seoane
 */
public class FiltroCategoriaRS implements Filtro{
    static String categoria;
    public FiltroCategoriaRS(String categoria){
       this.categoria = categoria;
    }
    public List filtrar(List lista){
        List<RazonSocial> lista2 = new ArrayList<RazonSocial>();
        
        for (Object entrada : lista){
            if ((((RazonSocial)entrada).getCategoria().toUpperCase().trim()).equals(this.categoria.toUpperCase().trim()))
                lista2.add((RazonSocial)entrada);
        }
        return lista2;
    }
    
    public boolean filtrar(int index,List lista){
        RazonSocial entrada = (RazonSocial)lista.get(index);
            if ((entrada.getCategoria().toUpperCase().trim()).equals(this.categoria.toUpperCase().trim()))
                return true;
        return false;
    }
    
    public Object filtrar(Object f){
        if ((((RazonSocial)f).getCategoria().toUpperCase().trim()).equals(this.categoria.toUpperCase().trim()))
            return f;
        return null;
    }
}

