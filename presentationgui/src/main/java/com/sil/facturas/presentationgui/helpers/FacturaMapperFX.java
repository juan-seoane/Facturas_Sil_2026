package com.sil.facturas.presentationgui.helpers;

import com.sil.facturas.domain.pojos.Factura;
import com.sil.facturas.presentationgui.viewmodels.FacturaFX;

public class FacturaMapperFX {
    public static FacturaFX toFX(Factura f) {
        return FacturaFX.fromDomain(f);
    }
}
