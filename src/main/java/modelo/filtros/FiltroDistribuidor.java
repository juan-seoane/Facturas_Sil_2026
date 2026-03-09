/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo.filtros;

import java.util.ArrayList;
import java.util.List;

import modelo.records.Factura;

/**
 *
 * @author Juan Seoane
 */
public class FiltroDistribuidor {
    String cadena;
    public FiltroDistribuidor(String texto){
       this.cadena = texto;
    }
    public List filtrar(List lista){
        List<Factura> lista2 = new ArrayList<Factura>();
        
        for (Object entrada : lista){
            //System.out.println("nombre del distrib : "+((Factura)entrada).getNombreRS());
            //System.out.println("razon social : "+((Factura)entrada).getDistribuidor().getRazon());
            if (((((Factura)entrada).getNombreRS().toUpperCase().trim()).contains(this.cadena.toUpperCase().trim()))
                    || ((((Factura)entrada).getDistribuidor().getRazon().toUpperCase().trim()).contains(this.cadena.toUpperCase().trim())))
                lista2.add((Factura)entrada);
        }
        return lista2;
    }
    
    public boolean filtrar(int index,List lista){
        Factura entrada = (Factura)lista.get(index);
            if((entrada.getNombreRS().toUpperCase().trim()).contains(this.cadena.toUpperCase().trim())
                    || (entrada.getRazonRS().toUpperCase().trim()).contains(this.cadena.toUpperCase().trim()))
                return true;
        return false;
    }
    
    public Object filtrar(Object f){
        if (((((Factura)f).getNombreRS().toUpperCase().trim()).contains(this.cadena.toUpperCase().trim()))
                    || ((((Factura)f).getRazonRS().toUpperCase().trim()).contains(this.cadena.toUpperCase().trim())))
            return f;
        return null;
    }
}
