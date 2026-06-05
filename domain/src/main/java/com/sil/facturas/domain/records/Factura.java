package com.sil.facturas.domain.records;

// TODO: 26-03-14 : Hay que separar la lógica de dominio de la lógica de negocio en los records

//import modeloNegocio.base.ModeloFacturas;

import java.util.ArrayList;
//import java.util.Vector;

public class Factura {

    //#region CAMPOSF
    public Integer ID;
    public String numeroFactura;
    public Fecha fecha;
    public RazonSocial RS;
    public TipoGasto concepto;
    public boolean esDevolucion;
    public ArrayList<Extracto> extractos;
    public Totales totales;
    private boolean notaExiste;
    public Nota nota;
    //#endregion

    //#region CONSTR
    public Factura(Integer ID, String numeroFactura, Fecha fecha, RazonSocial RS, TipoGasto concepto,
            boolean esDevolucion, ArrayList<Extracto> extractos, Totales totales, Nota nota) {
        this.ID = ID;
        this.numeroFactura = numeroFactura;
        this.fecha = fecha;
        this.RS = RS;
        this.concepto = concepto;
        this.esDevolucion = esDevolucion;
        this.extractos = extractos;
        this.totales = totales;
        this.nota = (nota == null ? new Nota("") : nota);
        this.notaExiste = (this.nota.getTexto().equals("") ? false : true);
        this.normalizarSignos();
    }

    public Factura() {
    this(
        0,
        "000000-OOO",
        new Fecha(17, 03, 24),
        new RazonSocial(),
        new TipoGasto("tipoGasto_generico", "descripción"),
        false,
        new ArrayList<>(),
        new Totales(),
        null);
    }
    //#endregion

    public Integer getID() {
        return ID;
    }

    public void setID(Integer iD) {
        ID = iD;
    }

    public String getNumeroFactura() {
        return numeroFactura;
    }

    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    public Fecha getFecha() {
        return fecha;
    }

    public void setFecha(Fecha fecha) {
        this.fecha = fecha;
    }

    public RazonSocial getRS() {
        return RS;
    }

    public void setRS(RazonSocial rS) {
        RS = rS;
    }

    public TipoGasto getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = new TipoGasto(concepto, concepto);
    }

    public boolean isEsDevolucion() {
        return esDevolucion;
    }

    public void setEsDevolucion(boolean esDevolucion) {
        this.esDevolucion = esDevolucion;
    }

    public ArrayList<Extracto> getExtractos() {
        return extractos;
    }

    public void setExtractos(ArrayList<Extracto> extractos) {
        this.extractos = extractos;
    }

    public Totales getTotales() {
        return totales;
    }

    public void setTotales(Totales totales) {
        this.totales = totales;
    }

    public Nota getNota() {
        return nota;
    }

    public void setNota(Nota nota) {
        this.nota = nota;
    }

    public boolean notaExiste() {
        return notaExiste;
    }

    public void normalizarSignos() {

        if (this.esDevolucion) {
            this.totales.setBase(-Math.abs(this.totales.getBase()));
            this.totales.setIVA(-Math.abs(this.totales.getIVA()));
            this.totales.setBaseNI(-Math.abs(this.totales.getBaseNI()));
            this.totales.setRet(-Math.abs(this.totales.getRet()));
            this.totales.setRetenciones(-Math.abs(this.totales.getRetenciones()));
        } else {
            this.totales.setBase(Math.abs(this.totales.getBase()));
            this.totales.setIVA(Math.abs(this.totales.getIVA()));
            this.totales.setBaseNI(Math.abs(this.totales.getBaseNI()));
            this.totales.setRet(Math.abs(this.totales.getRet()));
            this.totales.setRetenciones(Math.abs(this.totales.getRetenciones()));
        }

        // ✔ Recalcular subtotal SIEMPRE
        this.totales.setSubtotal(this.totales.getBase() + this.totales.getIVA());

        // ✔ Recalcular total SIEMPRE
        this.totales.setTotal(
                this.totales.getSubtotal()
                + this.totales.getBaseNI()
                + this.totales.getRetenciones());
    }

    @Override
    public String toString() {
    String cadenaResp =
        this.ID
            + ","
            + this.numeroFactura
            + ","
            + this.fecha.toString()
            + ","
            + this.RS.getID()
            + ","
            + this.RS.getNif()
            + ","
            + this.RS.getNombre()
            + ","
            + this.concepto.getTipo()
            + ","
            + (this.esDevolucion ? "S" : "N")
            + ","
            + this.extractos.size()
            + ", TOTS-> "
            + this.totales.getBase()
            + ","
            + ((this.totales.isVariosIVAs()) ? "S" : "N," + this.totales.getTipoIVA())
            + ","
            + this.totales.getIVA()
            + ","
            + this.totales.getRet()
            + ","
            + this.totales.getRetenciones()
            + ","
            + this.totales.getTotal()
            + ","
            + ((this.nota != null) ? this.nota.getTexto() : "sinNOTA");
        // ojo que sólo puede haber una Nota...
        if (this.extractos.size() > 1) {
            for (int i = 0; i < this.extractos.size(); i++) {
        cadenaResp +=
            "\nEXTR->"
                + this.extractos.get(i).getBase()
                + ","
                + this.extractos.get(i).getTipoIVA()
                + ","
                + this.extractos.get(i).getIVA()
                + ","
                + this.extractos.get(i).getSubtotal()
                + "#";
            }
        }

        return cadenaResp;
    }


}

