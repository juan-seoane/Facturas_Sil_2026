/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo.base;

import ui.*;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.JOptionPane;

import modelo.records.Factura;
/**
 *
 * @author Juan Seoane
 */
public class FundirArchivos extends Thread{
 /*   
    ArrayList<Factura > refundido;
    ArrayList<Caja > refundidocajas;
    VentanaFundicion vf;
    
    public FundirArchivos(){
        this.refundido = new ArrayList<Factura >();
        this.refundidocajas = new ArrayList<Caja >();
        vf = new VentanaFundicion();
        vf.setVisible(true);

    }
    
    public void run(){
        while(!vf.cancelado() && ! vf.hecho()){
            System.out.print("");
        }
        if (vf.cancelado())
            vf.dispose();
        if (vf.hecho())
            if (fundirArchivos(vf.getAnho(),vf.getTrimestres()))
                JOptionPane.showMessageDialog(null,"Archivos del año "+vf.getAnho()+" refundidos!");
        vf.dispose();

    }
    
    public boolean fundirArchivos(int año, ArrayList<Integer > trimestres){
        if (vf.facturas()){
            for (Integer i : trimestres){
                Fichero<Factura > nuevofichero = new Fichero<Factura>("datos/"+Config.getConfig().getUsuario()+"/FCT"+año+i+".fct");
                ArrayList<Factura > nuevalista = nuevofichero.leer();
                this.refundido.addAll(nuevalista);
            }

            for (int i = 0; i < refundido.size(); i++){
                Factura f = refundido.get(i);
                for (int j = 0; j < refundido.size(); j++){
                Factura f2 = refundido.get(j);
                    if (i == j)
                        continue;
                    else if (f.equals(f2))
                            refundido.remove(f2);
                }
                i++;
                f.setID(i);
            }
            
            Collections.sort(refundido);

            Fichero<Factura > ficherofinal = new Fichero<Factura >("datos/"+Config.getConfig().getUsuario()+"/FCT"+año+"0.fct");
            if (ficherofinal.escribir(this.refundido))
                JOptionPane.showMessageDialog(null,"Facturas Fundidas!");
            else {
                JOptionPane.showMessageDialog(null,"Ha habido un error fundiendo las facturas!");
                return false;
            }
        }
         if (vf.cajas()){
            for (Integer i : trimestres){
                Fichero<Caja > nuevofichero = new Fichero<Caja >("datos/"+Config.getConfig().getUsuario()+"/CJA"+año+i+".cja");
                ArrayList<Caja > nuevalista = nuevofichero.leer();
                this.refundidocajas.addAll(nuevalista);
            }

            for (int i = 0; i < refundidocajas.size(); i++){
                Caja c = refundidocajas.get(i);
                for (int j = 0; j < refundidocajas.size(); j++){
                Caja c2 = refundidocajas.get(j);
                    if (i == j)
                        continue;
                    else if (c.equals(c2))
                            refundidocajas.remove(c2);
                }
                i++;
                c.setID(i);
            }
            
            Collections.sort(refundidocajas);

            Fichero<Caja  > ficherofinal = new Fichero<Caja   >("datos/"+Config.getConfig().getUsuario()+"/CJA"+año+"0.cja");
            if (ficherofinal.escribir(this.refundidocajas))
                JOptionPane.showMessageDialog(null,"Cajas Fundidas!");
            else {
                JOptionPane.showMessageDialog(null,"Ha habido un error fundiendo las cajas!");
                return false;
            }
        }
        return true;
    }
*/
}
