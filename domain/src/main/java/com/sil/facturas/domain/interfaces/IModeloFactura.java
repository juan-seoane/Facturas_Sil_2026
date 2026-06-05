package com.sil.facturas.domain.interfaces;

import java.util.Map;

import com.sil.facturas.domain.records.ROI;

import java.awt.image.BufferedImage;

public interface IModeloFactura {

    // Nombre del proveedor o tipo
    String nombre();

    // Mapa clave → ROI
    Map<String, ROI> zonas();

    // Opcional: regex específicas para interpretar valores
    default Map<String, String> patrones() {
        return Map.of();
    }

    // Opcional: preprocesado específico por proveedor
    default BufferedImage preprocesar(BufferedImage img) {
        return img;
    }
}

