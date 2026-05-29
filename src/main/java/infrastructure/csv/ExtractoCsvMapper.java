package infrastructure.csv;

import domain.records.Extracto;

public class ExtractoCsvMapper {

  public static Extracto fromLineaExtracto(LineaCsvDTO l) {

    // 1) NumExtracto (columna 1)
    int numExtracto = parseIntSafe(l.get(1));

    // 2) Concepto (columna 6)
    String concepto = l.get(6);

    // 3) Cantidad (columna 8)
    int cantidad = parseIntSafe(l.get(8));

    // 4) Base total (columna 9)
    double base = parseDoubleSafe(l.get(9));

    // 5) Precio unitario = base / cantidad
    double precioUnitario = (cantidad == 0) ? 0 : base / cantidad;

    // 6) Tipo IVA (columna 11)
    int tipoIVA = (int) parseDoubleSafe(l.get(11));

    // 7) IVA (columna 12)
    double iva = parseDoubleSafe(l.get(12));

    // 8) Total extracto (columna 13)
    double totalExtracto = parseDoubleSafe(l.get(13));

    return new Extracto(
         precioUnitario, cantidad, tipoIVA, iva, totalExtracto, concepto);
  }

    public static LineaCsvDTO toLineaExtracto(Extracto e) {
      String[] arr = new String[20];

      arr[0] = ""; // extracto → ID vacío
      // TODO 26-05-28 : Añadir numExtracto durante la entrada de datos (OCR o manual)
      arr[1] = e.getNumExtracto() != 0 ? String.valueOf(e.getNumExtracto()) : "0"; // sin num de extracto (por ahora)
      arr[6] = "extracto";

      arr[10] = String.valueOf(e.getBase());
      arr[12] = String.valueOf(e.getTipoIVA());
      arr[13] = String.valueOf(e.getIVA());
      arr[14] = String.valueOf(e.getSubtotal());

      return new LineaCsvDTO(arr);
    }

    public static LineaCsvDTO toLinea(Extracto ex) {

        String[] arr = new String[20];

        // 0) ID vacío → es extracto
        arr[0] = "";

        // 1) Nº extracto → tu modelo NO lo tiene
        arr[1] = "";

        // 6) Concepto
        arr[6] = ex.getConcepto();

        // Totales (según CSV real)
        arr[10] = String.valueOf(ex.getBase());
        arr[12] = String.valueOf(ex.getTipoIVA());
        arr[13] = String.valueOf(ex.getIVA());
        arr[14] = String.valueOf(ex.getSubtotal());

        // Rellenar el resto con vacío
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == null)
                arr[i] = "";
        }

        return new LineaCsvDTO(arr);
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


