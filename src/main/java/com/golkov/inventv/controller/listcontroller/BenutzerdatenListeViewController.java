package com.golkov.inventv.controller.listcontroller;

import com.golkov.inventv.model.daos.BenutzerDAO;
import com.golkov.inventv.model.entities.BenutzerEntity;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Supplier;

public class BenutzerdatenListeViewController extends ListeViewControllerBase<BenutzerEntity> implements Initializable{

    private static final Logger logger = LogManager.getLogger(BenutzerdatenListeViewController.class);
    BenutzerDAO b_dao = new BenutzerDAO();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.info("Initializing BenutzerdatenListeViewController...");
        tcBenutzerID.setCellValueFactory(new PropertyValueFactory<>("ID"));
        tcBenutzerKennung.setCellValueFactory(new PropertyValueFactory<>("kennung"));
        tcBenutzerNachname.setCellValueFactory(new PropertyValueFactory<>("nachname"));
        tcBenutzerVorname.setCellValueFactory(new PropertyValueFactory<>("vorname"));
        lstBenutzerEntities.setItems(foundEntities); //TODO: Tabelle aktualisiert sich nicht. Problem lösen!
        logger.info("Initialization complete");
    }

    @FXML
    private Button btnNewUser;

    @FXML
    private Button btnSearchBenutzer;

    @FXML
    private Label lblFoundBenutzerEntities;

    @FXML
    private TableView<BenutzerEntity> lstBenutzerEntities;

    @FXML
    private TableColumn<BenutzerEntity, ?> tcBenutzerAktion;

    @FXML
    private TableColumn<BenutzerEntity, Integer> tcBenutzerID;

    @FXML
    private TableColumn<BenutzerEntity, String> tcBenutzerKennung;

    @FXML
    private TableColumn<BenutzerEntity, String> tcBenutzerNachname;

    @FXML
    private TableColumn<BenutzerEntity, Boolean> tcBenutzerOffeneAusleihen;

    @FXML
    private TableColumn<BenutzerEntity, String> tcBenutzerVorname;

    @FXML
    private TextField txtBenutzerID;

    @FXML
    private TextField txtBenutzerKennung;

    @FXML
    private TextField txtBenutzerNachname;

    @FXML
    private TextField txtBenutzerVorname;

    @FXML
    void sucheStartenButtonTapped(ActionEvent event) {
        logger.info("Search initiated...");
        setFoundEntities(b_dao.getAllEntities());
        logger.info("Search finished!");
    }

}
