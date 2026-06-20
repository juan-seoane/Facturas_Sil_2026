package com.sil.facturas.domain.interfaces;

import com.sil.facturas.domain.enums._Colores;

public interface IDebugService {

    public static void print(String mensaje) {
        for (String linea : mensaje.split("\n")) {
            System.out.println(_Colores.GREEN.getANSICode() + linea + _Colores.RESET.getANSICode());
        }
    }

    public static void print(_Colores color, String mensaje) {
        for (String linea : mensaje.split("\n")) {
            System.out.println(color.getANSICode() + linea + _Colores.RESET.getANSICode());
        }
    }

    public static void printWarning(String mensaje) {
        for (String linea : mensaje.split("\n")) {
            System.out.println(_Colores.YELLOW.getANSICode() + linea + _Colores.RESET.getANSICode());
        }
    }

    public static void printError(String mensaje) {
        for (String linea : mensaje.split("\n")) {
            System.out.println(_Colores.RED.getANSICode() + linea + _Colores.RESET.getANSICode());
        }
    }
}
