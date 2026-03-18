package app.controladores.helpers;

public enum _Modo {
    NAV(0),
    INGR(1);

    private final int modo;

    _Modo(int modo) {
        this.modo = modo;
    }

    public int getModo() {
        return this.modo;
    }
}
