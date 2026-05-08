package domain.records;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Fecha implements Comparable<Fecha> {
    private int dia;
    private int mes;
    private int anho;

    public Fecha(int dia, int mes, int anho) {
        this.dia = dia;
        this.mes = mes;
        this.anho = anho;
    }

	public Date getFecha() {
		return new Date();
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
        return anho;
    }

    public void setAnho(int anho) {
        this.anho = anho;
    }

    public static Fecha fromString(String s) {
        String[] p = s.split("/");
        int anho = Integer.parseInt(p[2]);
        int mes = Integer.parseInt(p[1]);
        int dia = Integer.parseInt(p[0]);
        return new Fecha(dia, mes, anho);
    }

    public String format() {
        return this.toString();
    }

	@Override
	public String toString(){
		GregorianCalendar fecha = new GregorianCalendar(this.getAnho(), (this.getMes()-1), this.getDia());
		return (""+fecha.get(Calendar.DAY_OF_MONTH) + "/" + (fecha.get(Calendar.MONTH)+1) +"/"+ fecha.get(Calendar.YEAR));
		}

    @Override
    public int compareTo(Fecha b) {
        GregorianCalendar fechaGC = new GregorianCalendar(this.getAnho(), (this.getMes() - 1), this.getDia());
        Date fecha = fechaGC.getTime();
        if (b == null)
            throw new NullPointerException("Fecha.compareTo: parámetro null");
        if (fecha.before(b.getFecha()))
            return -1;
        else if (fecha.equals(b.getFecha()))
            return 0;
        else if (fecha.after(b.getFecha()))
            return 1;
        return 0;
    }

    public static Fecha hoy() {
        LocalDate hoy = LocalDate.now();
        return new Fecha(hoy.getDayOfMonth(), hoy.getMonthValue(), hoy.getYear());
    }

}





