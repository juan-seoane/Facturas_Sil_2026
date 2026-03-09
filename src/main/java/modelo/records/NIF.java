package modelo.records;

public class NIF implements Comparable<NIF> {
  private int numero;
  private String letra;
  private boolean isCIF;

  public NIF(int numero, String letra, boolean isCIF) {
      this.numero = numero;
      this.letra = letra;
      this.isCIF = isCIF;
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
      case 0: return "T";
      case 1: return "R";
      case 2: return "W";
      case 3: return "A";
      case 4: return "G";
      case 5: return "M";
      case 6: return "Y";
      case 7: return "F";
      case 8: return "P";
      case 9: return "D";
      case 10: return "X";
      case 11: return "B";
      case 12: return "N";
      case 13: return "J";
      case 14: return "Z";
      case 15: return "S";
      case 16: return "Q";
      case 17: return "V";
      case 18: return "H";
      case 19: return "L";
      case 20: return "C";
      case 21: return "K";
      case 22: return "E";
    }
    return "?";
  }

  public static NIF array2nif(String[] array){
    boolean iscif=false;
    String l ="A";
    int num = 11111111;
    if (array==null || array.length !=2){
      System.out.println("[NIF>array2nif] El array es NULL o no tiene 2 campos");
      return null;
    }
    if (array[0].length()>array[1].length()){
      l = array[1];
      try{
        num = Integer.parseInt(array[0]);
      }catch( NumberFormatException ex){
        ex.printStackTrace();
        return null;
      }
      iscif = false;
    } 
    else if (array[1].length()>array[0].length()){
      l = array[0];
      try{
        num = Integer.parseInt(array[1]);
      }catch( NumberFormatException ex){
        ex.printStackTrace();
        return null;
      }
      iscif = true;
    }
    return (new NIF(num, l, iscif));
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
    else if (this.getLetra().compareTo((b).getLetra()) == -1){
        return -1;
    }
    else if (this.getLetra().compareTo((b).getLetra()) == 1){
        return 1;
    }
    else if (this.getNumero() < (b).getNumero()){
        return -1;
    }
    else if (this.getNumero() > (b).getNumero()){
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


