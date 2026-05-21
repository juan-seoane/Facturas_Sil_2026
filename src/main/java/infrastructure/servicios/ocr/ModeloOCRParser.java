package infrastructure.servicios.ocr;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import domain.records.ROI;
import java.io.File;
import java.net.URI;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ModeloOCRParser {

  private final ObjectMapper mapper = new ObjectMapper();

  public ModeloOCR parse(File jsonFile) throws Exception {

    JsonNode root = mapper.readTree(jsonFile);

    String nombre = root.get("nombre").asText();
    String version = root.has("version") ? root.get("version").asText() : "1.0";
    int dpi = root.has("dpi") ? root.get("dpi").asInt() : 300;

    String rutaImagen = null;

    if (root.has("rutaImagen")) {
      String raw = root.get("rutaImagen").asText();

      if (raw.startsWith("file:/")) {
        rutaImagen = Paths.get(URI.create(raw)).toString();
      } else {
        rutaImagen = raw;
      }
    }

    Map<String, ROI> zonas = new HashMap<>();
    Map<String, String> ocrPorZona = new HashMap<>();

    JsonNode zonasNode = root.get("zonas");
    Iterator<String> campos = zonasNode.fieldNames();

    while (campos.hasNext()) {
      String clave = campos.next();
      JsonNode r = zonasNode.get(clave);

      ROI roi =
          new ROI(
              r.get("x1").asInt(), r.get("y1").asInt(), r.get("x2").asInt(), r.get("y2").asInt());

      zonas.put(clave, roi);

      // Leer OCR si existe
      if (r.has("ocr")) {
        ocrPorZona.put(clave, r.get("ocr").asText());
      }
    }

    return ModeloOCR.builder()
        .nombre(nombre)
        .version(version)
        .dpi(dpi)
        .rutaImagen(rutaImagen)
        .zonas(zonas)
        .ocrPorZona(ocrPorZona) // <-- añade este campo a tu ModeloOCR
        .build();
  }
}
