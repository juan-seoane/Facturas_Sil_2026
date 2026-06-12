package com.sil.facturas.presentationgui.fxcontrollers;

import com.sil.facturas.app.api.IaccesoUI;
import com.sil.facturas.app.core.AppContext;
import com.sil.facturas.app.core.AppController;
import com.sil.facturas.domain.interfaces.IDebugService;
import com.sil.facturas.presentationgui.helpers._VentanaFX;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
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

public class FxCntrlAcceso implements Initializable, IaccesoUI{

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

  // --- NUEVO LISTENER SEGURO ---
  public EventHandler<KeyEvent> handlerTeclas =
      (KeyEvent ke) -> {
        if (ke.getCode() == KeyCode.ENTER
            || ke.getCode().isLetterKey()
            || ke.getCode().isDigitKey()) {

          // System.out.println("[FxAcceso>HandlerTeclas] Key Pressed: " + ke.getCode());

          // 1. Si el nodo ya no está en escena → salir
          if (txtUsuario.getScene() == null) return;

          // 2. Obtener la ventana de forma segura
          Stage ventana = (Stage) txtUsuario.getScene().getWindow();
          if (ventana == null || !ventana.isShowing()) return;

          // 3. Ejecutar la acción
          try {
            pulsarbotonOK();
          } catch (Exception e) {
            IDebugService.printError(
                "[FxAcceso>HandlerTeclas] Error tipo "
                    + e.getClass()
                    + " al pulsar tecla "
                    + ke.getCode()
                    + " : "
                    + e.getMessage());
          }

          ke.consume();
        }
      };

    //#endregion

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

    // IDebugService.print(
    //     "[FxCntrlAcceso - initialize()] canvasAcceso activado: " + (canvasAcceso != null));
    }


    @FXML
    private void pulsarbotonOK() throws InterruptedException, IOException {
        probar();
    }

    private String getPass() {
        return txtPassword.getText();
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

        if (pass.isBlank() || pass.isEmpty())
            return;

        int resp = AppContext.auth().autenticar(user, pass, intentos);

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
    public String getUsuario() {
        //Este procedimiento tiene que leer el usuario antes de cerrarse la ventana...
        return this.txtUsuario.getText();
    }

    public static TextArea getCanvas() {
        return canvasAcceso;
    }

    public static void imprimir(String cont) {
        getCanvas().appendText("\n" + cont);
    }


    //#region post-auth
    public void fallo() {
        imprimirMensaje("El proceso de autenticación ha fallado.\nEl programa se cerrará.");
        PauseTransition pausa = new PauseTransition(Duration.seconds(1));
        pausa.setOnFinished(e -> {
            AppContext.get().nav().cerrarPorFallo(_VentanaFX.LOGIN);
        });
        pausa.play();

    }

    public void acierto() {
        String usuario = txtUsuario.getText();
        imprimirMensaje("Bienvenido, " + usuario + " a FacturasSil 2.6");
        PauseTransition pausa = new PauseTransition(Duration.seconds(1));
        pausa.setOnFinished(
                e -> {
                    try {
                        AppController.loginExitoso(usuario);
                    } catch (Exception ex) {
                        IDebugService.printError(
                                "[FxCntrlAcceso>acierto] ERROR tipo " + ex.getClass() + ": " + ex.getMessage());
                    }
                }
        );
        pausa.play();
    }

    public void reintentar() throws InterruptedException {
    imprimir(
        "\nDatos incorrectos: "
            + this.txtUsuario.getText()
            + " - "
            + this.txtPassword.getText()
            + "\n...Por favor vuelva a intentarlo... (intentos: "
            + intentos
            + ")");
        intentos++;

        this.txtUsuario.clear();
        this.txtPassword.clear();
        this.txtUsuario.requestFocus();
    }
    //#endregion

    @Override
    public void imprimirMensaje(String mensaje) {
        imprimir(mensaje);
    }
}
