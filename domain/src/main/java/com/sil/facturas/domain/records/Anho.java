package com.sil.facturas.domain.records;

public class Anho {
    private int anho;
    private int trimestre;

    public Anho(int anho, int trimestre) {
        this.anho = anho;
        this.trimestre = trimestre;
    }

    public int getAnho() {
        return anho;
    }

    public void setAnho(int anho) {
        this.anho = anho;
    }

    public int getTrimestre() {
        return trimestre;
    }

    public void setTrimestre(int trimestre) {
        this.trimestre = trimestre;
    }
}
