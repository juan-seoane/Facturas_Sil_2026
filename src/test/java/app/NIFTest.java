package app;

import domain.records.NIF;
import org.junit.jupiter.api.Test;

public class NIFTest {

	@Test
	public void testLetra(){
		int num= 00010001;
        int num2 = 00010010;
		String letra = NIF.dameLetraNIF(num);
        System.out.println("[testNIF] " + num + " - " + letra);
        String letra2 = NIF.dameLetraNIF(num2);
        System.out.println("[testNIF] " + num2 + " - " + letra2);
	}
}
