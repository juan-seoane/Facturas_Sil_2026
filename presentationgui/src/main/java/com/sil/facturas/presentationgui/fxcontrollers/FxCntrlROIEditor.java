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
import java.util.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.*;
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

  // ============================================================
  //  #region CAMPOS FXML
  // ============================================================

    @FXML private ScrollPane scrollPane;
    @FXML private ImageView imageView;
    @FXML private ComboBox<String> comboTipoBloque;
    @FXML private Pane overlayPane;

  // ============================================================
  //  #endregion
  // ============================================================

  // ============================================================
  //  #region OTROS CAMPOS
  // ============================================================

    // ROI interactivo
    private double startX;
    private double startY;
    private BloqueOCR bloqueTemp = null;
    private Rectangle rectTemp = null;
    public double x, y, w, h;

    // escala de la imagen
    private double scaleX = 1.0;
    private double scaleY = 1.0;
    private double realImageWidth;
    private double realImageHeight;

    private UIDataJson uiData;

    private enum Modo { BLOQUE, CAMPO }
    private Modo modoActual = Modo.BLOQUE;

    private final List<OCRItem> items = new ArrayList<>();

    private ModeloOCRService modeloService;
    private OCRService ocrService;
    private BloqueConfig cfg = new BloqueConfig();

    // modelo de bloques cargados
    private List<Bloque> bloques = new ArrayList<>();
    private Map<String, Bloque> mapaBloques = new HashMap<>();

    private ModeloOCRRenderer renderer;
    private InfoImagen infoImagen;

  // ============================================================
  //  #endregion
  // ============================================================

//   // ============================================================
//   //  #region INIT
//   // ============================================================

//   public void initialize() {

//     printWarning("[FxCntrlROIEditor.initialize] Dentro del initialize()");

//     modeloService = new ModeloOCRService();

//     // imageView
//     if (imageView == null) {
//         printError("[FxCntrlROIEditor>initialize] ERROR: imageView no fue inyectado desde el FXML");
//     } else {
//         imageView.setPreserveRatio(true);
//         imageView.setSmooth(true);
//     }

//     // overlayPane
//     if (overlayPane == null) {
//       printError("[FxCntrlROIEditor>initialize] ERROR: overlayPane no fue inyectado desde el FXML");
//     } else {
//         overlayPane.setPickOnBounds(false);
//     }

//     // ScrollPane
//     scrollPane.setFitToHeight(true);
//     scrollPane.setFitToWidth(true);

//     // Layout correcto: imageView + overlayPane dentro del mismo Group
//     Group group = new Group(imageView, overlayPane);
//     scrollPane.setContent(group);

//     // Crear renderer cuando la escena exista
//     Platform.runLater(() -> {
//         renderer = new ModeloOCRRenderer(imageView, overlayPane);
//     });

//     // Eventos de ratón para ROI (usando overlayPane)
//     overlayPane.setOnMousePressed(e -> {
//         if (rectTemp != null) {
//             overlayPane.getChildren().remove(rectTemp);
//             rectTemp = null;
//         }

//         startX = e.getX();
//         startY = e.getY();

//         rectTemp = new Rectangle(startX, startY, 1, 1);
//         rectTemp.setStroke(Color.YELLOW);
//         rectTemp.setStrokeWidth(2);
//         rectTemp.setFill(Color.color(1, 1, 0, 0.25));

//         overlayPane.getChildren().add(rectTemp);
//     });

//     overlayPane.setOnMouseDragged(e -> {
//         if (rectTemp == null) return;

//         double currentX = e.getX();
//         double currentY = e.getY();

//         double x = Math.min(startX, currentX);
//         double y = Math.min(startY, currentY);
//         double w = Math.abs(currentX - startX);
//         double h = Math.abs(currentY - startY);

//         rectTemp.setX(x);
//         rectTemp.setY(y);
//         rectTemp.setWidth(w);
//         rectTemp.setHeight(h);
//     });

//     overlayPane.setOnMouseReleased(
//         e -> {
//           if (rectTemp == null) return;

