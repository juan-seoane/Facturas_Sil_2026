package app;

import static org.junit.jupiter.api.Assertions.*;

import domain.src.main.java.com.sil.facturas.domain.records.Extracto;
import domain.src.main.java.com.sil.facturas.domain.records.Factura;
import domain.src.main.java.com.sil.facturas.domain.records.ROI;
import infrastructure.src.main.java.com.sil.facturas.infrastructure.filesystem._Ruta;
import infrastructure.src.main.java.com.sil.facturas.infrastructure.servicios.ocr.FacturaBuilder;
import infrastructure.src.main.java.com.sil.facturas.infrastructure.servicios.ocr.ModeloOCR;
import infrastructure.src.main.java.com.sil.facturas.infrastructure.servicios.ocr.ModeloOCRService;
import infrastructure.src.main.java.com.sil.facturas.infrastructure.servicios.ocr.OCRService;
import infrastructure.src.main.java.com.sil.facturas.infrastructure.servicios.ocr.aux_ocr.ModeloOCRParser;
import infrastructure.src.main.java.com.sil.facturas.infrastructure.servicios.ocr.aux_ocr.ModeloOCRVisualizer;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javafx.embed.swing.JFXPanel;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;

public class OCRTest {

  @Test
  void testOCRParaCadaModeloOCR() throws Exception {

    // 1) Verificar carpeta de modelos
    Path modelosDir = Paths.get(_Ruta.MODELOSOCR.getRuta());
    assertTrue(Files.exists(modelosDir), "No existe la carpeta de modelos OCR");

    ModeloOCRParser parser = new ModeloOCRParser();
    OCRService ocrService = new OCRService(); // <-- Tesseract aquí

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
        resultado.forEach(
            (campo, texto) -> {
              assertNotNull(texto, "OCR devolvió null en campo: " + campo);
              IDebugService.print(campo + " → " + texto);
            });

        IDebugService.print("=== Modelo OK ===");
      }
    }
  }

  @Test
  public void testFacturaDesdeImagen() throws Exception {

    // 1. Cargar modelo OCR
    ModeloOCRParser parser = new ModeloOCRParser();
    ModeloOCR modelo = parser.parse(new File("./data/datos/modelosOCR/modelo_test1A.json"));

    // 2. Cargar imagen
    Image img = new Image(new FileInputStream("./data/datos/ADMIN/scans/test1.png"));

    // 3. Ejecutar OCR por zonas
    OCRService ocr = new OCRService();
    Map<String, String> textos = ocr.ocrPorZonas(img, modelo);

    // 4. Construir Factura
    Factura factura = FacturaBuilder.fromOCR(textos);

    // 5. Validaciones
    assertEquals("165.00", format2(factura.getTotales().getBase()));
    assertEquals("21.00", format2(factura.getTotales().getTipoIVA()));
    assertEquals("34.65", format2(factura.getTotales().getIVA()));
    assertEquals("199.65", format2(factura.getTotales().getTotal()));

    assertEquals(3, factura.getExtractos().size());
    Extracto e = factura.getExtractos().get(0);

    assertEquals("1", e.getCantidad());
    // assertEquals("TALLA PEQUEÑA TRAJE DE LUCES EN ROJO", e.getConcepto());
    assertEquals("100.00", format2(e.getPrecioUnitario()));
    assertEquals("100.00", format2(e.getSubtotal()));
  }

  @Test
  public void testLeerExtractos() throws Exception {
    // 0. Inicializar JavaFX
    // Inicializa JavaFX
    new JFXPanel(); // ← Esto arranca el toolkit JavaFX

    // 1. Cargar modelo desde JSON
    ModeloOCRParser parser = new ModeloOCRParser();
    ModeloOCR modelo = parser.parse(new File("data/datos/modelosOCR/modelo_test1B.json"));

    // 2. Crear servicio OCR
    ModeloOCRService service = new ModeloOCRService();

    // 3. Cargar imagen base
    Image img = new Image("file:data/datos/ADMIN/scans/test1.png");
    service.setImagenBase(img);

    // 4. Dibujar ROIs
    List<ROI> rois = service.generarROIsLineasExtracto(modelo);
    BufferedImage img2 = ImageIO.read(new File("data/datos/ADMIN/scans/test2.png"));
    ModeloOCRVisualizer.dibujarROIs(img2, rois);
    ImageIO.write(img2, "png", new File("data/datos/ADMIN/debug/debug_extractos.png"));

    // 5. Llamar a leerExtractos
    List<Extracto> extractos = service.generarExtractos(modelo);

    // 6. Mostrar resultados
    System.out.println("Extractos detectados: " + extractos.size());
    extractos.forEach(System.out::println);
  }

  @Test
  public void testVisualizarModeloOCR() throws Exception {

    // 1. Cargar el JSON estructurado
    ModeloOCRParser parser = new ModeloOCRParser();
    ModeloOCR modelo = parser.parse(new File("data/datos/modelosOCR/modelo_test1B.json"));
    IDebugService.print("[OCRTest>testVisualizarModeloOCR] Lista de zonas:");
    modelo.zonas().forEach((s, roi) -> IDebugService.print(s + " -> " + roi));
    IDebugService.print(
        "[OCRTest>testVisualizarModeloOCR] alturaLineaAprox = " + modelo.alturaLineaAprox());

    // 2. Cargar la imagen real
    File imagenFactura = new File("data/datos/ADMIN/scans/test1.png");

    // 3. Archivo de salida para depuración
    File salida = new File("data/datos/ADMIN/debug/debug_modelo_test1B.png");

    // 4. Dibujar bloques y campos sobre la imagen
    ModeloOCRVisualizer.dibujarModelo(modelo, imagenFactura, salida);

    System.out.println("Imagen generada: " + salida.getAbsolutePath());
  }

  // ============================================================
  // APLICAR OCR A UN MODELO COMPLETO (TESSERACT)
  // ============================================================
  public Map<String, String> aplicarOCR(ModeloOCR modelo, OCRService ocrService) throws Exception {

    // 1) Cargar imagen
    File imgFile = new File(modelo.rutaImagen());

    IDebugService.print("\n=== Imagen del modelo ===");
    IDebugService.print("Ruta imagen: " + imgFile.getAbsolutePath());
    IDebugService.print("Existe: " + imgFile.exists());
    IDebugService.print("Tamaño: " + imgFile.length());
    IDebugService.print("\n----------------------------");
    IDebugService.print("ENCODING TEST = " + System.getProperty("file.encoding"));

    IDebugService.print("----------------------------");

    assertTrue(imgFile.exists(), "La imagen no existe: " + modelo.rutaImagen());
    assertTrue(imgFile.length() > 0, "La imagen está vacía: " + modelo.rutaImagen());

    BufferedImage imagen = ImageIO.read(imgFile);
    assertNotNull(imagen, "No se pudo cargar la imagen");

    Map<String, String> resultados = new LinkedHashMap<>();

    // 2) Recorrer ROIs
    for (Map.Entry<String, ROI> entry : modelo.zonas().entrySet()) {

      String campo = entry.getKey();
      ROI roi = entry.getValue();
      IDebugService.print("\n--- Procesando campo: " + campo + " ---");
      IDebugService.print(
          "ROI: x1=" + roi.x1() + " y1=" + roi.y1() + " x2=" + roi.x2() + " y2=" + roi.y2());
      BufferedImage recorte =
          imagen.getSubimage(
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

  public static String format2(double n) {
    return String.format(java.util.Locale.US, "%.2f", n);
  }
}
