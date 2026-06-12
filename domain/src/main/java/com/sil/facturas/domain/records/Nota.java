package com.sil.facturas.domain.records;

public record Nota(String texto) {

  public Nota {
    texto = (texto == null || texto.isBlank()) ? "" : texto;
  }

  public boolean isVacia() {
    return texto.isBlank();
  }
}
