package modelo.base;

public enum _Ruta {
    CONFIG("./data/config"),
    DATOS("./data/datos");

    private final String ruta;

    _Ruta(String ruta) {
        this.ruta = ruta;
    }

    public String getRuta() {
        return ruta;
    }
}
