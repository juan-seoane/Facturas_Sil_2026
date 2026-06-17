package com.sil.facturas.presentationgui.fxcontrollers;

import com.sil.facturas.domain.ocr.Bloque;
import com.sil.facturas.domain.ocr.enums._AnchorX;
import com.sil.facturas.domain.ocr.enums._AnchorY;
import com.sil.facturas.domain.ocr.enums._OffsetTipo;
import com.sil.facturas.domain.ocr.enums._TipoContenido;
import com.sil.facturas.infrastructure.services.ocr.ModeloOCRService;
import com.sil.facturas.infrastructure.services.ocr.OCRService;
import com.sil.facturas.infrastructure.services.ocr.aux_ocr.BloqueOCR;
import com.sil.facturas.infrastructure.services.ocr.aux_ocr.CampoOCR;
import com.sil.facturas.infrastructure.services.ocr.aux_ocr.OCRItem;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class FxCntrlROIEditor {
     @FXML private ScrollPane scrollPane;
    @FXML private Pane canvasPane;

    private ImageView imageView;
    private final List<OCRItem> items = new ArrayList<>();

    private ModeloOCRService modeloService;
    private OCRService ocrService;

    public void initialize() {
        modeloService = new ModeloOCRService();
        ocrService = new OCRService();
    }

    // ============================================================
    //  CARGAR IMAGEN
    // ============================================================

    @FXML
    private void onCargarImagen() {
        File file = new File("D:/facturas/ejemplo.png");
        Image img = new Image(file.toURI().toString());

        imageView = new ImageView(img);
        canvasPane.getChildren().add(imageView);

        canvasPane.setPrefSize(img.getWidth(), img.getHeight());
    }

  // ============================================================
  //  NUEVO BLOQUE
  // ============================================================

  @FXML
  private void onNuevoBloque() {

    List<String> cabecera = resolverCabeceraParaNuevoBloque();


    BloqueOCR b =
        new BloqueOCR(
            "bloque" + (items.size() + 1),
            "ref",
            50,
            50,
            200,
            80,
            _OffsetTipo.NONE,
            0,
            0,
            false,
            false,
            _AnchorX.LEFT,
            _AnchorY.TOP,
            cabecera);

    items.add(b);
    dibujarBloque(b);
  }

  private List<String> resolverCabeceraParaNuevoBloque() {

    // 1. Si ya existe un bloque de extractos en el modelo cargado → usar su cabecera
    var modelo = modeloService.getModelo();
    if (modelo != null) {
        for (Bloque b : modelo.getBloques()) {
            if (b.cabeceraColumnas() != null && !b.cabeceraColumnas().isEmpty()) {
                return new ArrayList<>(b.cabeceraColumnas());
            }
        }
    }

    // 2. Intentar detectar un bloque de cabecera dibujado por el usuario
    BloqueOCR bloqueCabecera = items.stream()
            .filter(i -> i instanceof BloqueOCR b && b.esCabecera())
            .map(i -> (BloqueOCR) i)
            .findFirst()
            .orElse(null);

    if (bloqueCabecera != null) {
        String texto = ocrService.ocr(toBufferedImage(imageView.getImage()), bloqueCabecera.toROI());
        List<String> columnas = parsearCabeceraDesdeTexto(texto);
        if (!columnas.isEmpty()) return columnas;
    }

    // 3. Último recurso: pedir al usuario que defina la cabecera
    return pedirCabeceraAlUsuario();
}

    // ============================================================
    //  NUEVO CAMPO
    // ============================================================

    @FXML
    private void onNuevoCampo() {
        // Debe haber un bloque seleccionado
        BloqueOCR bloque = (BloqueOCR) items.stream()
                .filter(i -> i instanceof BloqueOCR)
                .findFirst()
                .orElse(null);

        if (bloque == null) return;

        CampoOCR c = new CampoOCR(
                "campo" + (bloque.campos().size() + 1),
                bloque.nombre(),
                "concepto",
                bloque.x() + 10,
                bloque.y() + 10,
                100,
                30,
                _TipoContenido.TEXTO_COMPLETO
        );

        bloque.addCampo(c);
        items.add(c);

        dibujarCampo(c);
    }

    // ============================================================
    //  GUARDAR MODELO
    // ============================================================

    @FXML
    private void onGuardarModelo() {
        modeloService.nuevoModelo("Factura", "1.0");

        for (OCRItem item : items) {
            if (item instanceof BloqueOCR b) {
                modeloService.addBloque(modeloService.convertirBloqueOCR(b));
            }
        }

        modeloService.guardarModelo(new File("D:/facturas/modeloOCR.json"));
    }

    // ============================================================
    //  DIBUJAR ELEMENTOS
    // ============================================================

    private void dibujarBloque(BloqueOCR b) {
        var rect = new javafx.scene.shape.Rectangle(b.x(), b.y(), b.w(), b.h());
        rect.setStroke(javafx.scene.paint.Color.YELLOW);
        rect.setFill(javafx.scene.paint.Color.TRANSPARENT);
        canvasPane.getChildren().add(rect);
    }

    private void dibujarCampo(CampoOCR c) {
        var rect = new javafx.scene.shape.Rectangle(c.x(), c.y(), c.w(), c.h());
        rect.setStroke(javafx.scene.paint.Color.CYAN);
        rect.setFill(javafx.scene.paint.Color.TRANSPARENT);
        canvasPane.getChildren().add(rect);
    }

    private BufferedImage toBufferedImage(Image fxImage) {
        return javafx.embed.swing.SwingFXUtils.fromFXImage(fxImage, null);
    }

  private List<String> parsearCabeceraDesdeTexto(String texto) {
    if (texto == null || texto.isBlank()) return List.of();

    String[] tokens = texto.trim().split("\\s+");

    List<String> columnas = new ArrayList<>();
    for (String t : tokens) {
      columnas.add(normalizarNombreColumna(t));
    }

    return columnas;
  }

  private String normalizarNombreColumna(String t) {
      t = t.toLowerCase();

      return switch (t) {
          case "cant", "cantidad", "qty" -> "cantidad";
          case "desc", "descripción", "descripcion", "concepto" -> "concepto";
          case "precio", "unitario", "p.unit", "pvp" -> "precioUnitario";
          case "total", "importe" -> "subtotal";
          default -> t;
      };
  }
  private List<String> pedirCabeceraAlUsuario() {
    TextInputDialog dialog = new TextInputDialog("cantidad, concepto, precioUnitario");
    dialog.setTitle("Definir cabecera de extractos");
    dialog.setHeaderText("No se ha podido deducir la cabecera automáticamente.");
    dialog.setContentText("Introduce los nombres de las columnas, separados por comas:");

    Optional<String> result = dialog.showAndWait();
    if (result.isEmpty()) return List.of();

    String[] cols = result.get().split(",");
    List<String> lista = new ArrayList<>();
    for (String c : cols) {
        String trimmed = c.trim();
        if (!trimmed.isEmpty()) {
            lista.add(trimmed);
        }
    }
    return lista;
}

}
