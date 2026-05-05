package app;

import static org.junit.jupiter.api.Assertions.*;

import app.services.FacturasService;
import domain.records.Factura;
import domain.records.Fecha;
import infraestructure.csv.FacturaCSVRepo;
import infraestructure.servicios.config.Config;

import java.io.File;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
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
                .filter(f -> f.getID().equals(2))   // o "F002" si tu ID es String
                .findFirst()
                .orElseThrow();

        // 2. Modificar la fecha
        original.setFecha(new Fecha(24, 6, 2026));  // ejemplo: 24/06/2026

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
    public void testInsertarFactura() {}

    @Test
    public void testEditarFactura() {}

    @Test
    public void testBorrarFactura() {}




  /*
      @SuppressWarnings("unchecked")
      @Test
      public void convertirCSVok() throws NullPointerException, IOException, InterruptedException, BrokenBarrierException{
          //Se inicializan config y controladores
          String usuario = "admin";
          String rutaYnombre = "datos/" + usuario.toUpperCase() + "/FCT20240.csv";
          var config = Config.getConfig(usuario);
          var contrlPpal = Controlador_prev.getControlador();
          Controlador_prev.setUsuario("admin");
          var modeloFCT = ModeloFacturas.getModelo();
          //Se lee el fichero CSV
          var fichero = new Fichero(rutaYnombre);
          ArrayList<String[]> datosCSV = fichero.leerCSV(rutaYnombre);
          //comprobación
          assertNotNull(datosCSV);
          //Se convierte el CSV a una lista de Facturas
          var listaPruebaFCT = new ArrayList<Factura>();
          for(String[] linea : datosCSV){
              Factura f = Factura.convertirCSVaFCT(linea);
              if(f!=null){
                  listaPruebaFCT.add(f);
                  System.out.println("****[FacturaTest] Factura convertida: ");
                  System.out.println(f.toString());
              }
          }
          //Se comprueba...
          assertNotNull(listaPruebaFCT);
      }
  */
}
