package modelo.records;

// REVIEW - 2024-05-07 : La clase Totales...¿heredaba de la clase Extracto?

public class Totales extends Extracto{
  private boolean variosIVAs;
  private double baseNI;
  private int ret;
  private double retenciones;
  private double total;

  public Totales(){
    this(0.0, false,0, 0.0, 0.0, 0.0, 0, 0.0, 0.0, "");
  }

  public Totales(double base, boolean variosIVAs, int tipoIVA, double iva, double subtotal, double baseNI, int ret, double retenciones, double total, String categoria) {
    super(base, tipoIVA, iva, subtotal, categoria); 
    this.variosIVAs = variosIVAs;
    this.baseNI = baseNI;
    this.ret = ret;
    this.retenciones = retenciones;
    this.total = total;
  }


  public boolean isVariosIVAs() {
      return variosIVAs;
  }

  public void setVariosIVAs(boolean variosIVAs) {
      this.variosIVAs = variosIVAs;
  }

  public double getBaseNI() {
      return baseNI;
  }

  public void setBaseNI(double baseNI) {
      this.baseNI = baseNI;
  }

  public int getRet() {
      return ret;
  }

  public void setRet(int ret) {
      this.ret = ret;
  }

  public double getRetenciones() {
      return retenciones;
  }

  public void setRetenciones(double retenciones) {
      this.retenciones = retenciones;
  }

  public double getTotal() {
      return total;
  }

  public void setTotal(double total) {
      this.total = total;
  }

  public void calcularTotal(){
    this.total = (this.subtotal + this.baseNI)*((this.ret+100)/100);
  }

  // REVIEW - 24-05-07 : Puede que necesite calcular los totales pasando otros parámetros...
  public static boolean comprobarTotales(Totales tot) {
	  return ((tot.getBase() + tot.getIVA() - tot.getRetenciones() == tot.getTotal()) && (tot.getBase() * tot.getTipoIVA()/100 == tot.getIVA()) && (tot.getBase()*tot.getRet()/100 == tot.getRetenciones()));
  }
}
