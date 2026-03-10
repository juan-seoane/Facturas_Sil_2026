package ui.fxcontrollers;

import java.awt.HeadlessException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.BrokenBarrierException;

import controladores.Controlador;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import modelo.base.Config;
import modelo.helpers.ComprobacionesAcceso;
import ui.helpers.FxmlHelper;

public class Acceso implements Initializable{

//#region CAMPOS_FXML
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnOK;
    @FXML public TextArea txtArea;
//#endregion

//#region OTROS_CAMPOS
    public static Stage ventanaAcceso;

    private static Scene scene2;

    public static TextArea canvasAcceso;
    public static String usuario ="";
    public static int intentos = 1;

    private boolean credsOK;
    public static boolean aceptado = false;
//#endregion

//#region INIT
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
                Acceso.ventanaAcceso = (Stage) newScene.getWindow();
            }
        });
        //System.out.println("[Acceso - initialize()] canvasAcceso activado: " + (canvasAcceso!=null) );
    }
//#endregion

//#region RUN_CTLLR
    private void arrancarControlador() throws IOException, InterruptedException, BrokenBarrierException{
    // REVIEW - 24-05-13 : Aquí arroja una 'IOException' que afecta al hilo general del programa... Habría que ver cómo evitar que se propague a las llamadas anteriores...

           Controlador ctrThread = Controlador.getControlador();

           ctrThread.setName("Ctrl_Ppal");
           ctrThread.start();

           if (!cargarPanelControl())
               System.exit(0);
    }
   //#endregion

//#region GETTERS/SETTERS
    public static String getUsuario() {
        //Este procedimiento tiene que leer el usuario antes de cerrarse la ventana...
        return Acceso.usuario;
    }

    public static TextArea getCanvas(){
        return canvasAcceso;
    }
//#endregion

//#region EVT_BTNS
    private void pulsartecla() throws IOException {

        iniciarPrograma();

        if (!entrar()){
            System.exit(0);
        }
    }

    @FXML
    private void pulsarbotonOK() throws InterruptedException, HeadlessException, NullPointerException, IOException{
        if (entrar()){
            pulsartecla();
            System.out.println("[Acceso] Entrando...????");
        }else{
            probar();
        }
    }

    @FXML
    private void pulsarEnter(KeyEvent ke) throws InterruptedException, HeadlessException, NullPointerException, IOException{
        if(ke.getCode()==KeyCode.ENTER){
            pulsarbotonOK();
            ke.consume(); // <-- stops passing the event to next node
        }
    }

    public static void imprimir(String cont) {
        getCanvas().appendText("\n"+cont);
    }
//#endregion

