package com.sil.facturas.infrastructure.json.ocr;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sil.facturas.domain.ocr.*;
import com.sil.facturas.domain.ocr.enums.*;
import com.sil.facturas.infrastructure.json.ocr.dtos.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;
import java.util.stream.Collectors;

public class ModeloOCRParser {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // ============================================================
    //  PUBLIC API
    // ============================================================

    /** Carga un modeloOCR.json completo */
    public ModeloOCR parseModelo(File file) {
        try (FileReader reader = new FileReader(file)) {
            ModeloOCRDTO dto = gson.fromJson(reader, ModeloOCRDTO.class);
            return fromDTO(dto);
        } catch (Exception e) {
            throw new RuntimeException("Error leyendo modelo OCR: " + file, e);
        }
    }

    /** Carga un bloques.json (solo bloques) */
    public List<Bloque> parseBloques(File file) {
        try (FileReader reader = new FileReader(file)) {
            BloqueDTO[] dtos = gson.fromJson(reader, BloqueDTO[].class);
            return java.util.Arrays.stream(dtos)
                    .map(this::fromDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error leyendo bloques OCR: " + file, e);
        }
    }

  /** Guarda un modeloOCR.json completo */
  public void guardarModelo(ModeloOCR modelo, File file) {
    try {
      // Crear directorios si no existen
      File parent = file.getParentFile();
      if (parent != null && !parent.exists()) {
        parent.mkdirs();
      }

      try (FileWriter writer = new FileWriter(file)) {
        ModeloOCRDTO dto = toDTO(modelo);
        gson.toJson(dto, writer);
      }

    } catch (Exception e) {
      throw new RuntimeException(
          "[ModeloOCRParser>guardarModelo] Error guardando modelo OCR: " + file, e);
    }
  }

  /** Guarda un bloques.json (solo bloques) */
  public void guardarBloques(List<Bloque> bloques, File file) {
    try {
      // Crear directorios si no existen
      File parent = file.getParentFile();
      if (parent != null && !parent.exists()) {
        parent.mkdirs();
      }

      try (FileWriter writer = new FileWriter(file)) {
        List<BloqueDTO> dtos = bloques.stream().map(this::toDTO).collect(Collectors.toList());
        gson.toJson(dtos, writer);
      }

    } catch (Exception e) {
      throw new RuntimeException(
          "[ModeloOCRParser>guardarBloques] Error guardando bloques OCR: " + file, e);
    }
  }

    // ============================================================
    //  DTO → DOMINIO
    // ============================================================

    private ModeloOCR fromDTO(ModeloOCRDTO dto) {
        ModeloOCR modelo = new ModeloOCR();
        modelo.setNombre(dto.nombre);
        modelo.setVersion(dto.version);
        modelo.setRutaImagen(dto.rutaImagen);
        modelo.setDpi(dto.dpi);
        modelo.setZonasSueltas(dto.zonasSueltas);

        List<Bloque> bloques = dto.bloques.stream()
                .map(this::fromDTO)
                .collect(Collectors.toList());

        modelo.setBloques(bloques);
        return modelo;
    }

    private Bloque fromDTO(BloqueDTO dto) {

        // ============================
        // VALIDACIONES IMPORTANTES
        // ============================

        if (dto.nombre == null || dto.nombre.isBlank()) {
            throw new IllegalArgumentException("Bloque sin nombre detectado");
        }

        if (dto.zona == null) {
            throw new IllegalArgumentException("Bloque " + dto.nombre + " sin zona definida");
        }

        // Convertir campos con validación
        List<Campo> campos = dto.campos.stream()
                .map(c -> {

                    // Validación: parent debe coincidir con el nombre del bloque
                    if (!dto.nombre.equals(c.parent)) {
                        throw new IllegalArgumentException(
                                "Campo " + c.nombre + " tiene parent=" + c.parent +
                                " pero debería ser " + dto.nombre
                        );
                    }

                    // Validación: coordenadas relativas válidas
                    if (c.offsetX < 0 || c.offsetY < 0) {
                        throw new IllegalArgumentException(
                                "Campo " + c.nombre + " tiene offsets negativos"
                        );
                    }

                    if (c.w <= 0 || c.h <= 0) {
                        throw new IllegalArgumentException(
                                "Campo " + c.nombre + " tiene tamaño inválido"
                        );
                    }

                    return fromDTO(c);
                })
                .collect(Collectors.toList());

        List<String> cabecera = dto.cabeceraColumnas != null ? dto.cabeceraColumnas : List.of();

    return new Bloque(
        dto.nombre,
        dto.parentNombre,
        dto.referencia,
        fromDTO(dto.offset),
        fromDTO(dto.zona),
        dto.growVertical,
        dto.growHorizontal,
        _AnchorX.valueOf(dto.anchorX),
        _AnchorY.valueOf(dto.anchorY),
        campos,
        cabecera);
    }

    private Campo fromDTO(CampoDTO dto) {
        return new Campo(
                dto.nombre,
                dto.parent,
                dto.valorSemantico,
                dto.offsetX,
                dto.offsetY,
                dto.w,
                dto.h,
                _TipoContenido.valueOf(dto.tipoContenido)
        );
    }

    private Offset fromDTO(OffsetDTO dto) {
        return new Offset(
                _OffsetTipo.valueOf(dto.tipo),
                dto.dx,
                dto.dy
        );
    }

    private Rect fromDTO(RectDTO dto) {
        return new Rect(dto.x, dto.y, dto.w, dto.h);
    }

    // ============================================================
    //  DOMINIO → DTO
    // ============================================================

    private ModeloOCRDTO toDTO(ModeloOCR modelo) {
        ModeloOCRDTO dto = new ModeloOCRDTO();
        dto.nombre = modelo.getNombre();
        dto.version = modelo.getVersion();
        dto.rutaImagen = modelo.getRutaImagen();
        dto.dpi = modelo.getDpi();
        dto.zonasSueltas = modelo.getZonasSueltas();

        dto.bloques = modelo.getBloques().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return dto;
    }

    private BloqueDTO toDTO(Bloque bloque) {
        BloqueDTO dto = new BloqueDTO();
        dto.nombre = bloque.nombre();
        dto.parentNombre = bloque.parentNombre();
        dto.referencia = bloque.referencia();
        dto.offset = toDTO(bloque.offset());
        dto.zona = toDTO(bloque.zona());
        dto.growVertical = bloque.growVertical();
        dto.growHorizontal = bloque.growHorizontal();
        dto.anchorX = bloque.anchorX().name();
        dto.anchorY = bloque.anchorY().name();

        dto.campos = bloque.campos().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        if (bloque.cabeceraColumnas() != null && !bloque.cabeceraColumnas().isEmpty()) {
            dto.cabeceraColumnas = bloque.cabeceraColumnas();
        }

        return dto;
    }

    private CampoDTO toDTO(Campo campo) {
        CampoDTO dto = new CampoDTO();
        dto.nombre = campo.nombre();
        dto.parent = campo.parent();
        dto.valorSemantico = campo.valorSemantico();
        dto.offsetX = campo.offsetX();
        dto.offsetY = campo.offsetY();
        dto.w = campo.w();
        dto.h = campo.h();
        dto.tipoContenido = campo.tipoContenido().name();
        return dto;
    }

    private OffsetDTO toDTO(Offset offset) {
        OffsetDTO dto = new OffsetDTO();
        dto.tipo = offset.tipo().name();
        dto.dx = offset.dx();
        dto.dy = offset.dy();
        return dto;
    }

    private RectDTO toDTO(Rect rect) {
        RectDTO dto = new RectDTO();
        dto.x = rect.x();
        dto.y = rect.y();
        dto.w = rect.w();
        dto.h = rect.h();
        return dto;
    }

}
