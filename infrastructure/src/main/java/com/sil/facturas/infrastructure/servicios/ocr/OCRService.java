package com.sil.facturas.infrastructure.servicios.ocr;

import com.sil.facturas.domain.enums._Colores;
import com.sil.facturas.domain.interfaces.IDebugService;
import com.sil.facturas.domain.records.ROI;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    IDebugService.print(_Colores.BLUE,"[OCRService] Sistema operativo detectado: " + os);
    String datapath;

    if (os.contains("win")) {
      datapath = "C:/Program Files/Tesseract-OCR/tessdata";
      IDebugService.print(_Colores.BLUE,"[OCRService] Usando datapath para Windows: " + datapath);
    } else {
      datapath = "/usr/share/tesseract-ocr/5/tessdata";
      IDebugService.print(_Colores.BLUE,"[OCRService] Usando datapath para Linux: " + datapath);
    }

    t.setDatapath(datapath);
    t.setLanguage("spa");
    t.setOcrEngineMode(1); // LSTM
    // t.setPageSegMode(TessAPI.TessPageSegMode.PSM_AUTO);
    t.setVariable("user_defined_dpi", "600");
    // t.setVariable("tessedit_char_whitelist", "0123456789.");
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
    IDebugService.print("ROI REAL: x=" + x + " y=" + y + " w=" + w + " h=" + h);
    IDebugService.print("IMG REAL: w=" + img.getWidth() + " h=" + img.getHeight());

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
      return (_Colores.RED.getANSICode()
          + "[OCRService>ocrROIJavaFX] ERROR OCR: ROI vacío o demasiado pequeño"
          + _Colores.RESET.getANSICode());
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
      return (_Colores.RED.getANSICode()
          + "[ERROR OCRService>ocrROIJavaFX] "
          + e.getMessage()
          + _Colores.RESET.getANSICode());
    }
  }

  // ============================
  // OCR DE ROI REAL (ModeloOCR)
  // ============================
  public String ocrROIReal(Image imagenBase, ROI roiReal) {
    try {
      BufferedImage bimg = SwingFXUtils.fromFXImage(imagenBase, null);

      int x = (int) Math.floor(roiReal.x1());
      int y = (int) Math.floor(roiReal.y1());
      int w = (int) Math.floor(roiReal.x2() - roiReal.x1());
      int h = (int) Math.floor(roiReal.y2() - roiReal.y1());

      if (w <= 1 || h <= 1) {
        return "";
      }

      BufferedImage sub = bimg.getSubimage(x, y, w, h);

      return normalizar(t.doOCR(sub));

    } catch (Exception e) {
      return (_Colores.RED.getANSICode()
          + "[ERROR OCRService>ocrROIReal] "
          + e.getMessage()
          + _Colores.RESET.getANSICode());
    }
  }

  public List<String> ocrListaROIs(Image img, List<ROI> rois) {
    List<String> salida = new ArrayList<>();

    for (ROI roi : rois) {
      String texto = ocrROIReal(img, roi);
      if (texto != null && !texto.trim().isEmpty()) {
        salida.add(texto.trim());
      }
    }

    return salida;
  }

  public Map<String, String> ocrPorZonas(Image img, ModeloOCR modelo) {

    Map<String, String> salida = new HashMap<>();

    for (Map.Entry<String, ROI> entry : modelo.zonas().entrySet()) {

      String nombreZona = entry.getKey();
      ROI roi = entry.getValue();

      String texto = this.ocrROIReal(img, roi);
      salida.put(nombreZona, texto != null ? texto.trim() : "");
    }

    return salida;
  }

  // ============================
  // OCR DIRECTO
  // ============================
  public String ocr(BufferedImage img) throws TesseractException {
    return t.doOCR(img);
  }

  // ============================
  // NORMALIZACIÓN Y DEMÁS
  // ============================
  public static List<Double> extraerTodosLosNumeros(String linea) {
    List<Double> salida = new ArrayList<>();

    if (linea == null || linea.isEmpty()) {
        return salida;
    }

    // Normalizar comas → puntos
    String norm = linea.replace(",", ".");

    // Regex para números tipo:
    //  - 123
    //  - 123.45
    //  - .45
    //  - 123.
    Pattern p = Pattern.compile("(?<![A-Za-z])\\d*\\.?\\d+");
    Matcher m = p.matcher(norm);

    while (m.find()) {
        try {
            double v = Double.parseDouble(m.group());
            salida.add(v);
        } catch (Exception ignored) {
        }
    }

    return salida;
}

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

  public static String extraerNumeros(String raw) {
    if (raw == null) return "";

    // 1. Reemplazar comas por puntos para unificar
    String t = raw.replace(",", ".");

    // 2. Regex para capturar números con decimales opcionales
    Matcher m = Pattern.compile("(\\d+\\.\\d+|\\d+)").matcher(t);

    List<String> nums = new ArrayList<>();
    while (m.find()) {
      nums.add(m.group());
    }

    return nums.getLast();
  }

  public static String normalizarCifraOCR(String raw) {
    if (raw == null || raw.isBlank()) return "";

    // Unificar comas a puntos
    String t = raw.replace(",", ".").trim();

    // Extraer último número
    Matcher m = Pattern.compile("(\\d+\\.\\d+|\\d+)").matcher(t);
    String num = "";
    while (m.find()) num = m.group();

    if (num.isEmpty()) return "";

    // Si ya tiene punto decimal → devolver tal cual
    if (num.contains(".")) {
      return num;
    }

    // Si NO tiene punto y tiene más de 2 dígitos → OCR perdió el decimal
    if (num.length() > 2) {
      double v = Double.parseDouble(num) / 100.0;
      return String.format(Locale.US, "%.2f", v);
    }

    // Si es un número pequeño (1 o 2 dígitos) → devolver tal cual
    return num;
  }
}
