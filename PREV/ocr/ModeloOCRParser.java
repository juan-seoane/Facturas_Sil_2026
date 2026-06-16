package com.sil.facturas.infrastructure.servicios.ocr.editor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sil.facturas.domain.enums._Colores;
import com.sil.facturas.domain.interfaces.IDebugService;
import com.sil.facturas.domain.pojos.Extracto;
import com.sil.facturas.domain.records.ROI;
import com.sil.facturas.domain.records.Totales;
import com.sil.facturas.infrastructure.records.Rect;
import com.sil.facturas.infrastructure.servicios.ocr.ModeloOCR;
import com.sil.facturas.infrastructure.servicios.ocr.OCRService;
import com.sil.facturas.infrastructure.servicios.ocr.aux_ocr.Bloque;
import com.sil.facturas.infrastructure.servicios.ocr.aux_ocr.Campo;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Paths;
import java.util.*;
import net.sourceforge.tess4j.TesseractException;

public class ModeloOCRParser {

  private final ObjectMapper mapper = new ObjectMapper();

  private void cargarBloqueExtractos(JsonNode bloque, ModeloOCR.Builder builder) {

    if (bloque.has("lineaBase")) {
      JsonNode lb = bloque.get("lineaBase");
      ROI roiLB =
          new ROI(
              lb.get("x1").asDouble(),
              lb.get("y1").asDouble(),
              lb.get("x2").asDouble(),
              lb.get("y2").asDouble());
      builder.zonas.put("lineaBase", roiLB);
    }

    if (bloque.has("finalExtractos")) {
      JsonNode fe = bloque.get("finalExtractos");
      ROI roiFE =
          new ROI(
              fe.get("x1").asDouble(),
              fe.get("y1").asDouble(),
              fe.get("x2").asDouble(),
              fe.get("y2").asDouble());
      builder.zonas.put("finalExtractos", roiFE);
    }

    if (bloque.has("alturaLineaAprox")) {
      builder.alturaLineaAprox(bloque.get("alturaLineaAprox").asInt());
    }
  }

  public Extracto parsearLineaExtracto(String linea) {

    try {
        // Normalizar espacios
        linea = linea.trim().replaceAll("\\s+", " ");

        // Extraer todos los números (cantidad, precio, total…)
        List<Double> nums = OCRService.extraerTodosLosNumeros(linea);

        if (nums.isEmpty()) {
        IDebugService.printError("[ModeloOCRParser>parsearLineaExtracto] Sin números: " + linea);
            return null;
        }

        // Concepto = todo lo que no son números
        String concepto = linea.replaceAll("[0-9.,]+", "").trim();

        Extracto ex = new Extracto();
        ex.concepto = concepto;

        // Interpretación según cantidad de números detectados
        if (nums.size() == 1) {
            // Solo total
            ex.subtotal = nums.get(0);
            ex.cantidad = 1;
            ex.precioUnitario = ex.subtotal;
        }
        else if (nums.size() == 2) {
            // precio + total
            ex.precioUnitario = nums.get(0);
            ex.subtotal = nums.get(1);
            ex.cantidad = (int) Math.round(ex.subtotal / ex.precioUnitario);
        }
        else if (nums.size() >= 3) {
            // cantidad + precio + total (o más números)
            ex.cantidad = nums.get(0).intValue();
            ex.precioUnitario = nums.get(nums.size() - 2);
            ex.subtotal = nums.get(nums.size() - 1);
        }

        ex.base = ex.cantidad * ex.precioUnitario;

        return ex;

    } catch (Exception e) {
      IDebugService.printError(
          "[ModeloOCRParser>parsearLineaExtracto] No se pudo parsear extracto: " + linea);
        return null;
    }
}

