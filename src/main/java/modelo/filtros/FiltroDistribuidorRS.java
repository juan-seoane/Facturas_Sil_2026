/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo.filtros;

import java.util.ArrayList;
import java.util.List;

import modelo.records.RazonSocial;

/**
 *
 * @author Juan Seoane
 */
public class FiltroDistribuidorRS {
    String cadena;
    public FiltroDistribuidorRS(String texto){
       this.cadena = texto;
    }
    public List filtrar(List lista){
        List<RazonSocial> lista2 = new ArrayList<RazonSocial>();
        
        for (Object entrada : lista){
            //System.out.println("nombre del distrib : "+((Factura)entrada).getNombreRS());
            //System.out.println("razon social : "+((Factura)entrada).getDistribuidor().getRazon());
            if (((((RazonSocial)entrada).getNombre().toUpperCase().trim()).contains(this.cadena.toUpperCase().trim()))
                    || ((((RazonSocial)entrada).getRazon().toUpperCase().trim()).contains(this.cadena.toUpperCase().trim())))
                lista2.add((RazonSocial)entrada);
        }
        return lista2;
    }
    
    public boolean filtrar(int index,List lista){
        RazonSocial entrada = (RazonSocial)lista.get(index);
            if((entrada.getNombre().toUpperCase().trim()).contains(this.cadena.toUpperCase().trim())
                    || (entrada.getRazon().toUpperCase().trim()).contains(this.cadena.toUpperCase().trim()))
                return true;
        return false;
    }
    
    public Object filtrar(Object f){
        if (((((RazonSocial)f).getNombre().toUpperCase().trim()).contains(this.cadena.toUpperCase().trim()))
                    || ((((RazonSocial)f).getRazon().toUpperCase().trim()).contains(this.cadena.toUpperCase().trim())))
            return f;
        return null;
    }
}
