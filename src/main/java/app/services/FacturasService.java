package app.services;

import java.util.List;

import domain.records.ConfigData;
import domain.records.Factura;
import infraestructure.csv.FacturaCSVRepo;
import presentation.viewmodels.FacturaFX;

public class FacturasService {

  private final FacturaCSVRepo repo;

  public FacturasService(ConfigData cfgdata) {
    this.repo = new FacturaCSVRepo(cfgdata.getRutas().getFCT());
  }

  public List<Factura> leerFacturas() {
    return repo.leerListaFacturas();
  }

  public List<FacturaFX> leerFacturasFX() {
    return leerFacturas().stream().map(FacturaFX::fromDomain).toList();
  }

  public void guardarFacturas(List<Factura> lista) {
    repo.guardarListaFacturas(lista);
  }
}
