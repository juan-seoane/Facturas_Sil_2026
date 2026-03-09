package modelo.records;

public class TipoGasto {
  private String tipo;
  private String descripcion;

  public TipoGasto(String tipo, String descripcion){
    if (tipo!=null){
      this.tipo = tipo.toUpperCase();
    }else{
	    this.tipo = "INICIO";
    }
    if (descripcion != null){
      this.descripcion = descripcion;
    }else{
      this.descripcion = "descripcion del tipo de gasto";
    }
  }

  public String getTipo() {
      return tipo;
  }

  public void setTipo(String tipo) {
      this.tipo = tipo;
  }

  public String getDescripcion() {
      return descripcion;
  }

  public void setDescripcion(String descripcion) {
      this.descripcion = descripcion;
  }

  public boolean equals(Object b){

	  return (this.getTipo().equals(((TipoGasto)b).getTipo()));
  }
}
