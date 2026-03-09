package modelo.records;

import java.util.Vector;
import java.util.HashMap;
import java.util.ArrayList;

public class MisDatos implements Comparable<RazonSocial> {
    private String user;
    private NIF nif;
    private String nombreEmpresa;
    private String nombre;
    private String apellidos;
    private String razon;
    private String direccion;
    private String CP;
    private String poblacion;
    private String telefono;
    // private HashMap<String , String> otrosDatos;
    // private Totales totales;
    // private Nota nota;
    // private String categoria;
    // private ArrayList<Integer> tiposIVA;

    public MisDatos(String user, NIF nif,String razon) {
        this.user = user;
        this.nif = nif;
        this.nombreEmpresa = "Nombre de la Empresa";
        this.nombre = "nombre";
        this.apellidos = "apellidos";
        this.razon = razon.toUpperCase();
        this.direccion = "direccion del distribuidor";
        this.CP = "99999";
        this.poblacion = "Población del distribuidor";
        this.telefono = "900-000000";

        // this.otrosDatos = new HashMap<String, String>();
        // this.totales = new Totales();
        // this.nota = nota;
        // this.categoria = "COMPRAS";
        // this.tiposIVA = new ArrayList<Integer>();
    }
    // Getters
    public String getUser() {
        return user;
    }

    public NIF getNif() {
        return nif;
    }

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getRazon() {
        return razon;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getCP() {
        return CP;
    }

    public String getPoblacion() {
        return poblacion;
    }

    public String getTelefono() {
        return telefono;
    }

    // public HashMap<String , String> getOtrosDatos() {
    //     return otrosDatos;
    // }

    // public Totales getTotales() {
    //     return totales;
    // }

    // public Nota getNota() {
    //     return nota;
    // }

    // public String getCategoria() {
    //     return categoria;
    // }

    // public ArrayList<Integer> getTiposIVA() {
    //     return tiposIVA;
    // }

    // Setters
    public void setUser(String user) {
        this.user = user;
    }

    public void setNif(NIF nif) {
        this.nif = nif;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public void setRazon(String razon) {
        this.razon = razon;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public void setCP(String CP) {
        this.CP = CP;
    }

    public void setPoblacion(String poblacion) {
        this.poblacion = poblacion;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    // public void setOtrosDatos(HashMap<String , String> otrosDatos) {
    //     this.otrosDatos = otrosDatos;
    // }

    // public void setTotales(Totales totales) {
    //     this.totales = totales;
    // }

    // public void setNota(Nota nota) {
    //     this.nota = nota;
    // }

    // public void setCategoria(String categoria) {
    //     this.categoria = categoria;
    // }

    // public void setTiposIVA(ArrayList<Integer> tiposIVA) {
    //     this.tiposIVA = tiposIVA;
    // }

    public Vector<Object> toVector(){
        Vector<Object> vector = new Vector<Object>();

        // vector.add(this.ID());
        vector.add(this.nif);
        vector.add(this.nombre);
        vector.add(this.razon);
        vector.add(this.direccion);
        vector.add(this.CP);
        vector.add(this.poblacion);
        vector.add(this.telefono);
        // vector.add(this.totales.total());
        // vector.add(this.categoria);

        return vector;
    }

    public String toJSON() {
// REVIEW - 24-05-07 : Revisar esta salida comparándola con '/config/ADMIN/misdatos.json', y pensar si el archivo puede ser un JsonArray de objetos MisDatos...
		String cadenaResp = "{\n\t\"user\": \"" + this.user + "\",\n\t\"nif\": { \"numero\": " + this.nif.getNumero() +", \"letra\": \"" + this.nif.getLetra() + "\", \"isCIF\": " + this.nif.isCIF() + "},\n\t\"nombreEmpresa\": \"" + this.nombreEmpresa + "\",\n\t\"nombre\": \"" + this.nombre + "\",\n\t\"apellidos\": \"" + this.apellidos + "\",\n\t\"razon\": \"" + this.razon + "\",\n\t\"direccion\": \"" + this.direccion + "\",\n\t\"CP\": \"" + this.CP + "\",\n\t\"poblacion\": \"" + this.poblacion + "\",\n\t\"telefono\": \"" + this.telefono + "\""+/*,\n\t\"otrosDatos\": " + this.otrosDatos +*/"\n}";
        return cadenaResp;
    }

    @Override
    public int compareTo(RazonSocial b){
// Revisar el modo de comparación de MisDatos... ¡¡¿¿se comparan por el NIF de la Razón Social??!!
        if (this.equals(b)){
            return 0;
        }
        else return (this.getNif().compareTo(((RazonSocial)b).getNif()));
     
  }   
}


    
