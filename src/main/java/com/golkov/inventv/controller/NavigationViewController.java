package com.golkov.inventv.controller;

import com.golkov.inventv.Globals;
import com.golkov.inventv.InventVPreferences;
import com.golkov.inventv.Main;
import com.golkov.inventv.ViewNavigation;
import com.golkov.inventv.model.daos.BenutzerDAO;
import com.golkov.inventv.model.entities.BenutzerEntity;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
import java.util.Optional;
import java.util.ResourceBundle;

import static com.golkov.inventv.InventVPreferences.*;


public class NavigationViewController implements Initializable {

    private static final Logger logger = LogManager.getLogger(NavigationViewController.class);

    private static NavigationViewController instance;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.info("Initializing NavigationViewController...");
        lblBriefText.setText("Startseite");
        lblConnectionAdress.setText(getShortServerUrl());
        lblUsername.setText(InventVPreferences.getUsername());
        Globals.loadFreshPane(vpAnchorPane, "views/StartseiteView.fxml", 0);
        btnStartseite.setStyle("-fx-font-weight: bold;");
        instance = this;
    }

    public static NavigationViewController getInstance() {
        return instance;
    }

    @FXML
    private Button btnTypAblageortverwaltung;

    @FXML
    private Button btnAusleihenverwaltung;

    @FXML
    private Button btnBenutzerverwaltung;

    @FXML
    private Button btnInfo;

    @FXML
    private Button btnChangeConnection;

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

    public AnchorPane getAnchorPane() {
        return vpAnchorPane;
    }

    @FXML
    void changeConnectionButtonTapped(ActionEvent event){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Verbindung ändern");
        alert.setHeaderText("Wollen Sie die aktuelle Verbindung trennen?");
        alert.setContentText("Das Programm wird neu gestartet und Sie gelangen in das Verbindungsmenü.");

        ButtonType jaButton = new ButtonType("Ja");
        ButtonType neinButton = new ButtonType("Nein");

        alert.getButtonTypes().setAll(jaButton, neinButton);

        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == jaButton) {
                alert.close();
                saveServerCredentials("", "", "", "", false);
                Main.restartApplication();
            } else if (buttonType == neinButton) {
                alert.close();
            }
        });
    }

    @FXML
    void typablageortverwaltungButtonTapped(ActionEvent event) {
        logger.info("Knopf gedrückt: Typ/Ablageortverwaltung");
        resetAllButtonFonts();
        btnTypAblageortverwaltung.setStyle("-fx-font-weight: bold;");
        lblBriefText.setText("Verwaltung: Typen und Ablageorte");
        if (ViewNavigation.getSize(4) == 0)
            Globals.loadFreshPane(vpAnchorPane, "views/TypAblageortListeView.fxml", 4);
        else
            Globals.loadExistingPane(vpAnchorPane, 4);
    }

    @FXML
    void ausleihenverwaltungButtonTapped(ActionEvent event) throws IOException {
        logger.info("Knopf gedrückt: Ausleihenverwaltung");
        resetAllButtonFonts();
        btnAusleihenverwaltung.setStyle("-fx-font-weight: bold;");
        lblBriefText.setText("Verwaltung: Ausleihen");
        if (ViewNavigation.getSize(3) == 0)
            Globals.loadFreshPane(vpAnchorPane, "views/AusleihenListeView.fxml", 3);
        else
            Globals.loadExistingPane(vpAnchorPane, 3);
    }



    @FXML
    void benutzerverwaltungButtonTapped(ActionEvent event) throws IOException {
        logger.info("Knopf gedrückt: Benutzerverwaltung");
        resetAllButtonFonts();
        btnBenutzerverwaltung.setStyle("-fx-font-weight: bold;");
        lblBriefText.setText("Verwaltung: Benutzer");
        if (ViewNavigation.getSize(1) == 0)
            Globals.loadFreshPane(vpAnchorPane, "views/BenutzerdatenListeView.fxml", 1);
        else
            Globals.loadExistingPane(vpAnchorPane, 1);
    }

    @FXML
    void infoButtonTapped(ActionEvent event) {
        logger.info("Knopf gedrückt: Info");
        resetAllButtonFonts();
        btnInfo.setStyle("-fx-font-weight: bold;");
        lblBriefText.setText("Über das Programm");
        //TODO: InfoView.fxml erstellen und Code ergänzen
    }

    @FXML
    void objektverwaltungButtonTapped(ActionEvent event) {
        logger.info("Knopf gedrückt: Objektverwaltung");
        resetAllButtonFonts();
        btnObjektverwaltung.setStyle("-fx-font-weight: bold;");
        lblBriefText.setText("Verwaltung: Objekte");
        if (ViewNavigation.getSize(2) == 0)
            Globals.loadFreshPane(vpAnchorPane, "views/ObjektdatenListeView.fxml", 2);
        else
            Globals.loadExistingPane(vpAnchorPane, 2);
    }

    @FXML
    void startseiteButtonTapped(ActionEvent event) {
        logger.info("Knopf gedrückt: Startseite");
        resetAllButtonFonts();
        btnStartseite.setStyle("-fx-font-weight: bold;");
        lblBriefText.setText("Startseite");
        if (ViewNavigation.getSize(0) == 0)
            Globals.loadFreshPane(vpAnchorPane, "views/StartseiteView.fxml", 0);
        else
            Globals.loadExistingPane(vpAnchorPane, 0);
    }

    private void resetAllButtonFonts(){
        btnBenutzerverwaltung.setStyle("-fx-font-weight: normal;");
        btnAusleihenverwaltung.setStyle("-fx-font-weight: normal;");
        btnObjektverwaltung.setStyle("-fx-font-weight: normal;");
        btnInfo.setStyle("-fx-font-weight: normal;");
        btnStartseite.setStyle("-fx-font-weight: normal;");
        btnTypAblageortverwaltung.setStyle("-fx-font-weight: normal;");
    }
}