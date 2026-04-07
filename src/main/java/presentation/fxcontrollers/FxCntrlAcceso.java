package presentation.fxcontrollers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import app.core.AppContext;
import app.core.AppController;
import javafx.animation.PauseTransition;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Duration;


public class FxCntrlAcceso implements Initializable {

//#region campos fxml
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnOK;
    @FXML
    public TextArea txtArea;
    @FXML
    public ImageView imgvAcceso;
//#endregion

//#region otros campos
    public static Stage ventanaAcceso;

    public static Scene scene_acceso2;
    public Stage stage;
    public static TextArea canvasAcceso;
    public static String usuario ="";
    public static int intentos = 1;
    private static boolean entrando = false;


    //private boolean credsOK;
    public static boolean aceptado = false;


    // --- NUEVO LISTENER SEGURO ---
    public EventHandler<KeyEvent> handlerTeclas = (KeyEvent ke) -> {
        if (ke.getCode() == KeyCode.ENTER || ke.getCode().isLetterKey() || ke.getCode().isDigitKey()) {

            //System.out.println("[FxAcceso>HandlerTeclas] Key Pressed: " + ke.getCode());

            // 1. Si el nodo ya no está en escena → salir
            if (txtUsuario.getScene() == null)
                return;

            // 2. Obtener la ventana de forma segura
            Stage ventana = (Stage) txtUsuario.getScene().getWindow();
            if (ventana == null || !ventana.isShowing())
                return;

            // 3. Ejecutar la acción
            try {
                pulsartecla();
            } catch (IOException e) {
                System.out.println("[FxAcceso>HandlerTeclas] Error al pulsar tecla: " + e.getMessage());
            }

            ke.consume();
        }
    };

//#endregion

//#region inicializacion
    // REVIEW: Lo de abajo sólo funciona si se implementa el Interfaz "Inicializable" (implements Initilizable)
    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        canvasAcceso = this.txtArea;
        // REVIEW: No sé cómo hacer para poner el foco al arrancar en ese campo de texto... la siguiente línea no funciona
        //txtUsuario.requestFocus();

        canvasAcceso.sceneProperty().addListener((observableScene, oldScene, newScene) -> {
            if (newScene != null) {
                // 'txtUsuario' está ahora en una escena, podemos obtener el Stage
                FxCntrlAcceso.ventanaAcceso = (Stage) newScene.getWindow();
            }
        });

    // Obtener el Stage cuando la escena esté lista
    txtArea.sceneProperty().addListener((obs, oldScene, newScene) -> {
        if (newScene != null) {
            stage = (Stage) newScene.getWindow();
        }
    });


        //System.out.println("[Acceso - initialize()] canvasAcceso activado: " + (canvasAcceso!=null) );
    }
//#endregion

//#region botones de eventos
    private void pulsartecla() throws IOException {

        AppController.loginExitoso(usuario);
        stage.close();

    }

    @FXML
    private void pulsarbotonOK() throws InterruptedException, IOException{
        if (aceptado&&!FxCntrlAcceso.entrando) {
            FxCntrlAcceso.entrando = true;
            try{
                pulsartecla();
                //System.out.println("[Acceso] Tecla Pulsada, entrando...");
            } catch (IOException ex) {
                System.out.println("Error " + ex.getClass() + " en FxCntrlAcceso, lin 125");
            }
        }else{
            probar();
        }
    }
// NOTE : 26-03-16 : pulsar Enter equivale a pulsar el botón OK
    @FXML
    private void pulsarEnter(KeyEvent ke) throws InterruptedException, IOException{
        if(ke.getCode()==KeyCode.ENTER){
            pulsarbotonOK();
            ke.consume(); // <-- stops passing the event to next node
        }
    }

    @FXML
    public void probar() throws InterruptedException, IOException {
        TextField userF = this.txtUsuario;
        PasswordField passF = this.txtPassword;

        String user = userF.getText();
        String pass = passF.getText();

        int resp = AppContext.get().auth().autenticar(user, pass, intentos);

        switch (resp) {
            case 1 -> {
                acierto();
                break;
            }
            case 0 -> {
                fallo();
                break;
            }
            default -> {
                reintentar();
                break;
            }

        }
    }
    //#endregion

//#region setters,getters y demás
    public static String getUsuario() {
        //Este procedimiento tiene que leer el usuario antes de cerrarse la ventana...
        return FxCntrlAcceso.usuario;
    }

    public static TextArea getCanvas() {
        return canvasAcceso;
    }

    public static void imprimir(String cont) {
        getCanvas().appendText("\n" + cont);
    }

    //#endregion

//#region post-auth
    public void fallo() {
        scene_acceso2 = AppContext.get().nav().crearEscena("FxAcceso2");
        AppContext.get().nav().cambiarEscena(stage, scene_acceso2, handlerTeclas);
        System.out.println("[Acceso>fallo] intentos>=5 y AUTH_FAIL] El proceso de Autenticación ha fallado!");
        System.out.println("[Acceso>fallo] El programa se cerrará!");
        imprimir("\nEl proceso de Autenticación ha fallado!");
        imprimir("\nEl programa se cerrará!\nPulse cualquier tecla para continuar...");
        ventanaAcceso.requestFocus();
        System.exit(0);
    }

    public void acierto() {
        FxCntrlAcceso.usuario = txtUsuario.getText();
        AppContext.setUsuarioActual(FxCntrlAcceso.usuario);
        FxCntrlAcceso.aceptado = true;
        // TODO : 26-03-16 : La nueva Config no se debería cargar desde el FxController...

        scene_acceso2 = AppContext.get().nav().crearEscena("FxAcceso2");
        AppContext.get().nav().cambiarEscena(stage, scene_acceso2, handlerTeclas);
        //System.out.println("[FxAcceso>acierto] intentos<5 y cred OK]...OK, entrando...pulse una tecla para continuar");
        imprimir("Ok...Entrando!\nBienvenido a FacturasSIL 24!\nPor favor espere...");
        ventanaAcceso.requestFocus();
        // NOTE : 26-03-17 : En vez de un Thread.sleep -> PauseTransition
        PauseTransition pausa = new PauseTransition(Duration.seconds(2));
        pausa.setOnFinished(e -> {
            // lo que quieras hacer después de los 3 segundos
            if (!FxCntrlAcceso.entrando) {
                FxCntrlAcceso.entrando = true;
                try{
                    pulsartecla();
                    System.out.println("[Acceso] Acierto - Entrando...!!!!");
                } catch (IOException ex) {
                    System.out.println("Error " + ex.getClass() + " en FxCntrlAcceso, lin 125");
                }
            }
        });
        pausa.play();
        // TODO : 26-03-17 : A partir de aquí se cierra esta escena, solamente, y por otro lado (AuthService) arranca el Controlador Principal, y con él el programa  en si...
    }

    public void reintentar() throws InterruptedException {
        imprimir("\nDatos incorrectos: " + this.txtUsuario.getText() + " - " + this.txtPassword.getText()
                + "\n...Por favor vuelva a intentarlo... (intentos: " + intentos + ")");
        intentos++;

        this.txtUsuario.clear();
        this.txtPassword.clear();
        this.txtUsuario.requestFocus();
    }
    //#endregion

}
