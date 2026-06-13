package com.sil.facturas.infrastructure.servicios.ocr;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;

import com.sil.facturas.domain.enums._Colores;
import com.sil.facturas.domain.interfaces.IDebugService;
import com.sil.facturas.domain.pojos.Extracto;
import com.sil.facturas.domain.pojos.Factura;
import com.sil.facturas.domain.records.ROI;
import com.sil.facturas.infrastructure.servicios.ocr.aux_ocr.ModeloOCRParser;
import com.sil.facturas.infrastructure.servicios.ocr.aux_ocr.ModeloOCRVisualizer;

import javafx.embed.swing.JFXPanel;
import javafx.scene.image.Image;

public class OCRTest {

    // ============================================================
    // TEST 1: Ejecutar OCR para cada modelo OCR del classpath
    // ============================================================
    @Test
    void testOCRParaCadaModeloOCR() throws Exception {

        IDebugService.print(_Colores.YELLOW, "----------------------\n--------TEST1---------\n----------------------");
        // 1) Localizar carpeta de modelos OCR en el classpath
        URL url = getClass().getResource("/data/datos/modelosOCR");
        assertNotNull(url, "No existe la carpeta de modelos OCR en el classpath");

        File modelosDir = new File(url.toURI());
        assertTrue(modelosDir.isDirectory(), "modelosOCR no es un directorio");

        // 2) Listar modelos OCR
        File[] modelos = modelosDir.listFiles(
            (d, name) -> name.startsWith("modelo_") && name.endsWith(".json")
        );

        assertNotNull(modelos);
        assertTrue(modelos.length > 0, "No se encontraron modelos OCR");

        ModeloOCRParser parser = new ModeloOCRParser();
        OCRService ocrService = new OCRService();

        // 3) Procesar cada modelo
        for (File modeloFile : modelos) {

            System.out.println("\n=== Probando modelo: " + modeloFile.getName() + " ===");

            ModeloOCR modelo = parser.parse(modeloFile);

            assertNotNull(modelo, "El modelo no debe ser null");
            assertNotNull(modelo.rutaImagen(), "El modelo no tiene rutaImagen");
            assertFalse(modelo.zonas().isEmpty(), "El modelo no tiene ROIs");

            // 4) Aplicar OCR
            Map<String, String> resultado = aplicarOCR(modelo, ocrService);

            assertFalse(resultado.isEmpty(), "El OCR no devolvió resultados");

            // 5) Mostrar resultados
            resultado.forEach((campo, texto) -> {
                assertNotNull(texto, "OCR devolvió null en campo: " + campo);
                IDebugService.print(campo + " → " + texto);
            });

            IDebugService.print("=== Modelo OK ===");
        }
    }

    // ============================================================
    // TEST 2: Leer extractos de un modelo concreto
    // ============================================================
    @Test
    public void testLeerExtractos() throws Exception {
        IDebugService.print(_Colores.YELLOW, "----------------------\n--------TEST2---------\n----------------------");
        new JFXPanel(); // Inicializa JavaFX

        // 1. Cargar modelo desde JSON
        ModeloOCRParser parser = new ModeloOCRParser();
        URL url = getClass().getResource("/data/datos/modelosOCR/modelo_test1B.json");
        assertNotNull(url, "No se encontró el modelo OCR en resources");

        ModeloOCR modelo = parser.parse(new File(url.toURI()));

        // 2. Crear servicio OCR
        ModeloOCRService service = new ModeloOCRService();

        // 3. Cargar imagen base
        URL urlImg = getClass().getResource("/data/datos/ADMIN/scans/test1.png");
        assertNotNull(urlImg, "No se encontró la imagen test1.png");

        Image img = new Image(urlImg.toExternalForm());
        service.setImagenBase(img);

        // 4. Dibujar ROIs
        List<ROI> rois = service.generarROIsLineasExtracto(modelo);

        URL urlImg2 = getClass().getResource("/data/datos/ADMIN/scans/test1.png");
        assertNotNull(urlImg2);

        BufferedImage img2 = ImageIO.read(urlImg2);
        ModeloOCRVisualizer.dibujarROIs(img2, rois);

        // Guardar en carpeta temporal
        File out = new File("target/debug_extractos.png");
        ImageIO.write(img2, "png", out);


        // 5. Llamar a leerExtractos
        List<Extracto> extractos = service.generarExtractos(modelo);

        // 6. Mostrar resultados
        IDebugService.print(_Colores.YELLOW,"Extractos detectados: " + extractos.size());
    }

