/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo.datasources;

import java.util.ArrayList;
import java.util.List;

import modelo.records.RazonSocial;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

/**
 *
 * @author Juan Seoane
 */
public class DistribuidoresDataSource implements JRDataSource{
   
    private List<RazonSocial> listaDistribuidores = new ArrayList<RazonSocial>();
    
    int indiceDistribuidorActual = -1;
    
    @Override
    public boolean next() throws JRException {
        return ++indiceDistribuidorActual < listaDistribuidores.size();
    }

    @Override
    public Object getFieldValue(JRField jrf) throws JRException {
        Object valor = null;  

    if("ID".equals(jrf.getName())) 
    { 
        valor = listaDistribuidores.get(indiceDistribuidorActual).getID() + ""; 
    }  
    else if("NIF".equals(jrf.getName())) 
    { 
        valor = listaDistribuidores.get(indiceDistribuidorActual).getNIF().toString(); 
    }
    if("NOMBRE".equals(jrf.getName())) 
    { 
        valor = listaDistribuidores.get(indiceDistribuidorActual).getNombre(); 
    }
    else if("RAZON".equals(jrf.getName())) 
    { 
        valor = listaDistribuidores.get(indiceDistribuidorActual).getRazon(); 
    } 
    else if("DIRECCION".equals(jrf.getName())) 
    { 
        valor = listaDistribuidores.get(indiceDistribuidorActual).getDireccion(); 
    } 
    else if("C.P.".equals(jrf.getName())) 
    { 
        valor = listaDistribuidores.get(indiceDistribuidorActual).getCP(); 
    }
    else if ("POBLACION".equals(jrf.getName()))
    {
        valor = listaDistribuidores.get(indiceDistribuidorActual).getPoblacion();
    }
    else if("TELEFONO".equals(jrf.getName())) 
    { 
        valor = listaDistribuidores.get(indiceDistribuidorActual).getTelefono(); 
    } 
    else if("CATEGORIA".equals(jrf.getName())) 
    { 
        valor = listaDistribuidores.get(indiceDistribuidorActual).getCategoria(); 
    }
    else if ("IVA".equals(jrf.getName()))
    {
        valor = listaDistribuidores.get(indiceDistribuidorActual).getTotales().getIVA();
    }
    else if ("TOTAL".equals(jrf.getName()))
    {
        valor = listaDistribuidores.get(indiceDistribuidorActual).getTotales().getTotal();
    }

    return valor;
    }
    
    public void addDistribuidor(RazonSocial rs){
        listaDistribuidores.add(rs);
    }
}
