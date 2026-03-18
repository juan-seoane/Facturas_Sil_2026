package infraestructure.csv;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

import domain.records.Factura;

public class CsvWriter {

    public static String[] toCsvArray(LineaCsvDTO dto) {
        return dto.columnas();
    }

    // ---------------------------------------------------------
    // 1) ESCRIBIR CSV COMPLETO (sobrescribe)
    // ---------------------------------------------------------
    public static boolean escribirCSV(String ruta, List<String[]> lineas) {

        Path p = Path.of(ruta);

        try {
            Files.createDirectories(p.getParent());

            List<String> salida = lineas.stream()
                    .map(arr -> String.join(";", arr))
                    .toList();

            Files.write(p, salida); // sobrescribe
            return true;

        } catch (IOException e) {
            System.out.println("Excepción " + e.getClass() + " al escribir en fichero CSV " + ruta);
            return false;
        }
    }

    // ---------------------------------------------------------
    // 2) ANEXAR UNA LÍNEA CSV (append)
    // ---------------------------------------------------------
    public static boolean appendCSV(String ruta, String[] linea) {

        Path p = Path.of(ruta);

        try {
            Files.createDirectories(p.getParent());

            String texto = String.join(";", linea) + System.lineSeparator();

            Files.writeString(
                    p,
                    texto,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );

            return true;

        } catch (IOException e) {
            System.out.println("Excepción " + e.getClass() + " al anexar en fichero CSV " + ruta);
            return false;
        }
    }

    // ---------------------------------------------------------
    // 3) ANEXAR UNA FACTURA COMPLETA (Factura + extractos)
    // ---------------------------------------------------------
    public static boolean appendFactura(String ruta, Factura factura) {

        List<String[]> lineas = FacturaCsvMapper.toLineas(factura);

        for (String[] linea : lineas) {
            if (!appendCSV(ruta, linea)) {
                return false;
            }
        }

        return true;
    }
}


