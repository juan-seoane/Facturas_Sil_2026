package com.sil.facturas.presentationgui.services.ocr;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class BloqueOCR {

    private final String id;        // Identificador único: "bloqueTotales", "bloque_1", etc.
    private String nombre;          // Nombre visible: "Totales", "Extractos", "Bloque 1"
    private final String entity;


    // Coordenadas relativas (0..1) respecto a la imagen
    private final DoubleProperty x = new SimpleDoubleProperty();
    private final DoubleProperty y = new SimpleDoubleProperty();
    private final DoubleProperty width = new SimpleDoubleProperty();
    private final DoubleProperty height = new SimpleDoubleProperty();

    // Hijos y campos
    private final List<BloqueOCR> hijos = new ArrayList<>();
    private final List<CampoOCR> campos = new ArrayList<>();

    public BloqueOCR(
        String id, String nombre, String entity) {
        this.id = id;
        this.nombre = nombre;
        this.entity = entity;
    }

    public BloqueOCR(
        String id, String nombre, String entity, double x, double y, double width, double height) {
        this.id = id;
        this.nombre = nombre;
        this.entity = entity;
        this.x.set(x);
        this.y.set(y);
        this.width.set(width);
        this.height.set(height);
    }

    // -------------------------
    // GETTERS / SETTERS
    // -------------------------

    public String getId() { return id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEntity() { return entity; }

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

    public List<BloqueOCR> getHijos() { return hijos; }
    public List<CampoOCR> getCampos() { return campos; }

    // -------------------------
    // MÉTODOS DE GESTIÓN
    // -------------------------

    public void addCampo(CampoOCR campo) {
        campos.add(campo);
    }

    public void addHijo(BloqueOCR bloque) {
        hijos.add(bloque);
    }

    public boolean isRoot() {
        return entity == null || entity.equalsIgnoreCase("generico");
    }

    @Override
    public String toString() {
        return "BloqueOCR{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", entity='" + entity + '\'' +
                ", x=" + getX() +
                ", y=" + getY() +
                ", width=" + getWidth() +
                ", height=" + getHeight() +
                ", campos=" + campos.size() +
                ", hijos=" + hijos.size() +
                '}';
    }

}

