package com.sil.facturas.infrastructure.filesystem;

public enum _Ruta {
    // Datos en filesystem (fuera de src/main/resources)
    CONFIG("data/config"),
    DATOS("data/datos"),
    MODELOSOCR("data/datos/modelosOCR"),

    // Recursos dentro de src/main/resources
    IMG("/images"),
    FXML("/fxml");

    final String ruta;

    _Ruta(String ruta) {
        this.ruta = ruta;
    }

    public String getRuta() {
        return ruta;
    }
}
