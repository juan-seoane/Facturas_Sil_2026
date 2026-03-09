              package ui.formularios;

import modelo.*;
import modelo.base.Config;
import modelo.records.Extracto;
import modelo.records.Factura;
import modelo.records.NIF;
import modelo.records.TipoGasto;
import modelo.records.TipoIVA;
import modelo.records.Totales;
import ui.helpers.Calculadora;

import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

public class FormularioFact extends JFrame {
                                                                                                
    private static FormularioFact instancia = null;
    private int x;
    private int y;
    private String estado = "insertando";
    private TextField txtDia;
    private TextField txtMes;
    private TextField txtAnho;
    private TextField txtNumeroFactura;
    private TextField txtLetraCIFRS;
    private TextField txtNumNIFRS;
    private TextField txtLetraNIFRS;
    private Button btndistribuidor;
    private TextField txtRS;
    private Choice chTipoGasto;
    private Checkbox chbxDevolucion;
    private TextField txtRetenciones;
    private Button btnCalculadora;
    private int numExtractos;
    private TextField[] txtBase;
    private Choice[] chIVA;
    private TextField[] txtIVA;
    private TextField[] txtTotal;
    private Button[] btnCalcular;
    private TextField txtTotalBase;
    private Choice chTotalIVA;
    private TextField txtTotalIVA;
    private TextField txtTotalTotal;
    private TextField txtTotalAbsoluto;
    private Button btnTotalCalcular;
    private TextField txtBaseNI;
    private TextField txtTotalRetenciones;
    private JTextArea txtNota;
    private Button btnOK;
    private Button btnCancel;
    private JPanel norte;
    private JPanel centro;
    private JPanel sur;
    private JPanel panelDatosFactura;
    private JPanel panelDatosRS;
    private JPanel panelNotas;
    private JPanel panelTipoGasto;
    private JPanel[] panelImportes;
    private JPanel panelTotales;
    private JPanel panelTotales1;
    private JPanel panelTotales2;
    private JPanel panelesImportes;
    private JPanel panelbotones;
    private static JPopupMenu popup;
    private JMenuItem pop01;
    private JMenuItem pop02;
    private JMenuItem pop03;
    private JMenuItem pop04;
    private ArrayList<Extracto> subfacturas;
    private Totales totales;
    private boolean enviado = false;
    private boolean revisarNIF = false;
    private boolean revisarRS = false;
    private boolean ventanaRS = false;
    private boolean pasoatras = false;
    private boolean pasoadelante = false;

    private FormularioFact() {
        super("Nueva Factura");

        this.setLayout(new BorderLayout());
        this.setResizable(false);

        numExtractos = 4;
        this.subfacturas = new ArrayList<Extracto>();

        initComponents();
        initControls();
        this.pack();
//        this.setVisible(true);
    }

    public static FormularioFact getFormulario() {
        if (instancia == null) {
            instancia = new FormularioFact();
        }
        return instancia;

    }

