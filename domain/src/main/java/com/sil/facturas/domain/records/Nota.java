package com.sil.facturas.domain.records;

public class Nota {

  private String texto;

  public Nota(String texto){

    if (texto==null || texto.equals(""))
        this.texto = "";
    else
        this.texto = texto;
  }

  public String getTexto() {
      return texto;
  }

  public void setTexto(String texto) {
      this.texto = texto;
  }

// REVIEW - 24-05-07 : Revisar este método, a lo mejor la nota formateada no tiene que devolver un TextArea...
  /*public TextArea format(int r, int c){

	  return new TextArea(this.texto,r,c);
  }*/
}
