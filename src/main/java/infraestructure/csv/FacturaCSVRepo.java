package infraestructure.csv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import domain.records.Extracto;
import domain.records.Factura;
import infraestructure.servicios.config.Config;
import domain.interfaces.IfacturasRepo;

public class FacturaCSVRepo implements IfacturasRepo {

    public static Factura leerFactura(List<String[]> lineas, int indexInicial) {
        LineaCsvDTO lineaFactura = CsvReader.fromCsvArray(lineas.get(indexInicial));
        Factura factura = FacturaCsvMapper.fromLinea(lineaFactura);

        int numExtractos = Integer.parseInt(lineaFactura.get(8));

        for (int j = 0; j < numExtractos; j++) {
            LineaCsvDTO lineaExtracto = CsvReader.fromCsvArray(lineas.get(indexInicial + 1 + j));
            Extracto ex = ExtractoCsvMapper.fromLinea(lineaExtracto);
            factura.extractos.add(ex);
        }

        return factura;
    }

    public static List<String[]> escribirFactura(Factura f) {
        List<String[]> lineas = new ArrayList<>();

        // 1) Factura
        LineaCsvDTO dtoFactura = FacturaCsvMapper.toLinea(f);
        lineas.add(CsvWriter.toCsvArray(dtoFactura));

        // 2) Extractos
        for (Extracto ex : f.extractos) {
            LineaCsvDTO dtoEx = ExtractoCsvMapper.toLinea(ex);
            lineas.add(CsvWriter.toCsvArray(dtoEx));
        }

        return lineas;
    }

    public static List<String[]> parsearListaFacturas(List<Factura> facturas) {
        List<String[]> salida = new ArrayList<>();

        for (Factura f : facturas) {
            // 1) Línea principal
            salida.addAll(escribirFactura(f));
        }

        return salida;
    }

    public static List<Factura> leerTodasLasFacturas() {
        String ruta = Config.getConfig(Config.usuario).getConfigData().getRutas().getFCT();
        try {
            List<Factura> listaFCT;
            listaFCT = CsvReader.leerFacturas(ruta);
            return listaFCT;
        } catch (IOException e) {
            System.out.println("Error " + e.getClass() + " leyendo listaFCT de " + ruta + " : " + e.getMessage());
            System.exit(1);
        }
        return null;
    }

    @Override
    public List<Factura> leerListaFacturas() {
        List<Factura> listaFCT = leerTodasLasFacturas();
        return listaFCT;
    }

    @Override
    public boolean guardarListaFacturas(List<Factura> listaFCT) {
        List<String[]> listaLineas = parsearListaFacturas(listaFCT);
        String ruta = Config.getConfig(Config.usuario).getConfigData().getRutas().getFCT();

        return (CsvWriter.escribirCSV(ruta, listaLineas));
    }
}
