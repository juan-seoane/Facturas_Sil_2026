package com.sil.facturas.app.services;
/* CONTROLA LAS PILAS DEL HISTORIAL */
import java.util.Stack;

import com.sil.facturas.domain.pojos.Factura;

public class UndoRedoService {

    private Stack<Factura> pilaFCTant;
    private Stack<Factura> pilaFCTsig;

    public Stack<Factura> getPilaFacturasAnt() {
        return pilaFCTant;
    }

    public Stack<Factura> getPilaFacturasSig() {
        return pilaFCTsig;
    }

}
