package infraestructure.csv;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import domain.records.Factura;

public class CsvReader {

    public static LineaCsvDTO fromCsvArray(String[] arr) {
        return new LineaCsvDTO(arr);
    }

    public static List<Factura> leerFacturas(String ruta) throws IOException {
        List<String[]> lineas = CsvReader.leerCSV(ruta);
        AtomicInteger idx = new AtomicInteger(0);

        List<Factura> facturas = new ArrayList<>();

        while (idx.get() < lineas.size()) {
            String[] linea = lineas.get(idx.get());

            if (esLineaFactura(linea)) {
                Factura f = FacturaCSVRepo.leerFacturaConExtractos(lineas, idx.get());

                facturas.add(f);

                // avanzar índice: 1 línea factura + N extractos
                idx.addAndGet(1 + f.extractos.size());
            } else {
                // si aparece un extracto sin factura, lo ignoramos
                idx.incrementAndGet();
            }
        }

        return facturas;
    }

    public static List<String[]> leerCSV(String ruta) throws IOException {
        Path p = Path.of(ruta);

        if (!Files.exists(p)) {
            return Collections.emptyList();
        }

        List<String> lineas = Files.readAllLines(p);

        List<String[]> resultado = new ArrayList<>();

        for (String linea : lineas) {
            if (!linea.isBlank()) {
                resultado.add(linea.split(";"));
            }
        }

        return resultado;
    }

    public static boolean esLineaFactura(String[] arr) {
        return arr.length > 0 && !arr[0].isBlank();
    }

    public static boolean esLineaExtracto(String[] arr) {
        return arr.length > 0 && arr[0].isBlank();
    }
}
