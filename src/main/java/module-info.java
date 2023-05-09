module com.example.inventv_java {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.apache.logging.log4j;
    requires java.sql;
    requires spring.jdbc;
    requires spring.orm;
    requires spring.tx;
    requires spring.context;
    requires jakarta.persistence;
    requires spring.beans;
    requires java.naming;
    requires jtds;
    requires org.hibernate.orm.core;
    requires javafx.base;
    requires java.prefs;


    opens com.golkov.inventv.model.entities to org.hibernate.orm.core, javafx.base;
    opens com.golkov.inventv to javafx.fxml;
    exports com.golkov.inventv;
    exports com.golkov.inventv.controller;
    opens com.golkov.inventv.controller to javafx.fxml;
    exports com.golkov.inventv.controller.listcontroller;
    opens com.golkov.inventv.controller.listcontroller to javafx.fxml;
    opens com.golkov.inventv.controller.detailcontroller to javafx.fxml;
}