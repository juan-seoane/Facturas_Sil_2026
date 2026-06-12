package com.sil.facturas.app.services;

import com.sil.facturas.domain.enums._Ruta;

import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

/* CONTROLA EL PROCESO DE CREACIÓN DE RUTAS DE USUARIO, CHEQUEO DE SU EXISTENCIA, CARGA DE IMÁGENES, DE FXML, ETC... */
public class ResourcesService {

    public static Image cargarImagen(String rutaRel) {
        String ruta = _Ruta.IMG.getRuta() + rutaRel;
        System.out.println("Cargando imagen desde ruta " + ruta);
        Image img = null;
        try {
            img = new Image(ResourcesService.class.getResource(ruta).toExternalForm());
        } catch (Exception e) {
            System.out.println(
                "ex tipo " + e.getClass() + " al cargar la imagen " + ruta + " desde el ResourcesService"
            );
            System.exit(1);
        }
        return img;
    }

    public static Parent ImagenARoot(Image image) {
        // Crea un ImageView y ajusta la imagen al tamanho de la escena
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);

        // Crea un nuevo Pane y añade el ImageView
        AnchorPane pane = new AnchorPane();
        pane.getChildren().add(imageView);

        // Ajusta el Pane a la escena
        return (Parent) pane;
    }
}
