package com.sil.facturas.domain.pojos;

public class Extracto implements Comparable<Extracto> {
    public String concepto;
    public double base;
    public int tipoiva;
    public double iva;
    public double subtotal;
    public int cantidad;
    private int numExtracto;
    public double precioUnitario;

    public Extracto( double precioUnitario, int cantidad, int tipoiva, double iva, double subtotal, String concepto) {
        this.precioUnitario = precioUnitario;
        this.cantidad = cantidad;
        this.base = precioUnitario * cantidad;
        this.tipoiva = tipoiva;
        this.iva = iva;
        this.subtotal = subtotal;
        this.concepto = concepto;
    }

    public Extracto() {
        //TODO Auto-generated constructor stub
    }

    public int getTipoiva() {
        return tipoiva;
    }

    public void setTipoiva(int tipoiva) {
        this.tipoiva = tipoiva;
    }

    public double getIva() {
        return iva;
    }

    public void setIva(double iva) {
        this.iva = iva;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getNumExtracto() {
        return numExtracto;
    }

    public void setNumExtracto(int numExtracto) {
        this.numExtracto = numExtracto;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public double getBase() {
        return base;
    }

    public void setBase(double base) {
        this.base = base;
    }

    public int getTipoIVA() {
        return tipoiva;
    }

    public void setTipoIVA(int tipoiva) {
        this.tipoiva = tipoiva;
    }

    public double getIVA() {
        return iva;
    }

    public void setIVA(double iva) {
        this.iva = iva;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public String getConcepto() {
        return this.concepto;
    }

    public void setConcepto(String c){
        this.concepto = c;
    }

    public void calcularSubtotales() {
        this.subtotal = (this.base + (this.base * this.tipoiva)/100 );
    }


// Revisar la forma de comparar extractos
    @Override
	public int compareTo(Extracto b){

		if (b == null)
			return 1;
		else if (this.cantidad < b.getCantidad())
			return -1;
		else if (this.cantidad == b.getCantidad())
			return 0;
		else return 1;

	}

}


