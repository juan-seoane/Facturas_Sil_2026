package com.sil.facturas.infrastructure.servicios.ocr.aux_ocr;

import java.util.List;
import java.util.Map;

import com.sil.facturas.domain.records.ROI;
import com.sil.facturas.infrastructure.servicios.ocr.ModeloOCR;

public class ModeloOCRDTO {
  public String nombre;
  public String version;
  public String rutaImagen;
  public int dpi;
  public Map<String, ROI> zonas;


  public static ModeloOCRDTO toDTO(ModeloOCR m) {
    ModeloOCRDTO dto = new ModeloOCRDTO();
    dto.nombre = m.nombre();
    dto.version = m.version();
    dto.rutaImagen = m.rutaImagen();
    dto.dpi = m.dpi();
    dto.zonas = m.zonas();

    return dto;
}

}
