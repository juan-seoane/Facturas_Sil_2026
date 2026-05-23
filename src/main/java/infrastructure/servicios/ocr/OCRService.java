package infrastructure.servicios.ocr;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Locale;
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
      datapath = "D:/Program Files/JavaJDK/Tesseract_OCR";
    } else {
      datapath = "????"; //TODO : 26/02/22 : poner la ruta de tessdata en linux
    }

    t.setDatapath(datapath);
    t.setLanguage("spa");

    // Mejoras de precisión
    t.setOcrEngineMode(1); // LSTM
    t.setPageSegMode(6); // Bloque de texto
    t.setVariable("user_defined_dpi", "300");
    //t.setVariable("tessedit_char_whitelist", "0123456789.,€"); // whitelist para números y símbolo de euro
    //t.setVariable("classify_bln_numeric_mode", "1"); //sólo lector numérico
  }

  /** OCR de archivo completo */
  public String leerImagen(String ruta) throws TesseractException {
    return t.doOCR(new File(ruta));
  }

  /** OCR de ROI dentro de un archivo */
  public String leerROI(String rutaImagen, int x, int y, int w, int h) throws Exception {
    BufferedImage img = javax.imageio.ImageIO.read(new File(rutaImagen));
    BufferedImage sub = img.getSubimage(x, y, w, h);
    return normalizar(t.doOCR(sub));
  }

  /** OCR directo de BufferedImage (para ROIEditor) */
  public String ocr(BufferedImage img) throws TesseractException {
      String res = t.doOCR(img);
      System.out.println("[OCRService>ocr] OCR RESULT:\n" + res);
      return res;
  }

  public String normalizar(String txt) {
    if (txt == null) return "";

    // Limpieza básica
    String raw = txt.trim().replace("€", "").replace(",", ".");

    // ¿Es un número válido?
    // Permitimos: 123, 123.4, 123.456, etc.
    if (raw.matches("[0-9]+(\\.[0-9]+)?")) {

      // Convertir a double
      try {
        double value = Double.parseDouble(raw);

        // Si tiene más de 2 decimales → redondear
        int idx = raw.indexOf('.');
        if (idx != -1 && raw.length() - idx - 1 > 2) {
          return String.format(Locale.US, "%.2f", value);
        }

        // Si tiene 0–2 decimales → devolver tal cual
        return raw;
      } catch (Exception e) {
        // Si falla, tratamos como texto normal
        return txt.toUpperCase();
      }
    }

    // Si NO es un número → devolver en mayúsculas
    return txt.toUpperCase();
  }
}
