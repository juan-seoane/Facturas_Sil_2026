package modelo.base;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import static java.nio.file.StandardCopyOption.*;

/*
 * @author Juan Seoane
 */
// REVIEW - 09/04/24 : Guardar en formato json
public class Fichero<T> {

    public String rutaArchivo;
    
    File fichero;
    File directorio;
    File dir;
    FileInputStream fis;
    FileOutputStream fos;
    ObjectInputStream ois;
    ObjectOutputStream oos;
    ObjectOutputStream aos;

    public Fichero(String rutaArchivo) {
        ////System.out.println("[Fichero] Creando nuevo fichero".toUpperCase());
        this.rutaArchivo = rutaArchivo;
        
        this.directorio = new File(rutaArchivo.substring(0,rutaArchivo.lastIndexOf("/")));
        if (!this.directorio.exists()){
            try{
                this.directorio.mkdir();
            }catch(Exception e){
                this.dir = new File(rutaArchivo.substring(0,rutaArchivo.lastIndexOf("/")));
            }
        }
        this.fichero = new File(rutaArchivo);
        if (!this.fichero.exists()) {
            try {
                // A partir del objeto File creamos el fichero físicamente
                if (this.fichero.createNewFile()) {
                    ////System.out.println("[Fichero>constructor] El fichero " + rutaArchivo + " se ha creado correctamente");
                } else {
                    //System.out.println("[Fichero>constructor] No ha podido ser creado el fichero " + rutaArchivo);
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        } else {
            ////System.out.println("[Fichero>constructor] El fichero " + rutaArchivo + " ya estaba creado");
        }
    }
//#region LEER
    @SuppressWarnings("unchecked")
    public ArrayList<T> leer() {
        ////System.out.println("[Fichero] LEYENDO FICHERO "+this.rutaArchivo);

        ArrayList<T> entradas = new ArrayList<T>();
        T entrada = null;
        if (fichero.length()!=0){
        abrirInputStream();
            try {
                while ((entrada = (T) ois.readObject()) != null) {
                    ////System.out.println("objeto leido: " + entrada.toString());
                    entradas.add(entrada);
                }
                ////System.out.println("[Fichero>leer] tamaño del vector de lectura: " + entradas.size());
//                ////System.out.println("tipo de archivo : " + entradas.getClass().getName());
            } catch (NullPointerException | IOException | ClassNotFoundException e) {
                ////System.out.println("[Fichero.java>leer()] Error en la lectura del tipo " + e + " al leer el fichero "+ this.rutaArchivo);
            }
        } else { 
            ////System.out.println("[Fichero.java>leer()] El fichero "+ this.rutaArchivo + " está vacío...");
        }
        cerrarInputStream();
        return entradas;
    }
//#endregion
//#region ESCRIBIR
    public synchronized boolean escribir(ArrayList<T> entradas) {
        ////System.out.println("Escribiendo fichero".toUpperCase()+" "+ this.rutaArchivo);
        abrirOutputStream();
        try {
            ////System.out.println("tamaño del vector de escritura: " + entradas.size());
            for (int i = 0; i < entradas.size(); i++) {
                oos.writeObject((T)(entradas.get(i)));
                ////System.out.println("escribiendo objeto numero " + i + " del tipo " + entradas.get(i).getClass().getName());
                oos.flush();
            }
            //JOptionPane.showMessageDialog(null,"Escribiendo en el fichero "+this.rutaArchivo+" !");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        cerrarOutputStream();
        return false;

    }
//#endregion
//#region ANEXAR
    public boolean anexar(T entrada) {
        ////System.out.println(">>>>>>Añadiendo a fichero".toUpperCase());
        ArrayList<T> entradas = this.leer();
        
        entradas.add(entrada);
        
        this.escribir(entradas);
        ////System.out.println("Entrada guardada!");
        return true;
        
    }
//#endregion  
//#region EDITAR 
    public boolean editar(T entrada, int index){
	////System.out.println(">>>>>>Editando entrada en fichero con indice "+index);
	ArrayList<T> entradas = this.leer();
	   if((entradas.set(index,entrada))!=null){
		this.escribir(entradas);
		////System.out.println("Entrada editada!");
		return true;
	}
	return false;
    }
//#endregion
//#region BORRAR  
    public boolean borrar(T entrada, int index){
	////System.out.println(">>>>>>Borrando entrada en fichero con indice "+index);

	ArrayList<T> entradas = this.leer();
	   if((entradas.remove(entrada))){
		this.escribir(entradas);
		////System.out.println("Entrada borrada!");
		return true;
	}
	return false;
    }
    //#endregion
    //#region OIS
    public boolean abrirInputStream() {
        try {
            fis = new FileInputStream(this.fichero);
        } catch (FileNotFoundException ex) {
            //System.out.println("Error de tipo FNFEx al leer el fichero");
            return false;
        }
        try {
            ois = new ObjectInputStream(fis);
        } catch (IOException ex) {
            //System.out.println("Error de tipo IOEx " + ex + " al leer el fichero "+ this.rutaArchivo);
            return false;
        }
        ////System.out.println("Flujos de entrada cargados al leer el fichero "+ this.rutaArchivo);
        return true;
    }
//#endregion
//#region OOS
    public boolean abrirOutputStream() {
        try {
            fos = new FileOutputStream(this.fichero, false);
            ////System.out.println("Flujo de salida abierto!");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Fichero.class.getName()).log(Level.SEVERE, null, ex);
            
            return false;
        }
        try {
            oos = new ObjectOutputStream(fos);
            ////System.out.println("Flujo de objetos abierto!");
        } catch (IOException ex) {
            Logger.getLogger(Fichero.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        return true;
    }
//#endregion
//#region CIS
    public boolean cerrarInputStream() {
        try {
            if (fichero != null) {
                ois.close();
                fis.close();
                ////System.out.println("Flujo de entrada cerrado!");
                return true;
            }
        } catch (Exception e2) {
            //System.out.println("Error al cerrar la lectura del tipo " + e2 + " al leer el fichero "+ this.rutaArchivo);
        }
        return false;
    }
//#endregion
//#region COS
    public boolean cerrarOutputStream() {
        try {
            if (fichero != null) {
                aos.close();
                oos.close();
                fos.close();
                ////System.out.println("Flujo de objetos cerrado!");
                return true;
            }
        } catch (Exception e2) {
            //System.out.println("Error al cerrar la escritura del tipo " + e2 + " al leer el fichero");

        }
        return false;
    }
//#endregion
//#region COPIAR
    public static boolean copia(String rutaOrigen, String rutaDestino){
        try {
                Files.copy(new File(rutaOrigen).toPath(), new File(rutaDestino).toPath(), REPLACE_EXISTING);
            } catch (IOException ex) {
                //System.out.println("No se ha podido copiar el archivo "+rutaOrigen);
                return false;
            }
        return true;
    }
//#endregion
    public String toString(){
        
       return ("Fichero ->[ ruta= "+this.rutaArchivo+" ]"); 
        
    }
    
    public boolean estaVacio()
    {
        
        return (this.fichero.length()==0);
    }
//#region FILE_EXISTS
    public static synchronized boolean fileExists(String ruta) throws NullPointerException, IOException{

        File f= new File(ruta);
        if (f.exists()&&f.isFile()){
            return true;
        }
        return false;
    }
//#endregion
//#region DIR_EXISTS
    public static synchronized boolean dirExists(String ruta) throws NullPointerException, IOException{

        File f= new File(ruta);
        if (f.exists()&&f.isDirectory()){
            return true;
        }
        else if (!f.isFile())
            return false;
        else
            return false;
    }
//#endregion
//#region CREAR_CARPETA
    public static synchronized boolean crearCarpeta(String ruta, String nombre) {
        File carpeta = new File(ruta, nombre);
        //La carpeta NO existe -> intenta crearla
        if ( ! carpeta.exists( )) {

            try {
                carpeta.mkdir();
                return true;

            } catch (Exception ex) { 
                //System.out.println("[Fichero.java] Error creando la carpeta." +ruta + nombre);
                System.err.println( ex.toString( ));
                return false;
            }

        //La carpeta YA existe
        } else {
            ////System.out.println("[Fichero.java] La carpeta " + ruta + nombre + " ya existe.");
            return false; 
        }
    }

    public static synchronized boolean crearCarpeta(String rutaYnombre) {
        File carpeta = new File(rutaYnombre);
        //La carpeta NO existe -> intenta crearla
        if ( ! carpeta.exists( )) {

            try {
                carpeta.mkdir();
                return true;

            } catch (Exception e) { 
                //System.out.println("[Fichero.java] Error creando la carpeta " + rutaYnombre);
                System.err.println( e.toString( ));
                return false;
            }

        //La carpeta YA existe
        } else {
            ////System.out.println("[Fichero.java] La carpeta " + rutaYnombre + " ya existe.");
            return false; 
        }
    }
//#endregion
//#region CREAR_FICHERO
    public static synchronized boolean crearFichero(String ruta, String nombre) {
        File archivo = new File(ruta, nombre);
        //El archivo NO existe -> intenta crearlo
        if ( ! archivo.exists( )) {

            try {
                archivo.createNewFile();
                return true;

            } catch( Exception e ) {
                //System.out.println("[Fichero.java] Error creando el archivo " + ruta + nombre);
                System.err.println( e.toString( ));
                return false;
            }

        } else {
            ////System.out.println("[Fichero.java] El archivo " + ruta + nombre + " ya existe.");
            return false;
        }
    }
    public static synchronized boolean crearFichero(String rutaYnombre) {
        File archivo = new File(rutaYnombre);
        //El archivo NO existe -> intenta crearlo
        if ( ! archivo.exists( )) {

            try {
                archivo.createNewFile();
                ////System.out.println("[Fichero.java] Creado el archivo "  +  rutaYnombre);
                return true;

            } catch( Exception e ) {
                //System.out.println("[Fichero.java] Error creando el archivo "  +  rutaYnombre);
                System.err.println( e.toString( ));
                return false;
            }

        } else {
            ////System.out.println("[Fichero] El archivo " + rutaYnombre + " ya existe.");
            return false;
        }
    }
//#endregion
//#region LEER_JSON
public static synchronized String leerJSON(String rutaYnombre){
    
    String fichero = "";
    try {
        FileReader f = new FileReader(rutaYnombre);
        BufferedReader input = new BufferedReader(f);
        String linea;
        while ((linea = input.readLine()) != null) {
            fichero += linea;
        }
        input.close();
        ////System.out.println("[Fichero.java] Leído el fichero JSON "+ rutaYnombre);
    } catch (IOException ex) {
        //System.out.println(ex.getMessage());
        //System.out.println("[Fichero.java] Error leyendo el fichero JSON "+ rutaYnombre);
    }
    return fichero;
}
//#endregion
//#region GUARDAR_JSON
public static synchronized boolean guardarJSON(String datosFormateados, String rutaYnombre){
              
    try{
        FileWriter f = new FileWriter(rutaYnombre);
        BufferedWriter output = new BufferedWriter(f);
        output.write(datosFormateados);
        ////System.out.println("[Fichero.java]Fichero "+ rutaYnombre + " creado");
        output.close();
        ////System.out.println("[Fichero.java] Creado el fichero "+ rutaYnombre);
        return true;
    } catch (IOException ex) {
        //System.out.println("[Fichero.java] Error guardando el fichero " + rutaYnombre);
        ex.printStackTrace();
        return false;
    }        
}
//#endregion
//#region leerCSV
public synchronized ArrayList<String[]> leerCSV(String rutaYnombre){
    this.fichero = new File(rutaYnombre);
    abrirInputStream();
    CSVReader csvReader;
    ArrayList<String[]> resp = new ArrayList<String[]>();
    try {
        csvReader = new CSVReader(new FileReader(rutaYnombre));
 
        String[] fila = null;

        while((fila = csvReader.readNext()) != null) {
            /*
            //System.out.println("\n[Fichero.java>leerCSV] fila CSV: ");
            if (fila[0].contains("#")){
                //System.out.println(" --- comentario ---");
            }else{
                //System.out.println("\r\n");
                for (String campo : fila){
                    //System.out.print(campo.strip() + " - ");
                }
            */
                resp.add(fila);
            //}
        }
        
        csvReader.close();

    } catch (CsvValidationException | IOException e) {
        //System.out.println("[Fichero.java>leerCSV()] Excepcion " + e + " leyendo el archivo " + rutaYnombre);
    }
    cerrarInputStream();
    // REVIEW - 2024-06-14 : (Hay que devolver ya una lista de facturas) En fichero.leerCSV se devuelve un ArrayList<String[]> genérico, luego cada Modelo lo pasa a su ArrayList<T> parametrizado a la clase que convenga, Factura, RS, EntradaCaja...
    return resp;
    }
//#endregion 
//#region guardarCSV
    public synchronized boolean guardarCSV(ArrayList<String[]> datos){

        System.out.println("[Fichero>guardarCSV] Datos a guardar: " + datos);
        String archCSV = this.rutaArchivo;
        System.out.println("[Fichero>guardarCSV] Archivo CSV a guardar en ruta "+ archCSV +" de " + datos.size() + " lineas");
        CSVWriter writer;
        try {
            writer = new CSVWriter(new FileWriter(archCSV));
            for (String[] linea : datos){
            writer.writeNext(linea);
            }
            writer.close();
            return true;
        } catch (IOException e) {
            System.out.println("[Fichero.java>guardarCSV] Excepcion " + e + " guardando el archivo CSV " + archCSV);
            return false;
        }
    }
//#endregion
}