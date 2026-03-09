package tests;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;

import javax.swing.SwingUtilities;

import org.junit.jupiter.api.Test;

import controladores.fxcontrollers.Acceso;
import javafx.application.Application;
import javafx.embed.swing.JFXPanel;
import modelo.base.Config;
import ui.Splash;

public class LoginTest {

	@Test
	public void splashOK(){

		Splash window = new Splash();

        window.run();
        window.setAlwaysOnTop(true);
        window.setVisible(false);
		assertNotNull(window);
	}

	@Test
	public void javaFxFuncionaEnJUnit() throws InterruptedException, IllegalAccessError{
		final CountDownLatch latch = new CountDownLatch(1);
		assertTimeout(Duration.ofSeconds(45),()-> {
        	new JFXPanel(); // inicializa el entorno de JavaFX
        	latch.countDown();
		});
	latch.await();
	}
	 

	/*
	@Test
	void loginDesconocidoCreaCredsOK(){
		assertTrue(/* Credenciales usuario nuevo creadas );
	}

	@Test
	void controladorArrancaOK(){
		assertNotNull(/* Controlador Ppal Obj );
	}

	@Test
	void panelControlArrancaOK(){
		assertNotNull(/* Panel de Control Obj );
	}
*/

}
