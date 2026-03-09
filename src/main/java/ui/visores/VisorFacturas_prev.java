package ui.visores;

import modelo.*;
import modelo.records.Extracto;
import modelo.records.Factura;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.util.*;

public class VisorFacturas_prev extends JFrame{
	
        public static VisorFacturas_prev instancia = null;
	public static Factura f;
	public static int index;

	private boolean cambiado;
	private int pulsado = 0;

	private final int ATRAS = 1;
	private final int ADELANTE = 2;
	private final int EDITAR = 3;
	private final int NOTA = 4;
	private final int BORRAR = 5;
        private final int GUARDARNOTA = 6;

	private String titulo;
	private int x;
	private int y;

	private Label lblFecha;
	private Label lblNumeroFactura;
	private Label lblTipoGasto;
	
	private Label lblRS;
	
	private Label chbxDevolucion;
	private Label txtRetenciones;
	
	public ArrayList<Extracto> subfacturas;
	public int numExtractos;
	Vector<Vector > extractos;
	Vector columnas;
	Vector<Vector > totales;
	Vector columnas2;
	
	private Label lblTotalBase;
	private Label lbltipoIVA;
	private Label lblTotalIVA;
	private Label lblSubTotal;
	private Label lblTotalAbsoluto;
	private Label lblBaseNI;
	private Label lblTotalRetenciones;
	
	private TextArea txtNota;
	
	private Button btnanterior;
	private Button btneditarfactura;
	private Button btnguardarnota;
	private Button btnsiguiente;
	private Button btnborrarfactura;
	private Button btneditarnota;
	
	private JPanel panelDatosFactura;
	private JPanel panelDatosRS;
	private JPanel panelNotas;
	private JPanel panelTipoGasto;
	private JPanel panelSubfacturas;
	private JScrollPane panelImportes;
	private JPanel panelTotales;
	private JScrollPane panelTotales1;
	private JPanel panelControles;
	
	private JPopupMenu popup;
	private JMenuItem pop01;
	private JMenuItem pop02;
	private JMenuItem pop03;
	
	private JTable tabla;
	private JTable tabla2;	
/** 
	Construye el objeto VisorFacturas con un titulo, una posicion y una factura.
	El visor es un JFrame usado como Vista en una arquitectura MVC
	El objeto se reconstruye cada vez que avanzas en la lista de facturas que gestiona el Modelo.
	@param titulo el titulo de la ventana del visor.
	@param x la coordenada x de la esquina superior izquierda.
	@param y la coordenada y de la esquina superior izquierda.
	@param index el indice de la factura actual en el ArrayList de facturas que gestiona el modelo.
	@param factura la factura que se visualiza desde el visor.
	@see ControladorFacturas()
*/
	private VisorFacturas_prev(String titulo, int x, int y, int index, Factura f){
		
		super(titulo);		
		this.titulo = titulo;
		this.x = x;
		this.y = y;
		this.index = index;
		this.pulsado = 0;
		this.cambiado = false;
		
		this.f = f;
		numExtractos = 4;
		
		initComponents();
		initControls();
		
		this.pack();
//		this.setVisible(true);

	}
        
