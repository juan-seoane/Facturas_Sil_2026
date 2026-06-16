package com.sil.facturas.infrastructure.servicios.ocr.aux_ocr;

public class CampoDTO {
  public String id;
  public String nombre;
  public String dominio;

  public double x;
  public double y;
  public double width;
  public double height;

  public static CampoDTO toDTO(CampoOCR c) {
      CampoDTO dto = new CampoDTO();
      dto.id = c.getId();
      dto.nombre = c.getNombre();
      dto.dominio = c.getDominio();
      dto.x = c.getX();
      dto.y = c.getY();
      dto.width = c.getWidth();
      dto.height = c.getHeight();
      return dto;
  }

  public static CampoOCR fromDTO(CampoDTO dto) {
    CampoOCR c = new CampoOCR(dto.id, dto.nombre, dto.dominio);
    c.setX(dto.x);
    c.setY(dto.y);
    c.setWidth(dto.width);
    c.setHeight(dto.height);
    return c;
  }
}
