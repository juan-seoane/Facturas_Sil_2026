package com.sil.facturas.infrastructure.services.ocr.aux_ocr;


import com.sil.facturas.domain.ocr.ROI;
import com.sil.facturas.domain.ocr.enums.*;
import java.util.ArrayList;
import java.util.List;

public class BloqueOCR extends OCRItem {

  private String nombre;
  private String referencia;

  private _OffsetTipo offsetTipo;
  private int dx;
  private int dy;

  private boolean growVertical;
  private boolean growHorizontal;

  private _AnchorX anchorX;
  private _AnchorY anchorY;

  private final List<CampoOCR> campos = new ArrayList<>();
  private List<String> cabeceraColumnas;

  public BloqueOCR(
      String nombre,
      String referencia,
      double x,
      double y,
      double w,
      double h,
      _OffsetTipo offsetTipo,
      int dx,
      int dy,
      boolean growVertical,
      boolean growHorizontal,
      _AnchorX anchorX,
      _AnchorY anchorY, List<String> cabeceraColumnas) {
    super(x, y, w, h);
    this.nombre = nombre;
    this.referencia = referencia;
    this.offsetTipo = offsetTipo;
    this.dx = dx;
    this.dy = dy;
    this.growVertical = growVertical;
    this.growHorizontal = growHorizontal;
    this.anchorX = anchorX;
    this.anchorY = anchorY;
    this.cabeceraColumnas = cabeceraColumnas != null ? cabeceraColumnas : new ArrayList<>();
  }
  
  public ROI toROI() {
        double x1 = x;
        double y1 = y;
        double x2 = x + w;
        double y2 = y + h;
        return new ROI(x1, y1, x2, y2);
    }

    public String nombre() {
        return nombre;
    }
    
    public String referencia() {
        return referencia;
    }

    public _OffsetTipo offsetTipo() {
        return offsetTipo;
    }

    public int dx() {
        return dx;
    }

    public int dy() {
        return dy;
    }

    public boolean growVertical() {
        return growVertical;
    }

    public boolean growHorizontal() {
        return growHorizontal;
    }

    public _AnchorX anchorX() {
        return anchorX;
    }

    public _AnchorY anchorY() {
        return anchorY;
    }
    
    public List<CampoOCR> campos() {
        return campos;
    }

    public void addCampo(CampoOCR campo) {
        campos.add(campo);
    }

    public boolean esCabecera() {
        return nombre != null && nombre.toLowerCase().contains("cabecera");
    }

    public List<String> cabeceraColumnas() {
        return this.cabeceraColumnas;
    }

    public void setCabeceraColumnas(List<String> lista) {
        this.cabeceraColumnas = lista;
    }
    
        @Override
        public String toString() {
        return "BloqueOCR [nombre=" + nombre + ", referencia=" + referencia + ", offsetTipo=" + offsetTipo + ", dx=" + dx
                + ", dy=" + dy + ", growVertical=" + growVertical + ", growHorizontal=" + growHorizontal + ", anchorX="
                + anchorX + ", anchorY=" + anchorY + ", NumCampos=" + campos.size() + ", NumColumnasCabecera=" + cabeceraColumnas.size() + "]";
    }
}