        public static VisorFacturas_prev getVisor(int ind,Factura fact){
            
            if (instancia == null)
                instancia = new VisorFacturas_prev("Visor de Facturas",200,200,0,fact);
            
            instancia.reemplazarDatos(ind, fact);
            return instancia;
        }
        
/** 	
	Inicializa la Interfaz del Visor de Facturas, a partir de los parametros x (coord), y (coord) y f (factura).
	@see VisorFacturas().
*/
	public void initComponents(){
				
		this.setBounds(this.x,this.y,530,515);
		this.setResizable(true);
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
				
		//POPUP MENU
		
		popup = new JPopupMenu();
		pop01 = new JMenuItem("Salir");
		pop02 = new JMenuItem("Atras");
		pop03 = new JMenuItem("Adelante");
		popup.add(pop01);
		popup.add(pop02);
		popup.add(pop03);
		this.add(popup);
		
		//DATOS FACTURA
		
		panelDatosFactura = new JPanel();
		panelDatosFactura.setBorder(new TitledBorder("Datos Factura"));
		panelDatosFactura.setLayout(new FlowLayout());
		
		lblFecha = new Label(f.getFecha().toString());
		panelDatosFactura.add(new Label("Fecha"));
		panelDatosFactura.add(lblFecha);

		lblNumeroFactura = new Label(f.getNumeroFactura());
		panelDatosFactura.add(new Label("Num Factura"));
		panelDatosFactura.add(lblNumeroFactura);
		
		lblTipoGasto = new Label(f.getCategoria().getTipo());
		panelDatosFactura.add(lblTipoGasto);
		panelDatosFactura.setComponentPopupMenu(popup);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;
		this.add(panelDatosFactura,c);
		
		//DATOS RAZON SOCIAL
		
		panelDatosRS = new JPanel();
		panelDatosRS.setLayout(new BorderLayout());
		panelDatosRS.setBorder(new TitledBorder("Razon Social"));
		
		lblRS = new Label(f.getRS().getNombre());
		panelDatosRS.add(lblRS,BorderLayout.CENTER);
		panelDatosRS.setComponentPopupMenu(popup);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 1.0;
		c.weighty = 1.0;
		this.add(panelDatosRS,c);
		
		//DATOS IMPORTES
		
		extractos = new Vector<Vector >();
		columnas = new Vector();
		
		columnas.add(new String("Base"));
		columnas.add(new String("tipo"));
		columnas.add(new String("IVA"));
		columnas.add(new String("Total"));
	/*	
		subfacturas = f.getSubfacturas();		
		
		for (int i=0; i<subfacturas.size(); i++){
			Vector temp = new Vector();
			temp.add(subfacturas.get(i).getBase());
			temp.add(subfacturas.get(i).getTipoIVA().getFormat());
			temp.add(""+subfacturas.get(i).getIVA());
			temp.add(""+subfacturas.get(i).getTotal());
			extractos.add(temp);
		}
	*/	
		tabla = new JTable(extractos,columnas);
		
		panelSubfacturas = new JPanel();
		panelSubfacturas.setLayout(new BorderLayout());
		panelSubfacturas.setBorder(new TitledBorder("Subfacturas"));

		panelImportes = new JScrollPane(tabla);
		panelImportes.setLayout(new ScrollPaneLayout());
		panelImportes.setPreferredSize(new Dimension(530,120));
		panelSubfacturas.add(panelImportes,BorderLayout.CENTER);
		panelSubfacturas.setComponentPopupMenu(popup);
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 2;
		c.gridheight = 2;
		c.weightx = 1.0;
		c.weighty = 1.0;
		this.add(panelSubfacturas,c);
		
		//DATOS TOTALES
			
		panelTotales = new JPanel();
		panelTotales.setLayout(new GridLayout(1,1));
		panelTotales.setBorder(new TitledBorder("Totales"));
                panelTotales.setMinimumSize(new Dimension(445,70));
		
		totales = new Vector<Vector >(); 
		
		Vector temp = new Vector();
		
		temp.add(f.getTotales().getBase());
		temp.add(f.getTotales().getTipoIVA().getFormat());
		temp.add(f.getTotales().getIVA());
		temp.add(f.getTotales().getSubtotal());	
		temp.add(f.getTotales().getBaseNI());
		temp.add(f.getTotales().getRet());
		temp.add(f.getTotales().getRetenciones());
		temp.add(f.getTotales().getTotal());
		
		totales.add(temp);
		
		columnas2 = new Vector();
		columnas2.add("Base");
		columnas2.add("tipo");
		columnas2.add("IVA");
		columnas2.add("Subtotal");
		columnas2.add("Base NI");
		columnas2.add("% Ret");
		columnas2.add("Retenciones");
		columnas2.add("TOTAL");
		
		tabla2 = new JTable(totales,columnas2);
		
		panelTotales1 = new JScrollPane(tabla2);
		panelTotales1.setPreferredSize(new Dimension(440,60));
		panelTotales1.setLayout(new ScrollPaneLayout());
		
		// btnTotalCalcular = new Button("...");
		// panelTotales2.add(btnTotalCalcular);
		
		panelTotales.add(panelTotales1);
		panelTotales.setComponentPopupMenu(popup);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 6;
		c.gridheight = 2;
		c.weightx = 1.0;
		c.weighty = 1.0;
		this.add(panelTotales,c);
		
		//NOTA
		
		panelNotas = new JPanel();
		panelNotas.setBorder(new TitledBorder("Notas"));
		panelNotas.setLayout(new GridLayout(1,1));
		txtNota = new TextArea("",4,30);              
		
		panelNotas.add(txtNota);
		panelNotas.setComponentPopupMenu(popup);
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 8;
		c.gridheight = 2;
		c.weightx = 1.0;
		c.weighty = 1.0;
		this.add(panelNotas,c);
		
		//BOTONES
		
		panelControles = new JPanel();
		btnanterior = new Button("Anterior");
		btnborrarfactura = new Button("Borrar Factura");
		btnguardarnota = new Button("Guardar Nota");
		btneditarfactura = new Button("Editar Factura");
		btnsiguiente = new Button("Siguiente");
		
		panelControles.add(btnanterior);
		panelControles.add(btnborrarfactura);
		panelControles.add(btnguardarnota);
		panelControles.add(btneditarfactura);
		panelControles.add(btnsiguiente);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 12;
		c.gridheight = 1;
		c.weightx = 1.0;
		c.weighty = 1.0;
		this.add(panelControles,c);

	}
        
