package infrastructure.servicios.ocr;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

import com.asprise.ocr.Ocr;
import domain.records.ROI;

public class OCRExtractor {

    public static String extraerROI(String rutaImagen, ROI roi) throws Exception {

        BufferedImage original = ImageIO.read(new File(rutaImagen));

        BufferedImage sub = original.getSubimage(
            roi.x1(), roi.y1(),
            roi.x2() - roi.x1(),
            roi.y2() - roi.y1()
        );

        File temp = File.createTempFile("roi_", ".png");
        ImageIO.write(sub, "png", temp);

        Ocr.setUp();
        Ocr ocr = new Ocr();
        ocr.startEngine("spa", Ocr.SPEED_FASTEST);

        String texto = ocr.recognize(
            new File[] { temp },
            Ocr.RECOGNIZE_TYPE_TEXT,
            Ocr.OUTPUT_FORMAT_PLAINTEXT
        );

        ocr.stopEngine();
        return texto.trim();
    }
}

