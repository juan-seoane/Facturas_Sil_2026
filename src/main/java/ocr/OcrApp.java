/*

*/
package ocr;

import java.io.File;

import com.asprise.ocr.Ocr;



public class OcrApp {

    String s="OCR sin respuesta";

    public OcrApp(String ruta, int s_x, int s_y, int w, int h){
        Ocr.setUp(); // one time setup
        Ocr ocr = new Ocr(); // create a new OCR engine
        ocr.startEngine("spa", Ocr.SPEED_SLOW); // Español, antes eng:English
        //this.s = ocr.recognize(new File[] {new File(ruta)},Ocr.RECOGNIZE_TYPE_TEXT, Ocr.OUTPUT_FORMAT_PLAINTEXT);
        this.s = ocr.recognize(ruta, -1, s_x, s_y, w, h, Ocr.RECOGNIZE_TYPE_TEXT, Ocr.OUTPUT_FORMAT_PLAINTEXT);
    }


    public String getOCRResult(){
        return this.s;
    }



}
