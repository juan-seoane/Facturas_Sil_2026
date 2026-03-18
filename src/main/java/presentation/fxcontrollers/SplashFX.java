package presentation.fxcontrollers;

import app.services.NavService;
import app.services.ResourcesService;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class SplashFX extends Application {

    //#region CAMPOS
    Rectangle2D screenBounds;
    Image imagen;

    //#endregion

    //#region START
    @Override
    public void start(Stage primaryStage) {
        // Obtiene las dimensiones de la pantalla
        screenBounds = Screen.getPrimary().getVisualBounds();
        // Crea una nueva escena
        imagen = cargarImagenAleatoria();
        double imgwidth = imagen.getWidth();
        double imgheight = imagen.getHeight();
        Parent root = ResourcesService.ImagenARoot(imagen);
        Scene scene = new Scene(root, imgwidth, imgheight);
        primaryStage.setScene(scene);
        //primaryStage.setTitle("Bienvenido a FacturasSil v2.4");
        // Centra la ventana en la pantalla
        primaryStage.setX((screenBounds.getWidth() - 800) / 2);
        primaryStage.setY((screenBounds.getHeight() - 523) / 2);
        //modo sin controles
        primaryStage.initStyle(StageStyle.UNDECORATED);
        //muestra el Splash
        primaryStage.show();
        // Luego de mostrar la ventana de bienvenida, puedes cargar tu vista principal
        PauseTransition delay = new PauseTransition(Duration.seconds(5));
        delay.setOnFinished(event -> loadMainView(primaryStage));
        delay.play();
    }

    private void loadMainView(Stage primaryStage) {

        // Cargar la vista principal
        Scene scene = NavService.crearEscena("Acceso");
        //Creamos un nuevo Stage
        Stage st = NavService.crearStage(scene);

        //asignamos dimensiones y estilo al Stage
        st.setX((screenBounds.getWidth() - 525) / 2);
        st.setY((screenBounds.getHeight() - 550) / 2);
        st.initStyle(StageStyle.UNDECORATED);
        st.setResizable(false);
        st.setAlwaysOnTop(true);

        //ocultamos el Splash y mostramos el Stage
        primaryStage.hide();
        st.show();
        FxAcceso.ventanaAcceso = st;
        FxAcceso.canvasAcceso = FxAcceso.getCanvas();
    }

    //#endregion

    //#region HELPERS
    private synchronized Image cargarImagenAleatoria() {
        Image img;
        int i = (int) (Math.floor(Math.random() *4 + 1));
        String rutaRel = "/splash" + i + ".jpg";
        System.out.println("[FxSplash>cargarImagenAleatoria] Ruta de la Imagen : " + rutaRel);

        img = ResourcesService.cargarImagen(rutaRel);

        return img;
    }


    //#endregion

}
