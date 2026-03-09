package modelo.records;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Fecha implements Comparable<Fecha> {
    private int dia;
    private int mes;
    private int año;

    public Fecha(int dia, int mes, int año) {
        this.dia = dia;
        this.mes = mes;
        this.año = año;
    }

	public Fecha getFecha() {
		return this;
	}

    public int getDia() {
        return dia;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getAnho() {
        return año;
    }

    public void setAnho(int año) {
        this.año = año;
    }

	@Override
	public String toString(){
		GregorianCalendar fecha = new GregorianCalendar(this.getAnho(), (this.getMes()-1), this.getDia());
		return (""+fecha.get(Calendar.DAY_OF_MONTH) + "/" + (fecha.get(Calendar.MONTH)+1) +"/"+ fecha.get(Calendar.YEAR));
		}
	

    @Override
	public int compareTo(Fecha b) {
		GregorianCalendar fecha = new GregorianCalendar(this.getAnho(), (this.getMes()-1), this.getDia());
		if (b == null)
			return 1;
		if (fecha.before(b.getFecha()))
			return -1;
		else if (fecha.equals(b.getFecha()))
			return 0;
		else if (fecha.after(b.getFecha()))
			return 1;
		return 0;
			
	  }
	
}





