package com.sil.facturas.domain.interfaces;

import java.util.List;

import com.sil.facturas.domain.records.Factura;

public interface IfacturasRepo {
    //TODO : 26-05-08 : Completar este interface IfacturasRepo
    public List<Factura> leerListaFacturas();

    public boolean guardarListaFacturas(List<Factura> listaFCT);

    boolean guardarFactura(Factura factura);
}
