package app;

import java.io.IOException;
import java.util.concurrent.BrokenBarrierException;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modeloNegocio.Infraestructura.filesystem._Ruta;
import modeloNegocio.base.Config;
import vista.helpers.FxmlHelper;
import controladores.fxcontrollers.FxCntrlTablaFCT;
import controladores.fxcontrollers.PanelControl;
import controladores.helpers._Modo;
import controladores.helpers._Seccion;

public class Controlador_prev extends Thread {
// NOTE 26-03-10 : Hacer unos Enum con estas constantes??

//#region CAMPOS
    static Controlador_prev instancia;
    //static ControladorFicheros cfch;
    ControladorFacturas cfct;
    //static ControladorDistribuidores cd;
    //static ControladorEntradaCaja ccj;
    PanelControl pc;
    //static VisorNotas notas;
    static int seccion = _Seccion.FACT.getSeccion();

    public String usuario;

    //    public static CyclicBarrier barreraControladores;
    //endregion

    //#region CONTR
    private Controlador_prev() {

        System.out.println("[Controlador>constructor] Comenzando el constructor del Controlador Principal");
        this.usuario = Config.getUsuario();
        System.out.println("[Controlador>constructor] El usuario es " + Config.getUsuario());
        cargarPanelControl();

    }

    public static synchronized Controlador_prev getControlador() {
        if (instancia == null) {
            try {
                instancia = new Controlador_prev();
            } catch (Exception ex) {
                System.out.println("[Controlador.java>getControlador()] Excepcion " + ex.getClass() + " generando la instancia del Controlador Principal");
                System.exit(0);
            }
        }
        return instancia;
    }

    //#endregion

    //#region GET_P/C
    public PanelControl getPanelControl() {
        if (this.pc == null) {
            this.pc = new PanelControl();
        }
        return this.pc;
    }

    public void setPanelControl(PanelControl panelControl) {
        this.pc = panelControl;
    }

    //#endregion

    //#region GET_CFCT
    public synchronized ControladorFacturas getControladorFacturas() {
        if (this.cfct == null) {
            this.cfct = ControladorFacturas.getControlador();
        }
        return this.cfct;
    }

    //ANCHOR - 14-07-24 : FxCntrlTablaFCT
    public synchronized ControladorFacturas getControladorFacturas(FxCntrlTablaFCT fxc) {
        if (this.cfct == null) {
            this.cfct = new ControladorFacturas();
        }

        this.cfct.setFXcontrlTablaFCT(fxc);
        return this.cfct;
    }

    private synchronized boolean cargarPanelControl() {

        String ruta = _Ruta.FXML.getRuta() + "/PanelControl.fxml";
        System.out.println("[Acceso>cargarPanelControl] ruta  : " + ruta);

        FxmlHelper FXMLpc = new FxmlHelper(ruta);

        Parent root;

        root = FXMLpc.cargarFXML();

        Scene escena = new Scene(root);
        Stage vPC = Controlador_prev.setStage(escena, true);
        vPC.setResizable(true);
        // REVIEW - 24-05-30 : Aquí se ajusta el modo de la ventana de P/C

        vPC.setOnCloseRequest(e -> System.exit(0));
        PanelControl.setGUI(vPC);
        PanelControl.getPanelControl().mostrar();

        //Asignar el actual P/C a las clases necesarias
        //ControladorFacturas.FXcontrlTablaFCT = (FxCntrlTablaFCT)(FXMLtablafct.getFXcontr());
        //ControladorFacturas.FXcontrlVisorFCT = (FxCntrlVisorFCT)(FXMLvisorfct.getFXcontr());
        setPanelControl((PanelControl) (FXMLpc.getFXcontr()));

        return true;
    }

    public void setUsuario(String user) {
        this.usuario = user;
    }

    public static void reset() throws IOException {
        //System.out.println("[Controlador.java>reset()] El programa se cerrará!");
        quit();
        /* if(Config.getConfig(Controlador.usuario)!=null){
            cfct = getControladorFacturas();
            cd = getControladorDistribuidores();
            ccj = getControladorCaja();
            pc = getPanelControl();
        }*/
    }

    public static void quit() {
        System.exit(0);
    }

