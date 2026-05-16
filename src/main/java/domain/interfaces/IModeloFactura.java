package domain.interfaces;

import java.util.Map;
import java.awt.image.BufferedImage;


import domain.records.ROI;

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

