package com.sil.facturas.presentationgui.helpers;

import com.sil.facturas.app.api.ItablaFCT;
import com.sil.facturas.app.api.IventanaUI;

import javafx.stage.Stage;

public class TablaFCTFX implements ItablaFCT {

    private final Stage stage;

    public TablaFCTFX(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void mostrar() {
        stage.show();
    }

    @Override
    public void ocultar() {
        stage.hide();
    }

    @Override
    public void cerrar() {
        stage.close();
    }

	@Override
	public IventanaUI crear() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'crear'");
	}
}

