package com.sil.facturas.infrastructure.services.ocr;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sil.facturas.domain.ocr.*;
import com.sil.facturas.infrastructure.json.ocr.ModeloOCRParser;
import com.sil.facturas.infrastructure.services.ocr.aux_ocr.BloqueOCR;
import com.sil.facturas.infrastructure.services.ocr.aux_ocr.CampoOCR;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ModeloOCRService {

  private ModeloOCR modelo;
  private final ModeloOCRParser parser;

  public ModeloOCRService() {
    this.parser = new ModeloOCRParser();
  }

  public void nuevoModelo(String nombre, String version) {
    modelo =
        new ModeloOCR(
            nombre, version, new ArrayList<Bloque>() // lista de bloques vacía
            );
  }

  // ============================================================
  //  CARGA Y GUARDADO
  // ============================================================

  public ModeloOCR cargarModelo(File file) {
      this.modelo = parser.parseModelo(file);
      return this.modelo;
  }

  public void guardarModelo(File file) {
    parser.guardarModelo(modelo, file);
  }

  public void guardarBloques(File file) {
    parser.guardarBloques(modelo.getBloques(), file);
  }

  // ============================================================
  //  ACCESO AL MODELO
  // ============================================================

  public ModeloOCR getModelo() {
    return modelo;
  }

  public List<Bloque> getBloques() {
    return modelo.getBloques();
  }

  public Map<String, ROI> getZonasSueltas() {
    return modelo.getZonasSueltas();
  }

  // ============================================================
  //  CONVERSIÓN DESDE EL EDITOR (BloqueOCR, CampoOCR)
  // ============================================================

  public Bloque convertirBloqueOCR(BloqueOCR b) {

    // Convertir campos
    List<Campo> campos = new ArrayList<>();
    for (CampoOCR c : b.campos()) {
      campos.add(convertirCampoOCR(c, b));
    }

    // Cabecera (si la tienes en BloqueOCR)
    List<String> cabecera =
        b.cabeceraColumnas() != null ? new ArrayList<>(b.cabeceraColumnas()) : new ArrayList<>();

    return new Bloque(
        b.nombre(),
        b.getParentNombre(),
        b.referencia(),
        new Offset(b.offsetTipo(), b.dx(), b.dy()),
        new Rect(b.x(), b.y(), b.w(), b.h()),
        b.growVertical(),
        b.growHorizontal(),
        b.anchorX(),
        b.anchorY(),
        campos,
        cabecera);
  }

  public Campo convertirCampoOCR(CampoOCR c, BloqueOCR bloquePadre) {

    double offsetX = c.x() - bloquePadre.x();
    double offsetY = c.y() - bloquePadre.y();

    return new Campo(
        c.nombre(),
        bloquePadre.nombre(), // parent correcto
        c.valorSemantico(),
        offsetX,
        offsetY,
        c.h(),
        c.w(),
        c.tipoContenido());
  }

  // ============================================================
  //  MODIFICACIÓN DEL MODELO
  // ============================================================

  public void addBloque(Bloque bloque) {
    modelo.getBloques().add(bloque);
  }

  public void addCampo(String nombreBloque, Campo campo) {
    modelo.getBloques().stream()
        .filter(b -> b.nombre().equals(nombreBloque))
        .findFirst()
        .ifPresent(b -> b.campos().add(campo));
  }

  public void addZonaSuelta(String nombre, ROI roi) {
    modelo.getZonasSueltas().put(nombre, roi);
  }

  // ============================================================
  //  NORMALIZACIÓN / DESNORMALIZACIÓN
  // ============================================================

  public Rect normalizar(Rect absoluto, double imgW, double imgH) {
    return new Rect(
        (absoluto.x() * 1000.0 / imgW),
        (absoluto.y() * 1000.0 / imgH),
        (absoluto.w() * 1000.0 / imgW),
        (absoluto.h() * 1000.0 / imgH));
  }

  public Rect desnormalizar(Rect normalizado, double imgW, double imgH) {
    return new Rect(
        (normalizado.x() * imgW / 1000.0),
        (normalizado.y() * imgH / 1000.0),
        (normalizado.w() * imgW / 1000.0),
        (normalizado.h() * imgH / 1000.0));
  }

  // ============================================================
  //  ROI REAL PARA OCR
  // ============================================================

  public ROI calcularROIReal(Rect rect, double imgW, double imgH) {
      double x1 = (rect.x() * imgW / 1000.0);
      double y1 = (rect.y() * imgH / 1000.0);
      double x2 = x1 + (rect.w() * imgW / 1000.0);
      double y2 = y1 + (rect.h() * imgH / 1000.0);

      return new ROI(x1, y1, x2, y2);
  }
    public List<Bloque> cargarBloques(File file) {
    try (Reader r = new FileReader(file)) {
      Gson gson = new Gson();
      Type tipoLista = new TypeToken<List<Bloque>>() {}.getType();
      return gson.fromJson(r, tipoLista);
    } catch (Exception e) {
      e.printStackTrace();
      return List.of();
    }
  }

}
