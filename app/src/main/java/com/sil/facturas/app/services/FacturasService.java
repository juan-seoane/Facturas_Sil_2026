package com.sil.facturas.app.services;

import java.util.List;

import com.sil.facturas.app.core.AppContext;
import com.sil.facturas.domain.interfaces.IFacturaRepo;
import com.sil.facturas.domain.pojos.Factura;


public class FacturasService {

  private final IFacturaRepo repo;

  public FacturasService(IFacturaRepo repo) {
    this.repo = repo;
}

  public List<Factura> leerFacturas() {
    return repo.leerListaFacturas();
  }

  public void guardarFacturas(List<Factura> lista) {
      repo.guardarListaFacturas(lista);
  }

  public boolean borrarFactura(Factura f) {
    if (f == null || f.getID() == null) return false;

    List<Factura> lista = repo.leerListaFacturas();

    List<Factura> nuevaLista = lista.stream().filter(x -> !x.getID().equals(f.getID())).toList();

    return repo.guardarListaFacturas(nuevaLista);
  }

  public Integer generarID() {
    List<Factura> lista = repo.leerListaFacturas();

    if (lista.isEmpty()) {
      return 1;
    }

    // Tomamos el máximo ID existente
    return lista.stream().map(Factura::getID).max(Integer::compareTo).orElse(0) + 1;
  }

  public boolean editarFactura(Factura f) {
      boolean ok = repo.actualizarFactura(f);
      if (!ok) {
      System.err.println(
          "[FacturasService>editarFactura] Intento de editar factura inexistente: " + f.getID());
      }
      return ok;
  }

  public boolean introducirFactura(Factura factura) {
        if (factura == null) {
            return false;
        }
        if (factura.getID() == 0) {
        factura.setID(generarID());
        }
        return repo.guardarFactura(factura);
    }
}
