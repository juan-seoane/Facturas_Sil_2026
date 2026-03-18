package presentation.viewmodels;

import domain.records.Factura;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;

public class FacturaFX {

    public ObservableValue<Double> FxRetenciones;
    public ObservableValue<Integer> FxTipoRet;
    public ObservableValue<Double> FxTotal;
    public SimpleStringProperty FxNota;
    public ObservableValue<Double> FxBaseNI;
    public ObservableValue<Double> FxST;
    public ObjectProperty<Double> FxIVA;
    public ObservableValue<Integer> FxTipoIVA;
    public ObservableValue<Double> FxBase;
    public ObservableValue<Integer> FxNumExtr;
    public SimpleStringProperty FxRS;
    public SimpleStringProperty FxCat;
    public SimpleStringProperty FxEsDev;
    public SimpleStringProperty FxFecha;
    public SimpleStringProperty FxNumFact;
    public ObservableValue<Integer> FxID;

    public FacturaFX(Factura f) {
        /*
        stream().map(f -> {
                })
         */
    }
}
