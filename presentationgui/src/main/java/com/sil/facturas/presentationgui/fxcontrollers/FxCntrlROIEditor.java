package com.sil.facturas.presentationgui.fxcontrollers;

import static com.sil.facturas.domain.interfaces.IDebugService.*;

import com.sil.facturas.domain.ocr.Bloque;
import com.sil.facturas.domain.ocr.BloqueConfig;
import com.sil.facturas.domain.ocr.Campo;
import com.sil.facturas.domain.ocr.InfoImagen;
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
import com.sil.facturas.presentationgui.services.ocr.ModeloOCRRenderer;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Window;

public class FxCntrlROIEditor {

  @FXML private Pane rootPane;
  @FXML private Group group;
  @FXML private ImageView imageView;
  @FXML private Pane overlayPane;
  @FXML private ComboBox<String> comboTipoBloque;

  private double startX;
  private double startY;
  private BloqueOCR bloqueTemp = null;
  private Rectangle rectTemp = null;
  public double x, y, w, h;

  private double scaleX = 1.0;
  private double scaleY = 1.0;
  private double realImageWidth;
  private double realImageHeight;

  private UIDataJson uiData;

  private enum Modo {
    BLOQUE,
    CAMPO
  }

  private Modo modoActual = Modo.BLOQUE;

  private final List<OCRItem> items = new ArrayList<>();

  private ModeloOCRService modeloService;
  private OCRService ocrService;
  private BloqueConfig cfg = new BloqueConfig();

  private List<Bloque> bloques = new ArrayList<>();
  private Map<String, Bloque> mapaBloques = new HashMap<>();

  private ModeloOCRRenderer renderer;
  private InfoImagen infoImagen;

  @FXML
  public void initialize() {
    printWarning("[FxCntrlROIEditor.initialize] Dentro del initialize()");

    modeloService = new ModeloOCRService();
    renderer = new ModeloOCRRenderer();

    if (imageView != null) {
      imageView.setPreserveRatio(true);
      imageView.setSmooth(true);
    }

    if (overlayPane != null) {
      overlayPane.setPickOnBounds(false);
    }

    if (rootPane != null && group != null && imageView != null && overlayPane != null) {
      Platform.runLater(() -> renderer.attachTo(group, imageView, overlayPane, rootPane));
    }

    if (overlayPane != null) {
      overlayPane.setOnMousePressed(e -> onMousePressedROI(e.getX(), e.getY()));
      overlayPane.setOnMouseDragged(e -> onMouseDraggedROI(e.getX(), e.getY()));
      overlayPane.setOnMouseReleased(e -> onMouseReleasedROI());
    }
  }

  @FXML
  private void onCargarImagen() {
    FileChooser fc = new FileChooser();
    fc.setTitle("Seleccionar imagen");
    fc.getExtensionFilters()
        .add(new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));

    Window window = rootPane.getScene().getWindow();
    File file = fc.showOpenDialog(window);
    if (file == null) return;

    Image image = new Image(file.toURI().toString());
    renderer.setImage(image);

