package infraestructure.csv;

import domain.records.Factura;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CsvReader {

  public static LineaCsvDTO fromCsvArray(String[] arr) {
    return new LineaCsvDTO(arr);
  }

  public static List<Factura> leerFacturas(String ruta) throws IOException {
    List<String[]> lineas = CsvReader.leerCSV(ruta);
    System.out.println("[CsvReader>leerFacturas] CSV leído con " + lineas.size() + " líneas");
    AtomicInteger idx = new AtomicInteger(0);

    List<Factura> facturas = new ArrayList<>();

    while (idx.get() < lineas.size()) {
      String[] linea = lineas.get(idx.get());

      if (esLineaFactura(linea)) {
        Factura f = FacturaCSVRepo.leerFactura(lineas, idx.get());

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
      System.out.println("[CSVReader>leerCSV] El fichero NO existe!");
      return Collections.emptyList();
    }

    List<String[]> resultado =
        Files.readAllLines(p).stream()
            .map(linea -> linea.replace("\uFEFF", "")) // quitar BOM
            .map(String::trim)
            .filter(linea -> !linea.isBlank())
            .filter(linea -> !linea.startsWith("\"#ID\"")) // filtrar cabecera
            .map(linea -> linea.split(",", -1)) // separador real
            .map(
                arr ->
                    Arrays.stream(arr)
                        .map(s -> s.replace("\"", "")) // quitar comillas
                        .toArray(String[]::new))
            .toList();

    // DEBUG opcional
    AtomicInteger idx = new AtomicInteger(1);
    resultado.forEach(
        arr -> System.out.println("[CSV][" + idx.getAndIncrement() + "] " + Arrays.toString(arr)));

    return resultado;
  }

  public static boolean esLineaFactura(String[] arr) {
    return arr.length > 0 && !arr[0].isBlank();
  }

  public static boolean esLineaExtracto(String[] arr) {
    return arr.length > 0 && arr[0].isBlank();
  }
}
