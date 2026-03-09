package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.util.concurrent.BrokenBarrierException;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;

import controladores.Controlador;
import controladores.ControladorFacturas;
import modelo.ModeloFacturas;
import modelo.base.Config;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ControladorTest {

	private Config config;
	private Controlador cntrlPpal;
	private ControladorFacturas cntrlFact;
	private ModeloFacturas modFact;

	@Before
	public void setUp() throws NullPointerException, IOException, InterruptedException, BrokenBarrierException{
		System.out.println("[ControladorTest] Comenzando el setUp");
		this.config = Config.getConfig("admin");
		this.cntrlPpal = Controlador.getControlador();
	}

	@Test
	public void CntrlPpalCargaOK() throws InterruptedException, BrokenBarrierException{
		System.out.println("[ControladorTest] Comenzando el test de carga del Controlador Principal");
		assertNotNull(this.config);
		assertNotNull(this.cntrlPpal);
		this.cntrlFact = Controlador.getControladorFacturas();
		assertNotNull(this.cntrlFact);
		assertEquals(this.cntrlFact,ControladorFacturas.getControlador());
		this.modFact = this.cntrlFact.m;
		assertNotNull(this.modFact);
		assertEquals(this.modFact,ModeloFacturas.getModelo());

		//NOTE - 10-07-24 : No carga los controladoresFX
		//assertNotNull(Controlador.getControladorFacturas().getFXcontrlTablaFCT());
		//assertNotNull(Controlador.getControladorFacturas().getFXcontrlVisorFCT());
	}
}
