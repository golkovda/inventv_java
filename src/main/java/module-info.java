module com.example.inventv_java {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;

    opens com.example.inventv_java to javafx.fxml;
    exports com.example.inventv_java;
}