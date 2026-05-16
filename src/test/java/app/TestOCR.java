package app;

import java.io.File;

import com.asprise.ocr.Ocr;

import infrastructure.filesystem._Ruta;

public class TestOCR {
    public static void main(String[] args) {
        Ocr.setUp();
        Ocr ocr = new Ocr();
        ocr.startEngine("spa", Ocr.SPEED_FASTEST);
        String usuario = "admin";
        String fichero = "test1.png";
        String ruta = _Ruta.DATOS.getRuta() + "/" + usuario.toUpperCase() + "/scans/" + fichero;
        String texto = ocr.recognize(new File[] { new File(ruta) }, Ocr.RECOGNIZE_TYPE_TEXT,
                Ocr.OUTPUT_FORMAT_PLAINTEXT);

        System.out.println(texto);

        ocr.stopEngine();
    }
    
}
