package presentation.fxcontrollers;

import app.core.AppContext;
import infraestructure.servicios.config.Config;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.BrokenBarrierException;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.stage.Popup;
import javafx.stage.Stage;

public class FxCntrlPanelControl implements Initializable {

  // #region CAMPOS_FXML
  @FXML private ToggleButton btnFCT;
  @FXML private Button btnRS;
  @FXML private Button btnCJA;
  @FXML private Button btnCFG;
  @FXML private Button btnNTS;
  @FXML private Button btnAutosave;

  @FXML private ToggleButton toggleModo;

  @FXML private Label lblEntradas;
  @FXML private Label lblTrimestre;
  @FXML private Label lblAnho;
  @FXML private Label lblUsuario;
  // #endregion

  // #region OTROS_CAMPOS
  static Image icon;
  // static TrayIcon trayIcon;
  static Popup popMenu;
  public static Scene escena1;
  // TODO : 26-03-21 : Cuando iniciamos el PC se tiene que cargar (desde AppContext) la Config
  // personal etc...
  // REVIEW - 24-06-30 : Los controladoresFx de FCT deberían estar en el ControladorFacturas, no
  // aquí...
  // static FxCntrlTablaFCT fxTablaFCTcontr;

  // static FxCntrlVisorFCT fxVisorFCTcontr;

  static FxCntrlPanelControl instancia_pc = null;
  static int modo;
  static boolean botonpulsado = false;
  static int botonactivo = 1;
  static Stage GUIpanel;

  // Config configActual;
  // Controlador_prev ctrlPpal;
  // ControladorFacturas ctrlFct;
  // NOTE : 26-03-30 : El usuario actual se establece en el AppContext, no aquí...
  // String usuarioActual;
  // #endregion

  // REVIEW - 24-04-12 : ¿Porqué no puede seguir siendo un Singleton?
  // REVIEW - 24-04-12 : Hay que definir un usuariActual, y una configActual

  // #region CONSTR
  public FxCntrlPanelControl() {
    // this.usuarioActual = AppContext.usuarioActual.toLowerCase();
    // System.out.println("[FxCntrlPanelControl] En el constructor del FxCntrlPanelControl,
    // estableciendo el usuario actual en : " + AppContext.usuarioActual);
    // Luego habrá que cambiar esto de abajo a modo NAV por defecto...
    // PanelControl.modo = Controlador_prev.INGR;
    // this.configActual = Config.getConfig(this.usuarioActual);
    // REVIEW - 24-06-21 : Estas asignaciones me hacen falta
    // this.ctrlPpal = Controlador_prev.getControlador();
    // this.ctrlFct = Controlador_prev.getControladorFacturas();
  }

  // #endregion

  // #region INITIALIZE
  @FXML
  @Override
  public void initialize(URL arg0, ResourceBundle arg1) {
    setUsuario(AppContext.usuarioActual);
    // REVIEW - 24-04-09 : Crear un Controlador general, y decidir cómo abrirá las tablas, etc...
    // REVIEW : Cambiar el diseño de los ToggleButton al pulsarse y el mensaje que arrojan
    // REVIEW : Arreglar la inicialización de la GUI del PanelControl... No funciona
    setAnho((Integer) Config.configActual.getConfigData().getAnho().getAnho());
    setTrimestre(((Integer) (Config.configActual.getConfigData().getAnho().getTrimestre())));
    System.out.println(
        "[FxCntrlPanelControl] Inicializando, estableciendo la etiqueta de usuario en : "
            + AppContext.usuarioActual);
    System.out.println(
        "[FxCnytrlPanelControl] Anho y trimestre establecidos en : "
            + Config.configActual.getConfigData().getAnho().getAnho()
            + " - "
            + Config.configActual.getConfigData().getAnho().getTrimestre());

    FxCntrlPanelControl.instancia_pc = this;

  }

  // #endregion

  // #region GETTERS/SETTERS
  public void setAnho(int i) {
    Platform.runLater(() -> this.lblAnho.setText(i + ""));
  }

  public void setNumfacturas(int i) {
    // Platform.runLater(() -> Controlador_prev.getPanelControl().lblEntradas.setText(i +""));

  }

  public void setTrimestre(int i) {
    if (i == 0) {
      Platform.runLater(() -> FxCntrlPanelControl.getPanelControl().lblTrimestre.setText("TODOS"));
    } else {
      Platform.runLater(() -> FxCntrlPanelControl.getPanelControl().lblTrimestre.setText(i + ""));
    }
  }

  public void setUsuario(String user) {
    Platform.runLater(() -> this.lblUsuario.setText(user + ""));
  }

  public static int getModo() {
    return FxCntrlPanelControl.modo;
  }

  public Stage getGUI() {
    return GUIpanel;
  }

  public static void setGUI(Stage vPC) {
    GUIpanel = vPC;
  }

  // #endregion

  // #region EVT_BTNS
  @FXML
  private void btnCFGpulsado(Event evt) throws InterruptedException, BrokenBarrierException {
    // this.ctrlPpal = Controlador_prev.getControlador();
    // System.out.println(" [PanelControl] Boton CFG pulsado!");
    // botonactivo = 4;
    // botonpulsado = true;
  }

  @FXML
  private void btnNTSpulsado(Event evt) {
    System.out.println(" [PanelControl] Boton NTS pulsado!");
    botonactivo = 3;
    botonpulsado = true;
  }

