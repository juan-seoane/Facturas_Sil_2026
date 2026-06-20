package com.sil.facturas.presentationgui.fxcontrollers;

import static com.sil.facturas.domain.interfaces.IDebugService.*;

import com.google.gson.Gson;
import com.sil.facturas.app.core.AppContext;
import com.sil.facturas.domain.ocr.Bloque;
import com.sil.facturas.domain.ocr.enums._AnchorX;
import com.sil.facturas.domain.ocr.enums._AnchorY;
import com.sil.facturas.domain.ocr.enums._OffsetTipo;
import com.sil.facturas.domain.ocr.enums._TipoContenido;
import com.sil.facturas.domain.pojos.UIDataJson;
import com.sil.facturas.infrastructure.services.ocr.ModeloOCRService;
import com.sil.facturas.infrastructure.services.ocr.OCRService;
import com.sil.facturas.infrastructure.services.ocr.aux_ocr.BloqueOCR;
import com.sil.facturas.infrastructure.services.ocr.aux_ocr.CampoOCR;
import com.sil.facturas.infrastructure.services.ocr.aux_ocr.OCRItem;
import com.sil.facturas.infrastructure.services.ocr.aux_ocr._TipoBloque;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Window;

public class FxCntrlROIEditor {
     @FXML private ScrollPane scrollPane;
     @FXML
     private Pane canvasPane;

    // === VARIABLES PARA DIBUJAR ROIs ===
    private double startX;
    private double startY;
    private Rectangle rectTemp;

    private double scaleX = 1.0;
    private double scaleY = 1.0;

    private double realImageWidth;
    private double realImageHeight;

    private UIDataJson uiData;

    // === MODO ACTUAL (BLOQUE o CAMPO) ===
    private enum Modo {
        BLOQUE,
        CAMPO
    }

    private Modo modoActual = Modo.BLOQUE; // valor por defecto

    private ImageView imageView;
    private final List<OCRItem> items = new ArrayList<>();

    private ModeloOCRService modeloService;
    private OCRService ocrService;
    @FXML
    private ComboBox<String> comboTipoBloque;

    public void initialize() {
        modeloService = new ModeloOCRService();
        ocrService = new OCRService();
        canvasPane.setOnMousePressed(e -> {
            startX = e.getX();
            startY = e.getY();

            rectTemp = new Rectangle();
            rectTemp.setStroke(Color.YELLOW);
            rectTemp.setFill(Color.color(1, 1, 0, 0.2));
            canvasPane.getChildren().add(rectTemp);
        });

        canvasPane.setOnMouseDragged(e -> {
            double x = Math.min(startX, e.getX());
            double y = Math.min(startY, e.getY());
            double w = Math.abs(e.getX() - startX);
            double h = Math.abs(e.getY() - startY);

            rectTemp.setX(x);
            rectTemp.setY(y);
            rectTemp.setWidth(w);
            rectTemp.setHeight(h);
        });

    canvasPane.setOnMouseReleased(
        e -> {
          double x = rectTemp.getX();
          double y = rectTemp.getY();
          double w = rectTemp.getWidth();
          double h = rectTemp.getHeight();

          if (modoActual == Modo.BLOQUE) {
            BloqueOCR b =
                new BloqueOCR(
                    "bloque" + (items.size() + 1),
                    "ref",
                    x,
                    y,
                    w,
                    h,
                    _OffsetTipo.NONE,
                    0,
                    0,
                    false,
                    false,
                    _AnchorX.LEFT,
                    _AnchorY.TOP,
                    List.of());
            items.add(b);
          } else {
            CampoOCR c =
                new CampoOCR(
                    "campo" + (items.size() + 1),
                    "bloque",
                    "concepto",
                    x,
                    y,
                    w,
                    h,
                    _TipoContenido.TEXTO_COMPLETO);
            items.add(c);
          }

          try {
            Gson gson = new Gson();
            String usuario =
                (AppContext.usuarioActual.isEmpty() || AppContext.usuarioActual == null)
                    ? "ADMIN"
                    : AppContext.usuarioActual.toUpperCase();
            InputStream is = getClass().getResourceAsStream("/config/" + usuario + "/uidata.json");
            InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);

            uiData = gson.fromJson(reader, UIDataJson.class);

            cargarTiposDeBloqueEnComboBox();

          } catch (Exception ex) {
            printError(
                "[FxCntrlROIEditor>initialize] Excepción de tipo "
                    + ex.getClass()
                    + ": "
                    + ex.getMessage());
          }

          rectTemp = null;
        });
    }

  // ============================================================
  //  CARGAR IMAGEN
  // ============================================================

  @FXML
  private void onCargarImagen() {

    FileChooser fc = new FileChooser();
    fc.setTitle("Seleccionar imagen");
    fc.getExtensionFilters()
    .add(new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));

    Window window = canvasPane.getScene().getWindow();
    File file = fc.showOpenDialog(window);
    if (file == null) return;

    Image img = new Image(file.toURI().toString());

    imageView = new ImageView(img);
    imageView.setPreserveRatio(true);
    imageView.setSmooth(true);

    // El ImageView se ajusta al ScrollPane
    imageView.fitWidthProperty().bind(scrollPane.widthProperty());
    imageView.fitHeightProperty().bind(scrollPane.heightProperty());

    canvasPane.getChildren().clear();
    canvasPane.getChildren().add(imageView);

    // Guardamos dimensiones reales
    realImageWidth = img.getWidth();
    realImageHeight = img.getHeight();

    // Recalcular factor de escala cuando cambie el tamaño
    imageView.fitWidthProperty().addListener((obs, oldV, newV) -> updateScale());
    imageView.fitHeightProperty().addListener((obs, oldV, newV) -> updateScale());

    updateScale();
}

