package app.controladores.helpers;

public enum _Seccion {

    FACT(1),
    DIST(2),
    NOTAS(3),
    CONFIG(4),
    CAJA(5);

    private final int seccion;

    _Seccion(int seccion){
        this.seccion = seccion;
    }

    public int getSeccion() {
        return this.seccion;
    }
}
