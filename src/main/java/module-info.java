module facturas.sil {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
	requires com.google.gson;
	requires java.desktop;
	requires com.opencsv;
	requires java.logging;
	requires jasperreports;

    // Para controladores FXML
    opens presentation.fxcontrollers to javafx.fxml;
    opens presentation to javafx.fxml;

    // Para permitir que JavaFX instancie SplashFX (Application)
    exports presentation.fxcontrollers;

    // Para permitir que Main sea visible como entry point
    exports presentation;
}
