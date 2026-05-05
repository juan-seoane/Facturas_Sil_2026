package app.services;

import domain.records.ConfigData;
import domain.records.Factura;
import infraestructure.csv.FacturaCSVRepo;
import java.util.List;
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

  public void borarFactura(Factura f) {
        // TODO : 26-04-16 : Unimplemented method borrarFactura
  }

  public boolean editarFactura(Factura f) {
    boolean ok = repo.actualizarFactura(f);
    if (!ok) {
      System.err.println("[FacturasService>editarFactura] Intento de editar factura inexistente: " + f.getID());
    }
    return ok;
  }
}
