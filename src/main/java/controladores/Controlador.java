package controladores;

import java.io.IOException;
import java.util.concurrent.BrokenBarrierException;

import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modelo.base.Config;
import ui.fxcontrollers.FxCntrlTablaFCT;
import ui.fxcontrollers.PanelControl;

public class Controlador extends Thread {
// NOTE 26-03-10 : Hacer unos Enum con estas constantes??
    //#region CONST
    public static final int NAV = 0;
    public static final int INGR = 1;

    public static final int FACT = 1;
    public static final int DIST = 2;
    public static final int CAJA = 3;
    public static final int NOTAS = 4;
    public static final int CONFIG = 5;
    //#endregion

    //#region CAMPOS
    //static ControladorFicheros cfch;
    static ControladorFacturas cfct;
    static Controlador instancia;

    //static ControladorDistribuidores cd;
    //static ControladorEntradaCaja ccj;
    static PanelControl pc;
    //static VisorNotas notas;
    static int seccion = FACT;

    static String usuario;

    //    public static CyclicBarrier barreraControladores;
    //endregion

    //#region CONTR
    private Controlador() throws IOException, InterruptedException, BrokenBarrierException {
        //System.out.println("[Controlador>constructor] Comenzando el constructor del Controlador Principal");

        Controlador.usuario = Config.getConfig().getUsuario();
        //System.out.println("[Controlador>constructor] El usuario es "+ Config.getConfig().getUsuario());

        /* barreraControladores = new CyclicBarrier(2,() -> {
            //System.out.println("[Controlador>barreraControladores] El hilo "+Thread.currentThread().getName() + " acaba de entrar en la barreraControladores");
        }); */

        //System.out.println("[Controlador>constructor] Terminando el constructor del Controlador Principal");
    }

    //#endregion

    //#region (GET_CFICH)
    /*
    public ControladorFicheros getControladorFicheros(){
		return Controlador.cfch;
	}
*/
    //#endregion

    //#region GET CTRL_PPAL
    public static synchronized Controlador getControlador() {
        if (instancia == null) {
            try {
                instancia = new Controlador();
            } catch (IOException | InterruptedException | BrokenBarrierException e) {
                //System.out.println("[Controlador.java>getControlador()] Excepcion generando la instancia del Controlador Principal");
                System.exit(0);
                //                e.printStackTrace();
            }
        }
        return instancia;
    }

    //#endregion

    //#region GET_P/C
    public static PanelControl getPanelControl() {
        if (pc == null) {
            pc = new PanelControl();
        }
        return pc;
    }

    public static void setPanelControl(PanelControl panelControl) {
        pc = panelControl;
    }

    //#endregion

    //#region GET_CFCT
    public static synchronized ControladorFacturas getControladorFacturas() {
        if (cfct == null) {
            cfct = ControladorFacturas.getControlador();
        }
        return cfct;
    }

    //ANCHOR - 14-07-24 : FxCntrlTablaFCT
    public static ControladorFacturas getControladorFacturas(FxCntrlTablaFCT fxc) {
        if (cfct == null) {
            cfct = ControladorFacturas.getControlador();
        }

        cfct.setFXcontrlTablaFCT(fxc);
        return cfct;
    }

    //#endregion

    //#region (GET_CDIST)
    /*
    public ControladorCaja getControladorDist(){
		return Controlador.cd;
	}
*/
    //#endregion

    //#region (GET_CCJA)
    /*
    public ControladorCaja getControladorCaja(){
		return Controlador.ccj;
	}
*/
    //#endregion

    //#region GETTERS/SETTERS
    public static String getUsuario() {
        return usuario;
    }

    public static void setUsuario(String user) {
        Controlador.usuario = user;
    }

    //#endregion

    //#region RESET
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

    //#endregion

