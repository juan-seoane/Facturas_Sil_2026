package com.sil.facturas.infrastructure.csv;

import java.util.ArrayList;
import java.util.List;

import com.sil.facturas.domain.pojos.Factura;
import com.sil.facturas.domain.pojos.RazonSocial;
import com.sil.facturas.domain.pojos.TipoGasto;
import com.sil.facturas.domain.pojos.Totales;
import com.sil.facturas.domain.records.*;
import com.sil.facturas.infrastructure.records.LineaCsvDTO;

public class FacturaCsvMapper {

    public static Factura fromLinea(LineaCsvDTO l) {

    if (l.length() < 20) {
      System.out.println("[CSV] Línea inválida ignorada: " + l);
      return null;
    }

    // 1) Totales
    Totales totales =
        new Totales(
            parseDoubleSafe(l.get(9)),
            Boolean.parseBoolean(l.get(10)),
            parseIntSafe(l.get(11)),
            parseDoubleSafe(l.get(12)),
            parseDoubleSafe(l.get(13)),
            parseDoubleSafe(l.get(14)),
            parseIntSafe(l.get(15)),
            parseDoubleSafe(l.get(16)),
            parseDoubleSafe(l.get(17)),
            l.get(6));

        // 2) Nota
        boolean existe = l.get(18).equals("1");
        String contenido = l.get(19);
        Nota nota = (existe ? new Nota(contenido) : null);

        // 3) Factura
        return new Factura(
            Integer.valueOf(l.get(0)), // ID
            l.get(1), // número factura
            Fecha.fromString(l.get(2)), // fecha
            new RazonSocial(Integer.valueOf(l.get(3)), NIF.parseNif(l.get(4)), l.get(5)),
            new TipoGasto(l.get(6), l.get(6)), // categoría REAL
            Boolean.parseBoolean(l.get(7)), // esDevolucion
            new ArrayList<>(), // extractos se añaden después
            totales,
            nota
        );
    }

    public static LineaCsvDTO toLinea(Factura f) {
        String[] arr = new String[20];

        // 1) Datos principales
        arr[0] = String.valueOf(f.ID);
        arr[1] = f.numeroFactura;
        arr[2] = f.fecha.toString(); // o tu formateador
        arr[3] = String.valueOf(f.RS.getID());
        arr[4] = f.RS.getNif().toString();
        arr[5] = f.RS.getNombre();

        // 2) Categoría y devoluciones
        arr[6] = f.concepto.getTipo();
        arr[7] = String.valueOf(f.esDevolucion);

        // 3) Número de extractos
        arr[8] = String.valueOf(f.extractos.size());

        // 4) Totales
        Totales t = f.totales;

        arr[9] = String.valueOf(t.getBase());
        arr[10] = String.valueOf(t.isVariosIVAs());
        arr[11] = String.valueOf(t.getTipoIVA());
        arr[12] = String.valueOf(t.getIVA());
        arr[13] = String.valueOf(t.getSubtotal());
        arr[14] = String.valueOf(t.getBaseNI());
        arr[15] = String.valueOf(t.getRet());
        arr[16] = String.valueOf(t.getRetenciones());
        arr[17] = String.valueOf(t.getTotal());

        // 5) Nota
        arr[18] = f.notaExiste() ? "1" : "0";
        arr[19] = f.notaExiste() ? f.getNota().texto() : "";

        return new LineaCsvDTO(arr);
    }

    public static List<String[]> toLineas(Factura factura) {

        List<String[]> resultado = new ArrayList<>();

        // 1) Línea principal
        LineaCsvDTO dtoFactura = toLinea(factura);
        resultado.add(dtoFactura.columnas());

        // 2) Líneas de extractos
        factura.extractos.forEach(extracto -> {
            LineaCsvDTO dtoExt = ExtractoCsvMapper.toLineaExtracto(extracto);
            resultado.add(dtoExt.columnas());
        });

        return resultado;
    }

  private static double parseDoubleSafe(String s) {
    if (s == null || s.isBlank()) return 0.0;
    return Double.parseDouble(s);
  }

  private static int parseIntSafe(String s) {
    if (s == null || s.isBlank()) return 0;
    return Integer.parseInt(s);
  }
}
