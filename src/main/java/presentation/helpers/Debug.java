package presentation.helpers;

public class Debug {

    public static void print(String mensaje) {
        System.out.println(_Colores.GREEN.getANSICode() + mensaje + _Colores.RESET.getANSICode());
    }

    public static void printError(String mensaje) {
        System.err.println(_Colores.RED.getANSICode() + mensaje + _Colores.RESET.getANSICode());
    }
}
