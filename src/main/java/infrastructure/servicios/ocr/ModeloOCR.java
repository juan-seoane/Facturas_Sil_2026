package infrastructure.servicios.ocr;

import domain.records.ROI;
import infrastructure.servicios.ocr.aux.Bloque;

import java.util.List;
import java.util.Map;

public class ModeloOCR {

  private String nombre;
  private String version;
  private int dpi;
  private String rutaImagen;

  private Map<String, ROI> zonas;
  private Map<String, String> ocrPorZona;

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

  // ============================
  // BUILDER
  // ============================
  public static class Builder {

    private String nombre;
    private String version;
    private int dpi;
    private String rutaImagen;

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

    public ModeloOCR build() {
      ModeloOCR m = new ModeloOCR();
      m.nombre = this.nombre;
      m.version = this.version;
      m.dpi = this.dpi;
      m.rutaImagen = this.rutaImagen;
      m.zonas = this.zonas;
      m.ocrPorZona = this.ocrPorZona;
      m.bloques = this.bloques; // <-- NUEVO
      return m;
    }
  }
}
