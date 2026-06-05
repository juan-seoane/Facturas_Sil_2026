package infrastructure.servicios.ocr.aux_ocr;

import java.util.ArrayList;
import java.util.List;

public class Bloque {
  public String nombre;
  public Rect zona;

  public String extensibleHacia;
  public String relativoA;
  public double offsetY;

  public Rect cabecera;
  public Rect lineaBase;
  public Rect finalExtractos;

  public double alturaLineaAprox;

  public List<Campo> campos = new ArrayList<>();
}