    public boolean initComponents() {

        //POPUP MENU

        popup = new JPopupMenu();
        pop01 = new JMenuItem("Borrar Formulario");
        pop02 = new JMenuItem("Paso Atras");
        pop03 = new JMenuItem("Paso Adelante");
        pop04 = new JMenuItem("Borrar Datos Numericos");
        popup.add(pop01);
        popup.add(pop02);
        popup.add(pop03);
        popup.add(pop04);

        this.add(popup);


        //DATOS FACTURA
        norte = new JPanel();
        norte.setLayout(new GridLayout(3, 1));

        panelDatosFactura = new JPanel();
        panelDatosFactura.setBorder(new TitledBorder(""));
        panelDatosFactura.setLayout(new FlowLayout());
        panelDatosFactura.setSize(new Dimension((int) this.getSize().getWidth(), 20));
        norte.add(panelDatosFactura);

        txtDia = new TextField("", 2);
        panelDatosFactura.add(new Label("DD"));
        panelDatosFactura.add(txtDia);
        txtMes = new TextField("", 2);
        panelDatosFactura.add(new Label("MM"));
        panelDatosFactura.add(txtMes);
        txtAnho = new TextField("", 4);
        panelDatosFactura.add(new Label("AAAA"));
        panelDatosFactura.add(txtAnho);
        txtNumeroFactura = new TextField("", 20);
        panelDatosFactura.add(new Label("Num Factura"));
        panelDatosFactura.add(txtNumeroFactura);
        panelDatosFactura.setComponentPopupMenu(popup);

        //DATOS RAZON SOCIAL

        panelDatosRS = new JPanel();
        panelDatosRS.setLayout(new FlowLayout());
        norte.add(panelDatosRS);

        txtLetraCIFRS = new TextField("", 1);
        txtNumNIFRS = new TextField("", 8);
        txtLetraNIFRS = new TextField("", 1);
        panelDatosRS.add(new Label("CIF"));
        panelDatosRS.add(txtLetraCIFRS);
        panelDatosRS.add(txtNumNIFRS);
        panelDatosRS.add(txtLetraNIFRS);

        btndistribuidor = new Button("Nombre");
        panelDatosRS.add(btndistribuidor);
        txtRS = new TextField("", 36);
        panelDatosRS.add(new Label("R.S."));
        panelDatosRS.add(txtRS);
        panelDatosRS.setComponentPopupMenu(popup);

        //DATOS TIPO GASTO

        panelTipoGasto = new JPanel();
        panelTipoGasto.setLayout(new FlowLayout());
        norte.add(panelTipoGasto);

        chTipoGasto = new Choice();

        for (TipoGasto t : Config.getConfig().getTiposGasto()) {
            chTipoGasto.add(t.tipo);
        }

        panelTipoGasto.add(new Label("Concepto"));
        panelTipoGasto.add(chTipoGasto);

        chbxDevolucion = new Checkbox();

        panelTipoGasto.add(new Label("   "));
        panelTipoGasto.add(new Label("Devolucion"));
        panelTipoGasto.add(chbxDevolucion);

        txtRetenciones = new TextField("", 2);

        panelTipoGasto.add(new Label("Retenciones"));
        panelTipoGasto.add(txtRetenciones);
        panelTipoGasto.add(new Label("%"));

        btnCalculadora = new Button("Calc");

        panelTipoGasto.add(btnCalculadora);
        panelTipoGasto.setComponentPopupMenu(popup);

        this.add(norte, BorderLayout.NORTH);
        //DATOS IMPORTES

        centro = new JPanel();
        centro.setLayout(new GridLayout(1, 1));

        panelesImportes = new JPanel();
        panelesImportes.setLayout(new GridLayout(4, 1));

        panelImportes = new JPanel[numExtractos];
        txtBase = new TextField[numExtractos];
        chIVA = new Choice[numExtractos];
        txtIVA = new TextField[numExtractos];
        txtTotal = new TextField[numExtractos];
        btnCalcular = new Button[numExtractos];

        for (int i = 0; i < numExtractos; i++) {

            panelImportes[i] = new JPanel();
            panelImportes[i].setLayout(new FlowLayout());
            // panelImportes.setBorder(new LineBorder(new Color(0,0,0),1));

            panelImportes[i].add(new Label("Base"));
            txtBase[i] = new TextField("", 8);
            panelImportes[i].add(txtBase[i]);

            panelImportes[i].add(new Label("tipo"));
            chIVA[i] = new Choice();
            for (TipoIVA t : Config.getConfig().getTiposIVA()) {
                chIVA[i].add("" + t.getValor());
            }
            panelImportes[i].add(chIVA[i]);
            panelImportes[i].add(new Label("%"));

            panelImportes[i].add(new Label("IVA"));
            txtIVA[i] = new TextField("", 8);
            panelImportes[i].add(txtIVA[i]);

            panelImportes[i].add(new Label("TOTAL"));
            txtTotal[i] = new TextField("", 8);
            panelImportes[i].add(txtTotal[i]);

            btnCalcular[i] = new Button("...");
            panelImportes[i].add(btnCalcular[i]);

            panelImportes[i].setComponentPopupMenu(popup);
            panelesImportes.add((JPanel) panelImportes[i]);

        }
        centro.add(panelesImportes, BorderLayout.CENTER);
        this.add(centro, BorderLayout.CENTER);

        //DATOS TOTALES
        sur = new JPanel();
        sur.setLayout(new BorderLayout());

        panelTotales = new JPanel();
        panelTotales1 = new JPanel();
        panelTotales2 = new JPanel();
        panelTotales.setLayout(new GridLayout(2, 1));
        panelTotales.setMaximumSize(new Dimension(600, 600));

        panelTotales1.setLayout(new BoxLayout(panelTotales1, BoxLayout.LINE_AXIS));
        panelTotales2.setLayout(new BoxLayout(panelTotales2, BoxLayout.LINE_AXIS));
        panelTotales.setBorder(new TitledBorder("Totales"));
        // panelTotales.setBorder(new LineBorder(new Color(0,0,0),1));

        panelTotales1.add(new Label("Base"));
        txtTotalBase = new TextField("", 8);
        panelTotales1.add(txtTotalBase);

        panelTotales1.add(new Label("tipo"));
        chTotalIVA = new Choice();
        for (TipoIVA t : Config.getConfig().getTiposIVA()) {
            chTotalIVA.add("" + t.getValor());
        }
        chTotalIVA.add("V");
        panelTotales1.add(chTotalIVA);
        panelTotales1.add(new Label("%"));

        panelTotales1.add(new Label("IVA"));
        txtTotalIVA = new TextField("", 8);
        panelTotales1.add(txtTotalIVA);

        panelTotales1.add(new Label("SUBTOTAL"));
        txtTotalTotal = new TextField("", 8);
        panelTotales1.add(txtTotalTotal);
        panelTotales1.setComponentPopupMenu(popup);

        panelTotales2.add(new Label("Base NI"));
        txtBaseNI = new TextField("", 8);
        panelTotales2.add(txtBaseNI);

        panelTotales2.add(new Label("Total Ret"));
        txtTotalRetenciones = new TextField("", 8);
        panelTotales2.add(txtTotalRetenciones);

        panelTotales2.add(new Label("TOTAL"));
        txtTotalAbsoluto = new TextField("", 8);
        panelTotales2.add(txtTotalAbsoluto);

        btnTotalCalcular = new Button("...");
        panelTotales2.add(btnTotalCalcular, BorderLayout.EAST);
        panelTotales2.setComponentPopupMenu(popup);

        panelTotales.add(panelTotales1);
        panelTotales.add(panelTotales2);
        panelTotales.setSize(panelTotales.getPreferredSize());
        panelTotales.setComponentPopupMenu(popup);
        sur.add(panelTotales, BorderLayout.NORTH);


        //NOTA

        panelNotas = new JPanel();
        panelNotas.setBorder(new TitledBorder("Notas"));
        panelNotas.setLayout(new FlowLayout());
        panelNotas.setComponentPopupMenu(popup);

        sur.add(panelNotas, BorderLayout.CENTER);
        txtNota = new JTextArea(7, 35);
        panelNotas.add(txtNota);
        txtNota.setInheritsPopupMenu(true);

        //BOTONES
        panelbotones = new JPanel();
        panelbotones.setLayout(new BorderLayout());
        btnOK = new Button("  OK  ");
        btnCancel = new Button(" Cancel ");
        panelbotones.add(btnOK, BorderLayout.CENTER);
        panelbotones.add(btnCancel, BorderLayout.NORTH);
        sur.add(panelbotones, BorderLayout.EAST);

        this.add(sur, BorderLayout.SOUTH);

        //CAMPOS AUTOCOMPLETADOS
        this.txtAnho.setText(Config.getConfig().getAnho().getAnho() + "");

        return true;
    }

