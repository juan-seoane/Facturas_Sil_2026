package com.sil.facturas.domain.records;

public record NIF(int numero, String letra, boolean isCIF) implements Comparable<NIF> {

  public NIF {
    letra = letra == null ? "" : letra.toUpperCase();
  }

  public static NIF fromRaw(String raw) {
    if (raw == null || raw.trim().isEmpty()) {
      return new NIF(0, "", false);
    }
    return parseNif(raw.trim().toUpperCase());
  }

  public static NIF parseNif(String texto) {
    String[] array = texto.split("-");
    return array2nif(array);
  }

  public static NIF array2nif(String[] array) {
    boolean isCIF;
    String letra;
    int numero;

    if (array[0].matches("[A-Z]")) {
      isCIF = true;
      letra = array[0];
      numero = Integer.parseInt(array[1]);
    } else if (array[0].matches("[0-9]{8}")) {
      isCIF = false;
      numero = Integer.parseInt(array[0]);
      letra = array[1];
    } else {
      throw new IllegalArgumentException("Formato NIF/CIF no reconocido");
    }

    return new NIF(numero, letra, isCIF);
  }

  public static String dameLetraNIF(int num) {
    return "TRWAGMYFPDXBNJZSQVHLCKE".charAt(num % 23) + "";
  }

  public boolean comprobarNIF() {
    if (isCIF) return true;
    return dameLetraNIF(numero).equals(letra);
  }

  @Override
  public int compareTo(NIF b) {
    int cmp = Integer.compare(this.numero, b.numero());
    return cmp != 0 ? cmp : this.letra.compareTo(b.letra());
  }

  @Override
  public String toString() {
    if (isCIF) {
      String num = String.valueOf(numero);
      return letra + "-" + num.substring(0, 2) + "-" + num.substring(2);
    }
    return numero + "-" + letra;
  }
}
