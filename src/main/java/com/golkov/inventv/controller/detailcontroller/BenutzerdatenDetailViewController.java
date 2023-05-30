package com.golkov.inventv.controller.detailcontroller;

import com.golkov.inventv.ViewNavigation;
import com.golkov.inventv.controller.DataObserver;
import com.golkov.inventv.controller.NavigationViewController;
import com.golkov.inventv.controller.listcontroller.BenutzerdatenListeViewController;
import com.golkov.inventv.model.daos.AusleiheDAO;
import com.golkov.inventv.model.daos.BenutzerDAO;
import com.golkov.inventv.model.entities.AusleihEntity;
import com.golkov.inventv.model.entities.BenutzerEntity;
import com.golkov.inventv.model.entities.ObjektEntity;
import com.golkov.inventv.model.entities.TypEntity;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class BenutzerdatenDetailViewController extends DetailViewControllerBase<BenutzerEntity> {

    public BenutzerdatenDetailViewController(BenutzerEntity benutzer) {
        setUnchangedEntity(benutzer);
    }

    public BenutzerdatenDetailViewController() {
        setUnchangedEntity(null);
    }

    public void initialize() {
        btnSaveBenutzer.disableProperty().bind(saveButtonDisabled);

        if (UnchangedEntity != null) {

            txtBenutzerID.setText(Integer.toString(UnchangedEntity.getID()));
            txtBenutzerKennung.setText(UnchangedEntity.getKennung());
            txtBenutzerVorname.setText(UnchangedEntity.getVorname());
            txtBenutzerNachname.setText(UnchangedEntity.getNachname());
            txtBenutzerTelefon.setText(UnchangedEntity.getTelefon());
            txtBenutzerEmail.setText(UnchangedEntity.getEmail());

            checkAdministrator.setSelected(UnchangedEntity.isAdministrator());
            checkAdministrator.setDisable(true);

            AusleiheDAO a_dao = new AusleiheDAO();
            ObservableList<AusleihEntity> benutzerAusleihen = a_dao.getAusleihenByBenutzer(UnchangedEntity);
            lstOffeneBenutzerAusleihen.setItems(benutzerAusleihen);

            lblCountOffeneAusleihen.setText(Integer.toString(benutzerAusleihen.size()));

            tcAusleiheObjektInventarnummer.setCellValueFactory(new PropertyValueFactory<>("objekt"));

            //Algorithmus zum Umwandeln eines TypEntity-Ergebnisses in die Anzeige der Bezeichnung des Typs
            tcAusleiheObjektInventarnummer.setCellFactory(column -> new TableCell<>() {
                @Override
                protected void updateItem(ObjektEntity objektEntity, boolean empty) {
                    super.updateItem(objektEntity, empty);
                    if (objektEntity == null || empty) {
                        setText(null);
                    } else {
                        setText(Integer.toString(objektEntity.getInventarnummer()));
                    }
                }
            });
            tcAusleiheObjektTyp.setCellValueFactory(new PropertyValueFactory<>("objekt"));
            tcAusleiheObjektTyp.setCellFactory(column -> new TableCell<>() {
                @Override
                protected void updateItem(ObjektEntity objektEntity, boolean empty) {
                    super.updateItem(objektEntity, empty);
                    if (objektEntity == null || empty) {
                        setText(null);
                    } else {
                        setText(objektEntity.getTyp().getBezeichnung());
                    }
                }
            });

            tcAusleiheObjektHersteller.setCellValueFactory(new PropertyValueFactory<>("objekt"));
            tcAusleiheObjektHersteller.setCellFactory(column -> new TableCell<>() {
                @Override
                protected void updateItem(ObjektEntity objektEntity, boolean empty) {
                    super.updateItem(objektEntity, empty);
                    if (objektEntity == null || empty) {
                        setText(null);
                    } else {
                        setText(objektEntity.getHersteller());
                    }
                }
            });

            tcAusleiheObjektModell.setCellValueFactory(new PropertyValueFactory<>("objekt"));
            tcAusleiheObjektModell.setCellFactory(column -> new TableCell<>() {
                @Override
                protected void updateItem(ObjektEntity objektEntity, boolean empty) {
                    super.updateItem(objektEntity, empty);
                    if (objektEntity == null || empty) {
                        setText(null);
                    } else {
                        setText(objektEntity.getModell());
                    }
                }
            });

            txAusleiheObjektAusgeliehenAm.setCellValueFactory(new PropertyValueFactory<>("ausleihdatum"));
            txAusleiheObjektAusgeliehenAm.setCellFactory(column -> new TableCell<>() {
                @Override
                protected void updateItem(LocalDate localDate, boolean empty) {
                    super.updateItem(localDate, empty);
                    if (localDate == null || empty) {
                        setText(null);
                    } else {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                        setText(localDate.format(formatter));
                    }
                }
            });

            //Algorithmus zum Deaktivieren des "Speichern"-Knopfes wenn UnchangedEntity = CurrentEntity
            BooleanBinding saveButtonDisabled2 = new BooleanBinding() {
                {
                    super.bind(
                            txtBenutzerKennung.textProperty(),
                            txtBenutzerVorname.textProperty(),
                            txtBenutzerNachname.textProperty(),
                            txtBenutzerTelefon.textProperty(),
                            txtBenutzerEmail.textProperty()
                    );
                }

                @Override
                protected boolean computeValue() {
                    return txtBenutzerKennung.getText().equals(UnchangedEntity.getKennung())
                            && txtBenutzerVorname.getText().equals(UnchangedEntity.getVorname())
                            && txtBenutzerNachname.getText().equals(UnchangedEntity.getNachname())
                            && txtBenutzerTelefon.getText().equals(UnchangedEntity.getTelefon())
                            && txtBenutzerEmail.getText().equals(UnchangedEntity.getEmail());
                }
            };
            saveButtonDisabled.bind(saveButtonDisabled2);

        } else {
            saveButtonDisabled.bind(txtBenutzerKennung.textProperty().isEmpty()
                    .or(txtBenutzerNachname.textProperty().isEmpty())
                    .or(txtBenutzerVorname.textProperty().isEmpty())
            );
        }
    }

    private DataObserver dataObserver;

    public void setObserver(DataObserver observer) {
        this.dataObserver = observer;
    }

    private BooleanProperty saveButtonDisabled = new SimpleBooleanProperty(true);

    @FXML
    private Button btnSaveBenutzer;

    @FXML
    private Button btnZurueck;

    @FXML
    private CheckBox checkAdministrator;

    @FXML
    private Label lblCountOffeneAusleihen;

    @FXML
    private TableView<AusleihEntity> lstOffeneBenutzerAusleihen;

    @FXML
    private TableColumn<AusleihEntity, ObjektEntity> tcAusleiheObjektHersteller;

    @FXML
    private TableColumn<AusleihEntity, ObjektEntity> tcAusleiheObjektInventarnummer;

    @FXML
    private TableColumn<AusleihEntity, ObjektEntity> tcAusleiheObjektModell;

    @FXML
    private TableColumn<AusleihEntity, ObjektEntity> tcAusleiheObjektTyp;

    @FXML
    private TableColumn<AusleihEntity, LocalDate> txAusleiheObjektAusgeliehenAm;

    @FXML
    private TextField txtBenutzerEmail;

    @FXML
    private TextField txtBenutzerID;

    @FXML
    private TextField txtBenutzerKennung;

    @FXML
    private TextField txtBenutzerNachname;

    @FXML
    private TextField txtBenutzerTelefon;

    @FXML
    private TextField txtBenutzerVorname;

    @FXML
    void speichernButtonTapped(ActionEvent event) {
        //Wenn neuer Benutzer erstellt wird
        BenutzerEntity updatedUser = new BenutzerEntity();
        if(UnchangedEntity != null)
            updatedUser = UnchangedEntity;
        updatedUser.setKennung(txtBenutzerKennung.getText());
        updatedUser.setVorname(txtBenutzerVorname.getText());
        updatedUser.setNachname(txtBenutzerNachname.getText());
        updatedUser.setTelefon(txtBenutzerTelefon.getText());
        updatedUser.setEmail(txtBenutzerEmail.getText());
        updatedUser.setAdministrator(checkAdministrator.isSelected());

        BenutzerDAO b_dao = new BenutzerDAO();
        int error = 2;

        if (UnchangedEntity == null) {
            error = b_dao.insertEntity(updatedUser);
        } else { //Wenn vorhandener Benutzer aktualisiert wird
            error = b_dao.updateEntity(UnchangedEntity, updatedUser);
        }

        if (error == 1) { //TODO: Alerts in separate Klasse auslagern
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Fehler beim Speichern");
            alert.setHeaderText("Kennung existiert bereits");
            alert.setContentText("Bei der Verarbeitung der Benutzerdaten ist ein Fehler aufgetreten: Die Benutzerkennung '" + updatedUser.getKennung() + "' existiert bereits in der Datenbank. Bitte geben Sie eine gültige Benutzerkennung ein.");
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    alert.close();
                }
            });
        } else if (error == 2) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Fehler beim Speichern");
            alert.setHeaderText("Datenbankfehler");
            alert.setContentText("Bei der Verarbeitung der Benutzerdaten ist ein Fehler aufgetreten. Bitte wenden Sie sich an den Administrator.");
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    alert.close();
                }
            });
        } else {
            if (dataObserver != null) {
                dataObserver.updateData(); // Aktualisierung der Daten anfordern
            }
            zurueckButtonTapped(new ActionEvent());
        }
    }

    @FXML
    void zurueckButtonTapped(ActionEvent event) {
        Node node = ViewNavigation.pop(1);
        NavigationViewController navigationController = NavigationViewController.getInstance();
        AnchorPane ap = navigationController.getAnchorPane();
        ap.setLeftAnchor(node, 0.0);
        ap.setRightAnchor(node, 0.0);
        ap.setTopAnchor(node, 0.0);
        ap.setBottomAnchor(node, 0.0);

        ap.getChildren().setAll(node);

    }

}
