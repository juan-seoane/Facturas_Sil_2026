package infrastructure.informes.datasources;

import java.util.ArrayList;
import java.util.List;

import domain.records.Factura;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

/**
 *
 * @author Juan Seoane
 */
// NOTE : 26-03-13 : Los DataSources de JaperReports mapean listas de objetos y convierten cada uno de ellos en campos para rellenar un informe
// TODO : 26-03-13 : Cambiar de Arraylist<Factura> a stream()

public class FacturasDataSource implements JRDataSource {

    private List<Factura> listaFacturas = new ArrayList<>();

    int indiceFacturaActual = -1;

    @Override
    public boolean next() throws JRException {
        return ++indiceFacturaActual < listaFacturas.size();
    }

    @Override
    public Object getFieldValue(JRField jrf) throws JRException {
        Object valor = null;

        if (null != jrf.getName()) switch (jrf.getName()) {
            case "ID" -> valor = listaFacturas.get(indiceFacturaActual).ID + "";
            case "fecha" -> valor = listaFacturas.get(indiceFacturaActual).fecha.toString();
            case "numFactura" -> valor = listaFacturas.get(indiceFacturaActual).numeroFactura + "";
            case "NIF" -> valor = listaFacturas.get(indiceFacturaActual).RS.getNif().toString();
            default -> {
            }
        }
        if (null != jrf.getName()) switch (jrf.getName()) {
            case "R.S." -> valor = listaFacturas.get(indiceFacturaActual).RS.getNombre();
            case "base" -> valor = listaFacturas.get(indiceFacturaActual).totales.getBase();
            case "tipo" -> valor = listaFacturas.get(indiceFacturaActual).totales.getTipoIVA();
            case "IVA" -> valor = listaFacturas.get(indiceFacturaActual).totales.getIVA();
            case "SubTotal" -> valor = listaFacturas.get(indiceFacturaActual).totales.getSubtotal();
            case "base N.I." -> valor = listaFacturas.get(indiceFacturaActual).totales.getBaseNI();
            case "t ret" -> valor = listaFacturas.get(indiceFacturaActual).totales.getRet();
            case "Retenc" -> valor = listaFacturas.get(indiceFacturaActual).totales.getRetenciones();
            case "Total" -> valor = listaFacturas.get(indiceFacturaActual).totales.getTotal();
            case "Concepto" -> valor = listaFacturas.get(indiceFacturaActual).concepto;
            default -> {
            }
        }
        return valor;
    }

    public void addFactura(Factura f) {
        listaFacturas.add(f);
    }
}
