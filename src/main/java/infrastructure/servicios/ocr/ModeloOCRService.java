package infrastructure.servicios.ocr;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import domain.records.ROI;
import java.nio.file.Files;
import java.nio.file.Path;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ModeloOCRService {

    public final ModeloOCR.Builder builder;
    private final OCRService ocrService;

    private Image imagenBase;
    public String rutaImagen;

    public ModeloOCRService() {
        this.builder = new ModeloOCR.Builder();
    System.out.println(
        ">>> [ModeloOCRService] NUEVO ModeloOCRServicebuilder: " + this.builder.hashCode());
        this.ocrService = new OCRService();
    }

    // ============================
    // INFO GENERAL DEL MODELO
    // ============================
    public void setInfoModelo(String rutaImagen, int dpi, String nombre, String version) {
        this.rutaImagen = rutaImagen;
    System.out.println(
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
                    if (countNonWhite > 10)
                        return false; // hay contenido
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
    // GUARDAR ROI EN EL MODELO
    // ============================
    public void addROI(String nombreZona, Rectangle rectFX, double viewW, double viewH) {

        ROI roiReal = convertirAFisico(rectFX, viewW, viewH);
        String textoOCR = realizarOCR(rectFX, viewW, viewH);

        this.builder.zonas.put(nombreZona, roiReal);
        this.builder.ocrPorZona.put(nombreZona, textoOCR);
    System.out.println(
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
        System.out.println(
                "[ModeloOCRService>guardarJSON] ROIs en builder: "
                        + this.builder.zonas
                        + " - ROIs en modelo final: "
                        + modelo.zonas());
        Files.writeString(destino, json);
    }

}

