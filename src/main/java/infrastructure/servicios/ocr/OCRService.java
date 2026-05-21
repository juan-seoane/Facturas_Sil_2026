package infrastructure.servicios.ocr;

import java.awt.image.BufferedImage;
import java.io.File;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class OCRService {

  private final Tesseract t;

  public OCRService() {
    t = new Tesseract();

    // Autodetectar sistema operativo
    String os = System.getProperty("os.name").toLowerCase();
    String datapath;

    if (os.contains("win")) {
      // IMPORTANTE: carpeta que CONTIENE tessdata/
      datapath = "D:/Program Files/JavaJDK/Tesseract_OCR/";
    } else {
      datapath = "/usr/share/tesseract-ocr/5/";
    }

    t.setDatapath(datapath);
    t.setLanguage("spa");

    // Mejoras de precisión
    t.setOcrEngineMode(1); // LSTM
    t.setPageSegMode(6); // Bloque de texto
    t.setVariable("user_defined_dpi", "300");
  }

  /** OCR de archivo completo */
  public String leerImagen(String ruta) throws TesseractException {
    return t.doOCR(new File(ruta));
  }

  /** OCR de ROI dentro de un archivo */
  public String leerROI(String rutaImagen, int x, int y, int w, int h) throws Exception {
    BufferedImage img = javax.imageio.ImageIO.read(new File(rutaImagen));
    BufferedImage sub = img.getSubimage(x, y, w, h);
    return t.doOCR(sub);
  }

  /** OCR directo de BufferedImage (para ROIEditor) */
  public String ocr(BufferedImage img) throws TesseractException {
    return t.doOCR(img);
  }
}
