package com.sil.facturas.app.filtros;

import java.util.List;

public interface Ifiltro<T> {
    List<T> filtrar(List<T> lista);
    boolean filtrar(int index, List<T> lista);
    T filtrar(T elemento);
}
