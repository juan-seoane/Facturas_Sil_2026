package domain.interfaces;

import java.util.List;
import domain.records.Factura;

public interface IfacturasRepo {
    //TODO : 2026/05/08 : Completasr este interface IfacturasRepo
    public List<Factura> leerListaFacturas();

    public boolean guardarListaFacturas(List<Factura> listaFCT);

    boolean guardarFactura(Factura factura);
}