    public boolean initControls() {

        //CONTROLADOR FORMULARIO

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                formWindowClosing();
            }
        });

        this.btnOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                //JOptionPane.showMessageDialog(null,"Boton OK pulsado!");
                enviarFormulario();
            }
        });
        
        this.btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                //JOptionPane.showMessageDialog(null,"Boton Cancel pulsado!");
                reset();
                dispose();
            }
        });
        
        this.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popup.show((Component) (e.getSource()), e.getX(), e.getY());
                }
            }
        });

        this.pop01.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                limpiarFormulario();
            }
        });

        this.pop02.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
//                JOptionPane.showMessageDialog(null, "Ha pulsado el boton 'paso atras'");
               pasoatras = true;
            }
        });

        this.pop03.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
//                JOptionPane.showMessageDialog(null, "Ha pulsado el boton 'paso adelante'");
                pasoadelante = true;
            }
        });
        
        this.pop04.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                borrarDatosNumericos();
            }
        });
        
        this.chTipoGasto.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                //System.out.println("Tipo de gasto " + ((Choice) e.getSource()).getSelectedItem() + " seleccionado!");
            }
        });

        this.btnCalculadora.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                Calculadora calc = new Calculadora("Calculadora");
                calc.setVisible(true);
            }
        });

        for (int i = 0; i < panelImportes.length; i++) {
            final int j = i;
            this.btnCalcular[j].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    calcular(txtBase[j], chIVA[j], txtIVA[j], txtTotal[j]);
                }
            });
        }

        this.btnTotalCalcular.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                rellenarVacios();
                calcularExtractos();
                calcularTotales();
            }
        });
        this.txtLetraNIFRS.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                revisarNIF = true;
            }
        });
        this.txtRS.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                revisarRS = true;
            }
        });
        this.btndistribuidor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                ventanaRS = true;
            }
        });
