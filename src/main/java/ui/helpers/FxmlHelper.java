package ui.helpers;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

public class FxmlHelper {

	String ruta;
	FXMLLoader loader;

	public FxmlHelper(String ruta){

		this.ruta = ruta;
	/*
		try {
			System.out.println("[FxmlHelper>constructor] Fichero " + this.ruta + " existe: " + Fichero.fileExists(ruta));
			
		} catch (NullPointerException | IOException e) {
			System.out.println("[FxmlHelper>constructor] Excepcion comprobando ruta: " + ruta);
			e.printStackTrace();
		}
	*/
		this.loader = new FXMLLoader();

		this.loader.setLocation(getClass().getResource(ruta));

	}

	public synchronized Parent cargarFXML() {

		Parent root = new AnchorPane();

		try {
		root = this.loader.load();
		} catch (IOException ex) {
			System.out.println("[FxmlHelper>cargarFXML]: Excepcion del tipo " + ex + " al cargar FXML " + this.ruta);
			ex.printStackTrace();
		}

		return root;
	}

	public synchronized Object getFXcontr(){
		return this.loader.getController();
	}

}