//           this.x = rectTemp.getX();
//           this.y = rectTemp.getY();
//           this.w = rectTemp.getWidth();
//           this.h = rectTemp.getHeight();

//           printWarning(
//               "[ROIEditor] Bloque temporal definido en [" + x + ", " + y + "] w=" + w + ", h=" + h);

//           bloqueTemp =
//               new BloqueOCR(
//                   cfg.nombreBloque,
//                   "",
//                   cfg.referencia,
//                   x,
//                   y,
//                   w,
//                   h,
//                   cfg.offsetTipo,
//                   0,
//                   0,
//                   cfg.growVert,
//                   cfg.growHoriz,
//                   cfg.anchorX,
//                   cfg.anchorY,
//                   new ArrayList<>());

//           if (!"Imagen (root)".equals(cfg.parentNombre)) {
//             bloqueTemp.setParentNombre(cfg.parentNombre);
//           } else {
//             bloqueTemp.setParentNombre(null);
//           }
//         });
//     }

//   // ============================================================
//   //  #endregion
//   // ============================================================

//   // ============================================================
//   //  #region CARGAR IMAGEN
//   // ============================================================

//   @FXML
//   private void onCargarImagen() {

//         FileChooser fc = new FileChooser();
//         fc.setTitle("Seleccionar imagen");
//         fc.getExtensionFilters().add(
//             new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
//         );

//         Window window = scrollPane.getScene().getWindow();
//         File file = fc.showOpenDialog(window);
//         if (file == null) return;

//         renderer.cargarImagenYCalcularEscala(imageView, scrollPane,
//             file,
//             info -> {
//                 this.infoImagen = info;

//                 this.realImageWidth = info.realWidth();
//                 this.realImageHeight = info.realHeight();
//                 this.scaleX = info.escala().x();
//                 this.scaleY = info.escala().y();

//                 if (bloques != null && !bloques.isEmpty())
//                     renderer.dibujarBloques(bloques);
//             }
//         );
//     }

//     // ============================================================
//     //  #endregion
//     // ============================================================

//     // ============================================================
//     //  #region CARGAR BLOQUES
//     // ============================================================

//     @FXML
//     private void onCargarBloques() {

//         FileChooser fc = new FileChooser();
//         fc.setTitle("Seleccionar archivo de bloques");
//         fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Bloques (*.json)", "*.json"));

//         File file = fc.showOpenDialog(overlayPane.getScene().getWindow());
//         if (file == null) return;

//         bloques = modeloService.cargarBloques(file);

//         mapaBloques.clear();
//         for (Bloque b : bloques) {
//             mapaBloques.put(b.nombre(), b);
//         }

//         if (infoImagen != null) {
//             renderer.dibujarBloques(bloques);
//         } else {
//       printWarning(
//           "[FxCntrlROIEditor>onCargarBloques] No hay imagen cargada todavía; no se puede dibujar.");
//         }
//     }

//   // ============================================================
//   //  #endregion
//   // ============================================================

//   // ============================================================
//   //  #region DIÁLOGO CARGA INICIAL
//   // ============================================================

//   @FXML
//   private void mostrarDialogoCarga() {

//         ButtonType btnBloques = new ButtonType("Modelo de bloques");
//         ButtonType btnOCR = new ButtonType("Modelo OCR completo");
//         ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

//         Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//         alert.setTitle("Cargar modelo");
//         alert.setHeaderText("Selecciona el tipo de modelo que quieres cargar");
//         alert.getButtonTypes().setAll(btnBloques, btnOCR, btnCancelar);

//         Optional<ButtonType> result = alert.showAndWait();

//         if (result.isEmpty() || result.get() == btnCancelar) {
//             return;
//         }

//         if (result.get() == btnBloques) {
//             cargarModeloBloquesInicial();
//         } else if (result.get() == btnOCR) {
//             // cargarModeloOCRInicial();
//         }
//     }

//     private void cargarModeloBloquesInicial() {

//         FileChooser fc = new FileChooser();
//         fc.setTitle("Seleccionar archivo de bloques");
//         fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Bloques (*.json)", "*.json"));

