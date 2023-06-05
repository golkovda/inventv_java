package com.golkov.inventv.controller;

import com.golkov.inventv.AlertTexts;
import com.golkov.inventv.Globals;
import com.golkov.inventv.Main;
import com.golkov.inventv.model.daos.*;
import com.golkov.inventv.model.entities.AusleihEntity;
import com.golkov.inventv.model.entities.BenutzerEntity;
import com.golkov.inventv.model.entities.ObjektEntity;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.ResourceBundle;
public class StartseiteViewController implements Initializable{

    private static final Logger logger = LogManager.getLogger(StartseiteViewController.class);
    private BenutzerDAO b_dao = new BenutzerDAO();
    private ObjektDAO o_dao = new ObjektDAO();
    private AusleiheDAO a_dao = new AusleiheDAO();
    private TypDAO t_dao = new TypDAO();
    private AblageortDAO ao_dao = new AblageortDAO();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.info("Initializing StartseiteViewController...");
        tcAusleiheID.setCellValueFactory(new PropertyValueFactory<>("ID"));
        tcAusleihdatum.setCellValueFactory(new PropertyValueFactory<>("ausleihdatum"));

        tcAusleiheObjekt.setCellValueFactory(new PropertyValueFactory<>("objekt"));
        //Algorithmus zum Umwandeln eines ObjektEntity-Ergebnisses in die Anzeige der Bezeichnung des Typs
        tcAusleiheObjekt.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(ObjektEntity objektEntity, boolean empty) {
                super.updateItem(objektEntity, empty);
                if (objektEntity == null || empty) {
                    setText(null);
                } else {
                    setText(objektEntity.getHersteller() + " " + objektEntity.getModell());
                }
            }
        });

        tcAusleiheBenutzer.setCellValueFactory(new PropertyValueFactory<>("benutzer"));
        //Algorithmus zum Umwandeln eines BenutzerEntity-Ergebnisses in die Anzeige der Bezeichnung des Typs
        tcAusleiheBenutzer.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(BenutzerEntity benutzerEntity, boolean empty) {
                super.updateItem(benutzerEntity, empty);
                if (benutzerEntity == null || empty) {
                    setText(null);
                } else {
                    setText(benutzerEntity.getNachname() + ", " + benutzerEntity.getVorname());
                }
            }
        });

        ObservableList<AusleihEntity> ausleihenList = a_dao.getOffeneAusleihen();
        lstAusleiheEntities.setItems(ausleihenList);
        lblCountAusleihen.setText(Integer.toString(ausleihenList.size()));
        lblCountLaufendeAusleihen.setText(Integer.toString(ausleihenList.size()));

        lblCountBenutzer.setText(Long.toString(b_dao.getEntityCount()));
        lblCountObjekte.setText(Long.toString(o_dao.getEntityCount()));
        lblCountTypen.setText(Long.toString(t_dao.getEntityCount()));
        lblCountAblageorte.setText(Long.toString(ao_dao.getEntityCount()));

    }

    @FXML
    private Label lblCountAblageorte;

    @FXML
    private Label lblCountAusleihen;

    @FXML
    private Label lblCountBenutzer;

    @FXML
    private Label lblCountLaufendeAusleihen;

    @FXML
    private Label lblCountObjekte;

    @FXML
    private Label lblCountTypen;

    @FXML
    private Label lblTodaysDate;

    @FXML
    private TableView<AusleihEntity> lstAusleiheEntities;

    @FXML
    private TableColumn<AusleihEntity, LocalDate> tcAusleihdatum;

    @FXML
    private TableColumn<AusleihEntity, BenutzerEntity> tcAusleiheBenutzer;

    @FXML
    private TableColumn<AusleihEntity, Integer> tcAusleiheID;

    @FXML
    private TableColumn<AusleihEntity, ObjektEntity> tcAusleiheObjekt;

}
