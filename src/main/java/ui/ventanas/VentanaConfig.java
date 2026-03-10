/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ui.ventanas;

import java.awt.BorderLayout;
import java.awt.Button;
//TODO: HABRÁ QUE RECOMPONER TODOS LOS GRIDBAGLAYOUTS
import java.awt.GridBagLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import modelo.*;
import modelo.base.Config;
import modelo.base.Contrasena;
import modelo.base.Fichero;
import modelo.base.FundirArchivos;
import modelo.records.Anho;
import modelo.records.NIF;
import modelo.records.RazonSocial;
import modelo.records.TipoGasto;
import modelo.records.TipoIVA;
import ui.fxcontrollers.*;

/**
 *
 * @author Juan Seoane
 */
public class VentanaConfig extends javax.swing.JFrame {

    ArrayList<JPanel> usuarios;
    ArrayList<Button> botonesEditar;
    ArrayList<Button> botonesBorrar;

    /**
     * Creates new form VentanaConfig
     */
    public VentanaConfig() {
        initComponents();

        //        cargarUsuarios();
        cargarDatos();
        initControls();
    }

    /*
    public void cargarUsuarios(){
        if (Config.getConfigActual().getUsuario().equals("ADMIN")){
            this.pnlListaUsuarios.setLayout(new GridBagLayout());
            int i = 0;
            this.usuarios = new ArrayList<JPanel>();
            botonesEditar = new ArrayList<Button>();
            botonesBorrar = new ArrayList<Button>();
            for (Contrasena c : Config.getConfigActual().contrasenas()){
               botonesEditar.add(new Button("Cambiar Contrasena "+c.usuario()));
               botonesBorrar.add(new Button("Borrar "+c.usuario()));
               usuarios.add(new JPanel());
               usuarios.get(i).setLayout(new BorderLayout());
               usuarios.get(i).add(new Label((Config.getConfigActual().getContrasenas().get(i)).getUsuario()),BorderLayout.WEST);
               usuarios.get(i).add(botonesEditar.get(i),BorderLayout.CENTER);
               usuarios.get(i).add(botonesBorrar.get(i),BorderLayout.EAST);
               pnlListaUsuarios.add(usuarios.get(i));
               i++;
            }
        }
        else{
            this.pnlListaUsuarios.setLayout(new GridBagLayout());
            this.pnlListaUsuarios.add(new Label("La configuracion de Usuarios sólo está disponible en el administrador!"));
        }


    }
*/
    public void cargarDatos() {
        this.txtAnhoConfig.setText(Config.getConfigActual().configData.año().año() + "");
        this.txtTrimestreConfig.setText(Config.getConfigActual().configData.año().trimestre() + "");
        this.txtMiNegocio.setText(Config.getConfigActual().misDatos.nombre());
        this.txtNombreMiRS.setText(Config.getConfigActual().misDatos.nombreEmpresa());
        this.txtNIFLetraCIFMiRS.setText("");
        this.txtNIFLetraMiRS.setText("");
        if (Config.getConfigActual().misDatos.nif().isCIF()) this.txtNIFLetraCIFMiRS.setText(
                Config.getConfigActual().misDatos.nif().letra()
            ); else this.txtNIFLetraMiRS.setText(Config.getConfigActual().misDatos.nif().letra());
        this.txtNIFNumeroMiRS.setText(Config.getConfigActual().misDatos.nif().numero() + "");
        this.txtdireccionMiRS.setText(Config.getConfigActual().misDatos.direccion());
        this.txtCPMiRS.setText(Config.getConfigActual().misDatos.CP());
        this.txttelefonoMiRS.setText(Config.getConfigActual().misDatos.telefono());
        this.txtpoblacionMiRS.setText(Config.getConfigActual().misDatos.poblacion());
        /*
        String categorias = "";
        for (TipoGasto tipo : Config.getConfigActual().configData.tiposGasto())
             categorias += tipo.tipo +",";
        categorias = categorias.substring(0,categorias.length()-1);
        this.txtTiposGastos.setText(categorias);
*/
        String ivas = "";
        for (Integer iva : Config.getConfigActual().configData.tiposIVA()) ivas += iva + ",";
        ivas = ivas.substring(0, ivas.length() - 1);
        this.txtTiposIVA.setText(ivas);
        this.txtRutaFacturas.setText(
                Config
                    .getConfigActual()
                    .getRutaFCT()
                    .substring(0, Config.getConfigActual().getRutaFCT().lastIndexOf("."))
            );
        this.txtRutaDistribuidores.setText(Config.getConfigActual().getRutaRS().replace(".rs", ""));
        this.txtRutaCaja.setText(Config.getConfigActual().getRutaCJA().replace(".cja", ""));
    }

    public void initControls() {
        if (Config.getConfigActual().getUsuario().equals("ADMIN")) {
            for (int i = 0; i < this.usuarios.size(); i++) {
                botonesEditar
                    .get(i)
                    .addActionListener(
                        new ActionListener() {
                            public void actionPerformed(ActionEvent ae) {
                                //cambiarPassword(((Button)ae.getSource()).getLabel());
                            }
                        }
                    );
                botonesBorrar
                    .get(i)
                    .addActionListener(
                        new ActionListener() {
                            public void actionPerformed(ActionEvent ae) {
                                //borrarUsuario(((Button)ae.getSource()).getLabel());
                            }
                        }
                    );
            }
        }
    }

