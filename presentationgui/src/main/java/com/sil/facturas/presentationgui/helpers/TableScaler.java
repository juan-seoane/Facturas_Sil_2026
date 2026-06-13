package com.sil.facturas.presentationgui.helpers;

import java.util.HashMap;
import java.util.Map;

import com.sil.facturas.infrastructure.config.UIDataConfig;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeTableView;

public class TableScaler {

  private static Map<String, Double> calcularPesosDesdeJSON(
      ObservableList<? extends TableColumnBase<?, ?>> columnas, UIDataConfig cfg) {

    Map<String, Double> pesos = new HashMap<>();

    double totalBase = columnas.stream().mapToDouble(col -> cfg.getAncho(col.getId())).sum();

    if (totalBase <= 0) return pesos;

    columnas.forEach(
        col -> {
          double base = cfg.getAncho(col.getId());
          pesos.put(col.getId(), base / totalBase);
        });

    return pesos;
  }

  public static Map<String, Double> resizeColumns(TreeTableView<?> table, UIDataConfig cfg) {
    double anchoDisponible = table.getWidth();
    double anchoReal = anchoDisponible - 21;
    return resizeColumnsGeneric(table.getColumns(), cfg, anchoReal);
  }

  private static Map<String, Double> resizeColumnsGeneric(
      ObservableList<? extends TableColumnBase<?, ?>> columnas,
      UIDataConfig cfg,
      double anchoDisponible) {

    Map<String, Double> nuevos = new HashMap<>();

    if (anchoDisponible <= 0) return nuevos;

    Map<String, Double> pesos = calcularPesosDesdeJSON(columnas, cfg);

    columnas.forEach(
        col -> {
          double peso = pesos.getOrDefault(col.getId(), 0.0);
          double nuevoAncho = anchoDisponible * peso;
          col.setPrefWidth(nuevoAncho);
          nuevos.put(col.getId(), nuevoAncho);
        });

    return nuevos;
  }
}
