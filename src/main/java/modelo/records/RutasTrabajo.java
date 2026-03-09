package modelo.records;
// REVIEW - 24-05-07 : Hacer un método estático para chequear si los archivos de Trabajo de un Usuario determinado existen... public static archivosTrabajoExisten(String user)()
public class RutasTrabajo {
    private String FCT;
    private String RS;
    private String CJA;

    public RutasTrabajo(String FCT, String RS, String CJA) {
        this.FCT = FCT;
        this.RS = RS;
        this.CJA = CJA;
    }

    public String getFCT() {
        return FCT;
    }

    public void setFCT(String FCT) {
        this.FCT = FCT;
    }

    public String getRS() {
        return RS;
    }

    public void setRS(String RS) {
        this.RS = RS;
    }

    public String getCJA() {
        return CJA;
    }

    public void setCJA(String CJA) {
        this.CJA = CJA;
    }
}
