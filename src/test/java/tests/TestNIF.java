package tests;

import org.junit.Test;

import modelo.records.NIF;

public class TestNIF {

	@Test
	public void dameLetraNIFok(){
		int num= 11000011;

		String letra = NIF.dameLetraNIF(num);
		System.out.println("[testNIF] " + num + " - " + letra);
	}
}
