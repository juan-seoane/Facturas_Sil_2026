package com.sil.facturas.infrastructure.servicios.ocr;

import com.sil.facturas.domain.records.ROI;
import com.sil.facturas.infrastructure.servicios.ocr.aux_ocr.Bloque;
import com.sil.facturas.infrastructure.servicios.ocr.aux_ocr.Campo;
import java.util.List;
import java.util.Map;

public class ModeloOCR {

  private String nombre;
  private String version;
  private int dpi;
  private String rutaImagen;

  private Map<String, ROI> zonas;
  private Map<String, String> ocrPorZona;
  private int alturaLineaAprox;

  public List<Bloque> bloques; // <-- NUEVO

  // ============================
  // GETTERS
  // ============================
  public String nombre() {
    return nombre;
  }

  public String version() {
    return version;
  }

  public int dpi() {
    return dpi;
  }

  public String rutaImagen() {
    return rutaImagen;
  }

  public Map<String, ROI> zonas() {
    return zonas;
  }

  public Map<String, String> ocrPorZona() {
    return ocrPorZona;
  }

    public List<Bloque> bloques() {
        return bloques;
    } // <-- NUEVO

    public static Builder builder() {
        return new Builder();
    }

    public int alturaLineaAprox() {
      return alturaLineaAprox;
    }
  // ============================
  // BUILDER
  // ============================
  public static class Builder {

    private String nombre;
    private String version;
    private int dpi;
    private String rutaImagen;
    private int alturaLineaAprox;

    public Map<String, ROI> zonas = new java.util.LinkedHashMap<>();
    public Map<String, String> ocrPorZona = new java.util.LinkedHashMap<>();

    private List<Bloque> bloques; // <-- NUEVO

    public Builder nombre(String n) {
      this.nombre = n;
      return this;
    }

    public Builder version(String v) {
        this.version = v;
        return this;
    }

    public Builder zonas(Map<String, ROI> z) {
        this.zonas = z;
        return this;
    }

    public Builder ocrPorZona(Map<String, String> o) {
        this.ocrPorZona = o;
        return this;
    }

    public Builder dpi(int d) {
      this.dpi = d;
      return this;
    }

    public Builder rutaImagen(String r) {
      this.rutaImagen = r;
      return this;
    }

    public Builder bloques(List<Bloque> b) {
      this.bloques = b;
      return this;
    } // <-- NUEVO

    public Builder alturaLineaAprox(int altura) {
      this.alturaLineaAprox = altura;
      return this;
    }

    public ModeloOCR build() {
      ModeloOCR m = new ModeloOCR();
      m.nombre = this.nombre;
      m.version = this.version;
      m.dpi = this.dpi;
      m.rutaImagen = this.rutaImagen;
      m.zonas = this.zonas;
      m.ocrPorZona = this.ocrPorZona;
      m.bloques = this.bloques; // <-- NUEVO
      m.alturaLineaAprox = this.alturaLineaAprox; // <-- NUEVO
      return m;
    }
  }

  public ROI getCampoPorNombre(String nombreCampo) {

    for (Bloque bloque : bloques) {
      for (Campo campo : bloque.campos) {

        if (!campo.nombre.equals(nombreCampo)) continue;

        // ROI relativo del campo
        double cx1 = campo.x1;
        double cy1 = campo.y1;
        double cx2 = campo.x2;
        double cy2 = campo.y2;

        // ROI absoluto = bloque + campo + offset
        double x1 = bloque.zona.x1() + cx1 + campo.offsetX;
        double y1 = bloque.zona.y1() + cy1 + campo.offsetY;
        double x2 = bloque.zona.x1() + cx2 + campo.offsetX;
        double y2 = bloque.zona.y1() + cy2 + campo.offsetY;

        return new ROI(x1, y1, x2, y2);
      }
    }

    return null;
  }
}
