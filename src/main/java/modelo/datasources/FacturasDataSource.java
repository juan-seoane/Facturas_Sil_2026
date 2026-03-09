package modelo.datasources;

import java.util.ArrayList;
import java.util.List;

import modelo.records.Factura;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

/**
 *
 * @author Juan Seoane
 */

public class FacturasDataSource implements JRDataSource{
    
    private List<Factura> listaFacturas = new ArrayList<Factura>();
    
    int indiceFacturaActual = -1;
    
    @Override
    public boolean next() throws JRException {
        return ++indiceFacturaActual < listaFacturas.size();
    }

    @Override
    public Object getFieldValue(JRField jrf) throws JRException {
        Object valor = null;  

    if("ID".equals(jrf.getName())) 
    { 
        valor = listaFacturas.get(indiceFacturaActual).getID() + ""; 
    } 
    else if("fecha".equals(jrf.getName())) 
    { 
        valor = listaFacturas.get(indiceFacturaActual).getFecha().toString(); 
    } 
    else if("numFactura".equals(jrf.getName())) 
    { 
        valor = listaFacturas.get(indiceFacturaActual).getNumeroFactura()+""; 
    } 
    else if("NIF".equals(jrf.getName())) 
    { 
        valor = listaFacturas.get(indiceFacturaActual).getNIF().toString(); 
    }
    if("R.S.".equals(jrf.getName())) 
    { 
        valor = listaFacturas.get(indiceFacturaActual).getNombreRS(); 
    }
    else if("base".equals(jrf.getName())) 
    { 
        valor = listaFacturas.get(indiceFacturaActual).getTotales().getBase(); 
    } 
    else if("tipo".equals(jrf.getName())) 
    { 
        valor = listaFacturas.get(indiceFacturaActual).getTotales().getTipoIVA().getFormat()+""; 
    } 
    else if("IVA".equals(jrf.getName())) 
    { 
        valor = listaFacturas.get(indiceFacturaActual).getTotales().getIVA(); 
    }
    else if ("SubTotal".equals(jrf.getName()))
    {
        valor = listaFacturas.get(indiceFacturaActual).getTotales().getSubtotal();
    }
        else if("base N.I.".equals(jrf.getName())) 
    { 
        valor = listaFacturas.get(indiceFacturaActual).getTotales().getBaseNI(); 
    } 
    else if("t ret".equals(jrf.getName())) 
    { 
        valor = listaFacturas.get(indiceFacturaActual).getTotales().getTipoRetenciones()+""; 
    } 
    else if("Retenc".equals(jrf.getName())) 
    { 
        valor = listaFacturas.get(indiceFacturaActual).getTotales().getRetenciones(); 
    }
    else if ("Total".equals(jrf.getName()))
    {
        valor = listaFacturas.get(indiceFacturaActual).getTotales().getTotal();
    }
    else if ("Concepto".equals(jrf.getName()))
    {
        valor = listaFacturas.get(indiceFacturaActual).getCategoria();
    }
    return valor;
    }
    
    public void addFactura(Factura f){
        listaFacturas.add(f);
    }
}
