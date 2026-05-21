package infrastructure.servicios.ocr;

import domain.interfaces.IModeloFactura;
import domain.records.ROI;
import java.util.Map;

public class ModeloOCR implements IModeloFactura {

  private final String nombre;
  private final String version;
  private final String rutaImagen;
  private final int dpi;
  private final Map<String, ROI> zonas;
  private final Map<String, String> ocrPorZona;

  private ModeloOCR(Builder builder) {
    this.nombre = builder.nombre;
    this.version = builder.version;
    this.rutaImagen = builder.rutaImagen;
    this.dpi = builder.dpi;
    this.zonas = Map.copyOf(builder.zonas);
    this.ocrPorZona = Map.copyOf(builder.ocrPorZona);
  }

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

  // -------------------------------
  // Builder interno
  // -------------------------------
  public static class Builder {
      public Map<String, String> ocrPorZona;
      private String nombre;
      private String version = "1.0";
      private String rutaImagen;
      private int dpi = 300;
      private Map<String, ROI> zonas;

      public Builder nombre(String nombre) {
          this.nombre = nombre;
          return this;
        }

        public Builder version(String version) {
            this.version = version;
            return this;
        }

        public Builder rutaImagen(String ruta) {
            this.rutaImagen = ruta;
            return this;
        }


        public Builder dpi(int dpi) {
            this.dpi = dpi;
            return this;
        }


        public Builder zonas(Map<String, ROI> zonas) {
            this.zonas = zonas;
            return this;
        }

        public Builder ocrPorZona(Map<String, String> ocrPorZona) {
            this.ocrPorZona = ocrPorZona;
            return this;
        }
        
        public ModeloOCR build() {
            return new ModeloOCR(this);
        }
    }

    public static Builder builder() {
        return new Builder();
  }
}