//         File file = fc.showOpenDialog(scrollPane.getScene().getWindow());
//         if (file == null) return;

//         bloques = modeloService.cargarBloques(file);

//         mapaBloques.clear();
//         for (Bloque b : bloques) {
//             mapaBloques.put(b.nombre(), b);
//         }

//         if (infoImagen != null) {
//             renderer.dibujarBloques(bloques);
//         } else {
//       printWarning(
//           "[FxCntrlROIEditor>cargarModeloBloquesInicial] No hay imagen cargada; carga primero la"
//               + " imagen.");
//         }
//     }

//   // ============================================================
//   //  #endregion
//   // ============================================================

//   // ============================================================
//   // #region NUEVO BLOQUE
//   // ============================================================

//   @FXML
//   private void onNuevoBloque() {

//         String tipo = comboTipoBloque.getValue();
//         if (tipo == null || tipo.isEmpty()) {
//             printError("Selecciona un tipo de bloque antes de crear uno.");
//             return;
//         }

//         switch (tipo) {
//             case "bloqueExtractos" -> crearBloque(_TipoBloque.EXTRACTOS);
//             case "bloqueExtractos.cabecera" -> crearBloque(_TipoBloque.EXTRACTOS_CABECERA);
//             case "bloqueExtractos.linea" -> crearBloque(_TipoBloque.EXTRACTOS_LINEA);
//             case "bloqueTotales" -> crearBloque(_TipoBloque.TOTALES);
//             case "bloqueCabecera" -> crearBloque(_TipoBloque.CABECERA);
//             case "bloqueGenerico" -> crearBloque(_TipoBloque.GENERICO);
//         }
//     }

//     private void crearBloque(_TipoBloque tipo) {
//         switch (tipo) {
//             case CABECERA -> crearBloqueCabecera();
//             case EXTRACTOS -> crearBloqueExtractos();
//             case EXTRACTOS_CABECERA -> crearBloqueExtractosCabecera();
//             case EXTRACTOS_LINEA -> crearBloqueExtractosLinea();
//             case TOTALES -> crearBloqueTotales();
//             case GENERICO -> crearBloqueGenerico();
//         }
//     }

//     private BloqueOCR crearBloqueCabecera() {

//         if (this.x == 0.0 || this.y == 0.0 || this.w == 0.0 || this.h == 0.0) {
//             printError("[FxCntrlROIEditor>crearBloqueCabecera] Debes seleccionar un área antes.");
//             return null;
//         }

//         BloqueConfig cfg = pedirConfigBloqueAlUsuario();
//         if (cfg == null) return null;

//         BloqueOCR b = new BloqueOCR(
//                 "bloqueCabecera",
//                 cfg.parentNombre,
//                 "factura",
//                 this.x,
//                 this.y,
//                 this.h,
//                 this.w,
//                 cfg.offsetTipo,
//                 0,
//                 0,
//                 cfg.growVert,
//                 cfg.growHoriz,
//                 cfg.anchorX,
//                 cfg.anchorY,
//                 new ArrayList<>());

//         items.add(b);
//         dibujarBloque(b);
//         return b;
//     }

//     private BloqueOCR crearBloqueExtractos() {

//         if (this.x == 0.0 || this.y == 0.0 || this.w == 0.0 || this.h == 0.0) {
//             printError("[FxCntrlROIEditor>crearBloqueExtractos] Selecciona un área primero.");
//             return null;
//         }

//         BloqueConfig cfg = pedirConfigBloqueAlUsuario();
//         if (cfg == null) return null;

//         BloqueOCR b = new BloqueOCR(
//                 "bloqueExtractos",
//                 cfg.parentNombre,
//                 "extracto",
//                 this.x,
//                 this.y,
//                 this.h,
//                 this.w,
//                 cfg.offsetTipo,
//                 0,
//                 0,
//                 cfg.growVert,
//                 cfg.growHoriz,
//                 cfg.anchorX,
//                 cfg.anchorY,
//                 new ArrayList<>());

