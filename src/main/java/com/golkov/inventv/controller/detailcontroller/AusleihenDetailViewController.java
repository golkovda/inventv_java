package com.golkov.inventv.controller.detailcontroller;

import com.golkov.inventv.model.daos.AusleiheDAO;
import com.golkov.inventv.model.daos.BenutzerDAO;
import com.golkov.inventv.model.daos.ObjektDAO;
import com.golkov.inventv.model.entities.AusleihEntity;
import com.golkov.inventv.model.entities.BenutzerEntity;
import com.golkov.inventv.model.entities.ObjektEntity;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AusleihenDetailViewController implements Initializable {

    private Stage stage;

    public AusleihenDetailViewController(Stage stage){
        this.stage = stage;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        btnSaveAusleihe.disableProperty().bind(saveButtonDisabled);

        //BenutzerEntities in Combobox laden
        BenutzerDAO t_dao = new BenutzerDAO();
        ObservableList<BenutzerEntity> benutzerList = t_dao.getAllEntities();
        cbAusleiheBenutzer.setItems(benutzerList);
        //Logik für die ComboBox-Anzeige des benutzers (nur BenutzerEntity.Vorname und .Nachname soll sichtbar sein)
        cbAusleiheBenutzer.setConverter(new StringConverter<BenutzerEntity>() {
            @Override
            public String toString(BenutzerEntity benutzer) {
                // Wandelt das BenutzerEntity in die Bezeichnung um
                return benutzer.getNachname() + ", " + benutzer.getVorname();
            }

            @Override
            public BenutzerEntity fromString(String bezeichnung) {
                // Wandelt die ausgewählte Bezeichnung wieder in das zugehörige BenutzerEntity um
                for (BenutzerEntity benutzer : benutzerList) {
                    String temp = benutzer.getNachname() + ", " + benutzer.getVorname();
                    if (temp.equals(bezeichnung)) {
                        return benutzer;
                    }
                }
                return null;
            }
        });

        //ObjektEntities in Combobox laden
        ObjektDAO o_dao = new ObjektDAO();
        ObservableList<ObjektEntity> ObjektList = o_dao.getAvailableObjektEntities();
        cbAusleiheObjekt.setItems(ObjektList);
        //Logik für die ComboBox-Anzeige des Objekts
        cbAusleiheObjekt.setConverter(new StringConverter<>() {
            @Override
            public String toString(ObjektEntity Objekt) {
                // Wandelt das TypEntity in die Bezeichnung um
                return "[" + Objekt.getInventarnummer() + "] (" + Objekt.getTyp().getBezeichnung() + ") " + Objekt.getHersteller() + " " + Objekt.getModell();
            }

            @Override
            public ObjektEntity fromString(String bezeichnung) {
                // Wandelt die ausgewählte Bezeichnung wieder in das zugehörige TypEntity um
                for (ObjektEntity Objekt : ObjektList) {
                    String temp = "[" + Objekt.getInventarnummer() + "] (" + Objekt.getTyp().getBezeichnung() + ") " + Objekt.getHersteller() + " " + Objekt.getModell();
                    if (temp.equals(bezeichnung)) {
                        return Objekt;
                    }
                }
                return null;
            }
        });

        saveButtonDisabled.bind(
                cbAusleiheBenutzer.getSelectionModel().selectedItemProperty().isNull().or(
                        cbAusleiheObjekt.valueProperty().isNull())
        );
    }

    private BooleanProperty saveButtonDisabled = new SimpleBooleanProperty(true);

    @FXML
    private Button btnSaveAusleihe;

    @FXML
    private ComboBox<BenutzerEntity> cbAusleiheBenutzer;

    @FXML
    private ComboBox<ObjektEntity> cbAusleiheObjekt;

    @FXML
    void speichernButtonTapped(ActionEvent event) {
        AusleihEntity aentity = new AusleihEntity();
        aentity.setObjekt(cbAusleiheObjekt.getSelectionModel().getSelectedItem());
        aentity.setBenutzer(cbAusleiheBenutzer.getSelectionModel().getSelectedItem());
        aentity.setAbgegeben(false);
        aentity.setAusleihdatum(LocalDate.now());

        int error = 2;

        AusleiheDAO adao = new AusleiheDAO();
        error = adao.insertEntity(aentity);

        if (error == 2) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Fehler beim Speichern");
            alert.setHeaderText("Datenbankfehler");
            alert.setContentText("Bei der Verarbeitung der Objektdaten ist ein Fehler aufgetreten. Bitte wenden Sie sich an den Administrator.");
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    alert.close();
                }
            });
        } else {
            stage.close();
        }
    }
}
