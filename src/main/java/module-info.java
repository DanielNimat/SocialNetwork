module com.example.social_network {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires java.management;
    requires javafx.graphics;
    requires org.apache.pdfbox;
    opens com.example.social_network to javafx.fxml;
    exports com.example.controller;
    exports com.example.social_network;
    opens com.example.Service to javafx.fxml;
    opens com.example.controller to javafx.fxml;
    opens com.example.domain to javafx.graphics, javafx.fxml, javafx.base;

}