/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo.base;

import java.io.*;
import java.util.*;
import javax.swing.JOptionPane;

import modelo.records.Extracto;
import modelo.records.Factura;
import modelo.records.Fecha;
import modelo.records.NIF;
import modelo.records.Nota;
import modelo.records.RazonSocial;
import modelo.records.TipoGasto;
import modelo.records.Totales;
/**
 *
 * @author Juan Seoane
 */
public class Ficherosv1{
/*    
    public String rutaficherov1;
    public String rutanuevofichero;
    public String rutaficherorsv1;
    public String rutanuevoficherors;
    public ArrayList coleccion = new ArrayList();
    public ArrayList antcoleccion = new ArrayList();
    public ArrayList distribuidores = new ArrayList();
    public ArrayList antdistribuidores = new ArrayList();
    public ArrayList<Factura> nuevacoleccion = new ArrayList<Factura>();
    public ArrayList<RazonSocial> nuevosdistribuidores = new ArrayList<RazonSocial>();
    
    public Ficherosv1(){
        String año = JOptionPane.showInputDialog(null, "Escriba el año a importar", ""); 
        this.rutaficherov1 = "antvers/FACTURAS_"+año+".txt"; 
        this.rutanuevofichero = "datos/"+Config.getConfig().getUsuario()+"/FCT"+año+"0.fct";
        this.rutaficherorsv1 = "antvers/RS_"+año+".txt"; 
        this.rutanuevoficherors = "datos/"+Config.getConfig().getUsuario()+"/RS.rs";
        File ficherofct = new File(rutaficherov1);
            if (!ficherofct.exists()){
                JOptionPane.showMessageDialog(null,"El fihero no existe o no se ha encontrado");
            }else {
                convertirFCT();
            }
        File ficherors = new File(rutaficherorsv1);
            if (!ficherors.exists()){
                JOptionPane.showMessageDialog(null,"El fihero no existe o no se ha encontrado");
            }else {
                convertirRS();
            }    
        //leerFichero();
    }
    
    public void convertirFCT(){
        this.antcoleccion = leerFicheroFCT();
        int j= 0;
        for (Object filafact : antcoleccion){
            j++;

            String[] arrayfact = filafact.toString().split("#");
            
            int ID = Integer.parseInt(arrayfact[0]);
            
            String[] afecha = arrayfact[1].split("/");
            Fecha fecha = new Fecha(Integer.parseInt(afecha[0]),Integer.parseInt(afecha[1]),Integer.parseInt(afecha[2]));
            
            String numerofactura = arrayfact[2];
            
            String razon = arrayfact[3];
            
            boolean isCIF = false;
            String snif = arrayfact[4].replace(" ","").replace("/", "").replace("-","");
            char[] anif = snif.toCharArray();
            String numeros = "";
            char letra = 'A';
            if (anif[0]<'0' || anif[0]>'9')
                isCIF = true;
            else isCIF = false;
            for (int i = 0; i<9; i++){
                if (anif[i]>='0' && anif[i]<='9')
                    numeros += anif[i]+"";
                else
                    letra = anif[i];
            }
            NIF nif = new NIF(Integer.parseInt(numeros),letra+"",isCIF);
            
            double base = Double.parseDouble(arrayfact[5]);
            
            int tipoIVA = Integer.parseInt(arrayfact[6].replace("%", ""));
            boolean variosivas = false;
            if (tipoIVA == 0)
                variosivas = true;
            
            double iva = Double.parseDouble(arrayfact[7]);
            
            double total = Double.parseDouble(arrayfact[8]);
            
            Extracto ex = new Extracto(base,tipoIVA,iva,total);
            
            ArrayList<Extracto> extracto = new ArrayList<Extracto>();
            extracto.add(ex);
            
            String categoria = arrayfact[9];
            
            RazonSocial dist = new RazonSocial(j,nif,razon, new Nota());
            dist.setNombre(razon);

          Factura f = new Factura(ID,numerofactura,fecha,dist,new TipoGasto(categoria,""),false, extracto ,new Totales(base,variosivas,tipoIVA,iva,base+iva,0.0,0,0.0,total),new Nota(1,""));  
          
          nuevacoleccion.add(f);
        }
        
        Fichero<Factura> actFCT = new Fichero<Factura> (Config.getConfig().getRutaFCT());
        ArrayList<Factura> actlistafct = actFCT.leer();
        nuevacoleccion.addAll(actlistafct);
        
        Fichero<Factura> nuevoFCT = new Fichero<Factura>(rutanuevofichero);
        if (nuevoFCT.escribir(nuevacoleccion))
            JOptionPane.showMessageDialog(null,"El archivo se ha importado con éxito!");
        else
            JOptionPane.showMessageDialog(null,"Falló la importación!");
    }

    public void convertirRS(){
        this.antdistribuidores = leerFicheroRS();
        int j= 0;
        for (Object filars : antdistribuidores){
            j++;

            String[] arrayrs = filars.toString().split("#");
            
            int ID = Integer.parseInt(arrayrs[0]);
            
            boolean isCIF = false;
            String snif = arrayrs[1].replace(" ","").replace("/", "").replace("-","");
            char[] anif = snif.toCharArray();
            String numeros = "";
            char letra = 'A';
            if (anif[0]<'0' || anif[0]>'9')
                isCIF = true;
            else isCIF = false;
            for (int i = 0; i<9; i++){
                if (anif[i]>='0' && anif[i]<='9')
                    numeros += anif[i]+"";
                else
                    letra = anif[i];
            }
            NIF nif = new NIF(Integer.parseInt(numeros),letra+"",isCIF);

            
            String razon = arrayrs[2];

          RazonSocial rs = new RazonSocial(ID,nif,razon,new Nota());  
          rs.setNombre(razon);
          
          nuevosdistribuidores.add(rs);
        }
        
        Fichero<RazonSocial> actualRS = new Fichero<RazonSocial>(Config.getConfig().getRutaRS());
        ArrayList<RazonSocial> listadistactual = actualRS.leer();
        nuevosdistribuidores.addAll(listadistactual);
        
        Fichero<RazonSocial> nuevoRS = new Fichero<RazonSocial>(rutanuevoficherors);
        if (nuevoRS.escribir(nuevosdistribuidores))
            JOptionPane.showMessageDialog(null,"El archivo se ha importado con éxito!");
        else
            JOptionPane.showMessageDialog(null,"Falló la importación!");
    }
        
    public ArrayList leerFicheroFCT() {
        File archivo = null;
        FileReader fr = null;
        BufferedReader br = null;
        
        try {
            // Apertura del fichero y creacion de BufferedReader para poder 
            // hacer una lectura comoda (disponer del metodo readLine()).
            archivo = new File(this.rutaficherov1);
            fr = new FileReader(archivo);
            br = new BufferedReader(fr);
            // Lectura del fichero
            String linea;
            int i=0;
            while ((linea = br.readLine()) != null) {
                this.coleccion.add(linea);
                i++;
            };
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // En el finally cerramos el fichero, para asegurarnos
            // que se cierra tanto si todo va bien como si salta
            // una excepcion.
            try {
                if (null != fr) {
                    fr.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        
        return coleccion;
    }
    public ArrayList leerFicheroRS() {
        File archivo = null;
        FileReader fr = null;
        BufferedReader br = null;
        
        try {
            // Apertura del fichero y creacion de BufferedReader para poder 
            // hacer una lectura comoda (disponer del metodo readLine()).
            archivo = new File(this.rutaficherorsv1);
            fr = new FileReader(archivo);
            br = new BufferedReader(fr);
            // Lectura del fichero
            String linea;
            int i=0;
            while ((linea = br.readLine()) != null) {
                this.distribuidores.add(linea);
                i++;
            };
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // En el finally cerramos el fichero, para asegurarnos
            // que se cierra tanto si todo va bien como si salta
            // una excepcion.
            try {
                if (null != fr) {
                    fr.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        
        return distribuidores;
    }
    
    //
    public void EscribirColaFichero(String entrada) {

        FileWriter fichero = null;
        PrintWriter pw = null;
        try {
            fichero = new FileWriter(this.rutaficherov1,true);
            pw = new PrintWriter(fichero);
            //for (int i = 0; i < 10; i++) {
                pw.println(entrada);
            //}
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // Nuevamente aprovechamos el finally para
                // asegurarnos que se cierra el fichero.
                if (null != fichero) {
                    fichero.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    public void EscribirFichero(String[] lineas) {

        FileWriter fichero = null;
        PrintWriter pw = null;
        try {
            fichero = new FileWriter(this.rutaficherov1);
            pw = new PrintWriter(fichero);
            for (int i = 0; i < lineas.length; i++) {
                pw.println(lineas[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // Nuevamente aprovechamos el finally para
                // asegurarnos que se cierra el fichero.
                if (null != fichero) {
                    fichero.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    public void EscribirFichero(ArrayList lineas) {

        FileWriter fichero = null;
        PrintWriter pw = null;
        try {
            fichero = new FileWriter(this.rutaficherov1);
            pw = new PrintWriter(fichero);
            for (int i = 0; i < lineas.size(); i++) {
                pw.println(lineas.get(i).toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // Nuevamente aprovechamos el finally para
                // asegurarnos que se cierra el fichero.
                if (null != fichero) {
                    fichero.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }
    
    public static void main(String[] args){
        
        Ficherosv1 importar = new Ficherosv1();
    }
*/
}