//#region LOGIN
    @FXML
    public void probar() throws InterruptedException, HeadlessException, NullPointerException, IOException{
        TextField userF = this.txtUsuario;
        PasswordField passF = this.txtPassword;

        String user = userF.getText();
        String pass = passF.getText();
       // Acceso.imprimir(txtArea, "[Acceso.java>probar()]Datos introducidos : "+ userF.getText()+ " - "+passF.getText());

        //System.out.println("[Acceso.java>probar()] Contenido del Área de Texto: "+ this.txtArea.getText());
        //Acceso.imprimir("texto introducido : "+ userF.getText() + " - " +passF.getText()+" - intentos: "+ intentos);
        //Thread.sleep(2000);
        //System.out.println("[Acceso.java>probar()] texto introducido : "+ userF.getText() + " - " +passF.getText());
// REVIEW: OJO! Usuario siempre se contrasta en mayúsculas (aunque esté escrito en minúsculas)
        ComprobacionesAcceso check = new ComprobacionesAcceso();
        credsOK = check.comprobarCredenciales(user, pass);

        if (!credsOK&&intentos<3)
            reintento();
        else if(!credsOK&&intentos>=3)
            fallo();
        else
            acierto();
    }

    private void fallo() {
        scene2 = crearScene2();
        cambiarEscena(scene2);
        //System.out.println("[Acceso>fallo] intentos>=5 y cred NO] El proceso de Autenticación ha fallado!");
        //System.out.println("[Acceso>fallo] El programa se cerrará!");
        imprimir("\nEl proceso de Autenticación ha fallado!");
        imprimir("\nEl programa se cerrará!\nPulse cualquier tecla para continuar...");
        ventanaAcceso.requestFocus();
    }

    private void acierto() {
        usuario=txtUsuario.getText();
        aceptado = true;
        Config.getConfig(usuario);
        scene2 = crearScene2();
        cambiarEscena(scene2);
        imprimir("Ok...Entrando!\nBienvenido a FacturasSIL 24!\nPulse una tecla para continuar...");
        System.out.println("[Acceso>acierto] intentos<5 y cred OK]...OK, entrando...pulse una tecla para continuar");
        ventanaAcceso.requestFocus();
    }

    private void reintento() throws InterruptedException {
        imprimir("\nDatos incorrectos: " + this.txtUsuario.getText() +" - " + this.txtPassword.getText() + "\n...Por favor vuelva a intentarlo... (intentos: "+intentos+")");
        intentos++;

        this.txtUsuario.clear();
        this.txtPassword.clear();
        this.txtUsuario.requestFocus();
    }

    public boolean entrar(){
        if(aceptado)
            return true;
        else
            return false;
    }

    private void iniciarPrograma(){
        try {
            arrancarControlador();
        } catch (IOException | InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
        ventanaAcceso.close();
        //Platform.exit();
    }
//#endregion

//#region HELPERS

    public static Scene crearScene1 (Parent root) {

        Scene esc1 = new Scene(root,525,550);
        //scene.getStylesheets().add(getClass().getResource("acceso.css").toExternalForm());
        //System.out.println("[Acceso>crearScene1] escena1 creada : " + esc1.hashCode());
        return esc1;

    }

    private Scene crearScene2() {
        FxmlHelper loader2 = new FxmlHelper("/ui/resources/Acceso2.fxml");
        Parent root2 = loader2.cargarFXML();
        Scene esc2 = new Scene(root2);
        //scene.getStylesheets().add(getClass().getResource("acceso.css").toExternalForm());
        //System.out.println("[Acceso>crearScene2] escena2 creada : " + esc2.hashCode());
        return esc2;
    }
private EventHandler<KeyEvent> handlerTeclas = null;

private void cambiarEscena(Scene es) {

    Stage stage = (Stage) this.txtArea.getScene().getWindow();
    ventanaAcceso = stage;
    ventanaAcceso.setScene(es);

    // --- NUEVO LISTENER SEGURO ---
    handlerTeclas = ke -> {
        if (ke.getCode() == KeyCode.ENTER || ke.getCode().isLetterKey() || ke.getCode().isDigitKey()) {
            System.out.println("[Acceso>cambiarEscena] Key Pressed: " + ke.getCode());

            // Evitar ejecutar si la ventana ya no está visible
            if (!ventanaAcceso.isShowing()) return;

            try {
                pulsartecla();
            } catch (IOException e) {
                e.printStackTrace();
            }

            ke.consume();
        }
    };

    ventanaAcceso.getScene().addEventFilter(KeyEvent.KEY_PRESSED, handlerTeclas);

    // --- ELIMINAR EL LISTENER AL CERRAR LA VENTANA ---
    ventanaAcceso.setOnHidden(e -> {
        if (handlerTeclas != null) {
            ventanaAcceso.getScene().removeEventFilter(KeyEvent.KEY_PRESSED, handlerTeclas);
        }
    });
}
//#endregion

//#region LOAD_C/P
    private synchronized boolean cargarPanelControl(){
/*
        File miDir = new File (".");
        String path = "";
        try {
            path = miDir.getCanonicalPath();
            //System.out.println("[Acceso>cargarPanelControl] path: " + path);
        } catch (IOException e) {
            //System.out.println("[Acceso>cargarPanelControl] Error al obtener la ruta de los controladores");
            e.printStackTrace();
        }
        path = path.replace('\\','/');
*/
        String ruta  = "/ui/resources/PanelControl.fxml";
        //System.out.println("[Acceso>cargarPanelControl] ruta  : " + ruta);

        FxmlHelper FXMLpc = new FxmlHelper(ruta);

        Parent root;

        root = FXMLpc.cargarFXML();

        Scene escena = new Scene(root);
        Stage vPC = Controlador.setStage(escena, true);
        vPC.setResizable(false);
        // REVIEW - 24-05-30 : Aquí se ajusta el modo de la ventana de P/C

        vPC.setOnCloseRequest(e -> System.exit(0));
        PanelControl.setGUI(vPC);
        PanelControl.getPanelControl().mostrar();

        //Asignar el actual P/C a las clases necesarias
        //ControladorFacturas.FXcontrlTablaFCT = (FxCntrlTablaFCT)(FXMLtablafct.getFXcontr());
        //ControladorFacturas.FXcontrlVisorFCT = (FxCntrlVisorFCT)(FXMLvisorfct.getFXcontr());
        Controlador.setPanelControl((PanelControl)(FXMLpc.getFXcontr()));

        return true;
    }

    public void rutaExiste(String ruta){

        boolean rutaExiste = (new File(ruta)).exists();
        if (rutaExiste){
            //System.out.println("[Acceso>rutaExiste] El archivo " + ruta + " existe.");
        }
        else{
            //System.out.println("[Acceso>rutaExiste] El archivo " + ruta + " no existe.");
            //System.out.println ("[Acceso>rutaExiste] Directorio actual: " + miDir.getCanonicalPath());
        }
    }
//#endregion
}
