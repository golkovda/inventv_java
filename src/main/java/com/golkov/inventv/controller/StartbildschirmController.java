package com.golkov.inventv.controller;

import com.golkov.inventv.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Node;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class StartbildschirmController {

    private static final Logger logger = LogManager.getLogger(StartbildschirmController.class);
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
        //TODO: AusleihendatenListeView.fxml erstellen und Code ergänzen
    }

    @FXML
    void benutzerverwaltungButtonTapped(ActionEvent event) throws IOException {
        loadPane(vpAnchorPane, "views/BenutzerdatenListeView.fxml");
    }

    @FXML
    void infoButtonTapped(ActionEvent event) {

    }

    @FXML
    void objektverwaltungButtonTapped(ActionEvent event) {

    }

    @FXML
    void startseiteButtonTapped(ActionEvent event) {

    }

    private void loadPane(AnchorPane ap, String resource){
        logger.info("Loading '"+resource+"' into AnchorPane");
        try {
            Node node;

            logger.debug("Retrieving resource: '"+resource+"'");
            node = (Node) FXMLLoader.load(Main.class.getResource(resource));

            logger.debug("Setting anchors...");
            ap.setLeftAnchor(node, 0.0);
            ap.setRightAnchor(node, 0.0);
            ap.setTopAnchor(node, 0.0);
            ap.setBottomAnchor(node, 0.0);

            ap.getChildren().setAll(node);
        }catch(Exception e){
            logger.error("Failed to load '"+resource+"' into AnchorPane");
            logger.error(e.getMessage());
        }
    }


}