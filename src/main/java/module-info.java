module facturas.sil {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
	requires com.google.gson;
	requires java.desktop;
	requires com.opencsv;
	requires java.logging;
	requires jasperreports;
	requires javafx.base;

    // Para controladores FXML
    opens presentation.fxcontrollers to javafx.fxml;
    opens presentation to javafx.fxml;

    // Para permitir a GSON leer records (creds)
    opens domain.records to com.google.gson;

    // Para permitir que JavaFX instancie SplashFX (Application)
    exports presentation.fxcontrollers;

    // Para permitir que Main sea visible como entry point
    exports presentation;
}