  public ModeloOCR parse(File jsonFile) throws Exception {

    JsonNode root = mapper.readTree(jsonFile);

    ModeloOCR.Builder builder = ModeloOCR.builder();

    // ============================
    // CAMPOS BÁSICOS
    // ============================
    builder.nombre(root.get("nombre").asText());
    builder.version(root.has("version") ? root.get("version").asText() : "1.0");
    builder.dpi(root.has("dpi") ? root.get("dpi").asInt() : 300);

    // Ruta imagen
    if (root.has("rutaImagen")) {
      String raw = root.get("rutaImagen").asText();
      if (raw.startsWith("file:/")) {
        builder.rutaImagen(Paths.get(URI.create(raw)).toString());
      } else {
        builder.rutaImagen(raw);
      }
    }

    // ============================
    // OCR POR ZONA
    // ============================
    Map<String, String> ocrPorZona = new HashMap<>();

    if (root.has("ocrPorZona")) {
      JsonNode ocrNode = root.get("ocrPorZona");
      Iterator<String> it = ocrNode.fieldNames();
      while (it.hasNext()) {
        String clave = it.next();
        ocrPorZona.put(clave, ocrNode.get(clave).asText());
      }
    }

    builder.ocrPorZona(ocrPorZona);

    // ============================
    // BLOQUES
    // ============================
    List<Bloque> bloques = new ArrayList<>();

    JsonNode bloquesNode = root.get("bloques");
    if (bloquesNode != null && bloquesNode.isArray()) {

      for (JsonNode b : bloquesNode) {

        Bloque bloque = new Bloque();

        bloque.nombre = b.get("nombre").asText();
        bloque.zona = new Rect(
            b.get("x1").asDouble(),
            b.get("y1").asDouble(),
            b.get("x2").asDouble(),
            b.get("y2").asDouble());

        bloque.extensibleHacia =
            b.has("extensibleHacia") ? b.get("extensibleHacia").asText() : "ninguno";

        bloque.relativoA = b.has("relativoA") ? b.get("relativoA").asText() : null;

        bloque.offsetY = b.has("offsetY") ? b.get("offsetY").asDouble() : 0;

        // BLOQUE EXTRACTOS
        if (bloque.nombre.equals("bloqueExtractos")) {
          IDebugService.print("[ModeloOCRParser>parse] cargando bloque de extractos");
          cargarBloqueExtractos(b, builder);
        } else {
          IDebugService.printError("[ModeloOCRParser>parse] No es un bloque de extractos");
        }

        // CABECERA
        if (b.has("cabecera")) {
          JsonNode c = b.get("cabecera");
          bloque.cabecera = new Rect(
              c.get("x1").asDouble(),
              c.get("y1").asDouble(),
              c.get("x2").asDouble(),
              c.get("y2").asDouble());
        }

        // LINEA BASE
        if (b.has("lineaBase")) {
          JsonNode lb = b.get("lineaBase");
          bloque.lineaBase = new Rect(
              lb.get("x1").asDouble(),
              lb.get("y1").asDouble(),
              lb.get("x2").asDouble(),
              lb.get("y2").asDouble());
        }

        // FINAL EXTRACTOS
        if (b.has("finalExtractos")) {
          JsonNode fe = b.get("finalExtractos");
          bloque.finalExtractos = new Rect(
              fe.get("x1").asDouble(),
              fe.get("y1").asDouble(),
              fe.get("x2").asDouble(),
              fe.get("y2").asDouble());
        }

        // ALTURA LÍNEA
        bloque.alturaLineaAprox =
            b.has("alturaLineaAprox") ? b.get("alturaLineaAprox").asDouble() : 0;
 // CAMPOS
        bloque.campos = new ArrayList<>();
        JsonNode camposNode = b.get("campos");

        if (camposNode != null && camposNode.isArray()) {
          for (JsonNode c : camposNode) {

            Campo campo = new Campo();

            campo.nombre = c.get("nombre").asText();
            campo.parent = c.get("parent").asText();

            campo.offsetX = c.has("offsetX") ? c.get("offsetX").asDouble() : 0;
            campo.offsetY = c.has("offsetY") ? c.get("offsetY").asDouble() : 0;

            bloque.campos.add(campo);
          }
        }

        bloques.add(bloque);
      }
    }

    builder.bloques(bloques);

    // ============================
    // DEVOLVER MODELO
    // ============================
    return builder.build();
  }

