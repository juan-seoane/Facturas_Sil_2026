package app;

import static org.junit.jupiter.api.Assertions.*;


import domain.records.ROI;
import infrastructure.filesystem._Ruta;
import infrastructure.servicios.ocr.ModeloOCR;
import infrastructure.servicios.ocr.ModeloOCRParser;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.*;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;

public class TestOCR {

    // @Test
    // void testOCRParaCadaModeloOCR() throws Exception {

        // 1) Verificar que la carpeta de modelos OCR existe

    //     Path modelosDir = Paths.get(_Ruta.MODELOSOCR.getRuta());
    //     assertTrue(Files.exists(modelosDir), "No existe la carpeta de modelos OCR");

    //     ModeloOCRParser parser = new ModeloOCRParser();

    //     try (DirectoryStream<Path> stream = Files.newDirectoryStream(modelosDir, "modelo_*.json")) {

    //     for (Path modeloPath : stream) {

    //         System.out.println("\n=== Probando modelo: " + modeloPath + " ===");

    //         // 1) Parsear modelo
    //         ModeloOCR modelo = parser.parse(new File(modeloPath.toString()));

    //         System.out.println("JSON leído: " + modeloPath.toAbsolutePath());
    //         System.out.println("rutaImagen (del modelo): " + modelo.rutaImagen());

    //         assertNotNull(modelo, "El modelo no debe ser null");
    //         assertNotNull(modelo.rutaImagen(), "El modelo no tiene rutaImagen");
    //         assertFalse(modelo.zonas().isEmpty(), "El modelo no tiene ROIs");

    //         // 2) Aplicar OCR al modelo completo
    //         Map<String, String> resultado = aplicarOCR(modelo);

    //         assertFalse(resultado.isEmpty(), "El OCR no devolvió resultados");

    //         // 3) Mostrar resultados
    //         resultado.forEach(
    //             (campo, texto) -> {
    //             assertNotNull(texto, "OCR devolvió null en campo: " + campo);
    //             System.out.println(campo + " → " + texto);
    //             });

    //         System.out.println("=== Modelo OK ===");
    //     }
    //     }
    // }

    // // ============================================================
    // // APLICAR OCR A UN MODELO COMPLETO
    // // ============================================================
    // public Map<String, String> aplicarOCR(ModeloOCR modelo) throws Exception {

    //     // 1) Cargar imagen desde rutaImagen
    //     File imgFile = new File(modelo.rutaImagen());

    //     // 🔍 Mostrar información completa ANTES de leer la imagen
    //     System.out.println("\n=== Imagen del modelo ===");
    //     System.out.println("Ruta imagen: " + imgFile.getAbsolutePath());
    //     System.out.println("Existe: " + imgFile.exists());
    //     System.out.println("Tamaño: " + imgFile.length());

    //     assertTrue(imgFile.exists(), "La imagen no existe: " + modelo.rutaImagen());
    //     assertTrue(imgFile.length() > 0, "La imagen está vacía: " + modelo.rutaImagen());

    //     BufferedImage imagen = ImageIO.read(imgFile);
    //     assertNotNull(imagen, "No se pudo cargar la imagen (¿no es PNG válido?)");

    //     Map<String, String> resultados = new LinkedHashMap<>();

    //     // 2) Recorrer todas las zonas (ROIs)
    //     for (Map.Entry<String, ROI> entry : modelo.zonas().entrySet()) {

    //         String campo = entry.getKey();
    //         ROI roi = entry.getValue();

    //         BufferedImage recorte = imagen.getSubimage(roi.x1(), roi.y1(), roi.x2() - roi.x1(), roi.y2() - roi.y1());

    //         String texto = ocrAsprise(recorte);
    //         resultados.put(campo, texto);
    //     }

    //     return resultados;
    // }

    // private String ocrAsprise(BufferedImage img) throws Exception {

    //     // Crear archivo temporal donde guardar el recorte
    //     File temp = File.createTempFile("roi_", ".png");
    //     ImageIO.write(img, "png", temp);

    //     // Inicializar motor OCR
    //     Ocr.setUp();
    //     Ocr ocr = new Ocr();
    //     ocr.startEngine("spa", Ocr.SPEED_FASTEST);

    //     // Reconocer texto
    //     String texto =
    //         ocr.recognize(new File[] {temp},
    //                     Ocr.RECOGNIZE_TYPE_TEXT,
    //                     Ocr.OUTPUT_FORMAT_PLAINTEXT);

    //     // Parar motor y borrar archivo temporal
    //     ocr.stopEngine();
    //     temp.delete();

    //     return texto != null ? texto.trim() : "";
    // }

}
