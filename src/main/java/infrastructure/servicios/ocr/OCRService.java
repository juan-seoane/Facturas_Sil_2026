package infrastructure.servicios.ocr;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Locale;

import domain.records.ROI;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class OCRService {

    private final Tesseract t;

    public OCRService() {
        t = new Tesseract();

        String os = System.getProperty("os.name").toLowerCase();
        String datapath;

        if (os.contains("win")) {
            datapath = "D:/Program Files/JavaJDK/Tesseract_OCR";
        } else {
            datapath = "/usr/share/tesseract-ocr/5/tessdata";
        }

        t.setDatapath(datapath);
        t.setLanguage("spa");

        t.setOcrEngineMode(1); // LSTM
        t.setPageSegMode(6);   // Bloque de texto
        t.setVariable("user_defined_dpi", "300");
    }

    // ============================
    // OCR DE ARCHIVO COMPLETO
    // ============================
    public String leerImagen(String ruta) throws TesseractException {
        return t.doOCR(new File(ruta));
    }

    // ============================
    // OCR DE ROI EN ARCHIVO
    // ============================
    public String leerROI(String rutaImagen, int x, int y, int w, int h) throws Exception {
        BufferedImage img = javax.imageio.ImageIO.read(new File(rutaImagen));
        BufferedImage sub = img.getSubimage(x, y, w, h);
        return normalizar(t.doOCR(sub));
    }

    // ============================
    // OCR DE ROI JavaFX (Editor)
    // ============================
    public String ocrROIJavaFX(Image imagenBase, Rectangle rectFX, double viewW, double viewH) {
        try {
            BufferedImage bimg = SwingFXUtils.fromFXImage(imagenBase, null);

            double scaleX = bimg.getWidth() / viewW;
            double scaleY = bimg.getHeight() / viewH;

            int x = (int) (rectFX.getX() * scaleX);
            int y = (int) (rectFX.getY() * scaleY);
            int w = (int) (rectFX.getWidth() * scaleX);
            int h = (int) (rectFX.getHeight() * scaleY);

            BufferedImage sub = bimg.getSubimage(x, y, w, h);

            return normalizar(t.doOCR(sub));

        } catch (Exception e) {
            e.printStackTrace();
            return "[ERROR OCR] " + e.getMessage();
        }
    }

    // ============================
    // OCR DE ROI REAL (ModeloOCR)
    // ============================
    public String ocrROIReal(Image imagenBase, ROI roiReal) {
    try {
        BufferedImage bimg = SwingFXUtils.fromFXImage(imagenBase, null);

        int x = (int) Math.round(roiReal.x1());
        int y = (int) Math.round(roiReal.y1());
        int w = (int) Math.round(roiReal.x2() - roiReal.x1());
        int h = (int) Math.round(roiReal.y2() - roiReal.y1());

        BufferedImage sub = bimg.getSubimage(x, y, w, h);

        return normalizar(t.doOCR(sub));

    } catch (Exception e) {
        e.printStackTrace();
        return "[ERROR OCR] " + e.getMessage();
    }
}

    // ============================
    // OCR DIRECTO
    // ============================
    public String ocr(BufferedImage img) throws TesseractException {
        return t.doOCR(img);
    }

    // ============================
    // NORMALIZACIÓN
    // ============================
    public String normalizar(String txt) {
        if (txt == null) return "";

        String raw = txt.trim().replace("€", "").replace(",", ".");

        if (raw.matches("[0-9]+(\\.[0-9]+)?")) {
            try {
                double value = Double.parseDouble(raw);

                int idx = raw.indexOf('.');
                if (idx != -1 && raw.length() - idx - 1 > 2) {
                    return String.format(Locale.US, "%.2f", value);
                }

                return raw;
            } catch (Exception e) {
                return txt.toUpperCase();
            }
        }

        return txt.toUpperCase();
    }
}
