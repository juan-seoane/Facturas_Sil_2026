package com.sil.facturas.infrastructure.services.ocr;

import com.sil.facturas.domain.ocr.ROI;
import java.awt.image.BufferedImage;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class OCRService {

    private final ITesseract tess;

    public OCRService() {
        tess = new Tesseract();
        tess.setDatapath("C:/Program Files/Tesseract-OCR/tessdata");
        tess.setLanguage("spa");
        tess.setVariable("tessedit_create_hocr", "0");
        tess.setVariable("tessedit_create_txt", "1");
        tess.setVariable("tessedit_char_blacklist", "");
        tess.setVariable("tessedit_char_whitelist", "");
        tess.setVariable("output_encoding", "UTF-8");
    }

    public OCRService(String dataPath, String language) {
        tess = new Tesseract();
        tess.setDatapath(dataPath);
        tess.setLanguage(language);
        tess.setVariable("tessedit_create_hocr", "0");
        tess.setVariable("tessedit_create_txt", "1");
        tess.setVariable("tessedit_char_blacklist", "");
        tess.setVariable("tessedit_char_whitelist", "");
        tess.setVariable("output_encoding", "UTF-8");
    }

    /** OCR de una subimagen definida por un ROI real */
    public String ocr(BufferedImage img, ROI roi) {

        int x = (int) roi.x1();
        int y = (int) roi.y1();
        int w = (int) (roi.x2() - roi.x1());
        int h = (int) (roi.y2() - roi.y1());

        try {
        BufferedImage sub = img.getSubimage(x, y, w, h);
        return tess.doOCR(sub).trim();
        } catch (TesseractException e) {
        throw new RuntimeException("Error en OCR", e);
        }
    }

    /** OCR de imagen completa */
    public String ocr(BufferedImage img) {
        try {
        return tess.doOCR(img).trim();
        } catch (TesseractException e) {
        throw new RuntimeException("Error en OCR", e);
        }
    }
}