    //#region VER??
    /*public static void verNotas(){
        seccion = NOTAS;
        notas = new VisorNotas();
        notas.getpanelfacturas().getpanelprincipal().setLayout(new GridBagLayout());
        notas.getpaneldistribuidores().getpanelprincipal().setLayout(new GridBagLayout());
        notas.getpanelCajas().getpanelprincipal().setLayout(new GridBagLayout());

        ArrayList<Button> botones = new ArrayList<Button>();

        for (final Factura f : new Fichero<Factura>(Config.getConfig().getRutaFCT()).leer()){
            String nota = f.getNota().getTexto().toString();
            //JOptionPane.showMessageDialog(null,"Nota "+f.getID()+" "+nota);
            if (!nota.trim().toUpperCase().equals("NUEVA NOTA") & !nota.trim().toUpperCase().equals("")){
                JPanel panel = new JPanel();
                panel.setLayout(new BorderLayout());
                panel.add(f.getNota().format(5,30),BorderLayout.CENTER);
                Button nuevoboton = new Button("FCT "+f.getID());
                botones.add(nuevoboton);
                panel.add(nuevoboton,BorderLayout.EAST);
                notas.getpanelfacturas().getpanelprincipal().add(panel);
            }
        }
        for (final RazonSocial rs : new Fichero<RazonSocial>(Config.getConfig().getRutaRS()).leer()){
            String nota = (rs.getNota().getTexto().toString()!=null?rs.getNota().getTexto().toString():"");
            //JOptionPane.showMessageDialog(null,"Nota "+f.getID()+" "+nota);
            if (!nota.trim().toUpperCase().equals("NUEVA NOTA") & !nota.trim().toUpperCase().equals("")){
                JPanel panel = new JPanel();
                panel.setLayout(new BorderLayout());
                panel.add(rs.getNota().format(5,30),BorderLayout.CENTER);
                Button nuevoboton = new Button("RS "+ rs.getID());
                botones.add(nuevoboton);
                panel.add(nuevoboton,BorderLayout.EAST);
                notas.getpaneldistribuidores().getpanelprincipal().add(panel);
            }
        }
        for (finalEntradaCajac : new Fichero<Caja>(Config.getConfig().getRutaCJA()).leer()){
            String nota = (c.getNota().getTexto().toString()!=null?c.getNota().getTexto().toString():"");
            JOptionPane.showMessageDialog(null,"Nota "+f.getID()+" "+nota);
            if (!nota.trim().toUpperCase().equals("NUEVA NOTA") & !nota.trim().toUpperCase().equals("")){
                JPanel panel = new JPanel();
                panel.setLayout(new BorderLayout());
                panel.add(c.getNota().format(5,30),BorderLayout.CENTER);
                Button nuevoboton = new Button("CJA "+ c.getID());
                botones.add(nuevoboton);
                panel.add(nuevoboton,BorderLayout.EAST);
                notas.getpanelCajas().getpanelprincipal().add(panel);
            }
        }
        for (Button b : botones){
            b.addActionListener(new ActionListener(){
                        public void actionPerformed(ActionEvent ae){
                            JOptionPane.showMessageDialog(null,"Ha seleccionado ver la factura "+((Button)ae.getSource()).getLabel().trim());
                            if (((Button)ae.getSource()).getLabel().trim().split(" ")[0].equals("FCT")){
                                int index = Integer.parseInt(((Button)ae.getSource()).getLabel().trim().split(" ")[1]);
                                index--;
                                verFactura(index);
                            }
                            if (((Button)ae.getSource()).getLabel().trim().split(" ")[0].equals("RS")){
                                int index = Integer.parseInt(((Button)ae.getSource()).getLabel().trim().split(" ")[1]);
                                index--;
                                //System.out.println("indice de la razon social seleccionada: "+index);
                                verDistribuidor(index);
                            }
                            if (((Button)ae.getSource()).getLabel().trim().split(" ")[0].equals("CJA")){
                                int index = Integer.parseInt(((Button)ae.getSource()).getLabel().trim().split(" ")[1]);
                                index--;
                                //System.out.println("indice de la razon social seleccionada: "+index);
                                verCaja(index);
                            }
                        }
                });
        }
        notas.pack();
        notas.setVisible(true);
    }

    public static void verFactura(int index){
        PanelControl.pulsarboton(1);throws InterruptedException, BrokenBarrierException{
        if (cfct == null){
        seccion = FACT;
        notas.dispose();
        cfct.visible(true);
        cfct.visorVisible(true);
        cfct.actualizarVisor(index);
    }

    public static void verDistribuidor(int index){
        PanelControl.pulsarboton(2);
        seccion = DIST;
        notas.dispose();
        cd.visible(true);
        cd.verRS(index);
    }

    public static void verCaja(int index){
        PanelControl.pulsarboton(2);
        seccion = CAJA;
        notas.dispose();
        ccj.visible(true);
        ccj.verCaja(index);
    }*/
    //#endregion

    //#region RUN_SWITCH
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
                        seccion = FACT;
                        if (PanelControl.getModo() == NAV) {
                            //Visor y Tabla FCT visibles
                            //Arrancar ctrFCT
                        } else if (PanelControl.getModo() == INGR) {
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
                        seccion = DIST;
                        //cfct.visible(false);
                        //ControladorDistribuidores.setEstado(1);
                        //cd.visible(true);
                        //ccj.visible(false);
                        PanelControl.reset();
                        break;
                    case 3:
                        seccion = NOTAS;
                        //cfct.visible(false);
                        //ControladorDistribuidores.setEstado(0);
                        //cd.visible(false);
                        //ccj.visible(false);
                        //verNotas();
                        PanelControl.reset();
                        break;
                    case 4:
                        seccion = CONFIG;
                        //cfct.visible(false);
                        //ControladorDistribuidores.setEstado(0);
                        //cd.visible(false);
                        //ccj.visible(false);
                        //VentanaConfig vc = new VentanaConfig();
                        //vc.setVisible(true);
                        PanelControl.reset();
                        break;
                    case 5:
                        seccion = CAJA;
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
                        if (PanelControl.getModo() == NAV) {
                            if (seccion == FACT) {
                                //cfct.visible(true);
                            }
                        } else {
                            if (seccion == FACT) {
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
                e.printStackTrace();
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
        st.setResizable(false);
        st.initModality(Modality.NONE);
        st.setAlwaysOnTop(AOT);
        //		st.show();
        return st;
    }
    //#endregion

}
