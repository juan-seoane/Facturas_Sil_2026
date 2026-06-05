package com.sil.facturas.domain.records;

public class NIF implements Comparable<NIF> {
  private int numero;
  private String letra;
  private boolean isCIF;

  public NIF(int numero, String letra, boolean isCIF) {
      this.numero = numero;
      this.letra = letra;
      this.isCIF = isCIF;
  }

  public NIF(String raw) {

    // 1. Normalizar entrada

    if (raw == null || raw.trim().isEmpty()) {
      // NIF vacío permitido
      this.numero = 0;
      this.letra = "";
      this.isCIF = false;
      return;
    }

    String s = raw.trim().toUpperCase();

    //2. parsear

    NIF nuevoNif = parseNif(s);
    this.numero = nuevoNif.getNumero();
    this.letra = nuevoNif.getLetra();
    this.isCIF = nuevoNif.isCIF();

  }

  public int getNumero() {
      return numero;
  }

  public void setNumero(int numero) {
      this.numero = numero;
  }

  public String getLetra() {
      return letra;
  }

  public void setLetra(String letra) {
      this.letra = letra.toUpperCase();
  }

  public boolean isCIF() {
      return isCIF;
  }

  public void setCIF(boolean isCIF) {
      this.isCIF = isCIF;
  }

  public static String dameLetraNIF(int num) {

    switch(num%23) {
      case 0 -> {
          return "T";
          }
      case 1 -> {
          return "R";
          }
      case 2 -> {
          return "W";
          }
      case 3 -> {
          return "A";
          }
      case 4 -> {
          return "G";
          }
      case 5 -> {
          return "M";
          }
      case 6 -> {
          return "Y";
          }
      case 7 -> {
          return "F";
          }
      case 8 -> {
          return "P";
          }
      case 9 -> {
          return "D";
          }
      case 10 -> {
          return "X";
          }
      case 11 -> {
          return "B";
          }
      case 12 -> {
          return "N";
          }
      case 13 -> {
          return "J";
          }
      case 14 -> {
          return "Z";
          }
      case 15 -> {
          return "S";
          }
      case 16 -> {
          return "Q";
          }
      case 17 -> {
          return "V";
          }
      case 18 -> {
          return "H";
          }
      case 19 -> {
          return "L";
          }
      case 20 -> {
          return "C";
          }
      case 21 -> {
          return "K";
          }
      case 22 -> {
          return "E";
          }
    }
    return "?";
  }

  public static NIF array2nif(String[] array) {
      // 1. Declarar las variables
      String letra = "Z";
      int numero = 99999999;
      boolean isCIF = false;


      if (array[0].matches("[A-Z]")) {
        // 2. Detectar si es CIF (empieza por letra)
        isCIF = true;
        letra = array[0].substring(0, 1); // prefijo CIF
        numero = Integer.parseInt(array[1]);
    } else if (array[0].matches("[0-9]{8}")) {
        // 3. Detectar DNI clásico: 12345678Z
        isCIF = false;
        numero = Integer.parseInt(array[0]);
        letra = array[1];
    } else {
        //TODO : 26/05/07 : Faltan los NIE
        throw new IllegalArgumentException("[NIF>array2nif] Formato de NIF/CIF no reconocido: "+array[0].toString() + " - " + array[1].toString());
    }

/*
    // 4. Detectar NIE: X1234567L, Y1234567L, Z1234567L
    if (s.matches("[XYZ][0-9]{7}[A-Z]")) {
      this.isCIF = false;
      // NIE: convertir X/Y/Z a número equivalente
      char prefix = s.charAt(0);
      int base =
          switch (prefix) {
            case 'X' -> 0;
            case 'Y' -> 1;
            case 'Z' -> 2;
            default -> throw new IllegalStateException("Prefijo NIE inválido");
          };
*/

      return (new NIF(numero, letra, isCIF));
  }

  public static NIF parseNif(String texto) {
    String[] array = texto.trim().split("-");
    return (array2nif(array));
  }

  public boolean comprobarNIF() {
      if (!this.isCIF()){
        if (dameLetraNIF(this.numero).equals(this.getLetra())){
          System.out.println("OK, la letra se corresponde con el número");
          return true;
        }else {
          System.out.println("La letra de este NIF no se corresponde con su número");
          return false;
        }
      } else{
          System.out.println("No se puede comprobar porque no es un NIF, es un CIF");
          return true;
      }
  }

  @Override
  public int compareTo(NIF b){
    if (this == b){
        return 0;
    }
    else if (this.getNumero() < (b).getNumero()){
        return -1;
    }
    else if (this.getNumero() > (b).getNumero()){
        return 1;
    }
    else if (this.getLetra().compareTo((b).getLetra()) == -1){
        return -1;
    }
    else if (this.getLetra().compareTo((b).getLetra()) == 1){
        return 1;
    }
    else return 0;
  }

  @Override
  public String toString(){
    if (this.isCIF){
            String num = this.numero +"";
            String prov = num.substring(0,2);
            String resto = num.substring(2,num.length());
            return (this.letra+"-"+prov+"-"+resto);
          }
          else
              return (this.numero +"-"+this.letra);
  }
}


