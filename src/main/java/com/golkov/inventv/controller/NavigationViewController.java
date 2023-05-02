package com.golkov.inventv.controller;

import com.golkov.inventv.Globals;
import com.golkov.inventv.Main;
import com.golkov.inventv.model.daos.BenutzerDAO;
import com.golkov.inventv.model.entities.BenutzerEntity;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Node;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

public class NavigationViewController implements Initializable {

    private static final Logger logger = LogManager.getLogger(NavigationViewController.class);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.info("Initializing NavigationViewController...");
        lblBriefText.setText("Startseite");
        loadPane(vpAnchorPane, "views/StartseiteView.fxml");
    }

    @FXML
    private Button btnAusleihenverwaltung;

    @FXML
    private Button btnBenutzerverwaltung;

    @FXML
    private Button btnInfo;

    @FXML
    private Button btnObjektverwaltung;

    @FXML
    private Button btnStartseite;

    @FXML
    private ImageView imgLogo;

    @FXML
    private Label lblBriefText;

    @FXML
    private Label lblConnectionAdress;

    @FXML
    private Label lblUsername;

    @FXML
    private AnchorPane vpAnchorPane;

    @FXML
    void ausleihenverwaltungButtonTapped(ActionEvent event) throws IOException {
        lblBriefText.setText("Ausleihenverwaltung");
        loadPane(vpAnchorPane, "views/AusleihenListeView.fxml");
    }

    @FXML
    void benutzerverwaltungButtonTapped(ActionEvent event) throws IOException {
        logger.info("Knopf gedrückt: Benutzerverwaltung");
        lblBriefText.setText("Benutzerverwaltung");
        loadPane(vpAnchorPane, "views/BenutzerdatenListeView.fxml");
    }

    @FXML
    void infoButtonTapped(ActionEvent event) {
        logger.info("Knopf gedrückt: Info");
        lblBriefText.setText("Über das Programm");
        //TODO: InfoView.fxml erstellen und Code ergänzen
    }

    @FXML
    void objektverwaltungButtonTapped(ActionEvent event) {
        logger.info("Knopf gedrückt: Objektverwaltung");
        lblBriefText.setText("Objektverwaltung");
        loadPane(vpAnchorPane, "views/ObjektdatenListeView.fxml");
    }

    @FXML
    void startseiteButtonTapped(ActionEvent event) {
        logger.info("Knopf gedrückt: Startseite");
        lblBriefText.setText("Startseite");
        loadPane(vpAnchorPane, "views/StartseiteView.fxml");
    }

    private void loadPane(AnchorPane ap, String resource){
        logger.info("Loading '"+resource+"' into AnchorPane");
        try {
            Node node;

            logger.debug("Retrieving resource: '"+resource+"'");
            node = (Node) FXMLLoader.load(Objects.requireNonNull(Main.class.getResource(resource)));

            logger.debug("Setting anchors...");
            ap.setLeftAnchor(node, 0.0);
            ap.setRightAnchor(node, 0.0);
            ap.setTopAnchor(node, 0.0);
            ap.setBottomAnchor(node, 0.0);

            ap.getChildren().setAll(node);
        }catch(Exception e){
            logger.error("Failed to load '"+resource+"' into AnchorPane");
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }
}