//         items.add(b);
//         dibujarBloque(b);
//         return b;
//     }

//     private BloqueOCR crearBloqueExtractosCabecera() {

//         if (this.x == 0.0 || this.y == 0.0 || this.w == 0.0 || this.h == 0.0) {
//       printError("[FxCntrlROIEditor>crearBloqueExtractosCabecera] Selecciona un área primero.");
//             return null;
//         }

//         BloqueConfig cfg = pedirConfigBloqueAlUsuario();
//         if (cfg == null) return null;

//         BloqueOCR b = new BloqueOCR(
//                 "cabeceraExtractos",
//                 cfg.parentNombre,
//                 "extracto",
//                 this.x,
//                 this.y,
//                 this.h,
//                 this.w,
//                 cfg.offsetTipo,
//                 0,
//                 0,
//                 cfg.growVert,
//                 cfg.growHoriz,
//                 cfg.anchorX,
//                 cfg.anchorY,
//                 new ArrayList<>());

//         items.add(b);
//         dibujarBloque(b);
//         return b;
//     }

//     private BloqueOCR crearBloqueExtractosLinea() {

//         if (this.x == 0.0 || this.y == 0.0 || this.w == 0.0 || this.h == 0.0) {
//             printError("[FxCntrlROIEditor>crearBloqueExtractosLinea] Selecciona un área primero.");
//             return null;
//         }

//         BloqueConfig cfg = pedirConfigBloqueAlUsuario();
//         if (cfg == null) return null;

//         BloqueOCR b = new BloqueOCR(
//                 "lineaExtracto",
//                 cfg.parentNombre,
//                 "extracto",
//                 this.x,
//                 this.y,
//                 this.h,
//                 this.w,
//                 cfg.offsetTipo,
//                 0,
//                 0,
//                 cfg.growVert,
//                 cfg.growHoriz,
//                 cfg.anchorX,
//                 cfg.anchorY,
//                 new ArrayList<>());

//         items.add(b);
//         dibujarBloque(b);
//         return b;
//     }

//     private BloqueOCR crearBloqueTotales() {

//         if (this.x == 0.0 || this.y == 0.0 || this.w == 0.0 || this.h == 0.0) {
//             printError("[FxCntrlROIEditor>crearBloqueTotales] Selecciona un área primero.");
//             return null;
//         }

//         BloqueConfig cfg = pedirConfigBloqueAlUsuario();
//         if (cfg == null) return null;

//         BloqueOCR b = new BloqueOCR(
//                 "bloqueTotales",
//                 cfg.parentNombre,
//                 "totales",
//                 this.x,
//                 this.y,
//                 this.h,
//                 this.w,
//                 cfg.offsetTipo,
//                 0,
//                 0,
//                 cfg.growVert,
//                 cfg.growHoriz,
//                 cfg.anchorX,
//                 cfg.anchorY,
//                 new ArrayList<>());

//         items.add(b);
//         dibujarBloque(b);
//         return b;
//     }

//     private BloqueOCR crearBloqueGenerico() {

//         if (this.x == 0.0 || this.y == 0.0 || this.w == 0.0 || this.h == 0.0) {
//             printError("[FxCntrlROIEditor>crearBloqueGenerico] Selecciona un área primero.");
//             return null;
//         }

//         BloqueConfig cfg = pedirConfigBloqueAlUsuario();
//         if (cfg == null) return null;

//         String referencia = cfg.nombreBloque.toLowerCase().replace(" ", "_");

//         BloqueOCR b = new BloqueOCR(
//                 cfg.nombreBloque,
//                 cfg.parentNombre,
//                 referencia,
//                 this.x,
//                 this.y,
//                 this.h,
//                 this.w,
//                 cfg.offsetTipo,
//                 0,
//                 0,
//                 cfg.growVert,
//                 cfg.growHoriz,
//                 cfg.anchorX,
//                 cfg.anchorY,
//                 new ArrayList<>());

//         items.add(b);
//         dibujarBloque(b);
//         return b;
//     }

