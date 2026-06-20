package com.sil.facturas.infrastructure.services.ocr;

import static com.sil.facturas.domain.interfaces.IDebugService.*;
import static org.junit.jupiter.api.Assertions.*;

import com.sil.facturas.domain.enums._Colores;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;

public class OCRServiceTest {

  @Test
  void testOCRBasico() throws Exception {

    // 1. Asegurar que JNA encuentra las DLL
    System.setProperty("jna.library.path", "C:/Program Files/Tesseract-OCR");

    // 2. Cargar imagen
    BufferedImage img = ImageIO.read(getClass().getResourceAsStream("/ocr/imagenes/factura1.png"));

    assertNotNull(img);

    // 3. Crear OCRService
    OCRService ocr = new OCRService();

    // 4. Ejecutar OCR
    String texto = ocr.ocr(img);

    assertNotNull(texto);
    assertFalse(texto.isBlank());

    print(_Colores.YELLOW,texto);
  }
}
