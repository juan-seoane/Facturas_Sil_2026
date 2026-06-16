package com.sil.facturas.infrastructure.servicios.ocr;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import com.sil.facturas.domain.enums._Colores;
import com.sil.facturas.domain.interfaces.IDebugService;
import com.sil.facturas.domain.pojos.Extracto;
import com.sil.facturas.domain.pojos.Factura;
import com.sil.facturas.domain.records.Fecha;
import com.sil.facturas.domain.records.ROI;
import com.sil.facturas.domain.records.Totales;
import com.sil.facturas.infrastructure.servicios.ocr.editor.ModeloOCRParser;

import net.sourceforge.tess4j.TesseractException;

public class FacturaBuilderOCR {

    private final OCRService ocr;
    private final ModeloOCRParser parser;
    private final ModeloOCRService modeloOCRService;

    public FacturaBuilderOCR(OCRService ocr, ModeloOCRParser parser, ModeloOCRService modeloOCRService) {
        this.ocr = ocr;
        this.parser = parser;
        this.modeloOCRService = modeloOCRService;
    }

    public Factura construirFactura(BufferedImage img, ModeloOCR modeloOcr) {

        Factura factura = new Factura();

        // ============================
        // 0. CABECERA
        // ============================

        // Leer número de factura
        String numFactRaw = parser.leerCampo(img, modeloOcr, "factura.NumFact", ocr);
        if (numFactRaw != null && !numFactRaw.isBlank()) {
            String limpio = numFactRaw.replaceAll("[^A-Za-z0-9/-]", "").trim();
            factura.setNumeroFactura(limpio);
        }

        // Leer fecha
        String fechaRaw = parser.leerCampo(img, modeloOcr, "factura.Fecha", ocr);
        if (fechaRaw != null && !fechaRaw.isBlank()) {
            IDebugService.print(_Colores.WHITE, "[FacturaOCRBuilder>contruirFactura] Fecha RAW: " + fechaRaw);
            String soloFecha = fechaRaw.replaceAll("[^0-9/]", "");
            IDebugService.print(_Colores.WHITE, "[FacturaOCRBuilder>contruirFactura] Fecha: " + soloFecha);
            try {
                String[] p = soloFecha.split("/");
                int d = Integer.parseInt(p[0]);
                int m = Integer.parseInt(p[1]);
                int a = Integer.parseInt(p[2]);
                factura.setFecha(new Fecha(d, m, a));
            } catch (Exception ex) {
                System.err.println("[FacturaBuilderOCR] No se pudo parsear la fecha: " + fechaRaw);
            }
        }

        // ============================
        // 1. EXTRACTOS
        // ============================
        List<ROI> lineas = modeloOCRService.generarROIsLineasExtracto(modeloOcr);

        List<Extracto> listaExtractos = new ArrayList<>();
        int num = 1;

        for (ROI linea : lineas) {

            // Recortar la línea completa
            BufferedImage sub = img.getSubimage(
                (int) linea.x1(),
                (int) linea.y1(),
                (int) (linea.x2() - linea.x1()),
                (int) (linea.y2() - linea.y1())
            );

            // OCR de la línea completa
            String lineaOCR;
            try {
                lineaOCR = ocr.ocr(sub);
                // Parseo usando tu método que funciona
                Extracto e = parser.parsearLineaExtracto(lineaOCR);

                if (e != null) {
                    e.numExtracto = num++;
                    listaExtractos.add(e);
                }
            } catch (TesseractException e) {
                IDebugService.printError(
                        "[FacturaBuilderOCR>construirFactura] Excepcion tipo " + e.getClass() + " : " + e.getMessage());
            }
        }

        factura.setExtractos((ArrayList<Extracto>)listaExtractos);

        // ============================
        // 2. TOTALES
        // ============================
        Totales tot = parser.parsearTotales(img, modeloOcr, ocr);
        factura.setTotales(tot);

        IDebugService.print(_Colores.YELLOW,
                "[FacturasBuilderOCR>construirFactura] Construyendo:\n" + factura.toString());
        return factura;
    }
}