        public void reemplazarDatos(int in, Factura fc){
            index = in;
            this.setTitle("Factura numero "+fc.getID());
            lblFecha.setText(fc.getFecha().toString());
            
            lblNumeroFactura.setText(fc.getNumeroFactura());
            lblTipoGasto.setText(fc.getCategoria().getTipo());
            lblRS.setText(fc.getRS().getNombre());
            
            extractos.clear();
/*            
			subfacturas = fc.getSubfacturas();
            for (int i=0; i<subfacturas.size(); i++){
                    Vector temp = new Vector();
                    temp.add(subfacturas.get(i).getBase());
                    temp.add(subfacturas.get(i).getTipoIVA().getFormat());
                    temp.add(""+subfacturas.get(i).getIVA());
                    temp.add(""+subfacturas.get(i).getTotal());
                    extractos.add(temp);
            }
*/
            tabla.setModel(new DefaultTableModel(extractos,columnas));
            
            totales.clear();
            totales = new Vector<Vector >(); 
            Vector temp = new Vector();

            temp.add(fc.getTotales().getBase());
            temp.add(fc.getTotales().getTipoIVA().getFormat());
            temp.add(fc.getTotales().getIVA());
            temp.add(fc.getTotales().getSubtotal());	
            temp.add(fc.getTotales().getBaseNI());
            temp.add(fc.getTotales().getRet());
            temp.add(fc.getTotales().getRetenciones());
            temp.add(fc.getTotales().getTotal());

            totales.add(temp);

            columnas2 = new Vector();
            columnas2.add("Base");
            columnas2.add("tipo");
            columnas2.add("IVA");
            columnas2.add("Subtotal");
            columnas2.add("Base NI");
            columnas2.add("% Ret");
            columnas2.add("Retenciones");
            columnas2.add("TOTAL");

            tabla2.setModel(new DefaultTableModel(totales,columnas2));
            
            txtNota.setText(fc.getNota().getTexto());
            
            repaint();
        }
/** 	
	Inicializa los manipuladores de eventos del Visor de Facturas, esperando que los recoja el controlador de facturas.
	@see VisorFacturas().
	@see ControladorFacturas().
*/	
	public boolean initControls(){
	
	//CONTROLADOR VISOR
		
	this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent we){
				formWindowClosing();
			}
		});
						
	this.btnanterior.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				atras();
			}
		});
		
	this.btnsiguiente.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				adelante();
			}
		});
	this.btneditarfactura.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				editarFactura();
			}
		});
	this.btnborrarfactura.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				borrarFactura();
			}
		});
        this.btnguardarnota.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				guardarNota();
			}
		});
	this.addMouseListener( new MouseAdapter(){ 
		public void mouseReleased( MouseEvent e ){
		if ( e.isPopupTrigger() ) popup.show((Component)(e.getSource()),e.getX(),e.getY() );
		} 
	});
								
	this.pop01.addActionListener( new ActionListener(){ 
		public void actionPerformed( ActionEvent e ){ 
			formWindowClosing();
		} 
	});
						
	this.pop02.addActionListener( new ActionListener(){ 
		public void actionPerformed( ActionEvent e ){ 
			atras();
		} 
	});
						
	this.pop03.addActionListener( new ActionListener(){ 
		public void actionPerformed( ActionEvent e ){ 
			adelante();
			} 
		});
//        JOptionPane.showMessageDialog(null, "CARGADOS LOS ESCUCHADORES DEL VISOR");                   
	return true;
	}
	
	public void reset(){
		cambiado = false;
		pulsado = 0;
	}
	
	public boolean haCambiado(){
		return cambiado;
	}
	
	public int getPulsado(){
		return pulsado;
	}	
	
        public String getNota(){
            return this.txtNota.getText();
        }
	
        public static int getIndex(){
            ////System.out.println("++++++++++++++++++++++++++++ Indice en el visor -> "+index);
		return index;	
	}
        
        public void setIndex(int i){
            index = i;
        }
	
        public void atras(){
		pulsado = ATRAS;
		cambiado = true;
	}
	
	public void adelante(){
		pulsado = ADELANTE;
		cambiado = true;
	}
	
	public void editarFactura(){
		pulsado = EDITAR;	
		cambiado = true;
	}
	
	public void borrarFactura(){
		pulsado = BORRAR;
		cambiado = true;
	}
        
        public void guardarNota(){
                pulsado = GUARDARNOTA;
                cambiado = true;
        }
	
        public void avanzarIndex(){
		index++;
	}

	public void retrocederIndex(){
		index--;
	}
	
	public void formWindowClosing(){
		this.setVisible(false);
	}
}
