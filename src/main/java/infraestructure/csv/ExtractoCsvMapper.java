package infraestructure.csv;

import domain.records.Extracto;

public class ExtractoCsvMapper {

    public static Extracto fromLineaExtracto(LineaCsvDTO l) {

        double base = parseDoubleSafe(l.get(10));

        // tipo IVA puede venir como "10" o "10.0"
        int tipoIVA = (int) parseDoubleSafe(l.get(12));

        double iva = parseDoubleSafe(l.get(13));
        double subtotal = parseDoubleSafe(l.get(14));
        String concepto = l.get(6);

        return new Extracto(base, tipoIVA, iva, subtotal, concepto);
    }

    public static LineaCsvDTO toLineaExtracto(Extracto e) {
      String[] arr = new String[20];

      arr[0] = ""; // extracto → ID vacío
      arr[1] = ""; // sin num de extracto
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


