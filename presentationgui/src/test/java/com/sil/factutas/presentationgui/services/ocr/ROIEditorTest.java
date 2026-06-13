package com.sil.factutas.presentationgui.services.ocr;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;

import org.junit.jupiter.api.Test;

import com.sil.facturas.domain.interfaces.IDebugService;
import com.sil.facturas.infrastructure.servicios.ocr.ModeloOCR;
import com.sil.facturas.infrastructure.servicios.ocr.aux_ocr.ModeloOCRParser;
import com.sil.facturas.infrastructure.servicios.ocr.aux_ocr.ModeloOCRVisualizer;

public class ROIEditorTest {

    @Test
    public void testVisualizarModeloOCR() throws Exception {

        // 1. Cargar JSON desde el classpath
        URL urlModelo = getClass().getResource("/data/datos/modelosOCR/modelo_test1B.json");
        assertNotNull(urlModelo, "No se encontró modelo_test1B.json");

        // Copiar JSON a archivo temporal
        File modeloTemp = File.createTempFile("modelo_test1B", ".json");
        try (InputStream in = urlModelo.openStream()) {
            Files.copy(in, modeloTemp.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }

        ModeloOCRParser parser = new ModeloOCRParser();
        ModeloOCR modelo = parser.parse(modeloTemp);

        IDebugService.print("[ROIEditorTest] Zonas:");
        modelo.zonas().forEach((s, roi) -> IDebugService.print(s + " -> " + roi));

        // 2. Cargar imagen desde el classpath
        URL urlImagen = getClass().getResource("/data/datos/ADMIN/scans/test1.png");
        assertNotNull(urlImagen, "No se encontró test1.png");

        // Copiar imagen a archivo temporal
        File imagenTemp = File.createTempFile("imagen_test1", ".png");
        try (InputStream in = urlImagen.openStream()) {
            Files.copy(in, imagenTemp.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }

        // 3. Archivo de salida
        File salida = new File("target/debug_modelo_test1B.png");

        // 4. Dibujar modelo
        ModeloOCRVisualizer.dibujarModelo(modelo, imagenTemp, salida);

        System.out.println("Imagen generada: " + salida.getAbsolutePath());
    }
}
