package com.sil.facturas.infrastructure.servicios.ocr;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sil.facturas.domain.enums._Colores;
import com.sil.facturas.domain.interfaces.IDebugService;
import com.sil.facturas.domain.pojos.Extracto;
import com.sil.facturas.domain.records.ROI;

import com.sil.facturas.infrastructure.servicios.ocr.aux_ocr.ModeloOCRParser;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ModeloOCRService {

  public final ModeloOCR.Builder builder;
  private final OCRService ocrService;
  private final ModeloOCRParser parser = new ModeloOCRParser();

  private Image imagenBase;
  public String rutaImagen;

  public ModeloOCRService() {
    this.builder = new ModeloOCR.Builder();
    IDebugService.print(
        ">>> [ModeloOCRService] NUEVO ModeloOCRServicebuilder: " + this.builder.hashCode());
    this.ocrService = new OCRService();
  }

  // ============================
  // INFO GENERAL DEL MODELO
  // ============================
  public void setInfoModelo(String rutaImagen, int dpi, String nombre, String version) {
    this.rutaImagen = rutaImagen;
    IDebugService.print(
        "[ModeloOCRService>setInfoModelo] Guardando Ruta imagen: " + this.rutaImagen);
    this.builder.rutaImagen(rutaImagen);
    this.builder.dpi(dpi);
    this.builder.nombre(nombre.toUpperCase());
    this.builder.version(version);
  }

  public void setImagenBase(Image img) {
    this.imagenBase = img;
  }

  // ============================
  // TESTEAR ROI
  // ============================
  public boolean isROIEmpty(Rectangle rect, ImageView imageView) {
    // Extraer snapshot del ROI
    WritableImage snapshot = imageView.snapshot(null, null);

    PixelReader pr = snapshot.getPixelReader();

    int x0 = (int) rect.getX();
    int y0 = (int) rect.getY();
    int w = (int) rect.getWidth();
    int h = (int) rect.getHeight();

    int countNonWhite = 0;

    for (int y = y0; y < y0 + h; y++) {
      for (int x = x0; x < x0 + w; x++) {
        Color c = pr.getColor(x, y);
        if (c.getBrightness() < 0.98) { // no es blanco puro
          countNonWhite++;
          if (countNonWhite > 10) return false; // hay contenido
        }
      }
    }

    return true; // está vacío
  }

  // ============================
  // CONVERTIR ROI A PIXELES REALES
  // ============================
  public ROI convertirAFisico(Rectangle rectFX, double viewW, double viewH) {

    double realW = imagenBase.getWidth();
    double realH = imagenBase.getHeight();

    double factorX = realW / viewW;
    double factorY = realH / viewH;

    int x1 = (int) (rectFX.getX() * factorX);
    int y1 = (int) (rectFX.getY() * factorY);
    int x2 = (int) ((rectFX.getX() + rectFX.getWidth()) * factorX);
    int y2 = (int) ((rectFX.getY() + rectFX.getHeight()) * factorY);

    return new ROI(x1, y1, x2, y2);
  }

  // ============================
  // OCR SOBRE ROI
  // ============================
  public String realizarOCR(Rectangle rectFX, double viewW, double viewH) {
    return ocrService.ocrROIJavaFX(imagenBase, rectFX, viewW, viewH);
  }

  // ============================
  // GENERAR ROI DE LINEAEXTRACTO
  // ============================
  public List<ROI> generarROIsLineasExtracto(ModeloOCR modelo) {

    ROI lineaBase = modelo.zonas().get("lineaBase");
    ROI finalExtractos = modelo.zonas().get("finalExtractos");
    int altura = modelo.alturaLineaAprox();

    // Margen de error razonable
    int margen = 3; // tolerancia vertical
    int ajuste = 1; // desplazar ligeramente hacia abajo

    List<ROI> rois = new ArrayList<>();

    double y = lineaBase.y1() + ajuste;
    double yFin = finalExtractos.y1() + margen; // ← margen extra

    while (y < yFin - altura / 3) { // ← tolerancia parcial

      ROI roi = new ROI(lineaBase.x1(), y, lineaBase.x2(), y + altura);

      rois.add(roi);
      y += altura;
    }

    return rois;
  }

  public List<Extracto> leerExtractos(ModeloOCR modelo) {

    List<Extracto> salida = new ArrayList<>();

    List<ROI> rois = generarROIsLineasExtracto(modelo);

    for (ROI roi : rois) {
      String texto = ocrService.ocrROIReal(imagenBase, roi);
      IDebugService.print("[ModeloOCRService>leerExtractos] OCR ROI: [" + texto + "]");

      if (texto == null) continue;
      texto = texto.trim();
      if (texto.isEmpty()) continue;
      Extracto ext = parser.parsearLineaExtracto(texto);

      if (ext != null) {
        salida.add(ext);
      }
    }

    return salida;
  }

  public List<Extracto> generarExtractos(ModeloOCR modelo) {

    List<Extracto> salida = new ArrayList<>();

    // 1. Generar ROIs de cada línea
    List<ROI> rois = generarROIsLineasExtracto(modelo);

    // 2. Leer OCR de cada ROI
    for (ROI roi : rois) {
      String texto = ocrService.ocrROIReal(imagenBase, roi);

      if (texto == null) continue;
      texto = texto.trim();

      if (texto.isEmpty()) continue;

      // 3. Parsear la línea en un Extracto
      Extracto ext = parser.parsearLineaExtracto(texto);

      if (ext != null) {
        salida.add(ext);
      }
    }

    return salida;
  }

  // ============================
  // GUARDAR ROI EN EL MODELO
  // ============================
  public void addROI(String nombreZona, Rectangle rectFX, double viewW, double viewH) {

    ROI roiReal = convertirAFisico(rectFX, viewW, viewH);
    String textoOCR = realizarOCR(rectFX, viewW, viewH);

    this.builder.zonas.put(nombreZona, roiReal);
    this.builder.ocrPorZona.put(nombreZona, textoOCR);
    IDebugService.print(
        _Colores.BLUE,
        ">>> [ModeloOCRService>addROI] Añadiendo ROI al builder: "
            + this.builder.hashCode()
            + " - Zonas en builder: "
            + this.builder.zonas);
  }

  // ============================
  // CONSTRUIR MODELO FINAL
  // ============================
  public ModeloOCR build() {
    return this.builder.build();
  }

  // ============================
  // GUARDAR JSON
  // ============================
  public void guardarJSON(ModeloOCR modelo, Path destino) throws Exception {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    String json = gson.toJson(modelo);
    IDebugService.print(
        _Colores.BLUE,
        "[ModeloOCRService>guardarJSON] ROIs en builder: "
            + this.builder.zonas
            + " - ROIs en modelo final: "
            + modelo.zonas());
    Files.writeString(destino, json);
  }
}