  @FXML
  private void btnRSpulsado(Event evt) {
    System.out.println(" [PanelControl] Boton DIST pulsado!");
    botonactivo = 2;
    botonpulsado = true;
  }

  @FXML
  private void btnVisorFctPulsado(Event evt) {
    System.out.println(" [PanelControl] Boton VisorFCT pulsado!");
    //botonactivo = 2;
    botonpulsado = true;
  }

  @FXML
  private void btnFCTpulsado(Event evt) throws InterruptedException, BrokenBarrierException {
    // System.out.println(" [PanelControl] Boton FCT pulsado!");
    // this.ctrlFct = Controlador_prev.getControladorFacturas();
    if (((ToggleButton) (evt.getSource())).isSelected()) {
      btnFCT.setStyle(
          "-fx-background-color: yellow; -fx-border-color: #063970; -fx-border-radius: 10;"
              + " -fx-border-width: 3");
      System.out.println("[PanelControl>btnFCTpulsado] FCT activo!");
      botonactivo = 1;
      botonpulsado = true;

      Stage tabla = AppContext.getTablaFCT();
      tabla.show();
      tabla.toFront();
      System.out.println(
          "[FxcntrlPanelControl>btnFCTpulsado] TablaFCT desde AppContext = "
              + AppContext.getTablaFCT().hashCode());

    } else if (!((ToggleButton) (evt.getSource())).isSelected()) {
      btnFCT.setStyle(
          "-fx-background-color: transparent; -fx-border-color: #063970; -fx-border-radius: 10;"
              + " -fx-border-width: 3");
      System.out.println("[PanelControl>btnFCTpulsado] FCT desactivado!");
      botonactivo = 11;
      botonpulsado = true;

      Stage tabla = AppContext.getTablaFCT();
      tabla.hide();
    }

    // REVIEW - 24-05-29 : Hay que desactivar el botón mientras está en uso, y colorearlo de
    // amarillo (quizás pueda ser un ToggleButton)...
  }

  @FXML
  private void btnCJApulsado(Event evt) {
    System.out.println(" [PanelControl] Boton CJA pulsado!");
    botonactivo = 5;
    botonpulsado = true;
  }

  @FXML
  private void btnAutosavepulsado(Event evt) {
    botonactivo = 6;
    botonpulsado = true;
  }

  @FXML
  private void btnAutosavePressed(Event evt) {
    System.out.println(" [PanelControl] Boton AutoSave pulsado!");
    btnAutosavepulsado(evt);
    ((Button) evt.getSource())
        .setStyle(
            "-fx-background-color: yellow; -fx-border-color: #063970; -fx-border-radius: 10;"
                + " -fx-border-width: 3");
  }

  @FXML
  private void btnAutosaveReleased(Event evt) {
    ((Button) evt.getSource())
        .setStyle(
            "-fx-background-color: transparent; -fx-border-color: #063970; -fx-border-radius: 10;"
                + " -fx-border-width: 3");
  }

  @FXML
  private void toggleModopulsado(Event evt) {
    System.out.println(" [PanelControl] Boton MODO pulsado!");
    if (((ToggleButton) (evt.getSource())).isSelected()) {
      toggleModo.setText("MODO INGR");
      toggleModo.setStyle(
          "-fx-background-color: yellow; -fx-border-color: #063970; -fx-border-radius: 10;"
              + " -fx-border-width: 3");
      System.out.println(" [PanelControl] modo: INGR");
      // modo = Controlador_prev.INGR;
    } else if (!((ToggleButton) (evt.getSource())).isSelected()) {
      toggleModo.setText("MODO NAV");
      toggleModo.setStyle(
          "-fx-background-color: transparent; -fx-border-color: #063970; -fx-border-radius: 10;"
              + " -fx-border-width: 3");
      System.out.println(" [PanelControl] modo: NAV");
      // modo = Controlador_prev.NAV;
    }
    botonactivo = 7;
    botonpulsado = true;
  }

  public int seleccion() {
    return botonactivo;
  }

  public boolean botonpulsado() {
    return botonpulsado;
  }

  public static void pulsarboton(int boton) {
    botonactivo = boton;
    botonpulsado = true;
  }

  // #endregion

  // #region RESET
  public static void reset() {
    System.out.println("[PanelControl>reset] Reseteando P/C");
    botonpulsado = false;
  }

  // #endregion

  // #region GET_PC
  public static FxCntrlPanelControl getPanelControl() {
    if (instancia_pc == null) instancia_pc = new FxCntrlPanelControl();
    return instancia_pc;
  }

  // #endregion

  // #region RUN_CFCT_PREV
  /*public synchronized ControladorFacturas arrancarCfct() throws InterruptedException, BrokenBarrierException {

      Controlador.cfct = Controlador.getControladorFacturas();
      Controlador.cfct.setName("Contr_FCT");
      Controlador.cfct.start();

      return Controlador.cfct;
  }*/
  /*
  //#endregion

  //#region HELPERS
      public void mostrar() {
          // Plantearse lo de hacer un método (estático o no) en cada contrFX que muestre la GUI correspondiente (GUIx.mnostrar())
          // REVIEW - 24-07-20 : Este título está fuera de sitio ¿¿¿???
          GUIpanel.setTitle("Panel de Control");
          GUIpanel.show();
      }
  //#endregion

  */
}
