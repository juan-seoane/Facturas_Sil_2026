package com.sil.facturas.presentationgui.fxcontrollers;

import static com.sil.facturas.domain.interfaces.IDebugService.*;

import com.google.gson.Gson;
import com.sil.facturas.app.core.AppContext;
import com.sil.facturas.domain.enums._Colores;
import com.sil.facturas.domain.ocr.Bloque;
import com.sil.facturas.domain.ocr.BloqueConfig;
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
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
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
    public double x, y, w, h;

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
          this.x = rectTemp.getX();
          this.y = rectTemp.getY();
          this.w = rectTemp.getWidth();
          this.h = rectTemp.getHeight();
          printWarning("[FxCntrlROIEditor>initialize>onMouseReleased] Coordenadas del dibujo fijadas en [" + this.x+ ", "+this.y+"] ,w="+this.w+", h="+this.h+"!!!");

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

        Gson gson = new Gson();
        String usuario = (AppContext.usuarioActual == null || AppContext.usuarioActual.isEmpty())
                ? "ADMIN"
                : AppContext.usuarioActual;

        String rutaFS = "data/config/" + usuario.toUpperCase() + "/uidata.json";

        try (FileInputStream fis = new FileInputStream(rutaFS);
            InputStreamReader reader = new InputStreamReader(fis, StandardCharsets.UTF_8)) {

            uiData = gson.fromJson(reader, UIDataJson.class);

        } catch (Exception ee) {
            printError(
                        "[FxCntrlROIEditor>initialize] Excepción de tipo "
                            + ee.getClass()
                            + "al cargar el archivo uidata.json: "
                            + ee.getMessage());
        }
        try {
            
            cargarTiposDeBloqueEnComboBox();

          } catch (Exception ex) {
            printError(
                "[FxCntrlROIEditor>initialize] Excepción de tipo "
                    + ex.getClass()
                    + "en cargarTiposDeBloqueEnComboBox(): "
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

        if (tipo == null || tipo.isEmpty()) {
            printError("Selecciona un tipo de bloque antes de crear uno.");
            return; // ← SIN ESTO SE CREA EL RECTÁNGULO FANTASMA
        }

        switch (tipo) {
            case "bloqueExtractos":
                printWarning("[FxCntrlROIEDitor>onNuevoBloque] Creando bloque de extractos");
                crearBloque(_TipoBloque.EXTRACTOS);
                break;

            case "bloqueExtractos.cabecera":
                printWarning("[FxCntrlROIEDitor>onNuevoBloque] Creando bloque de cabecera de extractos");
                crearBloque(_TipoBloque.EXTRACTOS_CABECERA);
                break;

            case "bloqueExtractos.linea":
                printWarning("[FxCntrlROIEDitor>onNuevoBloque] Creando bloque de línea de extractos");
                crearBloque(_TipoBloque.EXTRACTOS_LINEA);
                break;

            case "bloqueTotales":
                printWarning("[FxCntrlROIEDitor>onNuevoBloque] Creando bloque de totales");
                crearBloque(_TipoBloque.TOTALES);
                break;

            case "bloqueCabecera":
                printWarning("[FxCntrlROIEDitor>onNuevoBloque] Creando bloque de cabecera de factura");
                crearBloque(_TipoBloque.CABECERA);
                break;

            case "bloqueGenerico":
                printWarning("[FxCntrlROIEDitor>onNuevoBloque] Creando bloque genérico");
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

    // 1. Validar selección del usuario
    if (this.x == 0.0 || this.y == 0.0 || this.w == 0.0 || this.h == 0.0) {
        printError("[FxCntrlROIEditor>crearBloqueGenerico] Debes seleccionar un área en la imagen antes de crear un bloque.");
        return null;
    }

    // 2. Pedir nombre del bloque
    TextInputDialog dialog = new TextInputDialog("bloqueGenerico");
    dialog.setTitle("Nuevo bloque genérico");
    dialog.setHeaderText("Introduce el nombre del bloque:");
    Optional<String> result = dialog.showAndWait();

    String nombre = result.orElse("bloqueGenerico");

    // 3. Pedir configuración avanzada (offset, anchors, grow…)
    BloqueConfig cfg = pedirConfigBloqueAlUsuario();
    if (cfg == null) {
        printWarning("[FxCntrlROIEditor>crearBloqueGenerico] Creación cancelada por el usuario.");
        return null;
    }

    // 4. Crear el bloque con los valores reales
    BloqueOCR b =
        new BloqueOCR(
            nombre,
            "generico",
            this.x,
            this.y,
            this.w,
            this.h,
            cfg.offsetTipo,
            0,
            0,
            cfg.growHoriz,
            cfg.growVert,
            cfg.anchorX,
            cfg.anchorY,
            new ArrayList<>());

    // 5. Registrar y dibujar
    items.add(b);
    printWarning("[FxCntrlROIEditor>crearBloqueGenerico] BloqueOCR: " + b.toString());
    dibujarBloque(b);

    printWarning("Bloque creado: " + b.toString());

    return b;
}

private BloqueOCR crearBloqueTotales() {

    // 1. Validar que el usuario ha seleccionado un área
    if (this.x == 0.0 || this.y == 0.0 || this.w == 0.0 || this.h == 0.0) {
        printError("[FxCntrlROIEditor>crearBloqueTotales] Debes seleccionar un área en la imagen antes de crear un bloque.");
        return null;
    }

    // 2. Pedir configuración al usuario
    BloqueConfig cfg = pedirConfigBloqueAlUsuario();
    if (cfg == null) {
        printWarning("[FxCntrlROIEditor>crearBloqueTotales] Creación de bloque cancelada por el usuario.");
        return null;
    }

    // 3. Crear el bloque con los valores reales
    BloqueOCR b =
        new BloqueOCR(
            "bloqueTotales",
            "totales",
            this.x,
            this.y,
            this.w,
            this.h,
            cfg.offsetTipo,
            0,
            0,
            cfg.growHoriz,
            cfg.growVert,
            cfg.anchorX,
            cfg.anchorY,
            new ArrayList<>());

    // 4. Registrar y dibujar
    items.add(b);
    printWarning("[FxCntrlROIEditor>crearBloqueTotales] BloqueOCR: " + b.toString());
    dibujarBloque(b);

    printWarning("Bloque creado: " + b.toString());

    return b;
}


    private BloqueOCR crearBloqueExtractosCabecera() {

    // 1. Validar selección del usuario
    if (this.x == 0.0 || this.y == 0.0 || this.w == 0.0 || this.h == 0.0) {
        printError("[FxCntrlROIEditor>crearBloqueExtractosCabecera] Debes seleccionar un área en la imagen antes de crear un bloque.");
        return null;
    }

    // 2. Pedir configuración avanzada al usuario
    BloqueConfig cfg = pedirConfigBloqueAlUsuario();
    if (cfg == null) {
        printWarning("[FxCntrlROIEditor>crearBloqueExtractosCabecera] Creación cancelada por el usuario.");
        return null;
    }

    // 3. Crear el bloque con los valores reales
    BloqueOCR b =
        new BloqueOCR(
            "cabeceraExtractos",
            "extracto",
            this.x,
            this.y,
            this.w,
            this.h,
            cfg.offsetTipo,
            0,
            0,
            cfg.growHoriz,
            cfg.growVert,
            cfg.anchorX,
            cfg.anchorY,
            new ArrayList<>());

    // 4. Registrar y dibujar
    items.add(b);
    printWarning("[FxCntrlROIEditor>crearBloqueExtractosCabecera] BloqueOCR: " + b.toString());
    dibujarBloque(b);

    printWarning("Bloque creado: " + b.toString());

    return b;
}


private BloqueOCR crearBloqueExtractosLinea() {

    // 1. Validar selección del usuario
    if (this.x == 0.0 || this.y == 0.0 || this.w == 0.0 || this.h == 0.0) {
        printError("[FxCntrlROIEditor>crearBloqueExtractosLinea] Debes seleccionar un área en la imagen antes de crear un bloque.");
        return null;
    }

    // 2. Pedir configuración avanzada al usuario
    BloqueConfig cfg = pedirConfigBloqueAlUsuario();
    if (cfg == null) {
        printWarning("[FxCntrlROIEditor>crearBloqueExtractosLinea] Creación cancelada por el usuario.");
        return null;
    }

    // 3. Crear el bloque con los valores reales
    BloqueOCR b =
        new BloqueOCR(
            "lineaExtracto",
            "extracto",
            this.x,
            this.y,
            this.w,
            this.h,
            cfg.offsetTipo,
            0,
            0,
            cfg.growHoriz,
            cfg.growVert,
            cfg.anchorX,
            cfg.anchorY,
            new ArrayList<>());

    // 4. Registrar y dibujar
    items.add(b);
    printWarning("[FxCntrlROIEditor>crearBloqueExtractosLinea] BloqueOCR: " + b.toString());
    dibujarBloque(b);

    printWarning("Bloque creado: " + b.toString());

    return b;
}


    private BloqueOCR crearBloqueExtractos() {

        if (this.x==0.0||this.y==0.0||this.w==0.0||this.h==0.0) {
            printError("[FxCntrlROIEditor>crearBloqueExtractos] Debes seleccionar un área en la imagen antes de crear un bloque.");
            return null;
        }

        BloqueConfig cfg = pedirConfigBloqueAlUsuario();
        if (cfg == null) {
            printWarning("[FxCntrlROIEditor>crearBloqueExtractos] Creación de bloque cancelada por el usuario.");
            return null;
        }

        BloqueOCR b =
            new BloqueOCR(
                "bloqueExtractos",
                "extracto",
                this.x,
                this.y,
                this.w,
                this.h,
                cfg.offsetTipo,
                0,
                0,
                cfg.growHoriz,
                cfg.growVert,
                cfg.anchorX,
                cfg.anchorY,
                new ArrayList<>());

        items.add(b);
        printWarning("[FxCntrlROIEditot>crearBloqueExtractos] BloqueOCR: " + b.toString());
        dibujarBloque(b);

        printWarning("Bloque creado: " + b.toString());

        return b;
    }

private BloqueOCR crearBloqueCabecera() {

    // 1. Validar selección del usuario
    if (this.x == 0.0 || this.y == 0.0 || this.w == 0.0 || this.h == 0.0) {
        printError("[FxCntrlROIEditor>crearBloqueCabecera] Debes seleccionar un área en la imagen antes de crear un bloque.");
        return null;
    }

    // 2. Pedir configuración avanzada al usuario
    BloqueConfig cfg = pedirConfigBloqueAlUsuario();
    if (cfg == null) {
        printWarning("[FxCntrlROIEditor>crearBloqueCabecera] Creación cancelada por el usuario.");
        return null;
    }

    // 3. Crear el bloque con los valores reales
    BloqueOCR b =
        new BloqueOCR(
            "bloqueCabecera",
            "factura",
            this.x,
            this.y,
            this.w,
            this.h,
            cfg.offsetTipo,
            0,
            0,
            cfg.growHoriz,
            cfg.growVert,
            cfg.anchorX,
            cfg.anchorY,
            new ArrayList<>());

    // 4. Registrar y dibujar
    items.add(b);
    printWarning("[FxCntrlROIEditor>crearBloqueCabecera] BloqueOCR: " + b.toString());
    dibujarBloque(b);

    printWarning("Bloque creado: " + b.toString());

    return b;
}


    // #endregion
    // ============================================================

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

    // 1. Preguntar tipo de guardado
    TipoGuardado tipo = pedirTipoGuardado();
    if (tipo == null) {
        printWarning("[onGuardarModelo] Guardado cancelado por el usuario.");
        return;
    }

    // 2. Crear modelo nuevo
    modeloService.nuevoModelo("Factura", "1.0");

    // 3. Añadir bloques al modelo
    for (OCRItem item : items) {
        if (item instanceof BloqueOCR b) {
            modeloService.addBloque(modeloService.convertirBloqueOCR(b));
        }
    }

    // 4. Pedir nombre del archivo
    String sugerencia = (tipo == TipoGuardado.BLOQUES)
            ? "bloques_"
            : "modeloOCR_";

    String nombre = pedirNombreArchivo(sugerencia);
    if (nombre == null || nombre.isBlank()) {
        printWarning("[onGuardarModelo] Nombre de archivo vacío. Cancelado.");
        return;
    }

    // 5. Construir ruta final (tu nueva ruta)
    String ruta = "da7a/datos/modelosOCR/" + nombre + ".json";

    // 6. Guardar modelo
    modeloService.guardarModelo(new File(ruta));

    printWarning("[onGuardarModelo] Guardado correcto en: " + ruta);
}


private enum TipoGuardado { MODELO, BLOQUES }

private TipoGuardado pedirTipoGuardado() {
    ChoiceDialog<String> dialog = new ChoiceDialog<>(
            "Modelo completo",
            "Modelo completo",
            "Solo bloques");
    dialog.setTitle("Guardar modelo OCR");
    dialog.setHeaderText("Selecciona el tipo de guardado:");

    Optional<String> result = dialog.showAndWait();
    if (result.isEmpty())
        return null;

    return result.get().equals("Solo bloques")
            ? TipoGuardado.BLOQUES
            : TipoGuardado.MODELO;
}

private String pedirNombreArchivo(String sugerencia) {
    TextInputDialog dialog = new TextInputDialog(sugerencia);
    dialog.setTitle("Nombre del archivo");
    dialog.setHeaderText("Introduce el nombre del archivo (sin extensión):");

    Optional<String> result = dialog.showAndWait();
    return result.orElse(null);
}


    // ============================================================
    //  #region DIBUJAR elementos
    // ============================================================

    private void dibujarBloque(BloqueOCR b) {
        var rect = new javafx.scene.shape.Rectangle(b.x(), b.y(), b.w(), b.h());
        rect.setStroke(javafx.scene.paint.Color.YELLOW);
        rect.setFill(javafx.scene.paint.Color.TRANSPARENT);
        print("[FxCntrlROIEditor>dibujarBloque] Dibujando Rect [" + b.x() + ", " + b.y() + ", " + b.w() + ", " + b.h()
                + "]");
        canvasPane.getChildren().add(rect);
    }

    private void dibujarCampo(CampoOCR c) {
        var rect = new javafx.scene.shape.Rectangle(c.x(), c.y(), c.w(), c.h());
        rect.setStroke(javafx.scene.paint.Color.CYAN);
        rect.setFill(javafx.scene.paint.Color.TRANSPARENT);
        canvasPane.getChildren().add(rect);
    }

    //  #endregion 
    // ============================================================

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

  private BloqueConfig pedirConfigBloqueAlUsuario() {

    Dialog<BloqueConfig> dialog = new Dialog<>();
    dialog.setTitle("Configuración del bloque");
    dialog.setHeaderText("Define el comportamiento del bloque");

    ButtonType okButton = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);

    // --- Controles ---
    ComboBox<_OffsetTipo> cbOffset = new ComboBox<>();
    cbOffset.getItems().setAll(_OffsetTipo.values());
    cbOffset.setValue(_OffsetTipo.NONE);

    CheckBox chkGrowH = new CheckBox("Grow Horizontal");
    CheckBox chkGrowV = new CheckBox("Grow Vertical");

    ComboBox<_AnchorX> cbAnchorX = new ComboBox<>();
    cbAnchorX.getItems().setAll(_AnchorX.values());
    cbAnchorX.setValue(_AnchorX.LEFT);

    ComboBox<_AnchorY> cbAnchorY = new ComboBox<>();
    cbAnchorY.getItems().setAll(_AnchorY.values());
    cbAnchorY.setValue(_AnchorY.TOP);

    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);

    grid.add(new Label("Offset Tipo:"), 0, 0);
    grid.add(cbOffset, 1, 0);

    grid.add(chkGrowH, 0, 1);
    grid.add(chkGrowV, 1, 1);

    grid.add(new Label("Anchor X:"), 0, 2);
    grid.add(cbAnchorX, 1, 2);

    grid.add(new Label("Anchor Y:"), 0, 3);
    grid.add(cbAnchorY, 1, 3);

    dialog.getDialogPane().setContent(grid);

    dialog.setResultConverter(dialogButton -> {
        if (dialogButton == okButton) {
            BloqueConfig cfg = new BloqueConfig();
            cfg.offsetTipo = cbOffset.getValue();
            cfg.growHoriz = chkGrowH.isSelected();
            cfg.growVert = chkGrowV.isSelected();
            cfg.anchorX = cbAnchorX.getValue();
            cfg.anchorY = cbAnchorY.getValue();
            return cfg;
        }
        return null;
    });

    Optional<BloqueConfig> result = dialog.showAndWait();
    return result.orElse(null);
}


}
