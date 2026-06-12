package com.sil.facturas.infrastructure.servicios.ocr.aux_ocr;

import com.sil.facturas.domain.records.ROI;
import com.sil.facturas.infrastructure.records.Rect;
import com.sil.facturas.infrastructure.servicios.ocr.ModeloOCR;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class ModeloOCRVisualizer {

  public static void dibujarModelo(ModeloOCR modelo, File imagenEntrada, File imagenSalida)
      throws Exception {

    BufferedImage img = ImageIO.read(imagenEntrada);
    Graphics2D g = img.createGraphics();

    g.setStroke(new BasicStroke(2));
    g.setFont(new Font("Arial", Font.BOLD, 18));

    for (Bloque b : modelo.bloques) {

      // DIBUJAR BLOQUE
      g.setColor(Color.RED);
      dibujarRect(g, b.zona);
      g.drawString(b.nombre, (int) b.zona.x1(), (int) b.zona.y1() - 5);

      // DIBUJAR CABECERA (si existe)
      if (b.cabecera != null) {
        g.setColor(Color.BLUE);
        dibujarRect(g, b.cabecera);
        g.drawString(b.nombre + ".cabecera", (int) b.cabecera.x1(), (int) b.cabecera.y1() - 5);
      }

      // DIBUJAR LINEA BASE
      if (b.lineaBase != null) {
        g.setColor(Color.GREEN);
        dibujarRect(g, b.lineaBase);
        g.drawString(b.nombre + ".lineaBase", (int) b.lineaBase.x1(), (int) b.lineaBase.y1() - 5);
      }

      // DIBUJAR FINAL EXTRACTOS
      if (b.finalExtractos != null) {
        g.setColor(Color.MAGENTA);
        dibujarRect(g, b.finalExtractos);
        g.drawString(
            b.nombre + ".finalExtractos",
            (int) b.finalExtractos.x1(),
            (int) b.finalExtractos.y1() - 5);
      }

      // DIBUJAR CAMPOS
      g.setColor(Color.ORANGE);
      for (Campo c : b.campos) {
        dibujarRect(g, c.zona);
        g.drawString(c.nombre, (int) c.zona.x1(), (int) c.zona.y1() - 5);
      }
    }

    g.dispose();
    ImageIO.write(img, "png", imagenSalida);
  }

  public static void dibujarROIs(BufferedImage img, java.util.List<ROI> rois) {
    Graphics2D g = img.createGraphics();
    g.setColor(Color.RED);
    g.setStroke(new BasicStroke(2));

    for (ROI roi : rois) {
      g.drawRect(
          (int) roi.x1(), (int) roi.y1(), (int) (roi.x2() - roi.x1()), (int) (roi.y2() - roi.y1()));
    }

    g.dispose();
  }

  private static void dibujarRect(Graphics2D g, Rect r) {
    g.drawRect((int) r.x1(), (int) r.y1(), (int) r.width(), (int) r.height());
  }
}