    realImageWidth = image.getWidth();
    realImageHeight = image.getHeight();
    scaleX = 1.0;
    scaleY = 1.0;
    infoImagen = null;
  }

  @FXML
  private void mostrarDialogoCarga() {
    ButtonType btnBloques = new ButtonType("Modelo de bloques");
    ButtonType btnOCR = new ButtonType("Modelo OCR completo");
    ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

    Alert alert = new Alert(AlertType.CONFIRMATION);
    alert.setTitle("Cargar modelo");
    alert.setHeaderText("Selecciona el tipo de modelo que quieres cargar");
    alert.getButtonTypes().setAll(btnBloques, btnOCR, btnCancelar);

    Optional<ButtonType> result = alert.showAndWait();
    if (result.isEmpty() || result.get() == btnCancelar) return;

    if (result.get() == btnBloques) {
      cargarModeloBloquesInicial();
    } else if (result.get() == btnOCR) {
      printWarning(
          "[FxCntrlROIEditor>mostrarDialogoCarga] Carga OCR completa no implementada aún.");
    }
  }

  @FXML
  private void onNuevoBloque() {
    String tipo = comboTipoBloque.getValue();
    if (tipo == null || tipo.isEmpty()) {
      printError("Selecciona un tipo de bloque antes de crear uno.");
      return;
    }

    switch (tipo) {
      case "bloqueExtractos" -> crearBloque(_TipoBloque.EXTRACTOS);
      case "bloqueExtractos.cabecera" -> crearBloque(_TipoBloque.EXTRACTOS_CABECERA);
      case "bloqueExtractos.linea" -> crearBloque(_TipoBloque.EXTRACTOS_LINEA);
      case "bloqueTotales" -> crearBloque(_TipoBloque.TOTALES);
      case "bloqueCabecera" -> crearBloque(_TipoBloque.CABECERA);
      case "bloqueGenerico" -> crearBloque(_TipoBloque.GENERICO);
    }
  }

  @FXML
  private void onNuevoCampo() {
    BloqueOCR bloque =
        (BloqueOCR) items.stream().filter(i -> i instanceof BloqueOCR).findFirst().orElse(null);

    if (bloque == null) return;

    CampoOCR c =
        new CampoOCR(
            "campo" + (bloque.campos().size() + 1),
            bloque.nombre(),
            "concepto",
            bloque.x() + 10,
            bloque.y() + 10,
            100,
            30,
            _TipoContenido.TEXTO_COMPLETO);

    bloque.addCampo(c);
    items.add(c);
    dibujarCampo(c);
  }

  @FXML
  private void onGuardarModelo() {
    TipoGuardado tipo = pedirTipoGuardado();
    if (tipo == null) {
      printWarning("[onGuardarModelo] Guardado cancelado por el usuario.");
      return;
    }

    modeloService.nuevoModelo("Factura", "1.0");

    for (OCRItem item : items) {
      if (item instanceof BloqueOCR b) {
        modeloService.addBloque(modeloService.convertirBloqueOCR(b));
      }
    }

    String sugerencia = (tipo == TipoGuardado.BLOQUES) ? "bloques_" : "modeloOCR_";
    String nombre = pedirNombreArchivo(sugerencia);
    if (nombre == null || nombre.isBlank()) {
      printWarning("[onGuardarModelo] Nombre de archivo vacío. Cancelado.");
      return;
    }

    String ruta = "data/datos/modelosOCR/" + nombre + ".json";
    modeloService.guardarModelo(new File(ruta));
    printWarning("[onGuardarModelo] Guardado correcto en: " + ruta);
  }

  @FXML
  private void onCargarBloques() {
    FileChooser fc = new FileChooser();
    fc.setTitle("Seleccionar archivo de bloques");
    fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Bloques (*.json)", "*.json"));

    File file = fc.showOpenDialog(rootPane.getScene().getWindow());
    if (file == null) return;

    bloques = modeloService.cargarBloques(file);
    mapaBloques.clear();
    for (Bloque b : bloques) mapaBloques.put(b.nombre(), b);

    if (infoImagen != null) {
      renderBloquesEnOverlay();
    } else {
      printWarning(
          "[FxCntrlROIEditor>onCargarBloques] No hay imagen cargada todavía; no se puede dibujar.");
    }
  }

  private void cargarModeloBloquesInicial() {
    FileChooser fc = new FileChooser();
    fc.setTitle("Seleccionar archivo de bloques");
    fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Bloques (*.json)", "*.json"));

    File file = fc.showOpenDialog(rootPane.getScene().getWindow());
    if (file == null) return;

    bloques = modeloService.cargarBloques(file);
    mapaBloques.clear();
    for (Bloque b : bloques) mapaBloques.put(b.nombre(), b);

    if (infoImagen != null) {
      renderBloquesEnOverlay();
    } else {
      printWarning(
          "[FxCntrlROIEditor>cargarModeloBloquesInicial] No hay imagen cargada; carga primero la"
              + " imagen.");
    }
  }

  private void renderBloquesEnOverlay() {
    overlayPane.getChildren().clear();
    for (Bloque b : bloques) {
      dibujarBloqueModelo(b);
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

  private BloqueOCR crearBloqueCabecera() {
    return crearBloqueBase("bloqueCabecera", "factura");
  }

  private BloqueOCR crearBloqueExtractos() {
    return crearBloqueBase("bloqueExtractos", "extracto");
  }

  private BloqueOCR crearBloqueExtractosCabecera() {
    return crearBloqueBase("cabeceraExtractos", "extracto");
  }

  private BloqueOCR crearBloqueExtractosLinea() {
    return crearBloqueBase("lineaExtracto", "extracto");
  }

  private BloqueOCR crearBloqueTotales() {
    return crearBloqueBase("bloqueTotales", "totales");
  }

  private BloqueOCR crearBloqueGenerico() {
    return crearBloqueBase(
        cfg.nombreBloque == null ? "bloqueGenerico" : cfg.nombreBloque, "generico");
  }

  private BloqueOCR crearBloqueBase(String nombre, String referencia) {
    if (this.x == 0.0 || this.y == 0.0 || this.w == 0.0 || this.h == 0.0) {
      printError("[FxCntrlROIEditor] Selecciona un área primero.");
      return null;
    }

    BloqueConfig cfgLocal = pedirConfigBloqueAlUsuario();
    if (cfgLocal == null) return null;

    String ref = referencia;
    if (cfgLocal.referencia != null && !cfgLocal.referencia.isBlank()) {
      ref = cfgLocal.referencia;
    }

    BloqueOCR b =
        new BloqueOCR(
            nombre,
            cfgLocal.parentNombre,
            ref,
            this.x,
            this.y,
            this.h,
            this.w,
            cfgLocal.offsetTipo,
            0,
            0,
            cfgLocal.growVert,
            cfgLocal.growHoriz,
            cfgLocal.anchorX,
            cfgLocal.anchorY,
            new ArrayList<>());

    items.add(b);
    dibujarBloque(b);
    return b;
  }

  private void dibujarBloque(BloqueOCR b) {
    Rectangle rect = new Rectangle(b.x(), b.y(), b.w(), b.h());
    rect.setStroke(Color.YELLOW);
    rect.setFill(Color.TRANSPARENT);
    rect.setOnMouseClicked(e -> print("Bloque seleccionado: " + b.nombre()));
    overlayPane.getChildren().add(rect);
  }

  private void dibujarCampo(CampoOCR c) {
    Rectangle rect = new Rectangle(c.x(), c.y(), c.w(), c.h());
    rect.setStroke(Color.CYAN);
    rect.setFill(Color.TRANSPARENT);
    overlayPane.getChildren().add(rect);
  }

  private void dibujarBloqueModelo(Bloque b) {
    double x = b.zona().x() * scaleX;
    double y = b.zona().y() * scaleY;
    double w = b.zona().w() * scaleX;
    double h = b.zona().h() * scaleY;

    Rectangle r = new Rectangle(x, y, w, h);
    r.setStroke(Color.RED);
    r.setStrokeWidth(2);
    r.setFill(Color.TRANSPARENT);
    overlayPane.getChildren().add(r);

    Text label = new Text(b.nombre());
    label.setFill(Color.RED);
    label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
    label.setX(x);
    label.setY(y - 6);
    overlayPane.getChildren().add(label);

    for (Campo c : b.campos()) {
      dibujarCampoModelo(b, c);
    }
  }

  private void dibujarCampoModelo(Bloque b, Campo c) {
    double bx = b.zona().x() * scaleX;
    double by = b.zona().y() * scaleY;
    double cx = c.offsetX() * scaleX;
    double cy = c.offsetY() * scaleY;
    double cw = c.w() * scaleX;
    double ch = c.h() * scaleY;

    Rectangle r = new Rectangle(bx + cx, by + cy, cw, ch);
    r.setStroke(Color.YELLOW);
    r.setStrokeWidth(2);
    r.setFill(Color.TRANSPARENT);
    overlayPane.getChildren().add(r);

    Text label = new Text(c.nombre());
    label.setFill(Color.YELLOW);
    label.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
    label.setX(bx + cx);
    label.setY(by + cy - 4);
    overlayPane.getChildren().add(label);
  }

  private void onMousePressedROI(double mx, double my) {
    if (rectTemp != null) {
      overlayPane.getChildren().remove(rectTemp);
      rectTemp = null;
    }
    startX = mx;
    startY = my;
    rectTemp = new Rectangle(startX, startY, 1, 1);
    rectTemp.setStroke(Color.YELLOW);
    rectTemp.setStrokeWidth(2);
    rectTemp.setFill(Color.color(1, 1, 0, 0.25));
    overlayPane.getChildren().add(rectTemp);
  }

  private void onMouseDraggedROI(double mx, double my) {
    if (rectTemp == null) return;
    double x = Math.min(startX, mx);
    double y = Math.min(startY, my);
    double w = Math.abs(mx - startX);
    double h = Math.abs(my - startY);
    rectTemp.setX(x);
    rectTemp.setY(y);
    rectTemp.setWidth(w);
    rectTemp.setHeight(h);
  }

  private void onMouseReleasedROI() {
    if (rectTemp == null) return;

    this.x = rectTemp.getX();
    this.y = rectTemp.getY();
    this.w = rectTemp.getWidth();
    this.h = rectTemp.getHeight();

    printWarning(
        "[ROIEditor] Bloque temporal definido en [" + x + ", " + y + "] w=" + w + ", h=" + h);

    bloqueTemp =
        new BloqueOCR(
            cfg.nombreBloque,
            "",
            cfg.referencia,
            x,
            y,
            w,
            h,
            cfg.offsetTipo,
            0,
            0,
            cfg.growVert,
            cfg.growHoriz,
            cfg.anchorX,
            cfg.anchorY,
            new ArrayList<>());

    bloqueTemp.setParentNombre("Imagen (root)".equals(cfg.parentNombre) ? null : cfg.parentNombre);
  }

  private BufferedImage toBufferedImage(Image fxImage) {
    return javafx.embed.swing.SwingFXUtils.fromFXImage(fxImage, null);
  }

  private void updateScale() {
    if (imageView == null || realImageWidth == 0 || realImageHeight == 0) return;
    double viewW = imageView.getBoundsInParent().getWidth();
    double viewH = imageView.getBoundsInParent().getHeight();
    scaleX = viewW / realImageWidth;
    scaleY = viewH / realImageHeight;
  }

  private List<String> parsearCabeceraDesdeTexto(String texto) {
    if (texto == null || texto.isBlank()) return List.of();
    String[] tokens = texto.trim().split("\\s+");
    List<String> columnas = new ArrayList<>();
    for (String t : tokens) columnas.add(normalizarNombreColumna(t));
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
      if (!trimmed.isEmpty()) lista.add(trimmed);
    }
    return lista;
  }

  private void cargarTiposDeBloqueEnComboBox() {
    List<String> bloquesIds =
        uiData.getOcr().stream()
            .filter(item -> "bloque".equals(item.getType()))
            .map(com.sil.facturas.domain.ocr.OCRItem::getId)
            .toList();
    comboTipoBloque.getItems().setAll(bloquesIds);
  }

  private BloqueConfig pedirConfigBloqueAlUsuario() {
    Dialog<BloqueConfig> dialog = new Dialog<>();
    dialog.setTitle("Configuración del bloque");
    dialog.setHeaderText("Define el comportamiento del bloque");

    ButtonType okButton = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);

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

    ComboBox<ParentOption> cbParent = new ComboBox<>();
    cbParent.getItems().add(new ParentOption(null, "Imagen (root)"));
    for (OCRItem item : items) {
      if (item instanceof BloqueOCR b) {
        cbParent.getItems().add(new ParentOption(b.referencia(), b.nombre()));
      }
    }
    cbParent.setValue(cbParent.getItems().get(0));

    TextField txtNombre = new TextField();
    txtNombre.setPromptText("Nombre del bloque");

    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.add(new Label("Nombre:"), 0, 0);
    grid.add(txtNombre, 1, 0);
    grid.add(new Label("Offset Tipo:"), 0, 1);
    grid.add(cbOffset, 1, 1);
    grid.add(chkGrowH, 0, 2);
    grid.add(chkGrowV, 1, 2);
    grid.add(new Label("Anchor X:"), 0, 3);
    grid.add(cbAnchorX, 1, 3);
    grid.add(new Label("Anchor Y:"), 0, 4);
    grid.add(cbAnchorY, 1, 4);
    grid.add(new Label("Parent:"), 0, 5);
    grid.add(cbParent, 1, 5);
    dialog.getDialogPane().setContent(grid);

    dialog.setResultConverter(
        dialogButton -> {
          if (dialogButton == okButton) {
            BloqueConfig cfg = new BloqueConfig();
            cfg.offsetTipo = cbOffset.getValue();
            cfg.growHoriz = chkGrowH.isSelected();
            cfg.growVert = chkGrowV.isSelected();
            cfg.anchorX = cbAnchorX.getValue();
            cfg.anchorY = cbAnchorY.getValue();
            cfg.nombreBloque = txtNombre.getText();
            cfg.referencia = comboTipoBloque.getValue();
            ParentOption opt = cbParent.getValue();
            cfg.parentNombre = opt.referencia;
            return cfg;
          }
          return null;
        });

    return dialog.showAndWait().orElse(null);
  }

  private enum TipoGuardado {
    MODELO,
    BLOQUES
  }

  private TipoGuardado pedirTipoGuardado() {
    ChoiceDialog<String> dialog =
        new ChoiceDialog<>("Modelo completo", "Modelo completo", "Solo bloques");
    dialog.setTitle("Guardar modelo OCR");
    dialog.setHeaderText("Selecciona el tipo de guardado:");

    Optional<String> result = dialog.showAndWait();
    if (result.isEmpty()) return null;

    return result.get().equals("Solo bloques") ? TipoGuardado.BLOQUES : TipoGuardado.MODELO;
  }

  private String pedirNombreArchivo(String sugerencia) {
    TextInputDialog dialog = new TextInputDialog(sugerencia);
    dialog.setTitle("Nombre del archivo");
    dialog.setHeaderText("Introduce el nombre del archivo (sin extensión):");
    return dialog.showAndWait().orElse(null);
  }

  public class ParentOption {
    public final String referencia;
    public final String nombre;

    public ParentOption(String referencia, String nombre) {
      this.referencia = referencia;
      this.nombre = nombre;
    }

    @Override
    public String toString() {
      return nombre;
    }
  }
}