//                JOptionPane.showMessageDialog(null, "CARGADOS LOS ESCUCHADORES DEL FORMULARIO");
        //
        return true;
    }

    public void reset() {
        limpiarFormulario();
        enviado = false;
        estado = "insertando";
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String e) {
        estado = e;
    }

    public void enviarFormulario() {
        calcularExtractos();
        calcularTotales();
        enviado = true;
    }

    public boolean seHaEnviado() {
        return enviado;
    }

    public void enCasodeError(boolean bool) {
        enviado = bool;
    }

    public int getDia() {
        return Integer.parseInt(txtDia.getText());
    }

    public int getMes() {
        return Integer.parseInt(txtMes.getText());
    }

    public int getAnho() {
        return Integer.parseInt(txtAnho.getText());
    }

    public String getNumeroFactura() {
        return txtNumeroFactura.getText();
    }
   
    public int getNumNIFRS() {
        return (Integer.parseInt(txtNumNIFRS.getText()));
    }

    public String getLetraNIFRS() {
        return txtLetraNIFRS.getText().toUpperCase().trim();
    }

    public String getLetraCIFRS() {
        return txtLetraCIFRS.getText().toUpperCase().trim();
    }

    public String getRS() {
        return txtRS.getText().toUpperCase().trim();
    }

    public String getTipoGasto() {
        return chTipoGasto.getSelectedItem();
    }

    public boolean esDevolucion() {
        return chbxDevolucion.getState();
    }

    public int getTipoRetenciones() {
        return Integer.parseInt(txtRetenciones.getText());
    }

    public int getNumExtractos() {
        return numExtractos;
    }

    public ArrayList<Extracto> getSubfacturas() {
        return subfacturas;
    }

    public Totales getTotales() {
        return totales;
    }

    public String getNota() {
        return txtNota.getText();
    }

    public void setPasoAtras(boolean pasoatras) {
        this.pasoatras = pasoatras;
    }

    public void setPasoAdelante(boolean pasoadelante) {
        this.pasoadelante = pasoadelante;
    }

    
    public void setNombreRS(String rs) {
        this.txtRS.setText(rs);
    }

    public void setLetraCIFRS(String letracif) {
        this.txtLetraCIFRS.setText(letracif);
    }

    public void setLetraNIFRS(String lnif) {
        this.txtLetraNIFRS.setText(lnif);
    }

    public void setNumNIFRS(String numnif) {
        this.txtNumNIFRS.setText(numnif);
    }
    
    public void setCategoria(String cat){
            try{
                this.chTipoGasto.select(cat.toUpperCase().trim());
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "no se ha encontrado la categoria "+cat);
                this.chTipoGasto.select(0);
            }
    }
 /** TODO : HAY QUE DEJAR LOS CALCULOS COMO EN LA VERSION 1.
     * @param base  
     * @param tipoIva   
     * @param iva   
     * @param total*/   
    public void calcular(TextField base, Choice tipoIva, TextField iva, TextField total) {
        
        DecimalFormatSymbols simbolo=new DecimalFormatSymbols();
        simbolo.setDecimalSeparator('.');
        DecimalFormat formateador = new DecimalFormat("######.###",simbolo);
        
        double dbbase; // = Double.parseDouble(base.getText());
        int inttipoIva = Integer.parseInt(tipoIva.getSelectedItem());
        double dbIVA;// = dbbase * inttipoIva / 100;
        double dbtotal;// = dbbase + dbIVA;
        
        if (base.getText().equals("") && iva.getText().equals("")){
            dbtotal = filtrar(total.getText());
            dbbase = (100*dbtotal)/(100+inttipoIva);
            dbIVA = dbbase * inttipoIva / 100;
            
            base.setText(formateador.format(dbbase));
            iva.setText(formateador.format(dbIVA));
            total.setText(formateador.format(dbtotal));
        }else if (total.getText().equals("") && iva.getText().equals("")){
            dbbase = filtrar(base.getText());
            dbIVA = dbbase * inttipoIva / 100;
            dbtotal = dbbase + dbIVA;
            
            base.setText(formateador.format(dbbase));
            iva.setText(formateador.format(dbIVA));
            total.setText(formateador.format(dbtotal));
        }
        else if (!base.getText().equals("")&&!iva.getText().equals("")){
            dbbase = filtrar(base.getText());
            dbIVA = filtrar(iva.getText());
            dbtotal = dbbase + dbIVA;
            
            base.setText(formateador.format(dbbase));
            iva.setText(formateador.format(dbIVA));
            total.setText(formateador.format(dbtotal));
        }

    }
