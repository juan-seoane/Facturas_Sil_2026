package modelo.base;
/*
 * @author Juan Seoane
 */
import java.io.*;
import java.lang.String;

public class FicheroAutomatico{

    String[] rutasArchivo = new String[3];
    
public FicheroAutomatico(boolean[] ficheros, String Usuario, int AnhoActual, int Trimestre){
    
    int i = 1;
    if (!existeFichero("datos/"+Usuario+"/autosave/"))
        new File("datos/"+Usuario+"/autosave/");
    
    rutasArchivo[0] = "datos/"+Usuario+"/autosave/FCT_"+AnhoActual+"_"+Trimestre+"_"+i+".svs";
    rutasArchivo[1] = "datos/"+Usuario+"/autosave/RS_"+i+".svs";
    rutasArchivo[2] = "datos/"+Usuario+"/autosave/CJA_"+AnhoActual+"_"+Trimestre+"_"+i+".svs";
    if (ficheros[0]){
        while (existeFichero(rutasArchivo[0])){
            System.out.println("comprobando...0");
            i++;
            rutasArchivo[0] = "datos/"+Usuario+"/autosave/FCT_"+AnhoActual+"_"+Trimestre+"_"+i+".svs";
            rutasArchivo[1] = "datos/"+Usuario+"/autosave/RS_"+i+".svs";
            rutasArchivo[2] = "datos/"+Usuario+"/autosave/CJA_"+AnhoActual+"_"+Trimestre+"_"+i+".svs";
        }
    }
    if (ficheros[1]){
        while (existeFichero(rutasArchivo[1])){
            System.out.println("comprobando...1");
            i++;
            rutasArchivo[0] = "datos/"+Usuario+"/autosave/FCT_"+AnhoActual+"_"+Trimestre+"_"+i+".svs";
            rutasArchivo[1] = "datos/"+Usuario+"/autosave/RS_"+i+".svs";
            rutasArchivo[2] = "datos/"+Usuario+"/autosave/CJA_"+AnhoActual+"_"+Trimestre+"_"+i+".svs";
        }
    }
    if (ficheros[2]){
        while (existeFichero(rutasArchivo[2])){
            System.out.println("comprobando...2");
            i++;
            rutasArchivo[0] = "datos/"+Usuario+"/autosave/FCT_"+AnhoActual+"_"+Trimestre+"_"+i+".svs";
            rutasArchivo[1] = "datos/"+Usuario+"/autosave/RS_"+i+".svs";
            rutasArchivo[2] = "datos/"+Usuario+"/autosave/CJA_"+AnhoActual+"_"+Trimestre+"_"+i+".svs";
        }
    }
}

public boolean existeFichero(String ruta){
            File fichero = new File(ruta);
            if (fichero.exists()){
                return true;
            }else {
                return false;
            }
}

public String[] getRutas(){   
    return rutasArchivo;
}
/*
public boolean crearArchivosAutomaticos(int i,String AnhoActual){
    
    String rutaArchivoFCT = "datos/autosave/FACTURAS_"+AnhoActual+"_"+i+".fct";
    String rutaArchivoRS = "datos/autosave/RS_"+AnhoActual+"_"+i+".rs";
    String rutaArchivoTG = "datos/autosave/TG_"+AnhoActual+"_"+i+".tg";
    try{
        Fichero ffacturas =  new Fichero("datos/FACTURAS_"+AnhoActual+".fct");

        Vector modelo = (Vector)ffacturas.leerFichero();

        Fichero autoFCT = new Fichero(rutaArchivoFCT);
        autoFCT.escribirFichero(modelo);

        Fichero frs =  new Fichero("datos/RS.rs");

        Vector rs = frs.leerFichero();

        Fichero autoRS = new Fichero(rutaArchivoRS);
        autoRS.escribirFichero(rs);

        Fichero ftg =  new Fichero("datos/TG.tg");

        Vector tg = ftg.leerFichero();

        Fichero autoTG = new Fichero(rutaArchivoTG);
        autoTG.escribirFichero(tg);
    
    } catch (Exception e){
        JOptionPane.showMessageDialog(null, e+" en el guardado automatico");
        return false;
    }
    return true;
    
}
*/
}
