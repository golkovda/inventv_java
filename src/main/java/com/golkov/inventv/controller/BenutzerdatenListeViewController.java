package com.golkov.inventv.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ResourceBundle;

public class BenutzerdatenListeViewController implements Initializable{

    private static final Logger logger = LogManager.getLogger(BenutzerdatenListeViewController.class);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.info("Initializing BenutzerdatenListeViewController...");
    }

    @FXML
    private Button btnNewUser;

    @FXML
    private Button btnSearchBenutzer;

    @FXML
    private Label lblFoundBenutzerEntities;

    @FXML
    private TableView<?> lstBenutzerEntities;

    @FXML
    private TextField txtBenutzerID;

    @FXML
    private TextField txtBenutzerKennung;

    @FXML
    private TextField txtBenutzerNachname;

    @FXML
    private TextField txtBenutzerVorname;



}
