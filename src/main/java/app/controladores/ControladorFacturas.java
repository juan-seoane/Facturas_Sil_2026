package app.controladores;
public class ControladorFacturas extends Thread {


    //NOTE - 24-07-14 : Singleton??
    // NOTE - 26-03-15 : Voy a probar a cambiar la clase Singleton por la Inyeccion de Depencias (ID) más recomendable según los principios SOLID
    //#region CAMPOS_CFCT
    //NOTE - 24-07-14 : Se intenta que los campos sean privados y que sólo se acceda a ellos mediante getters ... sobre todo los que pueden llevar a conflictos, como los contrFX
    Controlador_prev ctrlPpal;
    // public PanelControl pc;
    //static VentanaFiltros filtros;
    // public ModeloFacturas m;
    // static FxCntrlTablaFCT FXcontrlTablaFCT;
    // static FxCntrlVisorFCT FXcontrlVisorFCT;
    // public static Stage tablaFCT;
    // //ANCHOR - visor FCT
    // public static Stage visorFCT;
    // TableView<Factura> tableViewFCT;
    // public static Factura facturaActual = null;
    // static boolean GUIon = false;


    //#endregion

    //#region CFCT_constr
    public ControladorFacturas() {
    }
    //#endregion

    public Controlador_prev getCtrlPpal() {
        return ctrlPpal;
    }

    public void setCtrlPpal(Controlador_prev ctrlPpal) {
        this.ctrlPpal = ctrlPpal;
    }



}
