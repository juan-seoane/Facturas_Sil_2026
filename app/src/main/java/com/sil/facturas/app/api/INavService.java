package com.sil.facturas.app.api;

public interface INavService {
    void mostrarTablaFacturas();
    void ocultarTablaFacturas();
    void mostrarPanelControl();
    void mostrarLogin();
    void mostrarSplash();
	void loginCorrecto(String usuario);
	void cerrarLoginPorFallo();
}
