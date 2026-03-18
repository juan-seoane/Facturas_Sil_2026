package domain.records;

public class Anho {
    private int año;
    private int trimestre;

    public Anho(int año, int trimestre) {
        this.año = año;
        this.trimestre = trimestre;
    }

    public int getAnho() {
        return año;
    }

    public void setAnho(int año) {
        this.año = año;
    }

    public int getTrimestre() {
        return trimestre;
    }

    public void setTrimestre(int trimestre) {
        this.trimestre = trimestre;
    }
}
