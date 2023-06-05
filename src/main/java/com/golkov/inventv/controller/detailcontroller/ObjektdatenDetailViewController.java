package com.golkov.inventv.controller.detailcontroller;

import com.golkov.inventv.ViewNavigation;
import com.golkov.inventv.controller.DataObserver;
import com.golkov.inventv.controller.NavigationViewController;
import com.golkov.inventv.model.daos.*;
import com.golkov.inventv.model.entities.*;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ObjektdatenDetailViewController extends DetailViewControllerBase<ObjektEntity> {

    public ObjektdatenDetailViewController(ObjektEntity objekt) {
        setUnchangedEntity(objekt);
    }

    public ObjektdatenDetailViewController() {
        setUnchangedEntity(null);
    }

    public void initialize() {
        btnSaveObjekt.disableProperty().bind(saveButtonDisabled);

        if (UnchangedEntity != null) {

            txtInventarnummer.setDisable(true);

            txtInventarnummer.setText(String.valueOf(UnchangedEntity.getInventarnummer()));
            txtHersteller.setText(UnchangedEntity.getHersteller());
            txtModell.setText(UnchangedEntity.getModell());
            txtEinzelpreis.setText(String.valueOf(UnchangedEntity.getEinzelpreis()));
            cbTyp.setValue(UnchangedEntity.getTyp());
            cbAblageort.setValue(UnchangedEntity.getAblageort());
            cbKaufdatum.setValue(UnchangedEntity.getKaufdatum());

            AusleiheDAO a_dao = new AusleiheDAO();
            ObservableList<AusleihEntity> objektAusleihen = a_dao.getAusleihenByObjekt(UnchangedEntity);
            lstAusleihhistorie.setItems(objektAusleihen);

            tcAusleihenBenutzerkennung.setCellValueFactory(new PropertyValueFactory<>("benutzer"));
            //Algorithmus zum Umwandeln eines AusleihEntity-Ergebnisses in Textform
            tcAusleihenBenutzerkennung.setCellFactory(column -> new TableCell<>() {
                @Override
                protected void updateItem(BenutzerEntity benutzerEntity, boolean empty) {
                    super.updateItem(benutzerEntity, empty);
                    if (benutzerEntity == null || empty) {
                        setText(null);
                    } else {
                        setText(benutzerEntity.getKennung());
                    }
                }
            });

            tcAusleihenName.setCellValueFactory(new PropertyValueFactory<>("benutzer"));
            //Algorithmus zum Umwandeln eines AusleihEntity-Ergebnisses in Textform
            tcAusleihenName.setCellFactory(column -> new TableCell<>() {
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

            tcAusleihenAusleihdatum.setCellValueFactory(new PropertyValueFactory<>("ausleihdatum"));
            tcAusleihenAusleihdatum.setCellFactory(column -> new TableCell<>() {
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

            //Logik zum Umwandeln von true/false in ja/nein
            tcAusleihenAbgegeben.setCellFactory(column -> new TableCell<>() {
                @Override
                protected void updateItem(Boolean ausgeliehen, boolean empty) {
                    super.updateItem(ausgeliehen, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText(ausgeliehen ? "Ja" : "Nein");
                    }
                }
            });

            //Logik zur Bestimmung von Ausleihen des Objekts
            tcAusleihenAbgegeben.setCellValueFactory(cellData -> {
                AusleihEntity ausleihe = cellData.getValue();
                return new SimpleBooleanProperty(ausleihe.isAbgegeben());
            });

            //Algorithmus zum Deaktivieren des "Speichern"-Knopfes wenn UnchangedEntity = CurrentEntity
            BooleanBinding saveButtonDisabled2 = new BooleanBinding() {
                {
                    super.bind(
                            txtInventarnummer.textProperty(),
                            txtHersteller.textProperty(),
                            txtModell.textProperty(),
                            txtEinzelpreis.textProperty(),
                            cbTyp.getSelectionModel().selectedItemProperty(),
                            cbAblageort.getSelectionModel().selectedItemProperty(),
                            cbKaufdatum.valueProperty()
                    );
                }

                @Override
                protected boolean computeValue() {
                    return txtInventarnummer.getText().equals(String.valueOf(UnchangedEntity.getInventarnummer()))
                            && txtHersteller.getText().equals(UnchangedEntity.getHersteller())
                            && txtModell.getText().equals(UnchangedEntity.getModell())
                            && txtEinzelpreis.getText().equals(String.valueOf(UnchangedEntity.getEinzelpreis()))
                            && cbTyp.getValue().equals(UnchangedEntity.getTyp())
                            && cbAblageort.getValue().equals(UnchangedEntity.getAblageort())
                            && cbKaufdatum.getValue().equals(UnchangedEntity.getKaufdatum());
                }
            };
            saveButtonDisabled.bind(saveButtonDisabled2);
        } else {
            saveButtonDisabled.bind(txtInventarnummer.textProperty().isEmpty()
                    .or(txtHersteller.textProperty().isEmpty())
                    .or(txtModell.textProperty().isEmpty())
                    .or(txtEinzelpreis.textProperty().isEmpty())
                    .or(cbTyp.getSelectionModel().selectedItemProperty().isNull())
                    .or(cbAblageort.getSelectionModel().selectedItemProperty().isNull())
                    .or(cbKaufdatum.valueProperty().isNull())
            );
        }

        //TypEntities in Combobox laden
        TypDAO t_dao = new TypDAO();
        ObservableList<TypEntity> typList = t_dao.getAllEntities();
        cbTyp.setItems(typList);
        //Logik für die ComboBox-Anzeige des Typs (nur TypEntity.Bezeichnung soll sichtbar sein)
        cbTyp.setConverter(new StringConverter<TypEntity>() {
            @Override
            public String toString(TypEntity typ) {
                // Wandelt das TypEntity in die Bezeichnung um
                return typ.getBezeichnung();
            }

            @Override
            public TypEntity fromString(String bezeichnung) {
                // Wandelt die ausgewählte Bezeichnung wieder in das zugehörige TypEntity um
                for (TypEntity typ : typList) {
                    if (typ.getBezeichnung().equals(bezeichnung)) {
                        return typ;
                    }
                }
                return null;
            }
        });

        //AblageortEntities in Combobox laden
        AblageortDAO ao_dao = new AblageortDAO();
        ObservableList<AblageortEntity> ablageortList = ao_dao.getAllEntities();
        cbAblageort.setItems(ablageortList);
        //Logik für die ComboBox-Anzeige des Ablageorts (nur AblageortEntity.Bezeichnung soll sichtbar sein)
        cbAblageort.setConverter(new StringConverter<AblageortEntity>() {
            @Override
            public String toString(AblageortEntity ablageort) {
                // Wandelt das TypEntity in die Bezeichnung um
                return ablageort.getBezeichnung();
            }

            @Override
            public AblageortEntity fromString(String bezeichnung) {
                // Wandelt die ausgewählte Bezeichnung wieder in das zugehörige TypEntity um
                for (AblageortEntity ablageort : ablageortList) {
                    if (ablageort.getBezeichnung().equals(bezeichnung)) {
                        return ablageort;
                    }
                }
                return null;
            }
        });

        //Algorithmus zum Verhindern von nicht-numerischen Eingaben in diversen numerischen Feldern
        txtInventarnummer.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (!"0123456789".contains(event.getCharacter())) {
                event.consume();
            }
        });
        txtEinzelpreis.addEventFilter(KeyEvent.KEY_TYPED, event -> { //TODO: Nach dem Punkt nur zwei Stellen erlauben
            if (!".0123456789".contains(event.getCharacter())) {
                event.consume();
            }
        });
    }

    private BooleanProperty saveButtonDisabled = new SimpleBooleanProperty(true);

    @FXML
    private Button btnSaveObjekt;

    @FXML
    private Button btnZurueck;

    @FXML
    private ComboBox<AblageortEntity> cbAblageort;

    @FXML
    private DatePicker cbKaufdatum;

    @FXML
    private ComboBox<TypEntity> cbTyp;

    @FXML
    private TableView<AusleihEntity> lstAusleihhistorie;

    @FXML
    private TableColumn<AusleihEntity, Boolean> tcAusleihenAbgegeben;

    @FXML
    private TableColumn<AusleihEntity, LocalDate> tcAusleihenAusleihdatum;

    @FXML
    private TableColumn<AusleihEntity, BenutzerEntity> tcAusleihenBenutzerkennung;

    @FXML
    private TableColumn<AusleihEntity, BenutzerEntity> tcAusleihenName;

    @FXML
    private TextField txtEinzelpreis;

    @FXML
    private TextField txtHersteller;

    @FXML
    private TextField txtInventarnummer;

    @FXML
    private TextField txtModell;

    private DataObserver dataObserver;

    public void setObserver(DataObserver observer) {
        this.dataObserver = observer;
    }


    @FXML
    void speichernButtonTapped(ActionEvent event) {
        //Wenn neues Objekt erstellt wird
        ObjektEntity updatedObjekt = new ObjektEntity();
        if (UnchangedEntity != null)
            updatedObjekt = UnchangedEntity;
        updatedObjekt.setInventarnummer(Integer.parseInt(txtInventarnummer.getText()));
        updatedObjekt.setAblageort(cbAblageort.getSelectionModel().getSelectedItem());
        updatedObjekt.setTyp(cbTyp.getSelectionModel().getSelectedItem());
        updatedObjekt.setHersteller(txtHersteller.getText());
        updatedObjekt.setModell(txtModell.getText());
        updatedObjekt.setKaufdatum(cbKaufdatum.getValue());
        updatedObjekt.setEinzelpreis(Float.parseFloat(txtEinzelpreis.getText()));

        ObjektDAO o_dao = new ObjektDAO();
        int error = 2;

        if (UnchangedEntity == null) {
            error = o_dao.insertEntity(updatedObjekt);
        } else { //Wenn vorhandener Benutzer aktualisiert wird
            error = o_dao.updateEntity(UnchangedEntity, updatedObjekt);
        }

        if (error == 1) { //TODO: Alerts in separate Klasse auslagern
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Fehler beim Speichern");
            alert.setHeaderText("Inventarnummer existiert bereits");
            alert.setContentText("Bei der Verarbeitung der Objektdaten ist ein Fehler aufgetreten: Die Inventarnummer '" + updatedObjekt.getInventarnummer() + "' existiert bereits in der Datenbank. Bitte geben Sie eine gültige Inventarnummer ein.");
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    alert.close();
                }
            });
        } else if (error == 2) {
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
            if (dataObserver != null) {
                dataObserver.updateData(); // Aktualisierung der Daten anfordern
            }
            zurueckButtonTapped(new ActionEvent());
        }
    }

    @FXML
    void zurueckButtonTapped(ActionEvent event) {
        Node node = ViewNavigation.pop(2);
        NavigationViewController navigationController = NavigationViewController.getInstance();
        AnchorPane ap = navigationController.getAnchorPane();
        ap.setLeftAnchor(node, 0.0);
        ap.setRightAnchor(node, 0.0);
        ap.setTopAnchor(node, 0.0);
        ap.setBottomAnchor(node, 0.0);

        ap.getChildren().setAll(node);

    }

}
