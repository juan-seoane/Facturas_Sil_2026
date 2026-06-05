package com.sil.facturas.infrastructure.servicios.ocr.aux_ocr;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.sil.facturas.domain.records.Extracto;
import com.sil.facturas.domain.records.ROI;
import com.sil.facturas.infrastructure.debug.Debug;
import com.sil.facturas.infrastructure.servicios.ocr.ModeloOCR;

import java.io.File;
import java.net.URI;
import java.nio.file.Paths;
import java.util.*;

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

      // Separar por espacios simples
      String[] partes = linea.split(" ");

      if (partes.length < 4) {
        System.out.println("Línea demasiado corta: " + linea);
        return null;
      }

      // 1) Cantidad = primer token
      int cantidad = Integer.parseInt(partes[0]);

      // 2) Total = último token
      String totalStr = partes[partes.length - 1].replace(",", ".");
      double total = Double.parseDouble(totalStr);

      // 3) Precio unitario = penúltimo token
      String precioStr = partes[partes.length - 2].replace(",", ".");
      double precioUnitario = Double.parseDouble(precioStr);

      // 4) Concepto = todo lo que queda en medio
      StringBuilder sb = new StringBuilder();
      for (int i = 1; i < partes.length - 2; i++) {
        sb.append(partes[i]).append(" ");
      }
      String concepto = sb.toString().trim();

      // 5) Crear extracto
      Extracto ex = new Extracto();
      ex.cantidad = cantidad;
      ex.concepto = concepto;
      ex.precioUnitario = precioUnitario;
      ex.subtotal = total;

      return ex;

    } catch (Exception e) {
      System.out.println("No se pudo parsear extracto: " + linea);
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
            bloque.zona =
                new Rect(
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
                Debug.print("[ModeloOCRParser>parse] cargando bloque de extractos");
                cargarBloqueExtractos(b, builder);
            } else {
                Debug.printError("[ModeloOCRParser>parse] No es un bloque de extractos");
            }

            // CABECERA
            if (b.has("cabecera")) {
            JsonNode c = b.get("cabecera");
            bloque.cabecera =
                new Rect(
                    c.get("x1").asDouble(),
                    c.get("y1").asDouble(),
                    c.get("x2").asDouble(),
                    c.get("y2").asDouble());
            }

            // LINEA BASE
            if (b.has("lineaBase")) {
            JsonNode lb = b.get("lineaBase");
            bloque.lineaBase =
                new Rect(
                    lb.get("x1").asDouble(),
                    lb.get("y1").asDouble(),
                    lb.get("x2").asDouble(),
                    lb.get("y2").asDouble());
            }

            // FINAL EXTRACTOS
            if (b.has("finalExtractos")) {
            JsonNode fe = b.get("finalExtractos");
            bloque.finalExtractos =
                new Rect(
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

                campo.zona =
                    new Rect(
                        c.get("x1").asDouble(),
                        c.get("y1").asDouble(),
                        c.get("x2").asDouble(),
                        c.get("y2").asDouble());

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
}