// ============================================================
//  #region NUEVO BLOQUE
// ============================================================

@FXML
private void onNuevoBloque() {

    String tipo = comboTipoBloque.getValue().toString();
    //printError("[FxCntrlROIEditor>onNuevoBloque] Selecciona un tipo de bloque antes de crear uno.");

    switch (tipo) {
        case "bloqueExtractos":
            crearBloque(_TipoBloque.EXTRACTOS);
            break;

        case "bloqueExtractos.cabecera":
            crearBloque(_TipoBloque.EXTRACTOS_CABECERA);
            break;

        case "bloqueExtractos.linea":
            crearBloque(_TipoBloque.EXTRACTOS_LINEA);
            break;

        case "bloqueTotales":
            crearBloque(_TipoBloque.TOTALES);
            break;

        case "bloqueCabecera":
            crearBloque(_TipoBloque.CABECERA);
            break;

        case "bloqueGenerico":
            crearBloque(_TipoBloque.GENERICO);
            break;
    }
}

  private void crearBloque(_TipoBloque tipo) {
    switch (tipo) {
      case CABECERA -> crearBloqueCabecera();
      case EXTRACTOS -> crearBloqueExtractos();
      case EXTRACTOS_CABECERA -> crearBloqueExtractosCabecera();
      case EXTRACTOS_LINEA -> crearBloqueExtractosLinea();
      case TOTALES -> crearBloqueTotales();
      case GENERICO -> crearBloqueGenerico();
    }
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
        if (!columnas.isEmpty())
            return columnas;
    }

    // 3. Último recurso: pedir al usuario que defina la cabecera
    return pedirCabeceraAlUsuario();
}

  private BloqueOCR crearBloqueGenerico() {

    TextInputDialog dialog = new TextInputDialog("bloqueGenerico");
    dialog.setTitle("Nuevo bloque genérico");
    dialog.setHeaderText("Introduce el nombre del bloque:");
    Optional<String> result = dialog.showAndWait();

    String nombre = result.orElse("bloqueGenerico");

    BloqueOCR b =
        new BloqueOCR(
            nombre,
            "generico",
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
            // TODO - 26-06-19 : BloqueOCR tiene un campo llamado cabeceraColumnas ¿?
            new ArrayList<String>());

    items.add(b);
    dibujarBloque(b);
    return b;
  }

  private BloqueOCR crearBloqueTotales() {

    BloqueOCR b =
        new BloqueOCR(
            "bloqueTotales",
            "totales",
            50,
            150,
            250,
            120,
            _OffsetTipo.NONE,
            0,
            0,
            false,
            false,
            _AnchorX.LEFT,
            _AnchorY.TOP,
            // TODO - 26-06-19 : BloqueOCR tiene un campo llamado cabeceraColumnas ¿?
            new ArrayList<String>());

    items.add(b);
    dibujarBloque(b);
    return b;
  }

  private BloqueOCR crearBloqueExtractosCabecera() {

    BloqueOCR b =
        new BloqueOCR(
            "cabeceraExtractos",
            "extracto",
            50,
            200,
            400,
            40,
            _OffsetTipo.NONE,
            0,
            0,
            false,
            false,
            _AnchorX.LEFT,
            _AnchorY.TOP,
            // TODO - 26-06-19 : BloqueOCR tiene un campo llamado cabeceraColumnas ¿?
            new ArrayList<String>());

    items.add(b);
    dibujarBloque(b);
    return b;
  }

  private BloqueOCR crearBloqueExtractosLinea() {

    BloqueOCR b =
        new BloqueOCR(
            "lineaExtracto",
            "extracto",
            50,
            300,
            400,
            30,
            _OffsetTipo.NONE,
            0,
            0,
            false,
            false,
            _AnchorX.LEFT,
            _AnchorY.TOP,
            // TODO - 26-06-19 : BloqueOCR tiene un campo llamado cabeceraColumnas ¿?
            new ArrayList<String>());

    items.add(b);
    dibujarBloque(b);
    return b;
  }

  private BloqueOCR crearBloqueExtractos() {

    BloqueOCR b =
        new BloqueOCR(
            "bloqueExtractos",
            "extracto",
            50,
            250,
            400,
            200,
            _OffsetTipo.NONE,
            0,
            0,
            false,
            false,
            _AnchorX.LEFT,
            _AnchorY.TOP,
            // TODO - 26-06-19 : BloqueOCR tiene un campo llamado cabeceraColumnas ¿?
            new ArrayList<String>());

    items.add(b);
    dibujarBloque(b);
    return b;
  }

  private BloqueOCR crearBloqueCabecera() {

    BloqueOCR b =
        new BloqueOCR(
            "bloqueCabecera",
            "factura",
            50,
            50,
            400,
            100,
            _OffsetTipo.NONE,
            0,
            0,
            false,
            false,
            _AnchorX.LEFT,
            _AnchorY.TOP,
            // TODO - 26-06-19 : BloqueOCR tiene un campo llamado cabeceraColumnas ¿?
            new ArrayList<String>());
    items.add(b);
    dibujarBloque(b);
    return b;
  }

// #endregion

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
    if (result.isEmpty())
        return List.of();

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

private void updateScale() {
    if (imageView == null)
        return;

    double viewW = imageView.getBoundsInParent().getWidth();
    double viewH = imageView.getBoundsInParent().getHeight();

    scaleX = realImageWidth / viewW;
    scaleY = realImageHeight / viewH;

    //print(_Colores.YELLOW,"Escala actual: scaleX=" + scaleX + " scaleY=" + scaleY);
}

  private void cargarTiposDeBloqueEnComboBox() {

    List<String> bloques =
        uiData.getOcr().stream()
            .filter(item -> "bloque".equals(item.getType()))
            .map(com.sil.facturas.domain.ocr.OCRItem::getId)
            .toList();
    // TODO: 26-06-19 : Deshacer la duplicidad de la clase OCRItem(infrastructure y domain)

    comboTipoBloque.getItems().setAll(bloques);
  }


}