    // ============================================================
    // TEST 3: Convertir en Factura una imagen con un modelo concreto
    // ============================================================
    @Test
    public void testImagenAFactura() throws Exception {

        // ============================
        // 1. Cargar imagen
        // ============================
        InputStream is = getClass().getResourceAsStream(
                "/data/datos/ADMIN/scans/test1.png");
        assertNotNull(is, "No se encontró la imagen en el classpath");

        BufferedImage img = ImageIO.read(is);
        assertNotNull(img);

        // ============================
        // 2. Cargar modelo OCR
        // ============================
        ModeloOCRParser parser = new ModeloOCRParser();
        InputStream json = getClass().getResourceAsStream(
                "/data/datos/modelosOCR/modelo_test1B.json");
        assertNotNull(json, "No se encontró el modelo OCR modelo_test1B.json en resources");

        ModeloOCR modelo = parser.parse(json);
        assertNotNull(modelo);

        // ============================
        // 3. Crear servicios OCR
        // ============================
        OCRService ocr = new OCRService();
        ModeloOCRService modeloService = new ModeloOCRService();

        // ============================
        // 4. Construir Factura desde OCR
        // ============================
        FacturaBuilderOCR builder = new FacturaBuilderOCR(ocr, parser, modeloService);

        Factura factura = builder.construirFactura(img, modelo);
        IDebugService.print(_Colores.YELLOW, factura.toString());
        assertNotNull(factura);

        // ============================
        // 5. Asserts de extractos
        // ============================
        IDebugService.print("-------COMPROBACIONES DE EXTRACTOS-------");
        assertFalse(factura.getExtractos().isEmpty(),
                "La factura debe tener extractos");

        // Ejemplo: comprobar primer extracto
        var e1 = factura.getExtractos().get(0);
        assertTrue(e1.getConcepto().length() > 0);
        assertTrue(e1.getCantidad() >= 0);
        IDebugService.print("--------------EXTRACTOS OK!--------------");
        // ============================
        // 6. Asserts de totales
        // ============================
        
        IDebugService.print("--------COMPROBACIONES DE TOTALES--------");
        assertNotNull(factura.getTotales());
        assertTrue(factura.getTotales().getTotal() > 0);
        IDebugService.print("---------------TOTALES OK!---------------");

        // Ejemplo de comprobación concreta
        // assertEquals(123.45, factura.getTotales().getTotal(), 0.01);
    }
    
    // ============================================================
    // APLICAR OCR A UN MODELO COMPLETO (TESSERACT)
    // ============================================================
    public Map<String, String> aplicarOCR(ModeloOCR modelo, OCRService ocrService) throws Exception {

        // 1) Cargar imagen desde ruta física del modelo
        URL urlImg = getClass().getResource(modelo.rutaImagen());
		assertNotNull(urlImg, "No se encontró la imagen del modelo: " + modelo.rutaImagen());
		File imgFile = new File(urlImg.toURI());

        IDebugService.print(_Colores.YELLOW,"\n=== Imagen del modelo ===");
        IDebugService.print(_Colores.YELLOW,"Ruta imagen: " + imgFile.getAbsolutePath());
        IDebugService.print(_Colores.YELLOW,"Existe: " + imgFile.exists());
        IDebugService.print(_Colores.YELLOW,"Tamaño: " + imgFile.length());
        IDebugService.print(_Colores.YELLOW,"----------------------------");

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
            IDebugService.print("ROI: x1=" + roi.x1() + " y1=" + roi.y1() +
                                " x2=" + roi.x2() + " y2=" + roi.y2());

            BufferedImage recorte = imagen.getSubimage(
                (int) roi.x1(),
                (int) roi.y1(),
                (int) (roi.x2() - roi.x1()),
                (int) (roi.y2() - roi.y1())
            );

            // 3) OCR con Tesseract
            String texto = ocrService.ocr(recorte);
            resultados.put(campo, texto != null ? texto.trim() : "");
        }

        return resultados;
    }
}
