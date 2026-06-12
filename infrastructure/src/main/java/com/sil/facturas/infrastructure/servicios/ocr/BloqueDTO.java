package com.sil.facturas.infrastructure.servicios.ocr;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class BloqueDTO {
  public String id;
  public String nombre;
  public String entity;
  public double x;
  public double y;
  public double width;
  public double height;
  public List<BloqueDTO> hijos = new ArrayList<>();
  public List<CampoDTO> campos = new ArrayList<>();

  public static BloqueDTO toDTO(BloqueOCR b) {
    BloqueDTO dto = new BloqueDTO();
    dto.id = b.getId();
    dto.nombre = b.getNombre();
    dto.entity = b.getEntity();
    dto.x = b.getX();
    dto.y = b.getY();
    dto.width = b.getWidth();
    dto.height = b.getHeight();

    // Hijos
    for (BloqueOCR hijo : b.getHijos()) {
      dto.hijos.add(toDTO(hijo));
    }

    // Campos
    for (CampoOCR campo : b.getCampos()) {
      dto.campos.add(CampoDTO.toDTO(campo));
    }

    return dto;
  }

  public static BloqueOCR fromDTO(BloqueDTO dto) {
    BloqueOCR b =
        new BloqueOCR(dto.id, dto.nombre, dto.entity, dto.x, dto.y, dto.width, dto.height);

    // Hijos
    for (BloqueDTO hijoDTO : dto.hijos) {
      b.addHijo(fromDTO(hijoDTO));
    }

    // Campos
    for (CampoDTO campoDTO : dto.campos) {
      b.addCampo(CampoDTO.fromDTO(campoDTO));
    }

    return b;
  }

  public void guardarBloques(Path path, List<BloqueOCR> bloques) throws IOException {
      List<BloqueDTO> lista = new ArrayList<>();
      for (BloqueOCR b : bloques) {
          lista.add(toDTO(b));
      }

      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      Files.writeString(path, gson.toJson(lista));
  }

  public List<BloqueOCR> cargarBloques(Path path) throws IOException {
    Gson gson = new Gson();
    Type tipoLista = new TypeToken<List<BloqueDTO>>() {}.getType();

    List<BloqueDTO> listaDTO = gson.fromJson(Files.readString(path), tipoLista);

    List<BloqueOCR> resultado = new ArrayList<>();
    for (BloqueDTO dto : listaDTO) {
      resultado.add(fromDTO(dto));
    }

    return resultado;
  }
}
