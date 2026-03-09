package modelo.records;

import java.util.*;
import modelo.records.*;

public class RazonSocial implements Comparable<RazonSocial> {
    private int ID;
    private NIF nif;
    private String nombre;
    private String razon;
    private String direccion;
    private String CP;
    private String poblacion;
    private String telefono;
    private HashMap<String, String> otrosDatos;
    private Totales totales;
    private Nota nota;
    private String categoria;
    private int[] tiposIVA;

    public RazonSocial() {
        this(0, new NIF(0000, "A", false), "nombre de la empresa", "nombre Razon Social", "900-000000", "direccion del distribuidor", "99999", "Ourense", null, new Totales(), new Nota(""), "COMPRAS", null);
    }

    public RazonSocial(Integer ID, NIF nif,String nombre) {
        this(ID, nif, nombre, nombre.toUpperCase(), "900-000000", "direccion del distribuidor", "99999", "Ourense", null, new Totales(), null, "-", null);
    } 

    public RazonSocial(int ID, NIF nif, String nombre, String razon, String direccion, String CP, String poblacion, String telefono, HashMap<String, String> otrosDatos, Totales totales, Nota nota, String categoria, int[] tiposIVA) {
        this.ID = ID;
        this.nif = nif;
        this.nombre = nombre;
        this.razon = razon;
        this.direccion = direccion;
        this.CP = CP;
        this.poblacion = poblacion;
        this.telefono = telefono;
        this.otrosDatos = otrosDatos;
        this.totales = totales;
        this.nota = nota;
        this.categoria = categoria;
        this.tiposIVA = tiposIVA;
    }
    
    // Getters
    public int getID() {
        return ID;
    }

    public NIF getNif() {
        return nif;
    }

    public String getNombre() {
        return nombre;
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

    public HashMap<String, String> getOtrosDatos() {
        return otrosDatos;
    }

    public Totales getTotales() {
        return totales;
    }

    public Nota getNota() {
        return nota;
    }

    public String getCategoria() {
        return categoria;
    }

    public int[] getTiposIVA() {
        return tiposIVA;
    }

    // Setters
    public void setID(int ID) {
        this.ID = ID;
    }

    public void setNif(NIF nif) {
        this.nif = nif;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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

    public void setOtrosDatos(HashMap<String, String> otrosDatos) {
        this.otrosDatos = otrosDatos;
    }

    public void setTotales(Totales totales) {
        this.totales = totales;
    }

    public void setNota(Nota nota) {
        this.nota = nota;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public void setTiposIVA(int[] tiposIVA) {
        this.tiposIVA = tiposIVA;
    }
    
    public Vector<Object> toVector(){
  
        Vector<Object> vector = new Vector<Object>();

        vector.add(this.getID());
        vector.add(this.getNif());
        vector.add(this.getNombre());
        vector.add(this.getRazon());
        vector.add(this.getDireccion());
        vector.add(this.getCP());
        vector.add(this.getPoblacion());
        vector.add(this.getTelefono());
        vector.add(this.getTotales().getTotal());
        vector.add(this.getCategoria());

        ////System.out.println("transformando a Vector: "+vector.toString());
        return vector;

    }
    
// Revisar la forma de comparar RS
    @Override
    public int compareTo(RazonSocial b){
        if (this.equals(b)){
            return 0;
        }
        else return (this.getNif().compareTo(((RazonSocial)b).getNif()));
     
  } 
}

