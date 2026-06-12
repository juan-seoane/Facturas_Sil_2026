package com.sil.facturas.infrastructure.csv;

import com.sil.facturas.domain.interfaces.IDebugService;
import com.sil.facturas.domain.interfaces.IFacturaRepo;
import com.sil.facturas.domain.pojos.Extracto;
import com.sil.facturas.domain.pojos.Factura;
import com.sil.facturas.infrastructure.records.LineaCsvDTO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FacturaCSVRepo implements IFacturaRepo {

    private final String rutaCSV;

    public FacturaCSVRepo(String rutaCSV) {
        this.rutaCSV = rutaCSV;
    }

    @Override
    public List<Factura> leerListaFacturas() {
        try {
            return CsvReader.leerFacturas(rutaCSV);
        } catch (IOException e) {
            IDebugService.printError("[FacturaCSVRepo] Error leyendo " + rutaCSV + ": " + e.getMessage());
            return List.of();
        }
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
      IDebugService.printError(
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
    return CsvWriter.escribirCSV(rutaCSV, parsearListaFacturas(listaFCT));
  }

  public static List<String[]> parsearListaFacturas(List<Factura> facturas) {
    return facturas.stream()
        .peek(Factura::normalizarSignos) // normaliza cada factura
        .flatMap(f -> escribirFactura(f).stream()) // convierte cada factura en sus líneas CSV
        .toList(); // devuelve la lista final
  }
}
