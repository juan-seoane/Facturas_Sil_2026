package com.sil.facturas.presentationgui.helpers;

import static com.sil.facturas.domain.interfaces.IDebugService.*;

import java.net.URL;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class FxmlHelper {

	String ruta;
    FXMLLoader loader;
    Object fxController;

	public FxmlHelper(String ruta){

		this.ruta = ruta;
		this.loader = new FXMLLoader();

        this.loader.setLocation(getClass().getResource(ruta));

	}

    public synchronized Parent cargarFXML() {

        URL url = getClass().getResource("/fxml/FxModeloOCRVisualizer.fxml");
        printWarning("[ModeloOCRVisualizer>start] URL recurso: " + url);

        FXMLLoader loader = new FXMLLoader(url);
        this.fxController = this.loader.getController();

        try {
        Parent root = loader.load();
        return root;
        } catch (Exception e) {
        printError(
            "[ModeloOCRVisualizer>start] Error cargando FXML: "
                + e.getClass().getName()
                + " - "
                + e.getMessage());
        e.printStackTrace();
        Throwable cause = e.getCause();
        while (cause != null) {
            printError(
                "[ModeloOCRVisualizer>start] CAUSA: "
                    + cause.getClass().getName()
                    + " - "
                    + cause.getMessage());
            cause.printStackTrace();
            cause = cause.getCause();
        }
        throw new RuntimeException(e);
        }

	// 	Parent root = new AnchorPane();

	// 	try {
	// 	root = this.loader.load();
	// 	} catch (IOException ex) {
    //   printError(
    //       "[FxmlHelper>cargarFXML] Excepcion del tipo " + ex + " al cargar FXML " + this.ruta);
	// 	}

	// 	return root;
	}

	public synchronized Object getFXcontr(){
        return this.fxController;
	}

}
