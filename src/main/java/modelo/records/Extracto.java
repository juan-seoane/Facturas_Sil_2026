package modelo.records;

public class Extracto implements Comparable<Extracto> {
    public String concepto;
    public double base;
    public int tipoiva;
    public double iva;
    public double subtotal;

    public Extracto(double base, int tipoiva, double iva, double subtotal, String concepto) {
        this.base = base;
        this.tipoiva = tipoiva;
        this.iva = iva;
        this.subtotal = subtotal;
        this.concepto = concepto;
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

    public void setSubbtotal(double subtotal) {
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
		else if (this.subtotal < b.getSubtotal())
			return -1;
		else if (this.subtotal == b.getSubtotal())
			return 0;
		else return 1;
	
	}

}


