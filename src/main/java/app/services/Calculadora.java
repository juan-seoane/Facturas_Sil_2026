package app.services;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class Calculadora extends JFrame implements KeyListener{

    private TextField txtPantalla;
    private ArrayList<Button> botones;
    private ArrayList<Button> btnOperaciones;
    String[] arrayOperaciones = {"C", "CE", "%", "/", "*", "-", "+", "+/-", "="};
    double operando1 = 0.00;
    double operando2 = 0.00;

    String operacion = "";
    String operacionAnt = "";
    double resultado;
    private Label lblOperacion;
    private JPanel panelArriba;
    private JPanel panelCentro;
    private JPanel panelAbajo;
    private int estado = 0;

    public Calculadora(String titulo) {
        super(titulo);
        this.setSize(200, 300);

        initComponents();
        initControls();

    }

    public void initComponents() {

        //COMPONENTES

        this.setLayout(new BorderLayout());

        txtPantalla = new TextField("", 20);
        txtPantalla.setEditable(false);
        txtPantalla.setBackground(Color.white);

        panelArriba = new JPanel();
        panelArriba.setLayout(new GridLayout(1, 1));

        panelArriba.add(txtPantalla);

        this.add(panelArriba, "North");

        botones = new ArrayList<Button>();

        for (int i = 0; i < 10; i++) {
            botones.add(new Button("" + i));
        }
        botones.add(new Button("."));

        btnOperaciones = new ArrayList<Button>();

        for (int i = 0; i < arrayOperaciones.length; i++) {
            btnOperaciones.add(new Button("" + arrayOperaciones[i]));
        }

        panelCentro = new JPanel();
        panelCentro.setLayout(new GridLayout(5, 4));

        panelCentro.add(btnOperaciones.get(0));
        panelCentro.add(btnOperaciones.get(1));
        panelCentro.add(btnOperaciones.get(2));
        panelCentro.add(btnOperaciones.get(3));

        panelCentro.add((Button) botones.get(7));
        panelCentro.add((Button) botones.get(8));
        panelCentro.add((Button) botones.get(9));
        panelCentro.add(btnOperaciones.get(4));

        panelCentro.add((Button) botones.get(4));
        panelCentro.add((Button) botones.get(5));
        panelCentro.add((Button) botones.get(6));
        panelCentro.add(btnOperaciones.get(5));

        panelCentro.add((Button) botones.get(1));
        panelCentro.add((Button) botones.get(2));
        panelCentro.add((Button) botones.get(3));
        panelCentro.add(btnOperaciones.get(6));

        panelCentro.add(btnOperaciones.get(7));
        panelCentro.add((Button) botones.get(0));
        panelCentro.add((Button) botones.get(10));
        panelCentro.add(btnOperaciones.get(8));

        this.add(panelCentro);

        panelAbajo = new JPanel();
        panelAbajo.setLayout(new GridLayout(1, 1));

        lblOperacion = new Label("");
        panelAbajo.add(lblOperacion);

        this.add(panelAbajo, "South");
        this.setFocusable(true);
        this.setAutoRequestFocus(true);
    }

    public void initControls() {

        //CONTROLADORES

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                calculadoraWindowClosing();
            }
        });

	for (int i = 0;i<11; i++){
            botones.get(i).setFocusable(false);
            botones.get (i).addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                   if (((Button) e.getSource()).getLabel().equals(".") && txtPantalla.getText().contains(".")) {
                   } else {
                       String label = ((Button) e.getSource()).getLabel();
                       switcherBotonesNum(label);
                   }
               }
           });
        }
        for (int i = 0; i < arrayOperaciones.length; i++) {
            btnOperaciones.get(i).setFocusable(false);
            btnOperaciones.get(i).addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String oper = ((Button) e.getSource()).getLabel();
                    int op = -1;
                    for (int i = 0; i < arrayOperaciones.length; i++) {
                        if (oper.equals(arrayOperaciones[i])) {
                            op = i;
                        }
                    }
                    operacion = leerOperacion(op);
                    if (operacion.equals("BORRAR")) {
                        txtPantalla.setText("");
                    } else if (operacion.equals("BORRARTODO")) {
                        txtPantalla.setText("");
                        operando1 = 0;
                        operando2 = 0;
                        operacionAnt = "";
                        operacion = "";
                        estado = 0;
                    } else {
                        switcherBotonesOp();
                    }
                }
            });
        }
        this.addKeyListener(this);
    }

    public void estado(int i) {

        estado = i;
        //System.out.println("Estado : " + i);
    }

    public void switcherBotonesNum(String label){
        switch (estado) {
                        case 0: //
                            txtPantalla.setText(txtPantalla.getText() + label);
                            estado = 1;
                            break;
                        case 1: //
                            txtPantalla.setText(txtPantalla.getText() + label);
                            break;
                        case 2: //
                            txtPantalla.setText("");//
                            txtPantalla.setText(txtPantalla.getText() + label);
                            estado = 3;
                            break;
                        case 3: //
                            txtPantalla.setText(txtPantalla.getText() + label);
                            break;
                        case 4: //
                            break;
                        case 5: //
                            txtPantalla.setText("");//
                            txtPantalla.setText(txtPantalla.getText() + label);
                            estado = 1;
                            break;
                    }
    }

    public void switcherBotonesOp(){
        switch (estado) {
            case 0:
                if (operacion.equals("SIGNO") && !txtPantalla.getText().contains("-")) {
                    txtPantalla.setText("-" + txtPantalla.getText());
                } else if (operacion.equals("SIGNO") && txtPantalla.getText().contains("-")) {
                    txtPantalla.setText(txtPantalla.getText().replace("-", ""));
                }
                break;
            case 1:
                if (operacion.equals("SIGNO") && !txtPantalla.getText().contains("-")) {
                    txtPantalla.setText("-" + txtPantalla.getText());
                } else if (operacion.equals("SIGNO") && txtPantalla.getText().contains("-")) {
                    txtPantalla.setText(txtPantalla.getText().replace("-", ""));
                } else {
                    operando1 = Double.parseDouble(txtPantalla.getText());
                    operacionAnt = operacion;
                    txtPantalla.setText("");
                    estado = 2;
                }
                break;
            case 2:
                if (operacion.equals("SIGNO") && !txtPantalla.getText().contains("-")) {
                    txtPantalla.setText("-" + txtPantalla.getText());
                } else if (operacion.equals("SIGNO") && txtPantalla.getText().contains("-")) {
                    txtPantalla.setText(txtPantalla.getText().replace("-", ""));
                }
                break;
            case 3:
                if (operacion.equals("SIGNO") && !txtPantalla.getText().contains("-")) {
                    txtPantalla.setText("-" + txtPantalla.getText());
                } else if (operacion.equals("SIGNO") && txtPantalla.getText().contains("-")) {
                    txtPantalla.setText(txtPantalla.getText().replace("-", ""));
                } else if (operacion.equals("TANTOPORCIENTO")) {
                    estado = 4;
                    operando2 = Double.parseDouble(txtPantalla.getText());
                    resultado = calcularTantoPorCiento(operando1, operando2, operacionAnt);
                    txtPantalla.setText("" + resultado);
                    estado = 5;
                } else if (operacion.equals("IGUAL")) {
                    operando2 = Double.parseDouble(txtPantalla.getText());
                    txtPantalla.setText("");
                    resultado = calcular(operando1, operando2, operacionAnt);
                    txtPantalla.setText("" + resultado);
                    estado = 5;
                } else {
                    operando2 = Double.parseDouble(txtPantalla.getText());
                    txtPantalla.setText("");
                    resultado = calcular(operando1, operando2, operacionAnt);
                    txtPantalla.setText("" + resultado);
                    operando1 = resultado;
                    operacionAnt = operacion;
                    estado = 2;
                }
                break;
            case 5:
                if (operacion.equals("SIGNO") && !txtPantalla.getText().contains("-")) {
                    txtPantalla.setText("-" + txtPantalla.getText());
                } else if (operacion.equals("SIGNO") && txtPantalla.getText().contains("-")) {
                    txtPantalla.setText(txtPantalla.getText().replace("-", ""));
                } else if (!operacion.equals("IGUAL")) {
                    if(!txtPantalla.getText().equals("")){
                        operando1 = Double.parseDouble(txtPantalla.getText());
                        operacionAnt = operacion;
                        txtPantalla.setText("");
                        estado = 2;
                    }
                }
                break;

        }
    }
    public double calcular(double operando1, double operando2, String operacion) {

        if (operacion.equals("DIVISION")) {
            resultado = operando1 / operando2;

        } else if (operacion.equals("MULTIPLICACION")) {
            resultado = operando1 * operando2;

        } else if (operacion.equals("RESTA")) {
            resultado = operando1 - operando2;

        } else if (operacion.equals("SUMA")) {
            resultado = operando1 + operando2;

        }

        return resultado;
    }

    public double calcularTantoPorCiento(double operando1, double operando2, String operacion) {

        if (operacion.equals("DIVISION")) {
            resultado = operando1 * operando2 / 100;

        } else if (operacion.equals("MULTIPLICACION")) {
            resultado = operando1 * operando2 / 100;

        } else if (operacion.equals("RESTA")) {
            resultado = operando1 - operando1 * operando2 / 100;

        } else if (operacion.equals("SUMA")) {
            resultado = operando1 + operando1 * operando2 / 100;

        }

        return resultado;
    }

    public String leerOperacion(int op) {

        switch (op) {
            case 0:			// C
                operacion = "BORRAR";
                break;
            case 1: 		// CE
                operacion = "BORRARTODO";
                break;
            case 2:
                operacion = "TANTOPORCIENTO";
                break;
            case 3:
                operacion = "DIVISION";
                break;
            case 4:
                operacion = "MULTIPLICACION";
                break;
            case 5:
                operacion = "RESTA";
                break;
            case 6:
                operacion = "SUMA";
                break;
            case 7:
                operacion = "SIGNO";
                break;
            case 8:
                operacion = "IGUAL";
                break;
        }
        return operacion;
    }

    public void calculadoraWindowClosing() {
        this.dispose();
    }

    @Override
    public void keyTyped(KeyEvent k){

    }
    @Override
    public void keyPressed(KeyEvent k) {
        if((k.getKeyCode()>= KeyEvent.VK_NUMPAD0 && k.getKeyCode()<=KeyEvent.VK_NUMPAD9)||(k.getKeyCode()>= KeyEvent.VK_0 && k.getKeyCode()<=KeyEvent.VK_9)){
            String label = k.getKeyChar()+"";
//            System.out.println(label);
            switcherBotonesNum(label);
        }
        if(k.getKeyCode()== KeyEvent.VK_DECIMAL){
            String label = k.getKeyChar()+"";
//            System.out.println(label);
            switcherBotonesNum(label);
        }
        if(k.getKeyCode()== KeyEvent.VK_ADD){
//            System.out.println("+");
               leerOperacion(6);
               switcherBotonesOp();
        }
        if(k.getKeyCode()== KeyEvent.VK_SUBTRACT){
//                System.out.println("-");
               leerOperacion(5);
               switcherBotonesOp();
        }
        if(k.getKeyCode()== KeyEvent.VK_MULTIPLY){
//            System.out.println("*");
               leerOperacion(4);
               switcherBotonesOp();
        }
        if(k.getKeyCode()== KeyEvent.VK_DIVIDE){
//            System.out.println("/");
               leerOperacion(3);
               switcherBotonesOp();
        }
        if(k.getKeyCode()== KeyEvent.VK_ENTER){
//            System.out.println("=");
               leerOperacion(8);
               switcherBotonesOp();
        }
        if(k.getKeyCode()== KeyEvent.VK_BACK_SPACE){
//            System.out.println("=");
               if (!txtPantalla.getText().equals("")){
                   txtPantalla.setText(txtPantalla.getText().substring(0,txtPantalla.getText().length()-1));
               }
        }
    }

    @Override
    public void keyReleased(KeyEvent k) {

    }

    public static void main(String args[]){
        Calculadora c = new Calculadora("prueba");
            c.setVisible(true);
    }
}
