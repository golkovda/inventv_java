module com.example.inventv_java {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.apache.logging.log4j;
    requires javaee;

    opens com.golkov.inventv to javafx.fxml;
    exports com.golkov.inventv;
    exports com.golkov.inventv.controller;
    opens com.golkov.inventv.controller to javafx.fxml;
}