  public Totales parsearTotales(BufferedImage img, ModeloOCR modelo, OCRService ocr) {

    // 1. Buscar bloque Totales
    Bloque bloqueTotales = modelo.bloques().stream()
        .filter(b -> b.nombre.equals("bloqueTotales"))
        .findFirst()
        .orElse(null);

    if (bloqueTotales == null) return null;

    Map<String, String> valores = new HashMap<>();

    // 2. OCR por campo
    for (Campo c : bloqueTotales.campos) {

      ROI r = modelo.getCampoPorNombre(c.nombre);

      int x = (int) r.x1();
      int y = (int) r.y1();
      int w = (int) (r.x2() - r.x1());
      int h = (int) (r.y2() - r.y1());

      if (x < 0) x = 0;
      if (y < 0) y = 0;
      if (x + w > img.getWidth()) w = img.getWidth() - x;
      if (y + h > img.getHeight()) h = img.getHeight() - y;

      BufferedImage sub = img.getSubimage(x, y, w, h);

        String texto;
        try {
          texto = ocr.ocr(sub);
          valores.put(c.nombre, texto);
        } catch (TesseractException e) {
        IDebugService.printError(
            "[ModeloOCRParser>parsearTotales] Excepcion de tipo "
                + e.getClass()
                + " : "
                + e.getMessage());
        }
    }

    // 3. Normalizar usando tu OCRService
    double subTotal = parseDoubleOCR(valores.get("totales.subTotal"));
    double iva      = parseDoubleOCR(valores.get("totales.IVA"));
    int tipoIVA     = parseIntOCR(valores.get("totales.tipoIVA"));
    double total    = parseDoubleOCR(valores.get("totales.Total"));

    // 4. Construir objeto Totales
    Totales t = new Totales();
    t.setBase(subTotal);
    t.setIVA(iva);
    t.setTipoIVA(tipoIVA);
    t.setTotal(total);
    IDebugService.print(_Colores.YELLOW,"[ModeloOCRParser] PARSEANDO TOTALES:");
    IDebugService.print(_Colores.YELLOW, t.toString());
    return t;
}

private double parseDoubleOCR(String raw) {
    if (raw == null) return 0.0;
    String norm = OCRService.normalizarCifraOCR(raw);
    if (norm.isEmpty()) return 0.0;
    return Double.parseDouble(norm);
}

private int parseIntOCR(String raw) {
    if (raw == null) return 0;

    // Normaliza: convierte "21,0" o "21.0" a "21.0"
    String norm = OCRService.normalizarCifraOCR(raw);

    if (norm.isEmpty()) return 0;

    // Primero double
    double d = Double.parseDouble(norm);

    // Luego int
    return (int) d;
}


public ModeloOCR parse(InputStream is) throws Exception {
  JsonNode root = mapper.readTree(is);
  return parseJsonNode(root);
}

private ModeloOCR parseJsonNode(JsonNode root) throws Exception {

    ModeloOCR.Builder builder = ModeloOCR.builder();

    // ============================
    // CAMPOS BÁSICOS
    // ============================
    builder.nombre(root.get("nombre").asText());
    builder.version(root.has("version") ? root.get("version").asText() : "1.0");
    builder.dpi(root.has("dpi") ? root.get("dpi").asInt() : 300);

    // Ruta imagen
    if (root.has("rutaImagen")) {
        String raw = root.get("rutaImagen").asText();
        if (raw.startsWith("file:/")) {
            builder.rutaImagen(Paths.get(URI.create(raw)).toString());
        } else {
            builder.rutaImagen(raw);
        }
    }

    // ============================
    // OCR POR ZONA
    // ============================
    Map<String, String> ocrPorZona = new HashMap<>();

    if (root.has("ocrPorZona")) {
        JsonNode ocrNode = root.get("ocrPorZona");
        Iterator<String> it = ocrNode.fieldNames();
        while (it.hasNext()) {
            String clave = it.next();
            ocrPorZona.put(clave, ocrNode.get(clave).asText());
        }
    }

    builder.ocrPorZona(ocrPorZona);

    // ============================
    // BLOQUES
    // ============================
    List<Bloque> bloques = new ArrayList<>();

    JsonNode bloquesNode = root.get("bloques");
    if (bloquesNode != null && bloquesNode.isArray()) {

        for (JsonNode b : bloquesNode) {

            Bloque bloque = new Bloque();

            bloque.nombre = b.get("nombre").asText();
            bloque.zona = new Rect(
                    b.get("x1").asDouble(),
                    b.get("y1").asDouble(),
                    b.get("x2").asDouble(),
                    b.get("y2").asDouble());

        bloque.extensibleHacia =
            b.has("extensibleHacia") ? b.get("extensibleHacia").asText() : "ninguno";

            bloque.relativoA = b.has("relativoA") ? b.get("relativoA").asText() : null;

            bloque.offsetY = b.has("offsetY") ? b.get("offsetY").asDouble() : 0;

            // BLOQUE EXTRACTOS
            if (bloque.nombre.equals("bloqueExtractos")) {
                IDebugService.print("[ModeloOCRParser>parse] cargando bloque de extractos");
                cargarBloqueExtractos(b, builder);
            } else {
                IDebugService.printError("[ModeloOCRParser>parse] No es un bloque de extractos");
            }

            // CABECERA
            if (b.has("cabecera")) {
                JsonNode c = b.get("cabecera");
                bloque.cabecera = new Rect(
                        c.get("x1").asDouble(),
                        c.get("y1").asDouble(),
                        c.get("x2").asDouble(),
                        c.get("y2").asDouble());
            }

            // LINEA BASE
            if (b.has("lineaBase")) {
                JsonNode lb = b.get("lineaBase");
                bloque.lineaBase = new Rect(
                        lb.get("x1").asDouble(),
                        lb.get("y1").asDouble(),
                        lb.get("x2").asDouble(),
                        lb.get("y2").asDouble());
            }

            // FINAL EXTRACTOS
            if (b.has("finalExtractos")) {
                JsonNode fe = b.get("finalExtractos");
                bloque.finalExtractos = new Rect(
                        fe.get("x1").asDouble(),
                        fe.get("y1").asDouble(),
                        fe.get("x2").asDouble(),
                        fe.get("y2").asDouble());
            }

        // ALTURA LÍNEA
        bloque.alturaLineaAprox =
            b.has("alturaLineaAprox") ? b.get("alturaLineaAprox").asDouble() : 0;

            // CAMPOS
            bloque.campos = new ArrayList<>();
            JsonNode camposNode = b.get("campos");

            if (camposNode != null && camposNode.isArray()) {
                for (JsonNode c : camposNode) {

                    Campo campo = new Campo();

                    campo.nombre = c.get("nombre").asText();
                    campo.parent = c.get("parent").asText();

                    campo.x1 = c.get("x1").asDouble();
                    campo.y1 = c.get("y1").asDouble();
                    campo.x2 = c.get("x2").asDouble();
                    campo.y2 = c.get("y2").asDouble();

                    campo.offsetX = c.has("offsetX") ? c.get("offsetX").asDouble() : 0;
                    campo.offsetY = c.has("offsetY") ? c.get("offsetY").asDouble() : 0;

                    bloque.campos.add(campo);
                }
            }

            bloques.add(bloque);
        }
    }

    builder.bloques(bloques);

    // ============================
    // DEVOLVER MODELO
    // ============================
    return builder.build();
}

