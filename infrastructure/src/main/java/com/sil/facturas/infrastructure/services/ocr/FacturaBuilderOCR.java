package com.sil.facturas.infrastructure.services.ocr;

import com.sil.facturas.domain.ocr.*;
import com.sil.facturas.domain.pojos.Extracto;
import com.sil.facturas.domain.pojos.Factura;
import com.sil.facturas.domain.pojos.RazonSocial;
import com.sil.facturas.domain.pojos.Totales;
import com.sil.facturas.domain.records.Fecha;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class FacturaBuilderOCR {

  private final OCRService ocr;
  private final ModeloOCRService modeloService;

  public FacturaBuilderOCR(OCRService ocr, ModeloOCRService modeloService) {
    this.ocr = ocr;
    this.modeloService = modeloService;
  }

  // ============================================================
  //  OCR DE CAMPOS
  // ============================================================

  private String extraerCampo(BufferedImage img, Bloque bloque, Campo campo) {

    int imgW = img.getWidth();
    int imgH = img.getHeight();

    // ROI relativo del campo → ROI absoluto
    Rect rectCampo =
        new Rect(
            bloque.zona().x() + campo.offsetX(),
            bloque.zona().y() + campo.offsetY(),
            campo.w(),
            campo.h());

    ROI roi = modeloService.calcularROIReal(rectCampo, imgW, imgH);

    return ocr.ocr(img, roi);
  }

  // ============================================================
  //  OCR DE BLOQUES COMPLETOS
  // ============================================================

  private String extraerBloque(BufferedImage img, Bloque bloque) {

    int imgW = img.getWidth();
    int imgH = img.getHeight();

    ROI roi = modeloService.calcularROIReal(bloque.zona(), imgW, imgH);

    return ocr.ocr(img, roi);
  }

  // ============================================================
  //  CONSTRUIR FACTURA COMPLETA
  // ============================================================

  public Factura construirFactura(BufferedImage img, ModeloOCR modelo) {

    Factura factura = new Factura();
    int imgW = img.getWidth();
    int imgH = img.getHeight();

    List<Extracto> extractos = new ArrayList<>();
    Totales totales = new Totales();

    // ============================================================
    // 1. RECORRER BLOQUES
    // ============================================================

    for (Bloque bloque : modelo.getBloques()) {

      // Si el bloque tiene campos → cabecera o totales
      if (!bloque.campos().isEmpty()) {

        for (Campo campo : bloque.campos()) {

          String valor = extraerCampo(img, bloque, campo);

          switch (campo.valorSemantico()) {
            case "numFactura" -> factura.setNumeroFactura(valor);
            case "fecha" -> factura.setFecha(Fecha.fromString(valor));
            case "razonSocial" -> factura.setRS(new RazonSocial(0,null,valor));
            case "concepto" -> factura.setConcepto(valor);

            case "base" -> totales.setBase(Double.parseDouble(valor));
            case "iva" -> totales.setIva(Double.parseDouble(valor));
            case "total" -> totales.setTotal(Double.parseDouble(valor));
          }
        }

      } else {
        // ============================================================
        // 2. BLOQUE DE EXTRACTOS (LISTA DE LÍNEAS)
        // ============================================================

        String texto = extraerBloque(img, bloque);
        String[] lineas = texto.split("\n");

        for (String linea : lineas) {
          Extracto e = parsearLineaExtracto(linea, bloque);
          if (e != null) {
            extractos.add(e);
          }
        }
      }
    }

    factura.setExtractos((ArrayList<Extracto>)extractos);
    factura.setTotales(totales);

    return factura;
  }

  // ============================================================
  //  PARSEAR LÍNEA DE EXTRACTO (VERSIÓN SIMPLE)
  // ============================================================

  private Extracto parsearLineaExtracto(String linea, Bloque bloque) {

    if (linea == null || linea.isBlank()) return null;

    String[] tokens = linea.trim().split("\\s+");
    List<String> cab = bloque.cabeceraColumnas(); // ← orden real de columnas

    if (tokens.length < cab.size()) return null;

    Extracto e = new Extracto();

    for (int i = 0; i < cab.size(); i++) {
      String col = cab.get(i);
      String tok = tokens[i];

      switch (col) {
        case "cantidad" -> e.cantidad = Integer.parseInt(tok);
        case "concepto" -> e.concepto = tok;
        case "precioUnitario" -> e.precioUnitario = Double.parseDouble(tok);
        case "subtotal" -> e.subtotal = Double.parseDouble(tok);
        case "num" -> e.numExtracto = Integer.parseInt(tok);
        case "descripcion" -> e.concepto = tok;
        case "importe" -> e.subtotal = Double.parseDouble(tok);
      }
    }

    // Si no viene subtotal, lo calculamos
    if (e.subtotal == 0 && e.precioUnitario > 0 && e.cantidad > 0) {
      e.subtotal = e.cantidad * e.precioUnitario;
    }

    return e;
  }
}
