package presentation.viewmodels;

import app.services.FacturasService;
import domain.records.*;
import java.util.ArrayList;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class FacturaFX {

    // -------------------------
    // CAMPOS PRINCIPALES
    // -------------------------

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty numero = new SimpleStringProperty();
    private final ObjectProperty<Fecha> fecha = new SimpleObjectProperty<>();
    private final StringProperty razonSocial = new SimpleStringProperty();
    private final StringProperty concepto = new SimpleStringProperty();
    private final BooleanProperty devolucion = new SimpleBooleanProperty();
    private final IntegerProperty numExtractos = new SimpleIntegerProperty();
    private final StringProperty nota = new SimpleStringProperty();

    // -------------------------
    // CAMPOS DE TOTALES
    // -------------------------

    private final DoubleProperty base = new SimpleDoubleProperty();
    private final IntegerProperty tipoIVA = new SimpleIntegerProperty();
    private final DoubleProperty iva = new SimpleDoubleProperty();
    private final DoubleProperty subtotal = new SimpleDoubleProperty();
    private final BooleanProperty variosIVAs = new SimpleBooleanProperty();
    private final DoubleProperty baseNI = new SimpleDoubleProperty();
    private final IntegerProperty ret = new SimpleIntegerProperty();
    private final DoubleProperty retenciones = new SimpleDoubleProperty();
    private final DoubleProperty total = new SimpleDoubleProperty();

    private final ObservableList<ExtractoFX> extractos = FXCollections.observableArrayList();


    // -------------------------
    // GETTERS / SETTERS FX
    // -------------------------

    public int getId() {
        return id.get();
    }

    public void setId(int v) {
        id.set(v);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getNumero() {
        return numero.get();
    }

    public void setNumero(String v) {
        numero.set(v);
    }

    public StringProperty numeroProperty() {
        return numero;
    }

    public Fecha getFecha() {
        return fecha.get();
    }

    public void setFecha(Fecha v) {
        fecha.set(v);
    }

    public ObjectProperty<Fecha> fechaProperty() {
        return fecha;
    }

    public String getRazonSocial() {
        return razonSocial.get();
    }

    public void setRazonSocial(String v) {
        razonSocial.set(v);
    }

    public StringProperty razonSocialProperty() {
        return razonSocial;
    }

    public String getConcepto() {
        return concepto.get();
    }

    public void setConcepto(String v) {
        concepto.set(v);
    }

    public StringProperty conceptoProperty() {
        return concepto;
    }

    public boolean isDevolucion() {
        return devolucion.get();
    }

    public void setDevolucion(boolean v) {
        devolucion.set(v);
    }

    public BooleanProperty devolucionProperty() {
        return devolucion;
    }

    public int getNumExtractos() {
        return numExtractos.get();
    }

    public void setNumExtractos(int v) {
        numExtractos.set(v);
    }

    public IntegerProperty numExtractosProperty() {
        return numExtractos;
    }

    public String getNota() {
        return nota.get();
    }

    public void setNota(String v) {
        nota.set(v);
    }

    public StringProperty notaProperty() {
        return nota;
    }

    // ---- Totales ----

    public double getBase() {
        return base.get();
    }

    public void setBase(double v) {
        base.set(v);
    }

    public DoubleProperty baseProperty() {
        return base;
    }

    public int getTipoIVA() {
        return tipoIVA.get();
    }

    public void setTipoIVA(int v) {
        tipoIVA.set(v);
    }

    public IntegerProperty tipoIVAProperty() {
        return tipoIVA;
    }

    public double getIVA() {
        return iva.get();
    }

    public void setIVA(double v) {
        iva.set(v);
    }

    public DoubleProperty ivaProperty() {
        return iva;
    }

    public double getSubtotal() {
        return subtotal.get();
    }

    public void setSubtotal(double v) {
        subtotal.set(v);
    }

    public DoubleProperty subtotalProperty() {
        return subtotal;
    }

    public boolean isVariosIVAs() {
        return variosIVAs.get();
    }

    public void setVariosIVAs(boolean v) {
        variosIVAs.set(v);
    }

    public BooleanProperty variosIVAsProperty() {
        return variosIVAs;
    }

    public double getBaseNI() {
        return baseNI.get();
    }

    public void setBaseNI(double v) {
        baseNI.set(v);
    }

    public DoubleProperty baseNIProperty() {
        return baseNI;
    }

    public int getRet() {
        return ret.get();
    }

    public void setRet(int v) {
        ret.set(v);
    }

    public IntegerProperty retProperty() {
        return ret;
    }

    public double getRetenciones() {
        return retenciones.get();
    }

    public void setRetenciones(double v) {
        retenciones.set(v);
    }

    public DoubleProperty retencionesProperty() {
        return retenciones;
    }

    public double getTotal() {
        return total.get();
    }

    public void setTotal(Number v) {
        total.set((double) v);
    }

    public DoubleProperty totalProperty() {
        return total;
    }

    public ObservableList<ExtractoFX> getExtractos() {
        return extractos;
    }

    // -------------------------
    // DOMINIO → FX
    // -------------------------

    public static FacturaFX fromDomain(Factura f) {
        FacturaFX fx = new FacturaFX();

        fx.setId(f.ID);
        fx.setNumero(f.numeroFactura);
        fx.setFecha(f.fecha);
        fx.setRazonSocial(f.RS.getNombre());
        fx.setConcepto(f.concepto.getTipo());
        fx.setDevolucion(f.esDevolucion);
        fx.setNumExtractos(f.extractos.size());
        fx.setNota(f.nota != null ? f.nota.getTexto() : "");


        for (Extracto e : f.extractos) {
            fx.addExtracto(ExtractoFX.fromDomain(e)); // o ExtractoFX si lo tienes separado
        }


        // Totales
        fx.setBase(f.totales.getBase());
        fx.setTipoIVA(f.totales.getTipoIVA());
        fx.setIVA(f.totales.getIVA());
        fx.setSubtotal(f.totales.getSubtotal());
        fx.setVariosIVAs(f.totales.isVariosIVAs());
        fx.setBaseNI(f.totales.getBaseNI());
        fx.setRet(f.totales.getRet());
        fx.setRetenciones(f.totales.getRetenciones());
        fx.setTotal(f.totales.getTotal());

        return fx;
    }

    // -------------------------
    // FX → DOMINIO
    // -------------------------

    public Factura toDomain() {
        // Crear RazonSocial directamente desde el nombre
        RazonSocial rs = new RazonSocial(getRazonSocial(), "");

        Totales t = new Totales(
                getBase(),
                isVariosIVAs(),
                getTipoIVA(),
                getIVA(),
                getSubtotal(),
                getBaseNI(),
                getRet(),
                getRetenciones(),
                getTotal(),
                getConcepto());

        return new Factura(
                getId(),
                getNumero(),
                getFecha(),
                rs,
                new TipoGasto(getConcepto(), ""),
                isDevolucion(),
                new ArrayList<>(),
                t,
                getNota().isEmpty() ? null : new Nota(getNota()));
    }

    public void actualizarFactura(Factura original) {

        // Campos principales
        original.numeroFactura = getNumero();
        original.fecha = getFecha();
        original.concepto = new TipoGasto(getConcepto(), "");
        original.esDevolucion = isDevolucion();

        // Totales
        original.totales.setBase(getBase());
        original.totales.setTipoIVA(getTipoIVA());
        original.totales.setIVA(getIVA());
        original.totales.setVariosIVAs(isVariosIVAs());
        original.totales.setBaseNI(getBaseNI());
        original.totales.setRet(getRet());
        original.totales.setRetenciones(getRetenciones());
        original.totales.setTotal(getTotal());

        // Nota
        if (!getNota().isEmpty()) {
            original.nota = new Nota(getNota());
        }
    }

    public void addExtracto(ExtractoFX e) {
        extractos.add(e);
    }
}

