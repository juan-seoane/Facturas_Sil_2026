package app;

import static org.junit.jupiter.api.Assertions.*;

import app.core.AppContext;
import app.core.AppController;
import app.services.FacturasService;
import domain.records.*;
import infraestructure.csv.FacturaCSVRepo;
import infraestructure.servicios.config.Config;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FacturasServiceTest {

  // Declara el usuario del que testear los datos
  static String user = "admin";
  static Config config;
  static FacturasService servicioFacturas;
  private static FacturaCSVRepo repo;

    @BeforeAll
    static void init() {
        try {
            crearConfig();
        } catch (Exception e) {
        System.out.println(
            "[FacturasServiceTest] Error " + e.getClass() + " al crear config para Test");
        }
        initService();
    }

    static void crearConfig() throws Exception {
        config = Config.getConfig(user);

        assertNotNull(config, "Config no debería ser null");
        assertNotNull(config.getConfigData(), "ConfigData no debería ser null");
        assertNotNull(config.getConfigData().getRutas(), "Rutas no debería ser null");
        assertNotNull(config.getConfigData().getRutas().getFCT(), "Ruta FCT no debería ser null");

        System.out.println("[FacturasServiceTest>CrearConfig] " + config);
    }

    static void initService() {
        servicioFacturas = new FacturasService(config.getConfigData());
        assertNotNull(servicioFacturas, "FacturasService no debería ser null");

        String rutaCSV = config.getConfigData().getRutas().getFCT();
        assertNotNull(rutaCSV, "La ruta del CSV no puede ser null");

        File f = new File(rutaCSV);
        assertTrue(f.exists(), "El archivo CSV debe existir antes de los tests");
        assertTrue(f.canRead(), "El archivo CSV debe ser legible");
        assertTrue(f.canWrite(), "El archivo CSV debe ser escribible");

        repo = new FacturaCSVRepo(rutaCSV);
        assertNotNull(repo, "FacturaCSVRepo no debería ser null");
    }

    @BeforeEach
    void ensureInit() throws Exception {

        System.out.println("[FacturasServiceTest] Iniciando el BeforeEach");

        // 1) Inicializar config si falta
        if (config == null) {
            config = Config.getConfig(user);

            assertNotNull(config, "Config no debería ser null");
            assertNotNull(config.getConfigData(), "ConfigData no debería ser null");
            assertNotNull(config.getConfigData().getRutas(), "Rutas no debería ser null");
            assertNotNull(config.getConfigData().getRutas().getFCT(), "Ruta FCT no debería ser null");
        }

        // 2) Inicializar servicioFacturas si falta
        if (servicioFacturas == null) {
            servicioFacturas = new FacturasService(config.getConfigData());
            AppContext.setFacturasService(servicioFacturas);
            assertNotNull(servicioFacturas, "FacturasService no debería ser null");
        }

        // 3) Inicializar repo si falta
        if (repo == null) {
            String rutaCSV = config.getConfigData().getRutas().getFCT();
            assertNotNull(rutaCSV, "La ruta del CSV no puede ser null");

            repo = new FacturaCSVRepo(rutaCSV);
            assertNotNull(repo, "FacturaCSVRepo no debería ser null");
        }
    }


    @Test
    void testLeerArchivoFCT() throws Exception {

        // 1) Leer facturas desde el archivo
        List<Factura> lista = servicioFacturas.leerFacturas();

    // 2) Comprobaciones
    assertNotNull(lista, "[FacturasServiceTest>testLeerArchivoFCT] La lista no debería ser null");
        assertEquals(2, lista.size(), "[testLeerArchivoFCT] Debe haber 2 facturas");

        // 3) Logs:
        StringBuilder sb = new StringBuilder();
        sb.append("[testLeerArchivoFCT] ListaFacturas:\n");
        lista.forEach(f -> sb.append(f.toString()).append("\n"));
        System.out.println(sb.toString());
    }

    @Test
    void testActualizarFactura_CambiarFecha() {
        // 1. Leer la factura original
        Factura original = repo.leerListaFacturas().stream()
                .filter(f -> f.getID().equals(2)) // o "F002" si tu ID es String
                .findFirst()
                .orElseThrow();

        // 2. Modificar la fecha
        original.setFecha(new Fecha(24, 6, 2026)); // ejemplo: 24/06/2026

        // 3. Actualizar en el CSV
        boolean ok = repo.actualizarFactura(original);
        assertTrue(ok, "La actualización debería devolver true");

        // 4. Volver a leer la factura
        Factura modificada = repo.leerListaFacturas().stream()
                .filter(f -> f.getID().equals(2))
                .findFirst()
                .orElseThrow();

        // 5. Verificar que la fecha cambió
        assertEquals(24, modificada.getFecha().getDia());
        assertEquals(6, modificada.getFecha().getMes());
        assertEquals(2026, modificada.getFecha().getAnho());
    }

    @Test
    public void testIntroducirFactura() throws Exception {

        // Subfacturas reales según tu constructor
        ArrayList<Extracto> extractos = new ArrayList<>();
        extractos.add(new Extracto(10.0, 21, 2.1, 12.1, "SUB1"));
        extractos.add(new Extracto(20.0, 21, 4.2, 24.2, "SUB2"));
        List<Factura> lista = repo.leerListaFacturas();

        Factura f = new Factura(
                servicioFacturas.generarID(), // ID (Integer)
                "FTEST00" + (lista.size() + 1), // número de Factura
                new Fecha(1, 1, 2026),
                new RazonSocial("Cliente","A-11111111"),
                new TipoGasto("Concepto", ""),
                false,
                extractos,
                new Totales(),
                new Nota("nota"));

        boolean ok = servicioFacturas.introducirFactura(f);
        assertTrue(ok);

        lista = repo.leerListaFacturas();

        Factura f2 = lista.getLast();

        assertEquals("SUB1", f2.getExtractos().get(0).getConcepto());
    }

    @Test
    public void testGuardarFactura() throws Exception {

    Factura f =
        new Factura(
            servicioFacturas.generarID(), // ID (Integer)
            "F100",
            new Fecha(2, 2, 2026),
            new RazonSocial("Cliente", "A-11111111"),
            new TipoGasto("Servicios", "???"),
            false,
            new ArrayList<>(),
            new Totales(),
            new Nota(""));

        boolean ok = repo.guardarFactura(f);
        assertTrue(ok);

        List<Factura> lista = repo.leerListaFacturas();
    System.out.println(
        "[FacturasServiceTest>testGuardarFactura] Tamaño de la lista de facturas: " + lista.size());
        assertEquals(lista.size(), lista.getLast().getID());
    }

    @Test
    public void testInsertarFactura() {}

    @Test
    public void testEditarFactura() {}

    @Test
    public void testBorrarFactura() {}

}