    /*
    public void cambiarPassword(String boton){
        String usuario = boton.replace("Cambiar Contrasena ","");
        ArrayList<Contrasena> contrasenas = Config.getConfig("ADMIN").getContrasenhas();
        for (Contrasena c : contrasenas)
        {
            if (usuario.equals(c.usuario()))
            {
                String newPass = JOptionPane.showInputDialog("Introduzca la nueva contrasena :");
                c.setContrasenha(newPass);
            }
        }

        Config.getConfig().setContrasenhas(contrasenas);
        Config.recConfig("ADMIN",Config.getConfig());
     }

    public void borrarUsuario(String boton){
        String usuario = boton.replace("Borrar ","");
        ArrayList<Contrasenha> contrasenas = Config.getConfig("ADMIN").getContrasenhas();
        for (int i = 0;i<contrasenas.size(); i++)
        {
            if (usuario.equals(contrasenas.get(i).getUsuario()))
            {
                contrasenas.remove(i);
            }
        }
        Config.getConfig().setContrasenhas(contrasenas);
        Config.recConfig("ADMIN",Config.getConfig());

     }
*/

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        ConfigTabs = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtAnhoConfig = new javax.swing.JTextField();
        txtTrimestreConfig = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        btnFundir = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtNombreMiRS = new javax.swing.JTextField();
        txtNIFNumeroMiRS = new javax.swing.JTextField();
        txttelefonoMiRS = new javax.swing.JTextField();
        txtdireccionMiRS = new javax.swing.JTextField();
        txtCPMiRS = new javax.swing.JTextField();
        txtpoblacionMiRS = new javax.swing.JTextField();
        txtNIFLetraMiRS = new javax.swing.JTextField();
        txtMiNegocio = new javax.swing.JTextField();
        lblNegocio = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtNIFLetraCIFMiRS = new javax.swing.JTextPane();
        jPanel5 = new javax.swing.JPanel();
        txtTiposGastos = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtTiposIVA = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        pnlNuevoUsuario = new javax.swing.JPanel();
        btnNuevoUsuario = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        pnlListaUsuarios = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        btnConvertir = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        btnOKRutaFacturas = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        txtRutaFacturas = new javax.swing.JTextField();
        btnElegirRutaFacturas = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        btnOKRutaDistribuidores = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        txtRutaDistribuidores = new javax.swing.JTextField();
        btnElegirRutaDistribuidores = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        btnOKRutaCaja = new javax.swing.JButton();
        txtRutaCaja = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        btnElegirRutaCaja = new javax.swing.JButton();
        btnCopiarArchivosARuta = new javax.swing.JButton();
        jLabel21 = new javax.swing.JLabel();
        txtRutaCopiado = new javax.swing.JTextField();
        btnSeleccionarRutaCopiado = new javax.swing.JButton();
        PanelBotonesConfig = new javax.swing.JPanel();
        btnOKConfig = new javax.swing.JButton();
        btnCancelarConfig = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setText("Anho Actual");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel2.setText("trimestre");

        txtAnhoConfig.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtAnhoConfig.setText("2013");

        txtTrimestreConfig.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtTrimestreConfig.setText("0");

        jLabel3.setText("Escriba 0 si no quiere separar por trimestres");