//     private void dibujarBloque(BloqueOCR b) {
//         var rect = new javafx.scene.shape.Rectangle(b.x(), b.y(), b.w(), b.h());
//         rect.setStroke(javafx.scene.paint.Color.YELLOW);
//         rect.setFill(javafx.scene.paint.Color.TRANSPARENT);
//     rect.setOnMouseClicked(
//         e ->
//             print(
//                 " [FxCntrlROIEditor>dibujarBloque>onMouseClicked] Bloque seleccionado: "
//                     + b.nombre()));

//     print(
//         "[FxCntrlROIEditor>dibujarBloque] Dibujando Rect ["
//             + b.x()
//             + ", "
//             + b.y()
//             + ", "
//             + b.w()
//             + ", "
//             + b.h()
//             + "]");
//         overlayPane.getChildren().add(rect);
//     }

//     private void dibujarCampo(CampoOCR c) {
//         var rect = new javafx.scene.shape.Rectangle(c.x(), c.y(), c.w(), c.h());
//         rect.setStroke(javafx.scene.paint.Color.CYAN);
//         rect.setFill(javafx.scene.paint.Color.TRANSPARENT);
//         overlayPane.getChildren().add(rect);
//     }

//   // ============================================================
//   //  #endregion
//   // ============================================================

//   // ============================================================
//   //  #region NUEVO CAMPO
//   // ============================================================

//   @FXML
//   private void onNuevoCampo() {
//         BloqueOCR bloque = (BloqueOCR) items.stream()
//             .filter(i -> i instanceof BloqueOCR)
//             .findFirst()
//             .orElse(null);

//         if (bloque == null) return;

//         CampoOCR c = new CampoOCR(
//             "campo" + (bloque.campos().size() + 1),
//             bloque.nombre(),
//             "concepto",
//             bloque.x() + 10,
//             bloque.y() + 10,
//             100,
//             30,
//             _TipoContenido.TEXTO_COMPLETO
//         );

//         bloque.addCampo(c);
//         items.add(c);

//         dibujarCampo(c);
//     }

//   // ============================================================
//   //  #endregion
//   // ============================================================

//   // ============================================================
//   //  #region GUARDAR MODELO
//   // ============================================================

//   @FXML
//   private void onGuardarModelo() {

//         TipoGuardado tipo = pedirTipoGuardado();
//         if (tipo == null) {
//             printWarning("[onGuardarModelo] Guardado cancelado por el usuario.");
//             return;
//         }

//         modeloService.nuevoModelo("Factura", "1.0");

//         for (OCRItem item : items) {
//             if (item instanceof BloqueOCR b) {
//                 modeloService.addBloque(modeloService.convertirBloqueOCR(b));
//             }
//         }

//         String sugerencia = (tipo == TipoGuardado.BLOQUES)
//                 ? "bloques_"
//                 : "modeloOCR_";

//         String nombre = pedirNombreArchivo(sugerencia);
//         if (nombre == null || nombre.isBlank()) {
//             printWarning("[onGuardarModelo] Nombre de archivo vacío. Cancelado.");
//             return;
//         }

//         String ruta = "data/datos/modelosOCR/" + nombre + ".json";
//         modeloService.guardarModelo(new File(ruta));

//         printWarning("[onGuardarModelo] Guardado correcto en: " + ruta);
//     }

//     private enum TipoGuardado { MODELO, BLOQUES }

//     private TipoGuardado pedirTipoGuardado() {
//         ChoiceDialog<String> dialog = new ChoiceDialog<>(
//                 "Modelo completo",
//                 "Modelo completo",
//                 "Solo bloques");
//         dialog.setTitle("Guardar modelo OCR");
//         dialog.setHeaderText("Selecciona el tipo de guardado:");

//         Optional<String> result = dialog.showAndWait();
//         if (result.isEmpty())
//             return null;

//         return result.get().equals("Solo bloques")
//                 ? TipoGuardado.BLOQUES
//                 : TipoGuardado.MODELO;
//     }

