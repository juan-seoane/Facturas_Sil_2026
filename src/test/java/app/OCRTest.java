package app;

import static org.junit.jupiter.api.Assertions.*;

import domain.records.ROI;
import infrastructure.filesystem._Ruta;
import infrastructure.servicios.ocr.ModeloOCR;
import infrastructure.servicios.ocr.ModeloOCRParser;
import infrastructure.servicios.ocr.OCRService;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.*;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;

public class OCRTest {

    @Test
    void testOCRParaCadaModeloOCR() throws Exception {

        // 1) Verificar carpeta de modelos
        Path modelosDir = Paths.get(_Ruta.MODELOSOCR.getRuta());
        assertTrue(Files.exists(modelosDir), "No existe la carpeta de modelos OCR");

        ModeloOCRParser parser = new ModeloOCRParser();
        OCRService ocrService = new OCRService();   // <-- Tesseract aquí

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(modelosDir, "modelo_*.json")) {

            for (Path modeloPath : stream) {

                System.out.println("\n=== Probando modelo: " + modeloPath + " ===");

                // 2) Parsear modelo
                ModeloOCR modelo = parser.parse(modeloPath.toFile());

                assertNotNull(modelo, "El modelo no debe ser null");
                assertNotNull(modelo.rutaImagen(), "El modelo no tiene rutaImagen");
                assertFalse(modelo.zonas().isEmpty(), "El modelo no tiene ROIs");

                // 3) Aplicar OCR
                Map<String, String> resultado = aplicarOCR(modelo, ocrService);

                assertFalse(resultado.isEmpty(), "El OCR no devolvió resultados");

                // 4) Mostrar resultados
                resultado.forEach((campo, texto) -> {
                    assertNotNull(texto, "OCR devolvió null en campo: " + campo);
                    System.out.println(campo + " → " + texto);
                });

                System.out.println("=== Modelo OK ===");
            }
        }
    }

    // ============================================================
    // APLICAR OCR A UN MODELO COMPLETO (TESSERACT)
    // ============================================================
    public Map<String, String> aplicarOCR(ModeloOCR modelo, OCRService ocrService) throws Exception {

        // 1) Cargar imagen
        File imgFile = new File(modelo.rutaImagen());

        System.out.println("\n=== Imagen del modelo ===");
        System.out.println("Ruta imagen: " + imgFile.getAbsolutePath());
        System.out.println("Existe: " + imgFile.exists());
        System.out.println("Tamaño: " + imgFile.length());
        System.out.println("\n----------------------------");
        System.out.println("ENCODING TEST = " + System.getProperty("file.encoding"));

        System.out.println("----------------------------");

        assertTrue(imgFile.exists(), "La imagen no existe: " + modelo.rutaImagen());
        assertTrue(imgFile.length() > 0, "La imagen está vacía: " + modelo.rutaImagen());

        BufferedImage imagen = ImageIO.read(imgFile);
        assertNotNull(imagen, "No se pudo cargar la imagen");

        Map<String, String> resultados = new LinkedHashMap<>();

        // 2) Recorrer ROIs
        for (Map.Entry<String, ROI> entry : modelo.zonas().entrySet()) {

            String campo = entry.getKey();
            ROI roi = entry.getValue();

            BufferedImage recorte = imagen.getSubimage(
                    (int) roi.x1(),
                    (int) roi.y1(),
                    (int) (roi.x2() - roi.x1()),
                    (int) (roi.y2() - roi.y1()));

            // 3) OCR con Tesseract
            String texto = ocrService.ocr(recorte); // <-- tu método real
            resultados.put(campo, texto != null ? texto.trim() : "");
        }

        return resultados;
    }

  private static String rot13(String s) {
    StringBuilder sb = new StringBuilder();
    for (char c : s.toCharArray()) {
      if (c >= 'a' && c <= 'z') {
        sb.append((char) ((c - 'a' + 13) % 26 + 'a'));
      } else if (c >= 'A' && c <= 'Z') {
        sb.append((char) ((c - 'A' + 13) % 26 + 'A'));
      } else {
        sb.append(c);
      }
    }
    return sb.toString();
  }

  private static String bytesHex(String s) {
    StringBuilder sb = new StringBuilder();
    for (byte b : s.getBytes()) {
      sb.append(String.format("%02X ", b));
    }
    return sb.toString();
  }
}
