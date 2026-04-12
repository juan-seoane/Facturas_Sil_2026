package presentation.viewmodels;

import domain.records.Extracto;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ExtractoFX {

  // === PROPIEDADES FX ===
  private final DoubleProperty base = new SimpleDoubleProperty();
  private final DoubleProperty tipoIVA = new SimpleDoubleProperty();
  private final DoubleProperty iva = new SimpleDoubleProperty();
  private final DoubleProperty subtotal = new SimpleDoubleProperty();
  private final StringProperty concepto = new SimpleStringProperty();

  // === CONSTRUCTOR VACÍO ===
  public ExtractoFX() {}

  // === CONSTRUCTOR DESDE DOMINIO ===
  public static ExtractoFX fromDomain(Extracto e) {
    ExtractoFX fx = new ExtractoFX();

    fx.base.set(e.getBase());
    fx.tipoIVA.set(e.getTipoIVA());
    fx.iva.set(e.getIVA());
    fx.subtotal.set(e.getSubtotal());
    fx.concepto.set(e.getConcepto());
    System.out.println("[ExtractoFX.fromDomain] base= " + e.getBase() +" a FX: " + fx.baseProperty());

    return fx;
  }

  // === PROPERTIES PARA EL TREE TABLE VIEW ===

  public DoubleProperty baseProperty() {
    return base;
  }

  public DoubleProperty tipoIVAProperty() {
    return tipoIVA;
  }

  public DoubleProperty ivaProperty() {
    return iva;
  }

  public DoubleProperty subtotalProperty() {
    return subtotal;
  }

  public StringProperty conceptoProperty() {
    return concepto;
  }

  // === GETTERS SIMPLES (opcional, pero útil) ===

  public double getBase() {
    return base.get();
  }

  public double getTipoIVA() {
    return tipoIVA.get();
  }

  public double getIVA() {
    return iva.get();
  }

  public double getSubtotal() {
    return subtotal.get();
  }

  public String getConcepto() {
    return concepto.get();
  }
}
