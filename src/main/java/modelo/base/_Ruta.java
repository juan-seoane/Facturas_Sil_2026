package modelo.base;

public enum _Ruta {
    CONFIG("/config"),
    DATOS("/datos"),
    IMG("/imagenes"),
    FXML("/ui/resources");

    private final String ruta;

    _Ruta(String ruta) {
        this.ruta = ruta;
    }

    public String getRuta() {
        return ruta;
    }
}
