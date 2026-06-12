package com.sil.facturas.domain.enums;

public enum _Colores {
  // ANSI escape codes para colores en la consola
  // Acabar la impresión de colores con un RESET para volver al color por defecto
  RED("\u001B[31m"),
  GREEN("\u001B[32m"),
  YELLOW("\u001B[33m"),
  BLUE("\u001B[34m"),
  PURPLE("\u001B[35m"),
  CYAN("\u001B[36m"),
  WHITE("\u001B[37m"),
  RESET("\u001B[0m");

  private final String ANSIcode;

  _Colores(String ANSIcode) {
    this.ANSIcode = ANSIcode;
  }

  public String getANSICode() {
    return ANSIcode;
  }
}
