package com.golkov.inventv.controller.listcontroller;

import com.golkov.inventv.model.entities.BenutzerEntity;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Supplier;

public class BenutzerdatenListeViewController extends ListeViewControllerBase<BenutzerEntity> implements Initializable{

    private static final Logger logger = LogManager.getLogger(BenutzerdatenListeViewController.class);

    BenutzerdatenListeViewController(Supplier<? extends BenutzerEntity> ctor) {
        super(ctor);
    }

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
