/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo.filtros;

import java.util.*;

import modelo.base.Filtro;
import modelo.records.Factura;

/**
 *
 * @author Juan Seoane
 */
public class FiltroCategoria implements Filtro {

    String categoria;

    public FiltroCategoria(String categoria) {
        this.categoria = categoria;
    }

    @Override
    public List<Factura> filtrar(List lista){
        List<Factura> lista2 = new ArrayList<>();

        for (Object entrada : lista){
            if ((((Factura)entrada).getCategoria().toUpperCase().trim()).equals(this.categoria.toUpperCase().trim()))
                lista2.add((Factura)entrada);
        }
        return lista2;
    }
    @Override
    public boolean filtrar(int index,List lista){
        Factura entrada = (Factura)lista.get(index);
            if ((entrada.getCategoria().toUpperCase().trim()).equals(this.categoria.toUpperCase().trim()))
                return true;
        return false;
    }
    @Override
    public Object filtrar(Object f){
        if ((((Factura)f).getCategoria().toUpperCase().trim()).equals(this.categoria.toUpperCase().trim()))
            return f;
        return null;
    }
}

