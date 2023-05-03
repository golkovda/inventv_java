package com.golkov.inventv.controller.listcontroller;

import com.golkov.inventv.model.daos.BenutzerDAO;
import com.golkov.inventv.model.entities.BenutzerEntity;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Supplier;

public class BenutzerdatenListeViewController extends ListeViewControllerBase<BenutzerEntity> implements Initializable{

    private static final Logger logger = LogManager.getLogger(BenutzerdatenListeViewController.class);
    BenutzerDAO b_dao = new BenutzerDAO();

    public BenutzerdatenListeViewController(){
        super();
        foundEntities = FXCollections.observableArrayList();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.info("Initializing BenutzerdatenListeViewController...");
        tcBenutzerID.setCellValueFactory(new PropertyValueFactory<>("ID"));
        tcBenutzerKennung.setCellValueFactory(new PropertyValueFactory<>("kennung"));
        tcBenutzerNachname.setCellValueFactory(new PropertyValueFactory<>("nachname"));
        tcBenutzerVorname.setCellValueFactory(new PropertyValueFactory<>("vorname"));
        lstBenutzerEntities.setItems(foundEntities);

        btnSearchBenutzer.disableProperty().bind(searchButtonDisabled);

        searchButtonDisabled.bind(txtBenutzerKennung.textProperty().isEmpty()
                .and(txtBenutzerNachname.textProperty().isEmpty())
                .and(txtBenutzerVorname.textProperty().isEmpty())
                .and(txtBenutzerID.textProperty().isEmpty())
                );

        txtBenutzerID.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (!"0123456789".contains(event.getCharacter())) {
                event.consume();
            }
        });

        logger.info("Initialization complete");
    }

    private BooleanProperty searchButtonDisabled = new SimpleBooleanProperty(true);


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
        int searchID = 0;
        if(!txtBenutzerID.getText().equals(""))
            searchID = Integer.parseInt(txtBenutzerID.getText());
        foundEntities.setAll(b_dao.filterBenutzer(searchID, txtBenutzerKennung.getText(), txtBenutzerVorname.getText(), txtBenutzerNachname.getText()));
        lblFoundBenutzerEntities.setText(String.valueOf(foundEntities.size()));
        logger.info("Search finished!");
    }

}
