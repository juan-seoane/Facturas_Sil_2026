/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo.records;

import java.util.Date;
import java.util.GregorianCalendar;

import controladores.Controlador;

/**
 *
 * @author Juan Seoane
 */
import java.util.Date;

public class EntradaCaja implements Comparable<EntradaCaja> {
    private int ID;
    private Fecha fecha;
    private Date date;
    private String origen;
    private double caja;
    private boolean haber;
    private Nota nota;

    public EntradaCaja(int ID, Fecha fecha, Date date, String origen, double caja, boolean haber, Nota nota) {
        this.ID = ID;
        this.fecha = fecha;
        this.date = date;
        this.origen = origen;
        this.caja = caja;
        this.haber = haber;
        this.nota = nota;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public Fecha getFecha() {
        return fecha;
    }

    public void setFecha(Fecha fecha) {
        this.fecha = fecha;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public double getCaja() {
        return caja;
    }

    public void setCaja(double caja) {
        this.caja = caja;
    }

    public boolean isHaber() {
        return haber;
    }

    public void setHaber(boolean haber) {
        this.haber = haber;
    }

    public Nota getNota() {
        return nota;
    }

    public void setNota(Nota nota) {
        this.nota = nota;
    }
// Revisar la forma de comparar entradas de caja
    @Override
    public int compareTo(EntradaCaja b) {
        return (this.getFecha().compareTo(b.getFecha()));
    }
}

