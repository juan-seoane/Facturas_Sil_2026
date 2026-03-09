package ui.tablas;

import modelo.*;
import modelo.base.Config;
import modelo.records.Factura;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import javax.swing.*;
import java.util.*;
import java.util.ResourceBundle.Control;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;

import controladores.Controlador;
import javafx.collections.ObservableList;


public class TablaFacturas_prev extends JFrame implements KeyListener{
    
private Vector columnas;

private static boolean cambiado;	
private static int pulsado;
private int indice = 0;

private JTable tablafacturas; 
private JScrollPane panelbase;
private JPanel panelabajo;
private JPanel paneltotales;
private JPanel panelbotones;

private Label lblnumfacturas = new Label("0");
private Label lblbase = new Label("0");
private Label lbliva = new Label("0");
private Label lblsubtotal = new Label("0");
private Label lblbaseni = new Label("0");
private Label lblretenciones = new Label("0");
private Label lbltotal = new Label("0");

private Button btnfiltros;
private Button btnver;
private Button btninsertar;
private Button btnimprimir;
public static Checkbox chkfiltros;

//private static boolean filtrosActivos = false;

private Image icon;
		
    public TablaFacturas_prev(@SuppressWarnings("rawtypes") Vector<? extends Vector> vector, Vector<String> columnas) throws NullPointerException, IOException{
    // TODO  - 2024-05-13 : - Hay que conseguir que la tabla aparezca maximizada
        super("Listado de Facturas");
        this.setExtendedState(Frame.MAXIMIZED_VERT);
        this.setExtendedState(Frame.MAXIMIZED_HORIZ);
        this.setLayout(new BorderLayout());
    //	this.setResizable(false);

        tablafacturas = new JTable(vector, columnas);
        tablafacturas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        panelbase= new JScrollPane(tablafacturas);
        panelbase.setLayout(new ScrollPaneLayout());
        this.add(panelbase,BorderLayout.CENTER);
            
            panelabajo = new JPanel();
            panelabajo.setLayout(new BorderLayout());
            
            paneltotales = new JPanel();
            paneltotales.setLayout(new FlowLayout());
            paneltotales.add(new Label("base: "));
            paneltotales.add(lblbase);
            paneltotales.add(new Label("IVA: "));
            paneltotales.add(lbliva);
            paneltotales.add(new Label("subtotal: "));
            paneltotales.add(lblsubtotal);
            paneltotales.add(new Label("base N.I.: "));
            paneltotales.add(lblbaseni);
            paneltotales.add(new Label("retenciones: "));
            paneltotales.add(lblretenciones);
            paneltotales.add(new Label("TOTAL: "));
            paneltotales.add(lbltotal);
            
            panelabajo.add(paneltotales,BorderLayout.CENTER);
            
        panelbotones = new JPanel();
        panelbotones.setLayout(new FlowLayout());
        
            btnfiltros = new Button("fiLtros");
        btnver =new Button("Ver factura");			//aqui hay que hacer un array de botones...
        btninsertar = new Button("nueva Factura");
            btnimprimir = new Button("Pdf");
            chkfiltros = new Checkbox("filtros Activados");
            
            panelbotones.add(btnfiltros);
        panelbotones.add(btnver);
        panelbotones.add(btninsertar);
            panelbotones.add(btnimprimir);
            panelbotones.add(chkfiltros);
            
            panelabajo.add(panelbotones,BorderLayout.SOUTH);
            
        this.add(panelabajo,BorderLayout.SOUTH); 
            
            actualizarModelo(vector,0);
            
        this.pack();
    //	this.setVisible(true);
        
        initComponents();
            startup();
        }	
//#region get_V_FCT_casted
    /*
    private static Vector<Vector> getVectorFacturasCasted(Vector<Factura> vector) {
        Vector<Vector> vect2 = new Vector();
        for ( Factura f : vector){
            vect2.add(Factura.toVector(f));
        }
        return vect2;
    }
    */
//#endregion
    public void initComponents(){
        pulsado = 0;
        
        this.addWindowListener(new WindowAdapter(){
                public void windowClosing(WindowEvent we){
                    formWindowClosing();
                }
            });
            this.btnfiltros.addActionListener(new ActionListener(){
                            public void actionPerformed(ActionEvent ae){
                                            verFiltros();
                            }
            });
        this.btnver.addActionListener(new ActionListener(){   //un solo manejador
                public void actionPerformed(ActionEvent ae){			
                        verFactura();
                }
            });
        this.btninsertar.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent ae){			
                        nuevaFactura();
                }
            });
            this.btnimprimir.addActionListener(new ActionListener(){
                            public void actionPerformed(ActionEvent ae){
                                            imprimirInforme();
                            }
                    });
            chkfiltros.addItemListener(new ItemListener(){
                            public void itemStateChanged(ItemEvent ie){
                                            actualizar();
                            }
                    });
            ListSelectionModel row = this.tablafacturas.getSelectionModel();
            row.addListSelectionListener(new ListSelectionListener(){
                public void valueChanged(ListSelectionEvent e){
                        filaSeleccionada();
                }
            });
            
            this.setFocusable(true);
            this.setAutoRequestFocus(true);
            this.addKeyListener(this);
    }

        private ArrayList<Image> startup(){
            
            icon = Toolkit.getDefaultToolkit().getImage("imagenes/tray.png");
            setIconImage(icon);
            ArrayList<Image> listaIconos = new ArrayList<Image>();
            
            listaIconos.add(icon);
            return listaIconos;
        }
        
    public void verFactura(){
        int sel = 0;
        sel = tablafacturas.getSelectedRow();				
        if (sel >=0){
            pulsado = 1;
            cambiado = true;
            indice = sel;
        }
    }

    public void nuevaFactura(){
        pulsado = 2;
        cambiado = true;
        
    }

    public void imprimirInforme(){
        
            pulsado = 3;
            cambiado = true;
    }

    public void verFiltros(){
        pulsado = 4;
        cambiado = true;
    }

    public void reset(){
        pulsado = 0;
        cambiado = false;
    }

    public int getPulsado(){
        return pulsado;
    }

    public static void setPulsado(int p){
        pulsado = p;
        cambiado = true;
    }

    public int getIndice(){
        System.out.println("[TablaFacturas.java>getIndice()] Indice en la tabla -> "+indice);
        return indice;
    }

    public boolean haCambiado(){
        return cambiado;
    }

    public static boolean filtrosActivos(){
        return chkfiltros.getState();
    }

    public void toggleFiltros(){
        chkfiltros.setState(!chkfiltros.getState());
        actualizar();

    }

    public void actualizar(){
            pulsado = 5;
            cambiado = true;
    }

    public boolean actualizarModelo(ObservableList<Factura> facturasObs, int sel) throws NullPointerException, IOException{

        return true;
    }

    public boolean retrocederSeleccion(){
        indice--; 
        ListSelectionModel selectionModel = tablafacturas.getSelectionModel();
            if (indice<0)
                    indice = tablafacturas.getRowCount()-1;
        selectionModel.setSelectionInterval(indice, indice);
    
        return true;
    }

    public boolean seleccionarIndice(int index){
        indice = index;  
        ListSelectionModel selectionModel = tablafacturas.getSelectionModel();
        selectionModel.setSelectionInterval(indice, indice);

        return true;
    }

    public boolean avanzarSeleccion(){
        indice++;
        ListSelectionModel selectionModel = tablafacturas.getSelectionModel();
            if (indice > tablafacturas.getRowCount()-1)
                indice = 0;
        selectionModel.setSelectionInterval(indice, indice);
        return true;
    }

    public void filaSeleccionada(){
        ListSelectionModel selectionModel = tablafacturas.getSelectionModel();
        indice = selectionModel.getLeadSelectionIndex();
        if (indice <0)
            indice = 0;
        cambiado = true;
        pulsado = 6;
        //JOptionPane.showMessageDialog(null,"Ha seleccionado la factura "+(indice+1));
    }

    public boolean seleccionarPrimera(){
        indice = 0;
        ListSelectionModel selectionModel = tablafacturas.getSelectionModel();
        selectionModel.setSelectionInterval(indice, indice);
        return true;
    }

    public boolean seleccionarUltima(){
        indice = tablafacturas.getModel().getRowCount()-1;
        ListSelectionModel selectionModel = tablafacturas.getSelectionModel();
        selectionModel.setSelectionInterval(indice, indice);
        return true;
    }

    public void actualizarTotales(int numfacturas,double base, double iva, double subtotal, double baseNI, double retenc, double total){
        DecimalFormatSymbols simbolo=new DecimalFormatSymbols();
        simbolo.setDecimalSeparator('.');
        DecimalFormat formateador = new DecimalFormat("######.###",simbolo);
        
        this.lblnumfacturas.setText(formateador.format(numfacturas));
        this.lblbase.setText(formateador.format(base));
        this.lbliva.setText(formateador.format(iva));
        this.lblsubtotal.setText(formateador.format(subtotal));
        this.lblbaseni.setText(formateador.format(baseNI));
        this.lblretenciones.setText(formateador.format(retenc));
        this.lbltotal.setText(formateador.format(total));
        
    }
    //**TODO : CAMBIAR ESTE LISTENER AL CONTROLADOR FACTURAS, QUE ES LO LOGICO
    public void formWindowClosing(){
        this.dispose();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode()== KeyEvent.VK_DOWN || e.getKeyCode()== KeyEvent.VK_RIGHT ){
               avanzarSeleccion();
        }
        if(e.getKeyCode()== KeyEvent.VK_UP || e.getKeyCode()== KeyEvent.VK_LEFT ){
               retrocederSeleccion();
        }
        if(e.getKeyCode()== KeyEvent.VK_V ){
               verFactura();
        }
        if(e.getKeyCode()== KeyEvent.VK_F ){
               nuevaFactura();
        }
        if(e.getKeyCode()== KeyEvent.VK_L ){
               verFiltros();
        }
        if(e.getKeyCode()== KeyEvent.VK_A ){
            toggleFiltros();
        }
        if(e.getKeyCode()== KeyEvent.VK_P ){
            imprimirInforme();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}


