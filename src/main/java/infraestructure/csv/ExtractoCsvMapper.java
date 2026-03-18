package infraestructure.csv;

import domain.records.Extracto;

public class ExtractoCsvMapper {

    public static Extracto fromLinea(LineaCsvDTO l) {
        return new Extracto(
                Double.parseDouble(l.get(9)),
                Integer.parseInt(l.get(11)),
                Double.parseDouble(l.get(12)),
                Double.parseDouble(l.get(13)),
                l.get(6));
    }

    public static LineaCsvDTO toLinea(Extracto ex) {

        String[] arr = new String[20];

        // 1) Línea del extracto (si la tienes en el modelo)
        arr[1] = "";

        // 2) Concepto
        arr[6] = ex.getConcepto();

        // 3) Totales del extracto
        arr[9] = String.valueOf(ex.getBase());
        arr[11] = String.valueOf(ex.getTipoIVA());
        arr[12] = String.valueOf(ex.getIVA());
        arr[13] = String.valueOf(ex.getSubtotal());

        // 4) Resto de columnas vacías
        arr[0] = "";
        arr[2] = "";
        arr[3] = "";
        arr[4] = "";
        arr[5] = "";
        arr[7] = "";
        arr[8] = "";
        arr[10] = "";
        arr[14] = "";
        arr[15] = "";
        arr[16] = "";
        arr[17] = "";
        arr[18] = "";
        arr[19] = "";

        return new LineaCsvDTO(arr);
    }
    public static LineaCsvDTO toLineaExtracto(Extracto e) {

    String[] columnas = new String[] {
            "",                                 // 0 → vacío (no es factura)
            "",                                 // 1 → número de extracto (de existir)
            "",                                 // 2
            "",                                 // 3
            "",                                 // 4
            e.getConcepto(),                    // 5 → concepto
            "",                                 // 6
            "",                                 // 7
            String.valueOf(e.getBase()),        // 8 → base imponible
            String.valueOf(e.getTipoIVA()),     // 9 → tipoIVA
            String.valueOf(e.getIVA()),         // 10 → IVA
            String.valueOf(e.getSubtotal())     // 11 → subtotal (total línea)
    };

    return new LineaCsvDTO(columnas);
}


}
