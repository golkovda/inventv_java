package com.golkov.inventv.controller.listcontroller;

import com.golkov.inventv.AlertTexts;
import com.golkov.inventv.Globals;
import com.golkov.inventv.Main;
import com.golkov.inventv.controller.DataObserver;
import com.golkov.inventv.controller.detailcontroller.AusleihenDetailViewController;
import com.golkov.inventv.model.daos.*;
import com.golkov.inventv.model.entities.*;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AusleihdatenListeViewController extends ListeViewControllerBase<AusleihEntity> implements Initializable, DataObserver {

    private static final Logger logger = LogManager.getLogger(AusleihdatenListeViewController.class);
    AusleiheDAO a_dao = new AusleiheDAO();

    public AusleihdatenListeViewController(){
        super();
        foundEntities = FXCollections.observableArrayList();
    }

    @Override
    public void updateData() {
        sucheStartenButtonTapped(new ActionEvent());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.info("Initializing AusleihedatenListeViewController...");

        //region Definition und Verhalten der Controls
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

        //Logik zum Umwandeln von true/false in ja/nein
        tcAusleiheAbgegeben.setCellFactory(column -> new TableCell<AusleihEntity, Boolean>() {
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
        tcAusleiheAbgegeben.setCellValueFactory(cellData -> {
            AusleihEntity ausleihe = cellData.getValue();           
            return new SimpleBooleanProperty(ausleihe.isAbgegeben());
        });

        tcAusleiheAktion.setCellFactory(column -> new TableCell<>(){
            private final Button deleteButton = new Button();
            private final Button rueckgabeButton = new Button();

            {
                // Setze das Bild für den Delete-Button
                ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/images/delete.png")));
                imageView.setFitHeight(15);
                imageView.setFitWidth(15);
                deleteButton.setGraphic(imageView);

                // Setze das Bild für den Ausleihe-Button
                imageView = new ImageView(new Image(getClass().getResourceAsStream("/images/return.png")));
                imageView.setFitHeight(15);
                imageView.setFitWidth(15);
                rueckgabeButton.setGraphic(imageView);


                BooleanBinding isRueckgabeDisabled = new BooleanBinding() {
                    {
                        super.bind(emptyProperty()); // Bindung an den aktuellen Eintrag
                    }

                    @Override
                    protected boolean computeValue() {
                        if(isEmpty())
                            return true;
                        AusleihEntity ausleihe = getTableRow().getItem();
                        return ausleihe.isAbgegeben(); // true, wenn deaktiviert, false, wenn aktiviert
                    }
                };

                // Add action listeners to the buttons
                rueckgabeButton.setOnAction(event -> {
                    AusleihEntity ausleihe = getTableView().getItems().get(getIndex());
                    AusleihEntity newEntity = ausleihe;
                    newEntity.setAbgegeben(true);
                    int error = a_dao.updateEntity(ausleihe, newEntity);

                    if (error == 2) {
                        Globals.showAlert(
                                Alert.AlertType.ERROR,
                                String.format(AlertTexts.GENERIC_ERROR_HEADER, "Entfernen"),
                                "Datenbankfehler",
                                String.format(AlertTexts.GENERIC_ERROR_MESSAGE, "Entfernung", "Ausleiheneintrags"),
                                alert -> alert.close(),
                                ButtonType.OK
                        );
                    }else{
                        lstAusleiheEntities.getItems().set(getIndex(), newEntity);
                    }
                });
                rueckgabeButton.disableProperty().bind(isRueckgabeDisabled);


                deleteButton.setOnAction(event -> {
                    AusleihEntity ausleihe = getTableView().getItems().get(getIndex());
                    int error = a_dao.removeEntity(ausleihe);

                    if (error == 1) { //TODO: Alerts in separate Klasse auslagern
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Fehler beim Entfernen");
                        alert.setHeaderText("Ausleihe kann nicht aus Historie entfernt werden");
                        alert.setContentText("Beim Entfernen des Ausleihe ist ein Fehler aufgetreten: Der Benutzer '" + ausleihe.getBenutzer().getNachname() + ", " + ausleihe.getBenutzer().getVorname() + "' hat das Objekt mit der Inventarnummer '"+ausleihe.getObjekt().getInventarnummer()+"' noch nicht abgegeben. Bitte stellen Sie sicher, dass der betroffene Benutzer das Objekt zurückgibt und versuchen Sie es erneut.");
                        alert.showAndWait().ifPresent(rs -> {
                            if (rs == ButtonType.OK) {
                                alert.close();
                            }
                        });
                    } else if (error == 2) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Fehler beim Entfernen");
                        alert.setHeaderText("Datenbankfehler");
                        alert.setContentText("Bei der Entfernung der Ausleihe ist ein Fehler aufgetreten. Bitte wenden Sie sich an den Administrator.");
                        alert.showAndWait().ifPresent(rs -> {
                            if (rs == ButtonType.OK) {
                                alert.close();
                            }
                        });
                    } else {
                        lstAusleiheEntities.getItems().remove(ausleihe);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    // Clear the cell if the row is empty
                    setGraphic(null);
                } else {
                    // Set the buttons as the graphic of the cell
                    HBox container = new HBox(10);
                    container.setAlignment(Pos.CENTER);
                    container.getChildren().addAll(rueckgabeButton, deleteButton);
                    setGraphic(container);
                }
            }
        }); //Logik für die Knopfgenerierung in der Spalte "Aktion" und Delete- sowie Updatelogik

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
        ObjektDAO ao_dao = new ObjektDAO();
        ObservableList<ObjektEntity> ObjektList = ao_dao.getAllEntities();
        cbAusleiheObjekt.setItems(ObjektList);
        //Logik für die ComboBox-Anzeige des Objekts
        cbAusleiheObjekt.setConverter(new StringConverter<ObjektEntity>() {
            @Override
            public String toString(ObjektEntity Objekt) {
                // Wandelt das TypEntity in die Bezeichnung um
                return "["+Objekt.getInventarnummer()+"] ("+Objekt.getTyp().getBezeichnung() + ") "+Objekt.getHersteller() + " " + Objekt.getModell();
            }

            @Override
            public ObjektEntity fromString(String bezeichnung) {
                // Wandelt die ausgewählte Bezeichnung wieder in das zugehörige TypEntity um
                for (ObjektEntity Objekt : ObjektList) {
                    String temp = "["+Objekt.getInventarnummer()+"] ("+Objekt.getTyp().getBezeichnung() + ") "+Objekt.getHersteller() + " " + Objekt.getModell();
                    if (temp.equals(bezeichnung)) {
                        return Objekt;
                    }
                }
                return null;
            }
        });

        //endregion

        lstAusleiheEntities.setItems(foundEntities); //Bindung der TableView an ObservableCollection<BenutzerEntity> foundEntities

        //Algorithmus zum Deaktivieren des "Suchen"-Knopfes bei leeren Filterfeldern
        btnSearchAusleihen.disableProperty().bind(searchButtonDisabled);
        searchButtonDisabled.bind(cbAusleiheObjekt.valueProperty().isNull()
                .and(cbAusleiheBenutzer.valueProperty().isNull())
                .and(cbAusleiheDatum.valueProperty().isNull())
        );

        logger.info("Initialization complete");
    }

    private BooleanProperty searchButtonDisabled = new SimpleBooleanProperty(true);

    //region FXML-Entities

    @FXML
    private Button btnNewAusleihe;

    @FXML
    private Button btnSearchAusleihen;

    @FXML
    private Button btnSearchAllAusleihen;

    @FXML
    private ComboBox<BenutzerEntity> cbAusleiheBenutzer;

    @FXML
    private DatePicker cbAusleiheDatum;

    @FXML
    private ComboBox<ObjektEntity> cbAusleiheObjekt;

    @FXML
    private Label lblFoundAusleiheEntities;

    @FXML
    private TableView<AusleihEntity> lstAusleiheEntities;

    @FXML
    private TableColumn<AusleihEntity, LocalDate> tcAusleihdatum;

    @FXML
    private TableColumn<AusleihEntity, Boolean> tcAusleiheAbgegeben;

    @FXML
    private TableColumn<AusleihEntity, Void> tcAusleiheAktion;

    @FXML
    private TableColumn<AusleihEntity, BenutzerEntity> tcAusleiheBenutzer;

    @FXML
    private TableColumn<AusleihEntity, Integer> tcAusleiheID;

    @FXML
    private TableColumn<AusleihEntity, ObjektEntity> tcAusleiheObjekt;

    //endregion

    @FXML
    void sucheStartenButtonTapped(ActionEvent event) {
        logger.info("Search initiated...");
        LocalDate ausleihdatum = LocalDate.of(1900, 1, 1);
        ObjektEntity objekt = new ObjektEntity(); objekt.setID(-1);
        BenutzerEntity benutzer = new BenutzerEntity(); benutzer.setID(-1);


        if(!(cbAusleiheDatum.getValue() == null))
            ausleihdatum = cbAusleiheDatum.getValue();
        if(!(cbAusleiheObjekt.getValue() == null))
            objekt = cbAusleiheObjekt.getValue();
        if(!(cbAusleiheBenutzer.getValue() == null))
            benutzer = cbAusleiheBenutzer.getValue();


        foundEntities.setAll(a_dao.filterAusleihe(benutzer,objekt,ausleihdatum));
        lblFoundAusleiheEntities.setText(String.valueOf(foundEntities.size()));
        logger.info("Search finished!");
    }

    @FXML
    void sucheAlleButtonTapped(ActionEvent event) {
        logger.info("Search all...");
        foundEntities.setAll(a_dao.getAllEntities());
        lblFoundAusleiheEntities.setText(String.valueOf(foundEntities.size()));
        logger.info("Search finished!");
    }

    @FXML
    void newAusleiheButtonTapped(ActionEvent event){
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/AusleihenDetailView.fxml"));
            AusleihenDetailViewController controller = new AusleihenDetailViewController(stage);
            controller.setObserver(AusleihdatenListeViewController.this);
            loader.setController(controller);
            Parent root = loader.load();

            stage.setTitle("Neue Ausleihe");
            stage.setScene(new Scene(root, 500, 130));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
