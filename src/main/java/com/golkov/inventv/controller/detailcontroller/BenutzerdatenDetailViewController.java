package com.golkov.inventv.controller.detailcontroller;

import com.golkov.inventv.ViewNavigation;
import com.golkov.inventv.controller.NavigationViewController;
import com.golkov.inventv.controller.listcontroller.BenutzerdatenListeViewController;
import com.golkov.inventv.model.daos.AusleiheDAO;
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
        if (UnchangedEntity != null) {
            txtBenutzerID.setText(Integer.toString(UnchangedEntity.getID()));
            txtBenutzerKennung.setText(UnchangedEntity.getKennung());
            txtBenutzerVorname.setText(UnchangedEntity.getVorname());
            txtBenutzerNachname.setText(UnchangedEntity.getNachname());
            if (Objects.equals(UnchangedEntity.getTelefon(), ""))
                txtBenutzerTelefon.setText("Nicht angegeben");
            else
                txtBenutzerTelefon.setText(UnchangedEntity.getTelefon());

            if (Objects.equals(UnchangedEntity.getEmail(), ""))
                txtBenutzerEmail.setText("Nicht angegeben");
            else
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
            BooleanBinding saveButtonDisabled = new BooleanBinding() {
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
            btnSaveBenutzer.disableProperty().bind(saveButtonDisabled);
           /* saveButtonDisabled.bind(txtBenutzerKennung.textProperty().isEqualTo(UnchangedEntity.getKennung())
                    .and(txtBenutzerNachname.textProperty().isEqualTo(UnchangedEntity.getVorname()))
                    .and(txtBenutzerVorname.textProperty().isEqualTo(UnchangedEntity.getNachname()))
                    .and(txtBenutzerTelefon.textProperty().isEqualTo(UnchangedEntity.getTelefon()))
                    .and(txtBenutzerEmail.textProperty().isEqualTo(UnchangedEntity.getEmail()))
            );*/


        } else {
            //Algorithmus zum Deaktivieren des "Speichern"-Knopfes wenn Pflichtfelder nicht ausgefüllt sind
            btnSaveBenutzer.disableProperty().bind(saveButtonDisabled);
            saveButtonDisabled.bind(txtBenutzerKennung.textProperty().isEmpty()
                    .and(txtBenutzerNachname.textProperty().isEmpty())
                    .and(txtBenutzerVorname.textProperty().isEmpty())
            );
        }
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
