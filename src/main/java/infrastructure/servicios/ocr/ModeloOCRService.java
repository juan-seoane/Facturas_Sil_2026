package infrastructure.servicios.ocr;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import domain.records.ROI;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;

import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;

public class ModeloOCRService {

    private final ModeloOCR.Builder builder;
    private final OCRService ocrService;

    private Image imagenBase;
    private String rutaImagen;

    public ModeloOCRService() {
        this.builder = new ModeloOCR.Builder();
        this.builder.zonas = new LinkedHashMap<>();
        this.builder.ocrPorZona = new LinkedHashMap<>();
        this.ocrService = new OCRService();
    }

    // ============================
    // INFO GENERAL DEL MODELO
    // ============================
    public void setInfoModelo(String rutaImagen, int dpi, String nombre, String version) {
        this.rutaImagen = rutaImagen;
        System.out.println("[ModeloOCRService] Guardando Ruta imagen: " + this.rutaImagen);
        this.builder.rutaImagen(rutaImagen);
        this.builder.dpi(dpi);
        this.builder.nombre(nombre);
        this.builder.version(version);
    }

    public void setImagenBase(Image img) {
        this.imagenBase = img;
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
    // GUARDAR ROI EN EL MODELO
    // ============================
    public void addROI(String nombreZona, Rectangle rectFX, double viewW, double viewH) {

        ROI roiReal = convertirAFisico(rectFX, viewW, viewH);
        String textoOCR = realizarOCR(rectFX, viewW, viewH);

        builder.zonas.put(nombreZona, roiReal);
        builder.ocrPorZona.put(nombreZona, textoOCR);
    }

    // ============================
    // CONSTRUIR MODELO FINAL
    // ============================
    public ModeloOCR build() {
        return builder.build();
    }

    // ============================
    // GUARDAR JSON
    // ============================
    public void guardarJSON(ModeloOCR modelo, Path destino) throws Exception {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(modelo);
        Files.writeString(destino, json);
    }
}