        btnFundir.setText("FUNDIR TRIMESTRES");
        btnFundir.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    btnFundirActionPerformed(evt);
                }
            }
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(
                    jPanel2Layout
                        .createSequentialGroup()
                        .addContainerGap()
                        .addGroup(
                            jPanel2Layout
                                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(
                                    jPanel2Layout
                                        .createSequentialGroup()
                                        .addGroup(
                                            jPanel2Layout
                                                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(jLabel1)
                                                .addComponent(jLabel2)
                                        )
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(
                                            jPanel2Layout
                                                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addComponent(
                                                    txtAnhoConfig,
                                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                                    72,
                                                    Short.MAX_VALUE
                                                )
                                                .addComponent(txtTrimestreConfig)
                                        )
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel3)
                                )
                                .addComponent(btnFundir)
                        )
                        .addContainerGap(267, Short.MAX_VALUE)
                )
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(
                    jPanel2Layout
                        .createSequentialGroup()
                        .addContainerGap()
                        .addGroup(
                            jPanel2Layout
                                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel1)
                                .addComponent(
                                    txtAnhoConfig,
                                    javax.swing.GroupLayout.PREFERRED_SIZE,
                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                    javax.swing.GroupLayout.PREFERRED_SIZE
                                )
                        )
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(
                            jPanel2Layout
                                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel2)
                                .addGroup(
                                    jPanel2Layout
                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel3)
                                        .addComponent(
                                            txtTrimestreConfig,
                                            javax.swing.GroupLayout.PREFERRED_SIZE,
                                            javax.swing.GroupLayout.DEFAULT_SIZE,
                                            javax.swing.GroupLayout.PREFERRED_SIZE
                                        )
                                )
                        )
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 183, Short.MAX_VALUE)
                        .addComponent(btnFundir)
                        .addContainerGap()
                )
        );

        ConfigTabs.addTab("Anho y trimestre", jPanel2);

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel4.setText("mi Razon");

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel5.setText("mi CIF");

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel6.setText("mi Telefono");

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel7.setText("mi Direccion");

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel8.setText("mi C.P.");

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel9.setText("mi Poblacion");

        txtNombreMiRS.setText("El nombre de mi razon social");

        txtNIFNumeroMiRS.setText("12345678");

        txttelefonoMiRS.setText("El teléfono de mi negocio");

        txtdireccionMiRS.setText("La dirección de mi negocio");

        txtCPMiRS.setText("El Código Postal de mi negocio");

        txtpoblacionMiRS.setText("La población de mi negocio");

        txtNIFLetraMiRS.setText("NIF");

        txtMiNegocio.setText("El nombre de mi negocio");

        lblNegocio.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lblNegocio.setText("mi Negocio");

        txtNIFLetraCIFMiRS.setText("CIF");
        jScrollPane1.setViewportView(txtNIFLetraCIFMiRS);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(
                    jPanel1Layout
                        .createSequentialGroup()
                        .addContainerGap()
                        .addGroup(
                            jPanel1Layout
                                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(
                                    jPanel1Layout
                                        .createSequentialGroup()
                                        .addGroup(
                                            jPanel1Layout
                                                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addComponent(
                                                    jLabel5,
                                                    javax.swing.GroupLayout.Alignment.TRAILING,
                                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                                    Short.MAX_VALUE
                                                )
                                                .addComponent(
                                                    jLabel6,
                                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                                    Short.MAX_VALUE
                                                )
                                                .addComponent(
                                                    jLabel7,
                                                    javax.swing.GroupLayout.Alignment.TRAILING,
                                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                                    Short.MAX_VALUE
                                                )
                                                .addComponent(
                                                    jLabel8,
                                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                                    Short.MAX_VALUE
                                                )
                                                .addComponent(
                                                    jLabel9,
                                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                                    Short.MAX_VALUE
                                                )
                                                .addComponent(
                                                    jLabel4,
                                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                                    Short.MAX_VALUE
                                                )
                                        )
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(
                                            jPanel1Layout
                                                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(txtNombreMiRS)
                                                .addComponent(txttelefonoMiRS)
                                                .addComponent(txtdireccionMiRS)
                                                .addComponent(
                                                    txtCPMiRS,
                                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                                    507,
                                                    Short.MAX_VALUE
                                                )
                                                .addComponent(txtpoblacionMiRS)
                                                .addGroup(
                                                    jPanel1Layout
                                                        .createSequentialGroup()
                                                        .addComponent(
                                                            jScrollPane1,
                                                            javax.swing.GroupLayout.PREFERRED_SIZE,
                                                            39,
                                                            javax.swing.GroupLayout.PREFERRED_SIZE
                                                        )
                                                        .addPreferredGap(
                                                            javax.swing.LayoutStyle.ComponentPlacement.RELATED
                                                        )
                                                        .addComponent(
                                                            txtNIFNumeroMiRS,
                                                            javax.swing.GroupLayout.PREFERRED_SIZE,
                                                            122,
                                                            javax.swing.GroupLayout.PREFERRED_SIZE
                                                        )
                                                        .addPreferredGap(
                                                            javax.swing.LayoutStyle.ComponentPlacement.RELATED
                                                        )
                                                        .addComponent(
                                                            txtNIFLetraMiRS,
                                                            javax.swing.GroupLayout.PREFERRED_SIZE,
                                                            46,
                                                            javax.swing.GroupLayout.PREFERRED_SIZE
                                                        )
                                                        .addGap(0, 0, Short.MAX_VALUE)
                                                )
                                        )
                                )
                                .addGroup(
                                    jPanel1Layout
                                        .createSequentialGroup()
                                        .addComponent(
                                            lblNegocio,
                                            javax.swing.GroupLayout.DEFAULT_SIZE,
                                            334,
                                            Short.MAX_VALUE
                                        )
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(
                                            txtMiNegocio,
                                            javax.swing.GroupLayout.PREFERRED_SIZE,
                                            295,
                                            javax.swing.GroupLayout.PREFERRED_SIZE
                                        )
                                )
                        )
                        .addGap(16, 16, 16)
                )
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(
                    jPanel1Layout
                        .createSequentialGroup()
                        .addContainerGap()
                        .addGroup(
                            jPanel1Layout
                                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lblNegocio)
                                .addComponent(
                                    txtMiNegocio,
                                    javax.swing.GroupLayout.PREFERRED_SIZE,
                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                    javax.swing.GroupLayout.PREFERRED_SIZE
                                )
                        )
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(
                            jPanel1Layout
                                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel4)
                                .addComponent(
                                    txtNombreMiRS,
                                    javax.swing.GroupLayout.PREFERRED_SIZE,
                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                    javax.swing.GroupLayout.PREFERRED_SIZE
                                )
                        )
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(
                            jPanel1Layout
                                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel5)
                                .addGroup(
                                    jPanel1Layout
                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(
                                            txtNIFNumeroMiRS,
                                            javax.swing.GroupLayout.PREFERRED_SIZE,
                                            javax.swing.GroupLayout.DEFAULT_SIZE,
                                            javax.swing.GroupLayout.PREFERRED_SIZE
                                        )
                                        .addComponent(
                                            txtNIFLetraMiRS,
                                            javax.swing.GroupLayout.PREFERRED_SIZE,
                                            javax.swing.GroupLayout.DEFAULT_SIZE,
                                            javax.swing.GroupLayout.PREFERRED_SIZE
                                        )
                                )
                                .addComponent(
                                    jScrollPane1,
                                    javax.swing.GroupLayout.PREFERRED_SIZE,
                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                    javax.swing.GroupLayout.PREFERRED_SIZE
                                )
                        )
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(
                            jPanel1Layout
                                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel6)
                                .addComponent(
                                    txttelefonoMiRS,
                                    javax.swing.GroupLayout.PREFERRED_SIZE,
                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                    javax.swing.GroupLayout.PREFERRED_SIZE
                                )
                        )
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(
                            jPanel1Layout
                                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel7)
                                .addComponent(
                                    txtdireccionMiRS,
                                    javax.swing.GroupLayout.PREFERRED_SIZE,
                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                    javax.swing.GroupLayout.PREFERRED_SIZE
                                )
                        )
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(
                            jPanel1Layout
                                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel8)
                                .addComponent(
                                    txtCPMiRS,
                                    javax.swing.GroupLayout.PREFERRED_SIZE,
                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                    javax.swing.GroupLayout.PREFERRED_SIZE
                                )
                        )
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(
                            jPanel1Layout
                                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel9)
                                .addComponent(
                                    txtpoblacionMiRS,
                                    javax.swing.GroupLayout.PREFERRED_SIZE,
                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                    javax.swing.GroupLayout.PREFERRED_SIZE
                                )
                        )
                        .addContainerGap(79, Short.MAX_VALUE)
                )
        );

        ConfigTabs.addTab("Mis Datos Fiscales", jPanel1);

        txtTiposGastos.setText("Tipos de Gastos");

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel10.setText("Tipos de Gastos");

        jLabel11.setText("Inserte los tipos de gastos separados por comas, sin signos");

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel12.setText("Tipos de IVA");

        txtTiposIVA.setText("Tipos de IVA");

        jLabel13.setText("Inserte los tipos de IVA en su valor numérico, incluido el 0 (sin IVA), sin signos");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(
                    jPanel5Layout
                        .createSequentialGroup()
                        .addContainerGap()
                        .addGroup(
                            jPanel5Layout
                                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtTiposGastos)
                                .addComponent(txtTiposIVA)
                                .addGroup(
                                    jPanel5Layout
                                        .createSequentialGroup()
                                        .addGroup(
                                            jPanel5Layout
                                                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(jLabel10)
                                                .addComponent(jLabel11)
                                                .addComponent(jLabel12)
                                                .addComponent(jLabel13)
                                        )
                                        .addGap(0, 272, Short.MAX_VALUE)
                                )
                        )
                        .addContainerGap()
                )
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(
                    jPanel5Layout
                        .createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(
                            txtTiposGastos,
                            javax.swing.GroupLayout.PREFERRED_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.PREFERRED_SIZE
                        )
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(
                            txtTiposIVA,
                            javax.swing.GroupLayout.PREFERRED_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.PREFERRED_SIZE
                        )
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel13)
                        .addContainerGap(121, Short.MAX_VALUE)
                )
        );

        ConfigTabs.addTab("Tipos de Gastos e IVA", jPanel5);

        jPanel4.setLayout(new java.awt.BorderLayout());

        pnlNuevoUsuario.setPreferredSize(new java.awt.Dimension(466, 30));

        btnNuevoUsuario.setText("Nuevo Usuario");
        btnNuevoUsuario.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    //btnNuevoUsuarioActionPerformed(evt);
                }
            }
        );

        javax.swing.GroupLayout pnlNuevoUsuarioLayout = new javax.swing.GroupLayout(pnlNuevoUsuario);
        pnlNuevoUsuario.setLayout(pnlNuevoUsuarioLayout);
        pnlNuevoUsuarioLayout.setHorizontalGroup(
            pnlNuevoUsuarioLayout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(
                    pnlNuevoUsuarioLayout
                        .createSequentialGroup()
                        .addComponent(btnNuevoUsuario)
                        .addGap(0, 562, Short.MAX_VALUE)
                )
        );
        pnlNuevoUsuarioLayout.setVerticalGroup(
            pnlNuevoUsuarioLayout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(
                    javax.swing.GroupLayout.Alignment.TRAILING,
                    pnlNuevoUsuarioLayout
                        .createSequentialGroup()
                        .addGap(0, 7, Short.MAX_VALUE)
                        .addComponent(btnNuevoUsuario)
                )
        );

        jPanel4.add(pnlNuevoUsuario, java.awt.BorderLayout.SOUTH);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 665, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 28, Short.MAX_VALUE)
        );

        jPanel4.add(jPanel7, java.awt.BorderLayout.NORTH);

        javax.swing.GroupLayout pnlListaUsuariosLayout = new javax.swing.GroupLayout(pnlListaUsuarios);
        pnlListaUsuarios.setLayout(pnlListaUsuariosLayout);
        pnlListaUsuariosLayout.setHorizontalGroup(
            pnlListaUsuariosLayout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 665, Short.MAX_VALUE)
        );
        pnlListaUsuariosLayout.setVerticalGroup(
            pnlListaUsuariosLayout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 222, Short.MAX_VALUE)
        );

        jPanel4.add(pnlListaUsuarios, java.awt.BorderLayout.CENTER);

        ConfigTabs.addTab("Usuarios", jPanel4);

        btnConvertir.setText("Convertir");
        btnConvertir.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    btnConvertirActionPerformed(evt);
                }
            }
        );

        jLabel14.setText("Convertir archivos de la version 1 a la actual");

        jLabel15.setText("FACTURAS");

        btnOKRutaFacturas.setText("OK");
        btnOKRutaFacturas.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    //btnOKRutaFacturasActionPerformed(evt);
                }
            }
        );

        jLabel16.setText(".fct");

        btnElegirRutaFacturas.setText("...");
        btnElegirRutaFacturas.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    btnElegirRutaFacturasActionPerformed(evt);
                }
            }
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(
                    jPanel6Layout
                        .createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(
                            txtRutaFacturas,
                            javax.swing.GroupLayout.PREFERRED_SIZE,
                            347,
                            javax.swing.GroupLayout.PREFERRED_SIZE
                        )
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel16)
                        .addGap(9, 9, 9)
                        .addComponent(btnElegirRutaFacturas)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(
                            btnOKRutaFacturas,
                            javax.swing.GroupLayout.PREFERRED_SIZE,
                            70,
                            javax.swing.GroupLayout.PREFERRED_SIZE
                        )
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                )
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(
                    jPanel6Layout
                        .createSequentialGroup()
                        .addContainerGap()
                        .addGroup(
                            jPanel6Layout
                                .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel15)
                                .addComponent(
                                    txtRutaFacturas,
                                    javax.swing.GroupLayout.PREFERRED_SIZE,
                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                    javax.swing.GroupLayout.PREFERRED_SIZE
                                )
                                .addComponent(jLabel16)
                                .addComponent(btnOKRutaFacturas)
                                .addComponent(btnElegirRutaFacturas)
                        )
                        .addContainerGap()
                )
        );

        btnOKRutaDistribuidores.setText("OK");
        btnOKRutaDistribuidores.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    //btnOKRutaDistribuidoresActionPerformed(evt);
                }
            }
        );

        jLabel17.setText("DISTRIBUIDORES");

        jLabel18.setText(".rs");

        btnElegirRutaDistribuidores.setText("...");
        btnElegirRutaDistribuidores.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    btnElegirRutaDistribuidoresActionPerformed(evt);
                }
            }
        );

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(
                    jPanel8Layout
                        .createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(
                            txtRutaDistribuidores,
                            javax.swing.GroupLayout.PREFERRED_SIZE,
                            299,
                            javax.swing.GroupLayout.PREFERRED_SIZE
                        )
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnElegirRutaDistribuidores)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(
                            btnOKRutaDistribuidores,
                            javax.swing.GroupLayout.PREFERRED_SIZE,
                            72,
                            javax.swing.GroupLayout.PREFERRED_SIZE
                        )
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                )
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(
                    jPanel8Layout
                        .createSequentialGroup()
                        .addContainerGap()
                        .addGroup(
                            jPanel8Layout
                                .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel17)
                                .addComponent(
                                    txtRutaDistribuidores,
                                    javax.swing.GroupLayout.PREFERRED_SIZE,
                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                    javax.swing.GroupLayout.PREFERRED_SIZE
                                )
                                .addComponent(jLabel18)
                                .addComponent(btnOKRutaDistribuidores)
                                .addComponent(btnElegirRutaDistribuidores)
                        )
                        .addContainerGap()
                )
        );

        jLabel20.setText(".cja");

        btnOKRutaCaja.setText("OK");
        btnOKRutaCaja.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    //btnOKRutaCajaActionPerformed(evt);
                }
            }
        );

        jLabel19.setText("CAJA");

        btnElegirRutaCaja.setText("...");
        btnElegirRutaCaja.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    btnElegirRutaCajaActionPerformed(evt);
                }
            }
        );

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(
                    jPanel9Layout
                        .createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(
                            txtRutaCaja,
                            javax.swing.GroupLayout.PREFERRED_SIZE,
                            383,
                            javax.swing.GroupLayout.PREFERRED_SIZE
                        )
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnElegirRutaCaja)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(
                            btnOKRutaCaja,
                            javax.swing.GroupLayout.PREFERRED_SIZE,
                            71,
                            javax.swing.GroupLayout.PREFERRED_SIZE
                        )
                        .addGap(28, 28, 28)
                )
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(
                    jPanel9Layout
                        .createSequentialGroup()
                        .addContainerGap()
                        .addGroup(
                            jPanel9Layout
                                .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel19)
                                .addComponent(
                                    txtRutaCaja,
                                    javax.swing.GroupLayout.PREFERRED_SIZE,
                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                    javax.swing.GroupLayout.PREFERRED_SIZE
                                )
                                .addComponent(jLabel20)
                                .addComponent(btnOKRutaCaja)
                                .addComponent(btnElegirRutaCaja)
                        )
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                )
        );

        btnCopiarArchivosARuta.setText("COPIAR ARCHIVOS");
        btnCopiarArchivosARuta.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    btnCopiarArchivosARutaActionPerformed(evt);
                }
            }
        );

        jLabel21.setText("a la carpeta :");

        btnSeleccionarRutaCopiado.setText("...");
        btnSeleccionarRutaCopiado.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    btnSeleccionarRutaCopiadoActionPerformed(evt);
                }
            }
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(
                    jPanel3Layout
                        .createSequentialGroup()
                        .addContainerGap()
                        .addGroup(
                            jPanel3Layout
                                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(
                                    jPanel9,
                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                    Short.MAX_VALUE
                                )
                                .addGroup(
                                    jPanel3Layout
                                        .createSequentialGroup()
                                        .addGroup(
                                            jPanel3Layout
                                                .createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                .addComponent(
                                                    btnCopiarArchivosARuta,
                                                    javax.swing.GroupLayout.Alignment.LEADING,
                                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                                    Short.MAX_VALUE
                                                )
                                                .addComponent(
                                                    btnConvertir,
                                                    javax.swing.GroupLayout.Alignment.LEADING,
                                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                                    Short.MAX_VALUE
                                                )
                                        )
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(
                                            jPanel3Layout
                                                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(
                                                    jPanel3Layout
                                                        .createSequentialGroup()
                                                        .addComponent(jLabel14)
                                                        .addGap(0, 0, Short.MAX_VALUE)
                                                )
                                                .addGroup(
                                                    jPanel3Layout
                                                        .createSequentialGroup()
                                                        .addComponent(jLabel21)
                                                        .addPreferredGap(
                                                            javax.swing.LayoutStyle.ComponentPlacement.RELATED
                                                        )
                                                        .addComponent(txtRutaCopiado)
                                                        .addPreferredGap(
                                                            javax.swing.LayoutStyle.ComponentPlacement.UNRELATED
                                                        )
                                                        .addComponent(btnSeleccionarRutaCopiado)
                                                )
                                        )
                                )
                                .addComponent(
                                    jPanel8,
                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                    Short.MAX_VALUE
                                )
                                .addComponent(
                                    jPanel6,
                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                    Short.MAX_VALUE
                                )
                        )
                        .addContainerGap()
                )
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(
                    javax.swing.GroupLayout.Alignment.TRAILING,
                    jPanel3Layout
                        .createSequentialGroup()
                        .addContainerGap()
                        .addComponent(
                            jPanel6,
                            javax.swing.GroupLayout.PREFERRED_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.PREFERRED_SIZE
                        )
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(
                            jPanel8,
                            javax.swing.GroupLayout.PREFERRED_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.PREFERRED_SIZE
                        )
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(
                            jPanel9,
                            javax.swing.GroupLayout.PREFERRED_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.PREFERRED_SIZE
                        )
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(
                            jPanel3Layout
                                .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnCopiarArchivosARuta)
                                .addComponent(jLabel21)
                                .addComponent(
                                    txtRutaCopiado,
                                    javax.swing.GroupLayout.PREFERRED_SIZE,
                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                    javax.swing.GroupLayout.PREFERRED_SIZE
                                )
                                .addComponent(btnSeleccionarRutaCopiado)
                        )
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 54, Short.MAX_VALUE)
                        .addGroup(
                            jPanel3Layout
                                .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnConvertir)
                                .addComponent(jLabel14)
                        )
                        .addContainerGap()
                )
        );

        ConfigTabs.addTab("Rutas de Archivos", jPanel3);

        btnOKConfig.setText("OK - GUARDAR");
        btnOKConfig.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    try {
                        btnOKConfigActionPerformed(evt);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        );

        btnCancelarConfig.setText("CANCELAR");
        btnCancelarConfig.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    btnCancelarConfigActionPerformed(evt);
                }
            }
        );

        javax.swing.GroupLayout PanelBotonesConfigLayout = new javax.swing.GroupLayout(PanelBotonesConfig);
        PanelBotonesConfig.setLayout(PanelBotonesConfigLayout);
        PanelBotonesConfigLayout.setHorizontalGroup(
            PanelBotonesConfigLayout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(
                    javax.swing.GroupLayout.Alignment.TRAILING,
                    PanelBotonesConfigLayout
                        .createSequentialGroup()
                        .addComponent(btnCancelarConfig)
                        .addPreferredGap(
                            javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            Short.MAX_VALUE
                        )
                        .addComponent(btnOKConfig)
                )
        );
        PanelBotonesConfigLayout.setVerticalGroup(
            PanelBotonesConfigLayout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(
                    javax.swing.GroupLayout.Alignment.TRAILING,
                    PanelBotonesConfigLayout
                        .createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(
                            PanelBotonesConfigLayout
                                .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnOKConfig)
                                .addComponent(btnCancelarConfig)
                        )
                )
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(
                    PanelBotonesConfig,
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    Short.MAX_VALUE
                )
                .addComponent(ConfigTabs)
        );
        layout.setVerticalGroup(
            layout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(
                    layout
                        .createSequentialGroup()
                        .addComponent(
                            ConfigTabs,
                            javax.swing.GroupLayout.PREFERRED_SIZE,
                            308,
                            javax.swing.GroupLayout.PREFERRED_SIZE
                        )
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(
                            PanelBotonesConfig,
                            javax.swing.GroupLayout.PREFERRED_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.PREFERRED_SIZE
                        )
                )
        );

        pack();
    } // </editor-fold>//GEN-END:initComponents

    private void btnCancelarConfigActionPerformed(java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnCancelarConfigActionPerformed
        this.dispose();
        PanelControl.pulsarboton(1);
    } //GEN-LAST:event_btnCancelarConfigActionPerformed

    private void btnOKConfigActionPerformed(java.awt.event.ActionEvent evt) throws IOException { //GEN-FIRST:event_btnOKConfigActionPerformed
        if (recogerYgrabarConfig()) {
            /** TODO : FALTA GUARDAR EL RESTO DE LA CONFIGURACION */
            JOptionPane.showMessageDialog(null, "CONFIGURACION GUARDADA! \n El programa se cerrará.");
            PanelControl.getPanelControl().setAnho(Config.getConfigActual().configData.año().año());
            PanelControl.getPanelControl().setTrimestre(Config.getConfigActual().configData.año().trimestre());
            PanelControl.getPanelControl().setUsuario(Config.getConfigActual().configData.user());
        }
        /** TODO : VentanaConfig: boton OK : aqui faltan operaciones, cambiar el archivo, actualizar el numero de entradas, etc */
        this.dispose();
        //        Config.reinicializarConfig(Config.getConfigActual().usuario);
        PanelControl.pulsarboton(1);
        PanelControl.reset();
        //        Controlador.quit();
        //        PanelControl.getPanelControl().repaint();
    } //GEN-LAST:event_btnOKConfigActionPerformed

    private void btnConvertirActionPerformed(java.awt.event.ActionEvent evt) {} //GEN-FIRST:event_btnConvertirActionPerformed //GEN-LAST:event_btnConvertirActionPerformed

    private void btnFundirActionPerformed(java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnFundirActionPerformed
        FundirArchivos refundir = new FundirArchivos();
        refundir.start();
    } //GEN-LAST:event_btnFundirActionPerformed

    /*
    private void btnNuevoUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoUsuarioActionPerformed
        ArrayList<Contrasenha> contrasenhas = new ArrayList<Contrasenha>();
        String usuarioActual = Config.getConfig().getUsuario();

        String nuevousuario = JOptionPane.showInputDialog(null,"Introduzca el usuario");
        String nuevacontrasenha = JOptionPane.showInputDialog(null,"Introduzca la nueva contrasenha");

        Fichero<Config> cfgglobal = new Fichero<Config> ("config/ADMIN/config.cfg");
        Config configuracioninicial = cfgglobal.leer().get(0);
        Contrasenha nueva = new Contrasenha(nuevousuario,nuevacontrasenha);
        contrasenhas = configuracioninicial.getMisdatos().getContrasenhas();
        contrasenhas.add(nueva);
        configuracioninicial.setContrasenhas(contrasenhas);

//        for (Contrasenha c : contrasenhas)
//            //System.out.println("Grabando contrasena "+ c.getUsuario()+" >>> "+c.getContrasenha());
        if (Config.recConfig("ADMIN",configuracioninicial))
            JOptionPane.showMessageDialog(null,"Nuevo Usuario registrado!\nLa configuración se hará efectiva\nal reinicializar el programa");
        Config.getConfig(usuarioActual);
    }//GEN-LAST:event_btnNuevoUsuarioActionPerformed
*/
    private void btnElegirRutaFacturasActionPerformed(java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnElegirRutaFacturasActionPerformed
        JFileChooser v = new JFileChooser();
        v.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        v.setSelectedFile(new File(Config.getConfigActual().getRutaFCT()).getAbsoluteFile());
        int returnVal = v.showDialog(v, "Seleccionar");
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = v.getSelectedFile();
            //This is where a real application would open the file.
            txtRutaFacturas.setText(file.getAbsolutePath().replace(".fct", "").replace("\\", "/"));
        } else {}
    } //GEN-LAST:event_btnElegirRutaFacturasActionPerformed

    /*
    private void btnOKRutaFacturasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOKRutaFacturasActionPerformed
        if (!txtRutaFacturas.getText().equals("")){

            int res = JOptionPane.showConfirmDialog(null, "Desea guardar las Facturas actuales en la nueva ruta?");
            if (res == JOptionPane.YES_OPTION){
                Fichero.copia(Config.getConfigActual().getRutaFCT(), txtRutaFacturas.getText()+".fct");

            }else{
                Fichero nuevofichero = new Fichero(txtRutaFacturas.getText()+".fct");
            }
            Config.getConfigActual().configData.rutas().put("FCT",txtRutaFacturas.getText()+".fct");
        }
    }//GEN-LAST:event_btnOKRutaFacturasActionPerformed
*/
    private void btnElegirRutaDistribuidoresActionPerformed(java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnElegirRutaDistribuidoresActionPerformed
        JFileChooser v = new JFileChooser();
        v.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        v.setSelectedFile(new File(Config.getConfigActual().getRutaRS()).getAbsoluteFile());
        int returnVal = v.showDialog(v, "Seleccionar");
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = v.getSelectedFile();
            //This is where a real application would open the file.
            txtRutaDistribuidores.setText(file.getAbsolutePath().replace(".rs", "").replace("\\", "/"));
        } else {}
    } //GEN-LAST:event_btnElegirRutaDistribuidoresActionPerformed

    private void btnElegirRutaCajaActionPerformed(java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnElegirRutaCajaActionPerformed
        JFileChooser v = new JFileChooser();
        v.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        v.setSelectedFile(new File(Config.getConfigActual().getRutaCJA()).getAbsoluteFile());
        int returnVal = v.showDialog(v, "Seleccionar");
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = v.getSelectedFile();
            //This is where a real application would open the file.
            txtRutaCaja.setText(file.getAbsolutePath().replace(".cja", "").replace("\\", "/"));
        } else {}
    } //GEN-LAST:event_btnElegirRutaCajaActionPerformed

    /*
    private void btnOKRutaDistribuidoresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOKRutaDistribuidoresActionPerformed
        if (!txtRutaDistribuidores.getText().equals("")){

            int res = JOptionPane.showConfirmDialog(null, "Desea guardar los Distribuidores actuales en la nueva ruta?");
            if (res == JOptionPane.YES_OPTION){
                Fichero.copia(Config.getConfigActual().getRutaRS(), txtRutaDistribuidores.getText()+".rs");

            }else{
            }
            Config.getConfigActual().getRutas().put("RS",txtRutaDistribuidores.getText()+".rs");
        }
    }//GEN-LAST:event_btnOKRutaDistribuidoresActionPerformed

    private void btnOKRutaCajaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOKRutaCajaActionPerformed
        if (!txtRutaCaja.getText().equals("")){

            int res = JOptionPane.showConfirmDialog(null, "Desea guardar la Caja actual en la nueva ruta?");
            if (res == JOptionPane.YES_OPTION){
                Fichero.copia(Config.getConfigActual().getRutaCJA(), txtRutaCaja.getText()+".cja");

            }else{
            }
            Config.getConfigActual().getRutas().put("CJA",txtRutaCaja.getText()+".cja");
        }
    }//GEN-LAST:event_btnOKRutaCajaActionPerformed
*/
    private void btnSeleccionarRutaCopiadoActionPerformed(java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnSeleccionarRutaCopiadoActionPerformed
        JFileChooser v = new JFileChooser();
        v.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        v.setSelectedFile(new File(Config.getConfigActual().getRutaFCT()).getAbsoluteFile());
        int returnVal = v.showDialog(v, "Seleccionar");
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = v.getSelectedFile();
            //This is where a real application would open the file.
            txtRutaCopiado.setText(file.getAbsolutePath().replace("\\", "/"));
        } else {}
    } //GEN-LAST:event_btnSeleccionarRutaCopiadoActionPerformed

    private void btnCopiarArchivosARutaActionPerformed(java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnCopiarArchivosARutaActionPerformed
        if (!txtRutaCopiado.getText().equals("")) {
            String ruta = txtRutaCopiado.getText();

            String fct = Config.getConfigActual().getRutaFCT();
            String nombrefct = fct.substring(fct.lastIndexOf("/"));
            Fichero.copia(fct, ruta + "/" + nombrefct);

            String rs = Config.getConfigActual().getRutaRS();
            String nombrers = rs.substring(rs.lastIndexOf("/"));
            Fichero.copia(rs, ruta + "/" + nombrers);

            String cja = Config.getConfigActual().getRutaCJA();
            String nombrecja = cja.substring(cja.lastIndexOf("/"));
            Fichero.copia(cja, ruta + "/" + nombrecja);

            //**TODO: faltan las comprobaciones sobre el copiado.
            JOptionPane.showMessageDialog(null, "Los ficheros han sido copiados!");
        }
    } //GEN-LAST:event_btnCopiarArchivosARutaActionPerformed

    public boolean recogerYgrabarConfig() {
        RazonSocial miRazon = new RazonSocial();
        int año = Integer.parseInt(txtAnhoConfig.getText());
        int trimestre = Integer.parseInt(txtTrimestreConfig.getText());
        Anho anho = new Anho(año, trimestre);

        String miEmpresa = txtMiNegocio.getText();
        String miNombre = txtNombreMiRS.getText();
        NIF miNIF = null;
        if (txtNIFLetraMiRS.getText().equals("")) {
            miNIF =
                new NIF(
                    Integer.parseInt(txtNIFNumeroMiRS.getText()),
                    txtNIFLetraCIFMiRS.getText().trim().toUpperCase(),
                    true
                );
            //                miRazon.setNif(miNIF);
        } else if (txtNIFLetraCIFMiRS.getText().equals("")) {
            miNIF =
                new NIF(
                    Integer.parseInt(txtNIFNumeroMiRS.getText()),
                    txtNIFLetraMiRS.getText().trim().toUpperCase(),
                    false
                );
            //                miRazon.setNif(miNIF);
        }
        String miDireccion = txtdireccionMiRS.getText();
        String miCP = txtCPMiRS.getText();
        String miPoblacion = txtpoblacionMiRS.getText();
        String mitelefono = txttelefonoMiRS.getText();
        ArrayList<TipoGasto> categorias = new ArrayList<TipoGasto>();
        String[] miscategorias = txtTiposGastos.getText().split(",");
        for (String cat : miscategorias) categorias.add(
            new TipoGasto(cat.toUpperCase().trim(), "sin descripcion".toUpperCase())
        );
        ArrayList<TipoIVA> misivas = new ArrayList<TipoIVA>();
        String[] ivas = txtTiposIVA.getText().split(",");
        /*
        for (String iva :  ivas)
           misivas.add(new TipoIVA(Integer.parseInt(iva)));

        miRazon.setID(0);
        miRazon.setNombre(miEmpresa);

        miRazon.setRazon(miNombre);
        miRazon.setDireccion(miDireccion);
        miRazon.setCP(miCP);
        miRazon.setPoblacion(miPoblacion);
        miRazon.setTelefono(mitelefono);

        HashMap rutas = new HashMap();
        rutas.put("FCT",txtRutaFacturas.getText()+".fct");
        rutas.put("RS",txtRutaDistribuidores.getText()+".rs");
        rutas.put("CJA",txtRutaCaja.getText()+".cja");

        if (Config.getConfig().recConfig(Config.getConfig().getUsuario(),anho,miRazon,rutas
                , categorias,misivas,Config.getConfig().getContrasenhas()))
        {
            JOptionPane.showMessageDialog(null, "Guardando toda la configuración de "+Config.getConfig().getUsuario()+"\n para el año "+anho.getAnho()+" trimestre "+((anho.getTrimestre() == 0)?"(todos los trimestres) ": anho.getTrimestre()));
            return true;
        }
        else
*/
        return false;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger
                .getLogger(VentanaConfig.class.getName())
                .log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger
                .getLogger(VentanaConfig.class.getName())
                .log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger
                .getLogger(VentanaConfig.class.getName())
                .log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger
                .getLogger(VentanaConfig.class.getName())
                .log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(
            new Runnable() {
                public void run() {
                    new VentanaConfig().setVisible(true);
                }
            }
        );
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane ConfigTabs;
    private javax.swing.JPanel PanelBotonesConfig;
    private javax.swing.JButton btnCancelarConfig;
    private javax.swing.JButton btnConvertir;
    private javax.swing.JButton btnCopiarArchivosARuta;
    private javax.swing.JButton btnElegirRutaCaja;
    private javax.swing.JButton btnElegirRutaDistribuidores;
    private javax.swing.JButton btnElegirRutaFacturas;
    private javax.swing.JButton btnFundir;
    private javax.swing.JButton btnNuevoUsuario;
    private javax.swing.JButton btnOKConfig;
    private javax.swing.JButton btnOKRutaCaja;
    private javax.swing.JButton btnOKRutaDistribuidores;
    private javax.swing.JButton btnOKRutaFacturas;
    private javax.swing.JButton btnSeleccionarRutaCopiado;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblNegocio;
    private javax.swing.JPanel pnlListaUsuarios;
    private javax.swing.JPanel pnlNuevoUsuario;
    private javax.swing.JTextField txtAnhoConfig;
    private javax.swing.JTextField txtCPMiRS;
    private javax.swing.JTextField txtMiNegocio;
    private javax.swing.JTextPane txtNIFLetraCIFMiRS;
    private javax.swing.JTextField txtNIFLetraMiRS;
    private javax.swing.JTextField txtNIFNumeroMiRS;
    private javax.swing.JTextField txtNombreMiRS;
    private javax.swing.JTextField txtRutaCaja;
    private javax.swing.JTextField txtRutaCopiado;
    private javax.swing.JTextField txtRutaDistribuidores;
    private javax.swing.JTextField txtRutaFacturas;
    private javax.swing.JTextField txtTiposGastos;
    private javax.swing.JTextField txtTiposIVA;
    private javax.swing.JTextField txtTrimestreConfig;
    private javax.swing.JTextField txtdireccionMiRS;
    private javax.swing.JTextField txtpoblacionMiRS;
    private javax.swing.JTextField txttelefonoMiRS;
    // End of variables declaration//GEN-END:variables
}
