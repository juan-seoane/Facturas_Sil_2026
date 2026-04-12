package presentation.helpers;

import java.util.HashMap;
import java.util.Map;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeTableView;
import presentation.config.UIDataConfig;

public class TableScaler {

    private static Map<String, Double> pesosIniciales = new HashMap<>();

    private static void inicializarPesos(ObservableList<? extends TableColumnBase<?, ?>> columnas) {
        if (!pesosIniciales.isEmpty()) return; // ya inicializado

        double suma = columnas.stream().mapToDouble(TableColumnBase::getPrefWidth).sum();

        for (TableColumnBase<?, ?> col : columnas) {
        pesosIniciales.put(col.getId(), col.getPrefWidth() / suma);
        }
    }

    public static Map<String, Double> resizeColumns(TableView<?> tabla, UIDataConfig cfg) {

        Map<String, Double> nuevos = new HashMap<>();

        if (tabla == null || tabla.getColumns().isEmpty() || cfg == null) {
            System.out.println("[TableScaler] ERROR: Saliendo antes de calcular nuevos anchos!");
            return nuevos;
        }
        // 1) sumar anchos base de columnas visibles (las que tienen ancho > 0)
        double totalBase = tabla.getColumns().stream()
                .filter(col -> cfg.getAnchoBase(col.getId()) > 0)
                .mapToDouble(col -> cfg.getAnchoBase(col.getId()))
                .sum();

        if (totalBase <= 0)
            return nuevos;

        double anchoDisponible = tabla.getLayoutBounds().getWidth();
        if (anchoDisponible <= 0)
            return nuevos;

        double f = anchoDisponible / totalBase;

        // 2) aplicar factor
        tabla
                .getColumns()
                .forEach(
                        col -> {
                            double base = cfg.getAnchoBase(col.getId());

                            double nuevo;

                            if (base == 0) {
                                // columna oculta por configuración
                                nuevo = 0;
                            } else {
                                nuevo = base * f;
                                if (nuevo < 1)
                                    nuevo = 0; // si queda demasiado pequeña, ocultar
                            }

                            col.setPrefWidth(nuevo);
                            nuevos.put(col.getId(), nuevo);
                            System.out.println("Columna: " + col.getText() +
                                    " | id=" + col.getId() +
                                    " | base=" + cfg.getAnchoBase(col.getId()));
                        });
        return nuevos;
    }

    public static Map<String, Double> resizeColumns(TreeTableView<?> table, UIDataConfig cfg) {
        double anchoDisponible = table.getWidth();
        return resizeColumnsGeneric(table.getColumns(), cfg, anchoDisponible);
    }

  private static Map<String, Double> resizeColumnsGeneric(
        ObservableList<? extends TableColumnBase<?, ?>> columnas,
        UIDataConfig cfg,
        double anchoDisponible) {

        inicializarPesos(columnas);

        Map<String, Double> nuevos = new HashMap<>();

        for (TableColumnBase<?, ?> col : columnas) {

        double peso = pesosIniciales.getOrDefault(col.getId(), 1.0 / columnas.size());
        double nuevoAncho = anchoDisponible * peso;

        col.setPrefWidth(nuevoAncho);
        nuevos.put(col.getId(), nuevoAncho);
        }

        return nuevos;
    }
}
