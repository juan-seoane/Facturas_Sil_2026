package com.sil.facturas.domain.interfaces;

import java.util.List;

import com.sil.facturas.domain.pojos.Factura;

public interface IFacturaRepo {

  List<Factura> leerListaFacturas();

  boolean guardarListaFacturas(List<Factura> lista);

  boolean guardarFactura(Factura factura);

  boolean actualizarFactura(Factura factura);
}
