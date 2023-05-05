package com.golkov.inventv.controller.listcontroller;

import com.golkov.inventv.model.daos.BenutzerDAO;
import com.golkov.inventv.model.entities.BenutzerEntity;
import com.golkov.inventv.model.entities.ObjektEntity;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
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
        tcBenutzerAktion.setCellFactory(column -> new TableCell<BenutzerEntity, Void>(){
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
                    BenutzerEntity benutzer = getTableView().getItems().get(getIndex());
                    //TODO: Bearbeiten
                });

                deleteButton.setOnAction(event -> {
                    BenutzerEntity benutzer = getTableView().getItems().get(getIndex());
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
        lstBenutzerEntities.setItems(foundEntities); //Bindung der TableView an ObservableCollection<BenutzerEntity> foundEntities

        //Logik zum Umwandeln von true/false in ja/nein
        tcBenutzerOffeneAusleihen.setCellFactory(column -> new TableCell<>() {
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
        tcBenutzerOffeneAusleihen.setCellValueFactory(cellData -> {
            BenutzerEntity benutzer = cellData.getValue();
            boolean ausgeliehen = b_dao.hatOffeneAusleihen(benutzer);
            return new SimpleBooleanProperty(ausgeliehen);
        });

        //Algorithmus zum Deaktivieren des "Suchen"-Knopfes bei leeren Filterfeldern
        btnSearchBenutzer.disableProperty().bind(searchButtonDisabled);
        searchButtonDisabled.bind(txtBenutzerKennung.textProperty().isEmpty()
                .and(txtBenutzerNachname.textProperty().isEmpty())
                .and(txtBenutzerVorname.textProperty().isEmpty())
                .and(txtBenutzerID.textProperty().isEmpty())
                );

        //Algorithmus zum Verhindern von nicht-numerischen Eingaben im Feld txtBenutzerID
        txtBenutzerID.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (!"0123456789".contains(event.getCharacter())) {
                event.consume();
            }
        });

        logger.info("Initialization complete");
    }

    private BooleanProperty searchButtonDisabled = new SimpleBooleanProperty(true);

    //region FXML-Entities

    @FXML
    private Button btnNewUser;

    @FXML
    private Button btnSearchBenutzer;

    @FXML
    private Label lblFoundBenutzerEntities;

    @FXML
    private TableView<BenutzerEntity> lstBenutzerEntities;

    @FXML
    private TableColumn<BenutzerEntity, Void> tcBenutzerAktion;

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

    //endregion

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