  public String leerCampo(BufferedImage img, ModeloOCR modelo, String nombreCampo, OCRService ocr) {

    ROI campo = modelo.getCampoPorNombre(nombreCampo);
    if (campo == null) {
      IDebugService.printError("[ModeloOCRParser] Campo no encontrado en modelo: " + nombreCampo);
      return null;
    }

    double rx1 = campo.x1();
    double ry1 = campo.y1();
    double rx2 = campo.x2();
    double ry2 = campo.y2();

    IDebugService.printError(
        "[DEBUG ROI] "
            + nombreCampo
            + " x1="
            + rx1
            + " y1="
            + ry1
            + " x2="
            + rx2
            + " y2="
            + ry2
            + " imgW="
            + img.getWidth()
            + " imgH="
            + img.getHeight());

    try {
      int x = (int) rx1;
      int y = (int) ry1;
      int w = (int) (rx2 - rx1);
      int h = (int) (ry2 - ry1);

      IDebugService.printError(
          "[DEBUG SUBIMAGE] "
              + nombreCampo
              + " x="
              + x
              + " y="
              + y
              + " w="
              + w
              + " h="
              + h
              + " x+w="
              + (x + w)
              + " y+h="
              + (y + h));

      BufferedImage sub = img.getSubimage(x, y, w, h);
      String texto = ocr.ocr(sub);
      return texto != null ? texto.trim() : null;

    } catch (Exception e) {
      IDebugService.printError(
          "[ModeloOCRParser] Error leyendo campo " + nombreCampo + ": " + e.getMessage());
      return null;
    }
  }
}
