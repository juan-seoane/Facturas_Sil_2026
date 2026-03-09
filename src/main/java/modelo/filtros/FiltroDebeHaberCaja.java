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
public class FiltroDebeHaberCaja implements Filtro{
    static boolean isHaber;

    public FiltroDebeHaberCaja(boolean isHaber){
        this.isHaber = isHaber;
    }
    public List filtrar(List lista){

        List<Caja> lista2 = new ArrayList<Caja>();
        
        for (Object entrada : lista){
            try{
            if (((Caja)entrada).isHaber() == isHaber)
                lista2.add((Caja)entrada);
            }catch(Exception e){
                
            }
        }
        return lista2;
    }
    
    public boolean filtrar(int index,List lista){

        int cuenta = 0;

            Caja entrada = (Caja)lista.get(0);
            if (((Caja)entrada).isHaber() == isHaber)
                return true;
            else return false;

    }
    
    public Object filtrar(Object c){

        if (((Caja)c).isHaber() == isHaber)
            return c;

        return null;
    }
}