/** TODO : HAY QUE SOLUCIONAR LO DE FILTRAR SUMAS EN EL FORMULARIO
     * @param base
     * @return  */
    public double filtrar(String base) {
        double restas;
        double sumas = 0;
        if (base.contains("+") || base.contains("-")) {
            String operandos[] = base.split("\\+");
            for (String op : operandos) {
                if (op.contains("-")) {
                    String operandos2[] = op.split("\\-");
                    restas = Double.parseDouble(operandos2[0]);
                    for (int i = 1; i < operandos2.length; i++) {
                        restas -= Double.parseDouble(operandos2[i]);
                    }
                    sumas += restas;
                } else {
                    sumas += Double.parseDouble(op);
                }
            }
        }
        return sumas;
    }

    public void calcularExtractos() {

        subfacturas.clear();

        for (int i = 0; i < numExtractos; i++) {
            if (Double.parseDouble(txtTotal[i].getText()) != 0.0) {
                subfacturas.add(new Extracto(Double.parseDouble(txtBase[i].getText()), Integer.parseInt(chIVA[i].getSelectedItem()), Double.parseDouble(txtIVA[i].getText()), Double.parseDouble(txtTotal[i].getText())));
            }
        }
    }

    public void calcularTotales() {
        /**
         * TODO: Solucionar el modo de averiguar la existencia de varios tipos
         * de IVA (TotalTipos) y que pasa cuando solo hay base no imponible
         */
        DecimalFormatSymbols simbolo=new DecimalFormatSymbols();
        simbolo.setDecimalSeparator('.');
        DecimalFormat formateador = new DecimalFormat("######.###",simbolo);
        
        double totalBases = 0.00;
        double baseNI = 0.00;
        String totalTipos = "";
        double totalIVAS = 0.00;
        double totalesParciales = 0.00;
        double totalAbsoluto = 0.00;
        int ret = 0;
        double totalRetenciones = 0.00;

        if (this.subfacturas.size() == 1 && subfacturas.get(0).getTipoIVA().getValor() == 0) {
            totalBases = subfacturas.get(0).getBase();
            totalTipos = "22";
            totalIVAS = subfacturas.get(0).getIVA();
            totalesParciales = subfacturas.get(0).getTotal();

        } else {
            for (int i = 0; i < this.subfacturas.size(); i++) {
                if (subfacturas.get(i).getTipoIVA().getValor() != 0) {
                    totalBases += subfacturas.get(i).getBase();
                    totalTipos += subfacturas.get(i).getTipoIVA().getFormat() + "";
                    totalIVAS += subfacturas.get(i).getIVA();
                    totalesParciales += subfacturas.get(i).getTotal();
                } else {
                    baseNI += subfacturas.get(i).getBase();
                }
            }
        }

        if (!txtRetenciones.getText().equals("")) {
            ret = Integer.parseInt(txtRetenciones.getText());
        }
        totalRetenciones = ((totalBases * ret) / 100) * -1;
        totalAbsoluto = baseNI + totalesParciales + totalRetenciones;

        if (Integer.parseInt(totalTipos) > 21) {
            totalTipos = "V";
        }

        if (chbxDevolucion.getState()) {
            totalBases *= -1;
            baseNI *= -1;
            totalIVAS *= -1;
            totalesParciales *= -1;
            totalRetenciones *= -1;
            totalAbsoluto *= -1;
        }

        txtTotalBase.setText(formateador.format(totalBases));
        txtBaseNI.setText(formateador.format(baseNI));
        chTotalIVA.select(totalTipos);
        txtTotalIVA.setText(formateador.format(totalIVAS));
        txtTotalTotal.setText(formateador.format(totalesParciales));
        txtTotalRetenciones.setText(formateador.format(totalRetenciones));
        txtTotalAbsoluto.setText(formateador.format(totalAbsoluto));

        this.totales = new Totales(totalBases, (totalTipos.equals("V")), (totalTipos.equals("V") ? 0 : Integer.parseInt(totalTipos)), totalIVAS, totalesParciales, baseNI, ret, totalRetenciones, totalAbsoluto);

    }

    public boolean insertarFactura(Factura f) {
        txtDia.setText("" + f.getDia());
        txtMes.setText("" + f.getMes());
        txtAnho.setText("" + f.getAnho());
        txtNumeroFactura.setText(f.getNumeroFactura());
        
        if (f.getNIF().isCIF())
            txtLetraCIFRS.setText("" + f.getNIF().getLetra());
        else
            txtLetraNIFRS.setText("" + f.getNIF().getLetra());
        
        txtNumNIFRS.setText("" + f.getNIF().getNumero());
        
        txtRS.setText(f.getRazonRS());

        chTipoGasto.select(f.getCategoria());
        chbxDevolucion.setState(f.esDevolucion());
        txtRetenciones.setText("" + f.getTipoRetenciones());
        int i = 0;
        for (Extracto e : f.getSubfacturas()) {
            txtBase[i].setText("" + e.getBase());
            chIVA[i].select("" + e.getTipoIVA().getValor());
            txtIVA[i].setText("" + e.getIVA());
            txtTotal[i].setText("" + e.getTotal());
            i++;
        }

        txtTotalBase.setText("" + f.getTotales().getBase());
        chTotalIVA.select("" + f.getTotales().getTipoIVA().getValor());
        txtTotalIVA.setText("" + f.getTotales().getIVA());
        txtTotalTotal.setText("" + f.getTotales().getSubtotal());
        txtTotalAbsoluto.setText("" + f.getTotales().getTotal());
        txtBaseNI.setText("" + f.getTotales().getBaseNI());
        txtTotalRetenciones.setText("" + f.getTotales().getRetenciones());
        if (!f.getNota().getTexto().equals("")) {
            txtNota.setText(f.getNota().getTexto());
        } else {
            txtNota.setText("");
        }

        rellenarVacios();
        calcularExtractos();
        calcularTotales();
        return true;
    }

    public void rellenarVacios() {

        if (txtRetenciones.getText().trim().equals("")) {
            txtRetenciones.setText("0");
        }
        for (int i = 0; i < numExtractos; i++) {

            if (txtBase[i].getText().trim().equals("")) {
                txtBase[i].setText("0.0");
            }
            if (txtIVA[i].getText().trim().equals("")) {
                txtIVA[i].setText("0.0");
            }
            if (txtTotal[i].getText().trim().equals("")) {
                txtTotal[i].setText("0.0");
            }
        }
    }

    public void limpiarFormulario() {

        txtDia.setText("");
        txtMes.setText("");
        txtAnho.setText("");
        txtNumeroFactura.setText("");
        txtLetraCIFRS.setText("");
        txtNumNIFRS.setText("");
        txtLetraNIFRS.setText("");
        txtRS.setText("");
        txtNota.setText("");
        btnOK.setLabel("  OK  ");
        for (int i = 0; i < numExtractos; i++) {
            txtBase[i].setText("");
            chIVA[i].select(0);
            txtIVA[i].setText("");
            txtTotal[i].setText("");
        }
        txtTotalBase.setText("");
        chTotalIVA.select(0);
        txtTotalIVA.setText("");
        txtTotalTotal.setText("");
        txtRetenciones.setText("");
        txtBaseNI.setText("");
        txtTotalRetenciones.setText("");
        chbxDevolucion.setState(false);
        txtTotalAbsoluto.setText("");

        //CAMPOS AUTOCOMPLETADOS
        txtAnho.setText(Config.getConfig().getAnho().getAnho() + "");
    }

    public void borrarDatosNumericos(){
        for (int i= 0; i<numExtractos; i++ )
        {
            txtBase[i].setText("");
            chIVA[i].select(0);
            txtIVA[i].setText("");
            txtTotal[i].setText("");
        }
        chbxDevolucion.setState(false);
        txtRetenciones.setText("");
        txtTotalBase.setText("");
        chTotalIVA.select(0);
        txtTotalIVA.setText("");
        txtBaseNI.setText("");
        txtTotalAbsoluto.setText("");
        txtTotalRetenciones.setText("");
        txtTotalTotal.setText("");
        
    }
    
    public boolean comprobarNIF() {
          return revisarNIF;
    }
    
    public boolean validarNIF(NIF nif){
        if (nif.comprobarNIF()) {
            return true;
        } else {
            //JOptionPane.showMessageDialog(null,"ATENCION : El NIF es incorrecto!");
            return false;
        }
    }
    
    public boolean comprobarRS() {

        if (revisarRS) {
            return true;
        } else {
            return false;
        }
    }

    public boolean emergenteRS() {

        if (ventanaRS) {
            return true;
        } else {
            return false;
        }
    }
    
    public boolean pasoatras(){
        return this.pasoatras;
    }
    
    public boolean pasoadelante(){
        return this.pasoadelante;
    }

    public void resetRS() {
        this.revisarNIF = false;
        this.revisarRS = false;
        this.ventanaRS = false;
    }

    public void formWindowClosing() {
        this.setVisible(false);
    }
}
