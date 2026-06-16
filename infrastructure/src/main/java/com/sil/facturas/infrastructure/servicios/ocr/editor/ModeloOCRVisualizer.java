package com.sil.facturas.infrastructure.servicios.ocr.editor;

import com.sil.facturas.domain.records.ROI;
import com.sil.facturas.infrastructure.records.Rect;
import com.sil.facturas.infrastructure.servicios.ocr.ModeloOCR;
import com.sil.facturas.infrastructure.servicios.ocr.aux_ocr.Bloque;
import com.sil.facturas.infrastructure.servicios.ocr.aux_ocr.Campo;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Map;
import javax.imageio.ImageIO;

public class ModeloOCRVisualizer {

  public static void dibujarModelo(ModeloOCR modelo, File imagenEntrada, File imagenSalida)
      throws Exception {

    BufferedImage img = ImageIO.read(imagenEntrada);
    Graphics2D g = img.createGraphics();

    g.setStroke(new BasicStroke(2));
    g.setFont(new Font("Arial", Font.BOLD, 18));

    // ============================
    // 1. DIBUJAR BLOQUES
    // ============================
    for (Bloque b : modelo.bloques) {

      // BLOQUE
      g.setColor(Color.RED);
      dibujarRect(g, b.zona);
      g.drawString(b.nombre, (int) b.zona.x1(), (int) b.zona.y1() - 5);

      // CABECERA
      if (b.cabecera != null) {
        g.setColor(Color.BLUE);
        dibujarRect(g, b.cabecera);
        g.drawString(b.nombre + ".cabecera", (int) b.cabecera.x1(), (int) b.cabecera.y1() - 5);
      }

      // LINEA BASE
      if (b.lineaBase != null) {
        g.setColor(Color.GREEN);
        dibujarRect(g, b.lineaBase);
        g.drawString(b.nombre + ".lineaBase", (int) b.lineaBase.x1(), (int) b.lineaBase.y1() - 5);
      }

      // FINAL EXTRACTOS
      if (b.finalExtractos != null) {
        g.setColor(Color.MAGENTA);
        dibujarRect(g, b.finalExtractos);
        g.drawString(
            b.nombre + ".finalExtractos",
            (int) b.finalExtractos.x1(),
            (int) b.finalExtractos.y1() - 5);
      }

      // CAMPOS DEL BLOQUE
      g.setColor(Color.ORANGE);
      for (Campo c : b.campos) {
        dibujarRect(g, c.zona());
        g.drawString(c.nombre, (int) c.zona().x1(), (int) c.zona().y1() - 5);
      }
    }

    // ============================
    // 2. DIBUJAR ZONAS SUELTAS (ROIs del builder)
    // ============================
    g.setColor(Color.CYAN);
    for (Map.Entry<String, ROI> entry : modelo.zonas().entrySet()) {
      ROI roi = entry.getValue();
      dibujarRect(g, roi);
      g.drawString(entry.getKey(), (int) roi.x1(), (int) roi.y1() - 5);
    }

    // // ============================
    // // 3. DIBUJAR CAMPOS SUELTOS (si existen)
    // // ============================
    // if (modelo.campos() != null) {
    //   g.setColor(Color.YELLOW);
    //   for (Campo c : modelo.campos()) {
    //     dibujarRect(g, c.zona());
    //     g.drawString(c.nombre, (int) c.zona().x1(), (int) c.zona().y1() - 5);
    //   }
    // }

    g.dispose();
    ImageIO.write(img, "png", imagenSalida);
  }

  private static void dibujarRect(Graphics2D g, Rect r) {
    g.drawRect((int) r.x1(), (int) r.y1(), (int) r.width(), (int) r.height());
  }

  private static void dibujarRect(Graphics2D g, ROI roi) {
    g.drawRect(
        (int) roi.x1(), (int) roi.y1(), (int) (roi.x2() - roi.x1()), (int) (roi.y2() - roi.y1()));
  }
}
