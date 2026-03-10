# Facturas Sil 2026

<!-- #region INFO -->
## Getting Started

Welcome to the VS Code Java world. Here is a guideline to help you get started to write Java code in Visual Studio Code.

## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources, multiplatform
- `imagenes`: la carpeta donde se guardan los archivos de imagen (.png, .jpg, etc...)
- `escaneos`: la carpeta con las facturas escaneadas, en formato .png
- `informes`: la carpeta con los PDF de cada informe
- `config`: un archivo 'credenciales.json' con los pares (user:pass) para autenticarse,
            y carpetas para cada usuario con sus archivos de configuración en formato .json
- `datos`: carpetas para cada usuario con sus archivos de trabajo en formato .json

- `lib`: the folder to maintain dependencies, OS-exclusive
- `bin`: the folder to maintain binaries, OS-exclusive

The compiled output files will be generated in the `bin` folder by default.

> If you want to customize the folder structure, open `.vscode/settings.json` and update the related settings there. OS-exclusive.

## Dependency Management

Junit 5
JavaFX 23 / 25 (en Windows)
testFx 4.0.18
Plant UML
Cucumber

## Project Info

Actualizado a JDK 25
El proyecto trata de renovar el programa en Java,
y automatizar la entrada de facturas,
de manera que el procedimiento sea más ágil: en vez de escribir uno a uno los datos de cada factura,
escanear una factura física, y, después de un OCR que convierte sus datos relevantes
en un objeto Factura, ésta se añade automáticamente a la lista de facturas...
También se actualizan las GUI con JavaFX, en vez de Swing, y se usan FXtest + JUnit5 para pruebas unitarias. Esta vez se usa VSCode con Java, en vez de Netbeans IDE.

<!-- #endregion -->

<!-- #region TODO's -->
## TODO's y demás

    // TODO   - 26-03-08 [General, Previo] : Investigar cómo organizar usando MVC si JavaFX (V) es un MVC en sí...
    // TODO   - 26-03-08 [General, Previo] : Hacer Diagramas de clases con MS Copilot y Plant UML
    // TODO   - 26-03-08 [General, Previo] : Reorganizar la lógica para implementar Interfaces y streams...
    // TODO   - 26-03-08 [General, Previo] : Reorganizar las carpetas-paquetes 
