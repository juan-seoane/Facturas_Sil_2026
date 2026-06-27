package com.sil.facturas.infrastructure.json.ocr.dtos;

import java.util.List;

public class BloqueDTO {
    public String nombre;
    public String parentNombre;
    public String referencia;
    public OffsetDTO offset;
    public RectDTO zona;
    public boolean growVertical;
    public boolean growHorizontal;
    public String anchorX;
    public String anchorY;
    public java.util.List<CampoDTO> campos;
    public List<String> cabeceraColumnas;

    public BloqueDTO() {
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getParentNombre() {
        return parentNombre;
    }

    public void setParentNombre(String parentNombre) {
        this.parentNombre = parentNombre;
    }
    public String getReferencia() {
        return referencia;
    }
    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }
    public OffsetDTO getOffset() {
        return offset;
    }
    public void setOffset(OffsetDTO offset) {
        this.offset = offset;
    }
    public RectDTO getZona() {
        return zona;
    }
    public void setZona(RectDTO zona) {
        this.zona = zona;
    }
    public boolean isGrowVertical() {
        return growVertical;
    }
    public void setGrowVertical(boolean growVertical) {
        this.growVertical = growVertical;
    }
    public boolean isGrowHorizontal() {
        return growHorizontal;
    }
    public void setGrowHorizontal(boolean growHorizontal) {
        this.growHorizontal = growHorizontal;
    }
    public String getAnchorX() {
        return anchorX;
    }
    public void setAnchorX(String anchorX) {
        this.anchorX = anchorX;
    }
    public String getAnchorY() {
        return anchorY;
    }
    public void setAnchorY(String anchorY) {
        this.anchorY = anchorY;
    }
    public java.util.List<CampoDTO> getCampos() {
        return campos;
    }
    public void setCampos(java.util.List<CampoDTO> campos) {
        this.campos = campos;
    }
}
