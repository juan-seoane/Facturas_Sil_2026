package infrastructure.servicios.ocr;

import java.util.Map;

import domain.interfaces.IModeloFactura;
import domain.records.ROI;

public class ModeloOCR implements IModeloFactura{

    private final String nombre;
    private final String version;
    private final int dpi;
    private final Map<String, ROI> zonas;

    private ModeloOCR(Builder builder) {
        this.nombre = builder.nombre;
        this.version = builder.version;
        this.dpi = builder.dpi;
        this.zonas = Map.copyOf(builder.zonas);
    }

    public String nombre() { return nombre; }
    public String version() { return version; }
    public int dpi() { return dpi; }
    public Map<String, ROI> zonas() { return zonas; }

    // -------------------------------
    // Builder interno
    // -------------------------------
    public static class Builder {
        private String nombre;
        private String version = "1.0";
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

        public Builder dpi(int dpi) {
            this.dpi = dpi;
            return this;
        }

        public Builder zonas(Map<String, ROI> zonas) {
            this.zonas = zonas;
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