    @Override
    public void run() {
        // REVIEW - 24-07-05 : Asignaciones al empezar a ejecutarse el hilo
        cfct = getControladorFacturas();
        cfct.setName("Ctrl_FCT");
        cfct.start();
        //REVIEW - 24-06-04 : Tuve que cambiar el controlador de la clase PanelControl a public
        pc = getPanelControl();
        while (true) {
            if (getPanelControl().botonpulsado()) {
                switch (getPanelControl().seleccion()) {
                    case 1:
                        // Aquí hay que activar la Tabla/Visor (dependiendo del modo INGR/NAV) de Facturas
                        seccion = _Seccion.FACT.getSeccion();
                        if (PanelControl.getModo() == _Modo.NAV.getModo()) {
                            //Visor y Tabla FCT visibles
                            //Arrancar ctrFCT
                        } else if (PanelControl.getModo() == _Modo.INGR.getModo()) {
                            //pc.cargarTablaFacturas();
                            //this.cfct = getControladorFacturas();
                        }
                        //ANCHOR - tableView
                        cfct.mostrarTablaFacturas();
                        //Las demás ventanas se cierran
                        //El P/C se resetea ->pulsado = false
                        PanelControl.reset();
                        break;
                    case 2:
                        seccion = _Seccion.DIST.getSeccion();
                        //cfct.visible(false);
                        //ControladorDistribuidores.setEstado(1);
                        //cd.visible(true);
                        //ccj.visible(false);
                        PanelControl.reset();
                        break;
                    case 3:
                        seccion = _Seccion.NOTAS.getSeccion();
                        //cfct.visible(false);
                        //ControladorDistribuidores.setEstado(0);
                        //cd.visible(false);
                        //ccj.visible(false);
                        //verNotas();
                        PanelControl.reset();
                        break;
                    case 4:
                        seccion = _Seccion.CONFIG.getSeccion();
                        //cfct.visible(false);
                        //ControladorDistribuidores.setEstado(0);
                        //cd.visible(false);
                        //ccj.visible(false);
                        //VentanaConfig vc = new VentanaConfig();
                        //vc.setVisible(true);
                        PanelControl.reset();
                        break;
                    case 5:
                        seccion = _Seccion.CAJA.getSeccion();
                        //ccj.visible(true);
                        //cfct.visible(false);
                        //ControladorDistribuidores.setEstado(0);
                        //cd.visible(false);
                        PanelControl.reset();
                        break;
                    case 6:
                        //autosave();
                        PanelControl.reset();
                        break;
                    case 7:
                        if (PanelControl.getModo() == _Modo.NAV.getModo()) {
                            if (seccion == _Seccion.FACT.getSeccion()) {
                                //cfct.visible(true);
                            }
                        } else {
                            if (seccion == _Seccion.FACT.getSeccion()) {
                                //cfct.visible(true);
                            }
                        }
                        PanelControl.reset();
                        break;
                    case 11:
                        //ANCHOR - tableView
                        //System.out.println("[Controlador.java>run()] btn FCT desactivado!");
                        cfct.ocultarTablaFacturas();
                        PanelControl.reset();
                        break;
                    default:
                        PanelControl.reset();
                        break;
                }
            }

            //#region LATENCIA<400ms
            try {
                //System.out.println("[controlador] En el sleep: ");
                Thread.sleep(400);
            } catch (InterruptedException e) {
                System.out.println("Exc tipo " + e.getClass() + "en el run() del Controlador principal");
                System.exit(0);
            }
            //#endregion
        }
    }

    //#endregion

    //#region (AUTOSAVE)
    /*
    private void autosave() throws NullPointerException, IOException{

        boolean[] opciones =  new boolean[3];

        VentanaAutosave v = new VentanaAutosave();
        v.setVisible(true);
        while(!v.ok()){
                //System.out.println("");
            }
        if (v.fact()) {opciones[0] = true;//System.out.println("fact");} else {opciones[0] = false;}
        if (v.dist()) {opciones[1] = true;//System.out.println("dist");} else {opciones[1] = false;}
        if (v.caja()) {opciones[2] = true;//System.out.println("caja");} else {opciones[2] = false;}
        v.dispose();

       if (opciones[0] || opciones[1] || opciones[2])
           copiaseguridad(opciones);
    }

    private boolean copiaseguridad(boolean[] opciones) throws NullPointerException, IOException{
        FicheroAutomatico auto = new FicheroAutomatico(opciones, Config.getConfig(Controlador.usuario).getUsuario(),Config.getConfig(Controlador.usuario).configData.getAnho().getAnho(),Config.getConfig(Controlador.usuario).configData.getAnho().getTrimestre());
        String [] rutas = auto.getRutas();
        //System.out.println(rutas[0]);
        //System.out.println(rutas[1]);
        //System.out.println(rutas[2]);

        if (opciones[0])
        cfct.autosave(rutas[0]);
        if (opciones[1])
        cd.autosave(rutas[1]);
        if (opciones[2])
        ccj.autosave(rutas[2]);

        JOptionPane.showMessageDialog(null, "Los ficheros automáticos han sido guardados en: \n"+rutas[0]+"\n"+rutas[1]+"\n"+rutas[2]);
        return true;
    }
*/
    //#endregion

    //#region HELPERS
    public static Stage setStage(Scene escena, boolean AOT) {
        Stage st = new Stage();

        st.setScene(escena);
        st.setResizable(AOT);
        st.initModality(Modality.NONE);
        st.setAlwaysOnTop(AOT);
        //		st.show();
        return st;
    }
    //#endregion

}