//     private String pedirNombreArchivo(String sugerencia) {
//         TextInputDialog dialog = new TextInputDialog(sugerencia);
//         dialog.setTitle("Nombre del archivo");
//         dialog.setHeaderText("Introduce el nombre del archivo (sin extensión):");

//         Optional<String> result = dialog.showAndWait();
//         return result.orElse(null);
//     }

//   // ============================================================
//   //  #endregion
//   // ============================================================

  // ============================================================
  //  #region UTILIDADES
  // ============================================================

  private BufferedImage toBufferedImage(Image fxImage) {
        return javafx.embed.swing.SwingFXUtils.fromFXImage(fxImage, null);
    }

    private void updateScale() {
        if (imageView == null || realImageWidth == 0 || realImageHeight == 0)
            return;

        double viewW = imageView.getBoundsInParent().getWidth();
        double viewH = imageView.getBoundsInParent().getHeight();

        scaleX = viewW / realImageWidth;
        scaleY = viewH / realImageHeight;
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

        // --- Parent ---
        ComboBox<ParentOption> cbParent = new ComboBox<>();
        cbParent.getItems().add(new ParentOption(null, "Imagen (root)"));

        for (OCRItem item : items) {
            if (item instanceof BloqueOCR b) {
                cbParent.getItems().add(new ParentOption(b.referencia(), b.nombre()));
            }
        }

        cbParent.setValue(cbParent.getItems().get(0)); // Imagen (root)

        // --- Layout ---
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
                        cfg.nombreBloque = txtNombre.getText(); // ← NUEVO
                        cfg.referencia = comboTipoBloque.getValue();
                        ParentOption opt = cbParent.getValue();
                        cfg.parentNombre = opt.referencia; // null si es root
                        return cfg;
                    }
                    return null;
                });

        Optional<BloqueConfig> result = dialog.showAndWait();
        return result.orElse(null);
    }

    private void dibujarBloqueModelo(Bloque b) {
        double x = b.zona().x() * scaleX;
        double y = b.zona().y() * scaleY;
        double w = b.zona().w() * scaleX;
        double h = b.zona().h() * scaleY;

        // Rectángulo
        Rectangle r = new Rectangle(x, y, w, h);
        r.setStroke(Color.RED);
        r.setStrokeWidth(2);
        r.setFill(Color.TRANSPARENT);
        overlayPane.getChildren().add(r);

        // Nombre arriba a la izquierda
        Text label = new Text(b.nombre());
        label.setFill(Color.RED);
        label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        label.setX(x);
        label.setY(y - 6);
        overlayPane.getChildren().add(label);

        // Campos
        for (Campo c : b.campos()) {
            dibujarCampoModelo(b, c);
        }
    }

    private void dibujarCampoModelo(Bloque b, Campo c) {

        // Coordenadas absolutas del bloque
        double bx = b.zona().x() * scaleX;
        double by = b.zona().y() * scaleY;

        // Coordenadas relativas del campo
        double cx = c.offsetX() * scaleX;
        double cy = c.offsetY() * scaleY;
        double cw = c.w() * scaleX;
        double ch = c.h() * scaleY;

        // Rectángulo del campo
        Rectangle r = new Rectangle(bx + cx, by + cy, cw, ch);
        r.setStroke(Color.YELLOW);
        r.setStrokeWidth(2);
        r.setFill(Color.TRANSPARENT);

        overlayPane.getChildren().add(r);

        // Nombre del campo (encima a la izquierda)
        Text label = new Text(c.nombre());
        label.setFill(Color.YELLOW);
        label.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
        label.setX(bx + cx);
        label.setY(by + cy - 4);

        overlayPane.getChildren().add(label);
    }

    public class ParentOption {
        public final String referencia; // id real
        public final String nombre; // nombre visible

        public ParentOption(String referencia, String nombre) {
            this.referencia = referencia;
            this.nombre = nombre;
        }

        @Override
        public String toString() {
            return nombre; // ← lo que se muestra en el ComboBox
        }
    }

  // ============================================================
  //  #endregion
  // ============================================================

}
