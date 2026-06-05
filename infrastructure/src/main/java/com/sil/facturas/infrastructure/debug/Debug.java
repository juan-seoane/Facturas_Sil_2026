package com.sil.facturas.infrastructure.debug;

public class Debug {

    public static void print(String mensaje) {
        System.out.println(_Colores.GREEN.getANSICode() + mensaje + _Colores.RESET.getANSICode());
    }

    public static void printError(String mensaje) {
        System.err.println(_Colores.RED.getANSICode() + mensaje + _Colores.RESET.getANSICode());
    }

    public static void print(_Colores blue, String mensaje) {
        System.err.println(blue.getANSICode() + mensaje + _Colores.RESET.getANSICode());
    }
}
