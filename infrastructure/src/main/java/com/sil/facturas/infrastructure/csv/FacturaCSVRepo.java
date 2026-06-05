package com.sil.facturas.infrastructure.csv;

import com.sil.facturas.domain.interfaces.IfacturasRepo;
import com.sil.facturas.domain.records.Extracto;
import com.sil.facturas.domain.records.Factura;
import com.sil.facturas.infrastructure.debug.Debug;
import com.sil.facturas.infrastructure.servicios.config.Config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FacturaCSVRepo implements IfacturasRepo {

    private String rutaCSV;

    public FacturaCSVRepo(String rutaCSV) {
        this.rutaCSV = rutaCSV;
    }

    public static Factura leerFactura(List<String[]> lineas, int indexInicial) {
        LineaCsvDTO lineaFactura = CsvReader.fromCsvArray(lineas.get(indexInicial));
        Factura factura = FacturaCsvMapper.fromLinea(lineaFactura);

        int numExtractos = Integer.parseInt(lineaFactura.get(8));

        for (int j = 0; j < numExtractos; j++) {
            LineaCsvDTO lineaExtracto = CsvReader.fromCsvArray(lineas.get(indexInicial + 1 + j));
            Extracto ex = ExtractoCsvMapper.fromLineaExtracto(lineaExtracto);
            if (ex == null) continue;
            factura.extractos.add(ex);
        }

        return factura;
    }
    @Override
    public List<Factura> leerListaFacturas() {
    List<Factura> listaFCT = leerTodasLasFacturas();
    return listaFCT;
  }

    public static List<Factura> leerTodasLasFacturas() {
    // STUB : 26-03-18
    String ruta = Config.getConfig(Config.usuario).getConfigData().getRutas().getFCT();
    // System.out.println(
    //     "[FacturaCSVRepo>leerTodasLasFacturas()] Leyendo facturas del archivo " + ruta);
    try {
      List<Factura> listaFCT;
      listaFCT = CsvReader.leerFacturas(ruta);
    //   System.out.println(
    //       "[FacturaCSVRepo>leerTodasLasFacturas()] Facturas leidas = " + listaFCT.size());
      return listaFCT;
    } catch (IOException e) {
      Debug.printError(
          "[FacturaCSVRepo>leerTodasLasFacturas()] Error " + e.getClass() + " leyendo listaFCT de " + ruta + " : " + e.getMessage());
      System.exit(1);
    }
    return null;
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

  public boolean actualizarFactura(Factura facturaEditada) {
    List<Factura> lista = leerListaFacturas();

    boolean existe = lista.stream().anyMatch(f -> f.getID().equals(facturaEditada.getID()));

    if (!existe) {
      Debug.printError(
          "[FacturaCSVRepo>actualizarFactura] La factura no existía. La lista de facturas no se"
              + " modificará!");
      return false;
    }

    facturaEditada.normalizarSignos();

    return guardarListaFacturas(
        lista.stream()
            .map(f -> f.getID().equals(facturaEditada.getID()) ? facturaEditada : f)
            .toList());
  }

    public boolean borrarFactura(String idFactura) {
    List<Factura> lista = leerListaFacturas();

    // filtrar: eliminar factura base y sus extractos
    List<Factura> nuevaLista = lista.stream().filter(f -> !f.getID().equals(idFactura)).toList();

    return guardarListaFacturas(nuevaLista);
  }
    @Override
    public boolean guardarFactura(Factura factura) {
        // TODO : 26/05/08 : Chequear que ningún campo tenga una coma antes de guardar el CSV
        // 1. Leer lista actual
        List<Factura> lista = leerListaFacturas();

        // 2. Añadir la nueva factura
        factura.normalizarSignos();
        lista.add(factura);

        // 3. Convertir TODAS las facturas a CSV
        List<String[]> lineas = parsearListaFacturas(lista);

        // 4. Escribir CSV completo
        return CsvWriter.escribirCSV(rutaCSV, lineas);
    }

    @Override
    public boolean guardarListaFacturas(List<Factura> listaFCT) {
        return CsvWriter.escribirCSV(
            rutaCSV, parsearListaFacturas(listaFCT));
    }

    public static List<String[]> parsearListaFacturas(List<Factura> facturas) {
        return facturas.stream()
            .peek(Factura::normalizarSignos) // normaliza cada factura
            .flatMap(f -> escribirFactura(f).stream()) // convierte cada factura en sus líneas CSV
            .toList(); // devuelve la lista final
    }
}
