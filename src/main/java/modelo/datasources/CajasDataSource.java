/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo.datasources;

import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

/**
 *
 * @author Juan Seoane
 */
public class CajasDataSource implements JRDataSource{
   
    private List<Caja> listaCajas = new ArrayList<Caja>();
    
    int indiceCajaActual = -1;
    
    @Override
    public boolean next() throws JRException {
        return ++indiceCajaActual < listaCajas.size();
    }

    @Override
    public Object getFieldValue(JRField jrf) throws JRException {
        Object valor = null;  

    if("ID".equals(jrf.getName())) 
    { 
        valor = listaCajas.get(indiceCajaActual).getID(); 
    }  

    else if("FECHA".equals(jrf.getName())) 
    { 
        valor = listaCajas.get(indiceCajaActual).getFecha().format; 
    }
    else if("DEBE".equals(jrf.getName())) 
    { 
        if (listaCajas.get(indiceCajaActual).isHaber())
            valor = 0.00;
        else 
            valor = (-1)*(listaCajas.get(indiceCajaActual).getCaja());
    }
    else if("HABER".equals(jrf.getName())) 
    { 
        if (listaCajas.get(indiceCajaActual).isHaber())
            valor = listaCajas.get(indiceCajaActual).getCaja();
        else 
            valor = 0.00;
    }  
    else if("ORIGEN".equals(jrf.getName())) 
    { 
        valor = listaCajas.get(indiceCajaActual).getOrigen(); 
    }
    return valor;
    }
    
    public void addCaja(Caja cj){
        listaCajas.add(cj);
    }
}
