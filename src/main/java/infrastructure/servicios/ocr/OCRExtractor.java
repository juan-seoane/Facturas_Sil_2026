package infrastructure.servicios.ocr;

import domain.records.ROI;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import net.sourceforge.tess4j.Tesseract;

public class OCRExtractor {

  private final Tesseract t;

  public OCRExtractor() {
    t = new Tesseract();

    String os = System.getProperty("os.name").toLowerCase();
    String datapath;

    if (os.contains("win")) {
      datapath = "D:/Program Files/JavaJDK/Tesseract_OCR/"; // carpeta que contiene tessdata/
    } else {
      datapath = "/usr/share/tesseract-ocr/5/";
    }

    t.setDatapath(datapath);
    t.setLanguage("spa");
    t.setOcrEngineMode(1); // LSTM
    t.setPageSegMode(6); // bloque de texto
    t.setVariable("user_defined_dpi", "300");
  }

  /** OCR de un ROI dentro de una imagen */
  public String extraerROI(String rutaImagen, ROI roi) throws Exception {

    BufferedImage original = ImageIO.read(new File(rutaImagen));

    BufferedImage sub =
        original.getSubimage((int) roi.x1(), (int) roi.y1(), (int) (roi.x2() - roi.x1()), (int) (roi.y2() - roi.y1()));

    return t.doOCR(sub).trim();
  }
}
