package com.sil.facturas.app.api;

import java.util.function.Consumer;

public interface INavService {

    void ocultar(IVentanaID v);
    void cerrarPorFallo(IVentanaID v);
    void cerrar(IVentanaID v);
    void mostrar(IVentanaID id, Consumer<Object> initController);


}
