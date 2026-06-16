
package com.sil.facturas.infrastructure.servicios.ocr.aux_ocr;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class CampoOCR {

    private final String id;        // "factura.Fecha", "extracto.Base", etc.
    private final String nombre;    // "Fecha", "Base", "IVA"
    private final String dominio;   // "factura.fecha", "extracto.base", etc.

    private final DoubleProperty x = new SimpleDoubleProperty();
    private final DoubleProperty y = new SimpleDoubleProperty();
    private final DoubleProperty width = new SimpleDoubleProperty();
    private final DoubleProperty height = new SimpleDoubleProperty();

    public CampoOCR(String id, String nombre, String dominio) {
        this.id = id;
        this.nombre = nombre;
        this.dominio = dominio;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDominio() { return dominio; }

    public double getX() { return x.get(); }
    public void setX(double v) { x.set(v); }
    public DoubleProperty xProperty() { return x; }

    public double getY() { return y.get(); }
    public void setY(double v) { y.set(v); }
    public DoubleProperty yProperty() { return y; }

    public double getWidth() { return width.get(); }
    public void setWidth(double v) { width.set(v); }
    public DoubleProperty widthProperty() { return width; }

    public double getHeight() { return height.get(); }
    public void setHeight(double v) { height.set(v); }
    public DoubleProperty heightProperty() { return height; }
}