<!--- ( hasta aquí la ver 2024) --->
    // FIXME  - 24-08-08 [General] : Al mostrar la tablaFCT (y al Editar) tiene q ordenar la listaFCT (por fechas)...

    // TODO   - 24-08-01 [General,FxCntrlVisorFCT] : NuevaFactura
    // TODO   - 24-07-24 [General,FxCntrlVisorFCT] : Validaciónes de las facturas : fechas, numeros, nif, etc...
    // TODO   - 24-07-30 [General,FxCntrlVisorFCT] : Automatizar la entrada de extractos: sumar los de mismo tipo de IVA, calcular tipoIVA desde Base+IVA, Incluir Sumas de Totales, Añadir Conceptos cuando se suman extractos...

    // STUB   - 24-07-31 [ControladorFacturas>483] : Arreglar la asignac de index, filtradas las facts no son necesariamente correlativos, ID de la factura e index de la tabla
    // STUB   - 24-07-29 [ControladorFacturas>551] : reorganizar las facturas en la lista después de borrar una factura

    // TODO   - 24-07-31 [General,PanelControl>265] método (estático o no) en cada contrFX que muestre la GUI correspondiente (GUIx.mnostrar())
    // TODO   - 24-06-14 [General,ModeloFacturas>34,437] : Plantearse si se necesita un VectorFacturas (antes se usaba para la GUI de Swing)
    // TODO   - 24-07-29 [General,ControladorFacturas>550] : Cómo cojo el Index, si cuando se filtra no es igual a la ID???
    // TODO   - 24-07-02 [General,ControladorFacturas>780] : Tendré que SINCRONIZAR MEDIANTE BARRERAS, etc...
    // TODO   - 14-07-24 [ModeloFacturas>30] : En algún momento tendré que arreglar esto... facturas_prev puede no hacer falta...
    // TODO   - 24-07-01 [FxCntrlTablaFCT] : Investigar cómo maximizar la ventana manteniendo el formato y cómo cargar las columnas de la config
    // TODO   - 24-08-01 [fxmltablaFCT] : Poner la columna 'baseNI' en la tablaFCT, encoger el campo 'Nota'cfct
    // TODO   - 24-06-27 [Controlador>273] : Aquí hay que activar la Tabla/Visor (dependiendo del modo INGR/NAV) de Facturas
    // TODO   - 24-05-29 [PanelControl>70] : Habrá que decidir si el modo NAV es el modo por defecto en el P/C... 
    // TODO   - 24-06-30 [ControladorFactutas>371] : HAY QUE AJUSTAR EL VISOR PARA QUE SELECCIONE LA FACTURA ADECUADA DESDPUES DE ACTIVAR EL FILTRO
    // TODO   - 24-04-11 [ModeloFacturas>376] : HAY QUE CONSEGUIR INTRODUCIR TODA LA RAZON SOCIAL COMO APARECE EN DISTRIBUIDORES 
    // TODO   - 24-07-14 [ControladorFacturas>768] : Falta comprobar si es el controladorFX correcto
    // TODO   - 24-04-09 [PanelControl] : En el constructor, crear un controladorFicheros en un nuevo hilo para que gestione el Guardado Automático
    // TODO   - 24-07-31 [ModeloFacturas>268] : ACORDARSE DE ACTUALIZAR LOS TOTALES DESPUES DE FILTRAR! 
    // TODO   - 24-07-31 [OCRTest] : BDD con los tipos de fact y las coords a escanear y el tipo de dato que se extrae, con una GUI que lo facilite
    // TODO   - 24-05-07 [EntradaCaja, Extracto, Factura, MisDatos, NIF, RS] : Revisar la forma de comparar estas clases
    // TODO   - 24-06-19 [Factura>266] : Además habría que ver si existe ya la RS (según el ID)

    // REVIEW - 24-08-08 [TablaFCT] : El botón EDITAR (aún no en negrita) sólo abre el visor
    // REVIEW - 24-08-05 [VisorFCT] : Esconder los botones 'EDITAR' y 'BORRAR' del visorFCT mientras edita...
    // REVIEW - 24-08-03 [General,ControladorFacturas>537] Actualizar aquí las ID de las facturas siguientes 
    // REVIEW - 24-08-03 [General,ModeloFacturas>335]: Cambiar las ID de las facturas (no ordenarlas). Además, si se ordenan, debe ser por ID
    // REVIEW - 24-08-04 [General] : En Windows,  trasladar la carpeta 'datos/'
    // REVIEW - 24-08-04 [General] : En Windows, incluir en el settings.json: "java.project.sourcePaths"
    // REVIEW - 24-07-20 : Falta la carpeta 'informes' en el 'sourcePath' del programa...
    // REVIEW - 24-08-03 [ControladorFacturas>522] : Hacer que los Alert estén siempre en primer plano (AOF)
    // REVIEW - 24-08-01 [General,FxCntrlVisorFCT] : BorrrarFactura
    // REVIEW - 24-07-29 [ControladorFacturas>499-502,558-561] : Operaciones para el final de recogerFormYeditar
    // REVIEW - 24-07-30 [FxCntrlVisorFCT] : Organizar los signos en las devoluciones (Subtotal no tiene que tener el signo menos)
    // REVIEW - 24-07-25 [FxCntrlTablaFCT>264] : ¿¿Refrescar tabla después de actualizar etiqueta??
    // REVIEW - 24-07-30 [General] : Hay que aclarar lo del num de Extractos en la Factura, lo de la BaseNI y el TipoIVA de los totales...   
    // REVIEW - 24-07-30 [FxCntrlVisorFCT] : Me parece que va a ser mejor quitar el campo BaseNI de Totales, y dejarlos como un extracto... ver cómo contabilizarlo igualmente
    // REVIEW - 24-07-31 [General] : Revisar el ingreso de Facturas en el visor, el paso de Facturas a CSV, en lo referente al num de Extractos, los tipos de IVA... ¿El campo 'concepto' en los extractos debe ser "extracto" y no "leche" por ejemplo?...¿cómo puede informarse en el formulario?...¿Después de nota '0', hay una coma?
    // REVIEW - 24-07-29 [FxCntrlVisorFCT>511] : Revisar lo de variosIVAs... todavía no está
    // REVIEW - 24-07-31 [General] : En el visorFCT, no poner el signo '-' en los extractos de las devoluciones, sólo en el total ¿¿¿¿???
    // REVIEW - 24-07-31 [General] : La baseNI ¿¿¿debería meterse en un Extracto??? - Consultar cómo van baseNI y retenciones en una Factura
    // REVIEW - 24-07-31 [General] : Podría meterse el 'concepto' de cada extracto... ¿¿Deben ser independientes de los totales??
    // REVIEW - 24-07-31 [ComprobacionesAcceso] : OJO! Usuario siempre se contrasta en may [->ya no] (aunque esté escrito en min) ...pero luego importa si lo escribes en May
    // REVIEW - 24-06-14 [Factura>265] : Revisar... Habrá que hacer un método nuevo que convierta un array de cadenas de texto en un objeto NIF
    // REVIEW - 24-06-14 [Factura>277] : Revisar esto, sólo vale para cuando hay notas... Hacer un método para leer la nota si existe, y si no, hacerla null
    // REVIEW - 24-06-15 [Factura>278] : Se podría poner el TipoIVA como un Integer en vez de una clase TipoIVA... (-> No, seguirá siendo una clase propia por si acaso...)
    // REVIEW - 24-06-15 [Factura>145] : ojo que sólo puede haber una Nota...
    // REVIEW - 24-04-11 [ModeloFacturas>374] : Necesito un método (estático, a poder ser) para generar automáticamente el ID de cada Nota... ¿Hace falta una ID? ¿podría la Nota ser simplemente una String?
    // REVIEW - 24-07-31 [Factura>322] : Así que el número de la Nota siempre es 0 o 1...
    // REVIEW - 24-07-20 : Dos cosas que percibo en Linux: Las imágenes de los JFX no se cargan (seguramente no usan rutas relativas), y las ventanas tienen un título central superior '<1>', que habrá que quitar o cambiar...    
    // REVIEW - 24-07-20 : En Linux no funciona el Splash, habráque hacerlo con FX...   
    // REVIEW - 24-07-17 : Cuando cierras el VisorFCT y lo vuelves a abrir, habiendo seleccionado otra factura, no actualiza la Factura en el visor...
    // REVIEW - 24-07-23 : Falta poner el visorFCT AlwaysOnTop
    // REVIEW - 24-07-23 : Falta hacer los textfield del visorFCT no editables en principio
    // REVIEW - 24-07-23 : Falta limpiar el visorFCT antes de actualizar
    // REVIEW - 24-07-23 : Falta colocar en el Visor, el TipoIVA de cada Extracto
    // REVIEW - 24-07-23 : Al meter una Factura en CSV, si es devolución poner un signo negativo en el Total
    // REVIEW - 24-07-01 : Falta en el VisorFCT : informar la 'Categoría' de la Factura y reemplazar Labels (en principio no editables) por TextFields
    // REVIEW - 24-07-28 : Cuando se muestra el visorFCT aparece otra ventana... (como un new Stage que se crea al cargar o mostrare el visor)
    // REVIEW - 24-07-28 : Además el visor no puede pasar de la factura 2 a la 1 (bajando) o de la 4 a la 1 (subiendo)...
    // REVIEW - 24-07-29 : No se limpian los campos del visor cuando se mueve entre facturas con los controles
    // REVIEW - 24-07-01 : Falta en el VisorFCT : moverse a travéws de la tablaFCT con las flechas
    // REVIEW - 24-07-01 : Falta en el Main     : Organizar el arranque de las GUI's (y los hilos de los controladores)... runLater vs Application.launch...
    // REVIEW - 24-07-30 : Hay que actualizar la tabla ( y recargar la lista de Facturas) cuando se edita una Factura
    // REVIEW - 24-07-30 : Organizar lo de las BasesNI, si salen en extractos no salen en Totales, y vicecersa...
    // REVIEW - 24-05-13 [Controlador>constructor] : Falta rediseñar el PnlCtl y la tabla de Facturas
    // REVIEW - 24-06-04 [Controlador] : Comprobar los hilos que se generan (ControladorFCT, ControladorDIST, etc...)... Parece que sólo funciona el P/C
    // REVIEW - 24-06-04 [Controlador] : Hacer Singleton
    
<!-- #endregion -->
