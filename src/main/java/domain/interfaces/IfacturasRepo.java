package domain.interfaces;

import java.util.List;
import domain.records.Factura;

public interface IfacturasRepo {
    public List<Factura> leerListaFacturas();

    public boolean guardarListaFacturas(List<Factura> listaFCT);
}
