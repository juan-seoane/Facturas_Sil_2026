package com.sil.facturas.infrastructure.servicios.ocr;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sil.facturas.domain.pojos.Extracto;
import com.sil.facturas.domain.pojos.Factura;

public class FacturaBuilder {

  public static Factura fromOCR(Map<String, String> textos) {

    Factura f = new Factura();

    // ============================
    // 1. TOTALES
    // ============================
    f.totales.setBase(
        Double.parseDouble(OCRService.extraerNumeros(textos.getOrDefault("totales.subTotal", "0"))));
    f.totales.setTipoIVA(
        (int) Double.parseDouble(OCRService.extraerNumeros(textos.get("totales.tipoIVA"))));
    f.totales.setIVA(
        Double.parseDouble(OCRService.normalizarCifraOCR(OCRService.extraerNumeros(textos.getOrDefault("totales.IVA", "0")))));
    f.totales.setTotal(
        Double.parseDouble(OCRService.extraerNumeros(textos.getOrDefault("totales.Total", "0"))));

    // ============================
    // 2. EXTRACTOS (N líneas)
    // ============================
    List<Extracto> extractos = new ArrayList<>();

    int i = 1;
    while (true) {
      String pref = "extracto." + i + ".";

      // Si no existe el concepto, asumimos que no hay más líneas
      if (!textos.containsKey(pref + "Concepto")) break;

      Extracto e = new Extracto();
      e.setCantidad(
          Integer.parseInt(OCRService.extraerNumeros(textos.getOrDefault(pref + "Cantidad", "0"))));
      e.setConcepto((textos.getOrDefault(pref + "Concepto", "")).toUpperCase());
      e.setPrecioUnitario(
          Double.parseDouble(
              OCRService.extraerNumeros(textos.getOrDefault(pref + "PrecioUnitario", "0"))));
      e.setSubtotal(
          Double.parseDouble(
              OCRService.extraerNumeros(textos.getOrDefault(pref + "TotalExtracto", "0"))));

      extractos.add(e);
      i++;
    }

    f.setExtractos((ArrayList<Extracto>) extractos);

    return f;
  }
}
