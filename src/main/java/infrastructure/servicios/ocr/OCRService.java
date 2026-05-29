package infrastructure.servicios.ocr;

import domain.records.ROI;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Locale;
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
        System.out.println("[OCRService] Sistema operativo detectado: " + os);
        String datapath;

        if (os.contains("win")) {
            datapath = "C:/Program Files/Tesseract-OCR/tessdata";
            System.out.println("[OCRService] Usando datapath para Windows: " + datapath);
        } else {
            datapath = "/usr/share/tesseract-ocr/5/tessdata";
            System.out.println("[OCRService] Usando datapath para Linux: " + datapath);
        }

        t.setDatapath(datapath);
        t.setLanguage("spa");
        t.setOcrEngineMode(1); // LSTM
        //t.setPageSegMode(TessAPI.TessPageSegMode.PSM_AUTO);
        t.setVariable("user_defined_dpi", "600");
        //t.setVariable("tessedit_char_whitelist", "0123456789.");
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

        // Normalizar ROI para evitar width/height negativos
        Rectangle r = normalizar(rectFX);

        if (r.getWidth() <= 1 || r.getHeight() <= 1) {
            return "[OCRService>ocrROIJavaFX] ERROR OCR: ROI vacío o demasiado pequeño";
        }
        try {
            BufferedImage bimg = SwingFXUtils.fromFXImage(imagenBase, null);

            double scaleX = bimg.getWidth() / viewW;
            double scaleY = bimg.getHeight() / viewH;

            int x = (int) (r.getX() * scaleX);
            int y = (int) (r.getY() * scaleY);
            int w = (int) (r.getWidth() * scaleX);
            int h = (int) (r.getHeight() * scaleY);

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
    private Rectangle normalizar(Rectangle r) {
        double x1 = r.getX();
        double y1 = r.getY();
        double x2 = x1 + r.getWidth();
        double y2 = y1 + r.getHeight();

        double nx = Math.min(x1, x2);
        double ny = Math.min(y1, y2);
        double nw = Math.abs(r.getWidth());
        double nh = Math.abs(r.getHeight());

        return new Rectangle(nx, ny, nw, nh);
    }

    private String normalizar(String txt) {
        if (txt == null) return "";

        String raw = txt.trim().replace("€", "").replace(",", ".");

        // 1) Contar letras
        int letras = 0;
        for (char c : raw.toCharArray()) {
        if (Character.isLetter(c)) letras++;
        }

        // 2) Si hay 2+ letras → es texto
        if (letras >= 2) {
        return txt.toUpperCase();
        }

        // 3) Si hay saltos de línea → es texto
        if (raw.contains("\n") || raw.contains("\r")) {
        return txt.toUpperCase();
        }

        // 4) Intentar parsear como número si tiene 0 o 1 letra
        String soloNum = raw.replaceAll("[^0-9.]", "");

        if (soloNum.matches("[0-9]+(\\.[0-9]+)?")) {
        try {
            double value = Double.parseDouble(soloNum);

            // Si tiene más de 2 decimales → redondear
            int idx = soloNum.indexOf('.');
            if (idx != -1 && soloNum.length() - idx - 1 > 2) {
            return String.format(Locale.US, "%.2f", value);
            }

            return soloNum;

        } catch (Exception e) {
            return txt.toUpperCase();
        }
        }

        // 5) Si no es número → texto
        return txt.toUpperCase();
    }
}
