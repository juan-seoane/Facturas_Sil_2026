package com.sil.facturas.presentationgui.viewmodels;

import com.sil.facturas.domain.records.Extracto;
import javafx.beans.property.*;

public class ExtractoFX {

  // === PROPIEDADES FX ===
  private final DoubleProperty base = new SimpleDoubleProperty();
  private final DoubleProperty tipoIVA = new SimpleDoubleProperty();
  private final DoubleProperty iva = new SimpleDoubleProperty();
  private final DoubleProperty subtotal = new SimpleDoubleProperty();
  private final StringProperty concepto = new SimpleStringProperty();
  private final IntegerProperty cantidad = new SimpleIntegerProperty(1);
  private final BooleanProperty devolucion = new SimpleBooleanProperty();

  // === CONSTRUCTOR VACÍO ===
  public ExtractoFX() {}

  // === CONSTRUCTOR ===
  public ExtractoFX(double b, int ti, double i, double st, String conc) {
      ExtractoFX fx = new ExtractoFX();

      fx.base.set(b);
      fx.tipoIVA.set(ti);
      fx.iva.set(i);
      fx.subtotal.set(st);
      fx.concepto.set(conc);
    }

    // === CONSTRUCTOR DESDE DOMINIO ===
    public static ExtractoFX fromDomain(Extracto e) {
        ExtractoFX fx = new ExtractoFX();

        fx.base.set(e.getBase());
        fx.tipoIVA.set(e.getTipoIVA());
        fx.iva.set(e.getIVA());
        fx.subtotal.set(e.getSubtotal());
        fx.concepto.set(e.getConcepto());
        // System.out.println("[ExtractoFX.fromDomain] base= " + e.getBase() +" a FX: " + fx.baseProperty());

        return fx;
    }

  public void normalizarSignos() {
    if (devolucion.get()) {
      base.set(-Math.abs(base.get()));
      iva.set(-Math.abs(iva.get()));
      subtotal.set(-Math.abs(subtotal.get()));
    } else {
      base.set(Math.abs(base.get()));
      iva.set(Math.abs(iva.get()));
      subtotal.set(Math.abs(subtotal.get()));
    }
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

public IntegerProperty cantidadProperty() {
    return cantidad;
}

// === GETTERS SIMPLES (opcional, pero útil) ===


public int getCantidad() {
    return cantidad.get();
}

public void setCantidad(int v) {
    cantidad.set(v);
}


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

  public BooleanProperty devolucionProperty() {
    return devolucion;
  }

  public void setDevolucion(boolean b) {
    devolucion.set(b);
  }
}
