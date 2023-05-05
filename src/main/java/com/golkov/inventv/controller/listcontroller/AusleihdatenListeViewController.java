package com.golkov.inventv.controller.listcontroller;

import com.golkov.inventv.model.daos.*;
import com.golkov.inventv.model.entities.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AusleihdatenListeViewController extends ListeViewControllerBase<AusleihEntity> implements Initializable{

    private static final Logger logger = LogManager.getLogger(AusleihdatenListeViewController.class);
    AusleiheDAO a_dao = new AusleiheDAO();

    public AusleihdatenListeViewController(){
        super();
        foundEntities = FXCollections.observableArrayList();
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

        tcAusleiheAktion.setCellFactory(column -> new TableCell<>(){
            private final Button editButton = new Button();
            private final Button deleteButton = new Button();

            {
                // Setze das Bild für den Edit-Button
                ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/com.golkov.inventv.images/edit.png")));
                imageView.setFitHeight(15);
                imageView.setFitWidth(15);
                editButton.setGraphic(imageView);

                // Setze das Bild für den Delete-Button
                imageView = new ImageView(new Image(getClass().getResourceAsStream("/com.golkov.inventv.images/delete.png")));
                imageView.setFitHeight(15);
                imageView.setFitWidth(15);
                deleteButton.setGraphic(imageView);

                // Add action listeners to the buttons
                editButton.setOnAction(event -> {
                    AusleihEntity ausleihe = getTableView().getItems().get(getIndex());
                    //TODO: Bearbeiten
                });

                deleteButton.setOnAction(event -> {
                    AusleihEntity ausleihe = getTableView().getItems().get(getIndex());
                    //TODO: Löschsequenz
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
                    container.getChildren().addAll(editButton, deleteButton);
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
}
