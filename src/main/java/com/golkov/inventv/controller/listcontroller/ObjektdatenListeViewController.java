package com.golkov.inventv.controller.listcontroller;

import com.golkov.inventv.AlertTexts;
import com.golkov.inventv.Globals;
import com.golkov.inventv.Main;
import com.golkov.inventv.ViewNavigation;
import com.golkov.inventv.controller.NavigationViewController;
import com.golkov.inventv.controller.detailcontroller.BenutzerdatenDetailViewController;
import com.golkov.inventv.controller.detailcontroller.ObjektdatenDetailViewController;
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
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ObjektdatenListeViewController extends ListeViewControllerBase<ObjektEntity> implements Initializable{

    private static final Logger logger = LogManager.getLogger(ObjektdatenListeViewController.class);
    ObjektDAO o_dao = new ObjektDAO();

    public ObjektdatenListeViewController(){
        super();
        foundEntities = FXCollections.observableArrayList();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.info("Initializing ObjektdatenListeViewController...");

        //region Definition und Verhalten der Controls
        tcObjektInventarnummer.setCellValueFactory(new PropertyValueFactory<>("inventarnummer"));
        tcObjektTyp.setCellValueFactory(new PropertyValueFactory<>("typ"));

        //Algorithmus zum Umwandeln eines TypEntity-Ergebnisses in die Anzeige der Bezeichnung des Typs
        tcObjektTyp.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(TypEntity typEntity, boolean empty) {
                super.updateItem(typEntity, empty);
                if (typEntity == null || empty) {
                    setText(null);
                } else {
                    setText(typEntity.getBezeichnung());
                }
            }
        });

        //Logik zum Umwandeln von true/false in ja/nein
        tcObjektAusgeliehen.setCellFactory(column -> new TableCell<ObjektEntity, Boolean>() {
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
        tcObjektAusgeliehen.setCellValueFactory(cellData -> {
            ObjektEntity objekt = cellData.getValue();
            boolean ausgeliehen = o_dao.istAusgeliehen(objekt);
            return new SimpleBooleanProperty(ausgeliehen);
        });

        tcObjektHersteller.setCellValueFactory(new PropertyValueFactory<>("hersteller"));
        tcObjektModell.setCellValueFactory(new PropertyValueFactory<>("modell"));
        tcObjektAktion.setCellFactory(column -> new TableCell<ObjektEntity, Void>(){
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
                    ObjektEntity objekt = getTableView().getItems().get(getIndex());
                    //Logik zum wechseln der Ansichten von Liste zu Detailview mithilfe der ViewNavigation-Klasse
                    try {
                        FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/ObjektdatenDetailView.fxml"));
                        ObjektdatenDetailViewController controller = new ObjektdatenDetailViewController(objekt);
                        loader.setController(controller);
                        Node node = loader.load();
                        ViewNavigation.push(2, node);

                        NavigationViewController navigationController = NavigationViewController.getInstance();
                        AnchorPane ap = navigationController.getAnchorPane();
                        ap.setLeftAnchor(node, 0.0);
                        ap.setRightAnchor(node, 0.0);
                        ap.setTopAnchor(node, 0.0);
                        ap.setBottomAnchor(node, 0.0);

                        ap.getChildren().setAll(node);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    //lstObjektEntities.getItems().set(getIndex(), o_dao.getObjektById(objekt.getID()));
                });

                BooleanBinding isEntfernenDisabled = new BooleanBinding() {
                    {
                        super.bind(emptyProperty()); // Bindung an den aktuellen Eintrag
                    }

                    @Override
                    protected boolean computeValue() {
                        if(isEmpty())
                            return true;
                        ObjektEntity objekt = getTableRow().getItem();
                        return o_dao.istAusgeliehen(objekt); // true, wenn deaktiviert, false, wenn aktiviert
                    }
                };

                deleteButton.setOnAction(event -> {
                    ObjektEntity objekt = getTableView().getItems().get(getIndex());
                    int error = o_dao.removeEntity(objekt);

                    if (error == 2) {

                        Globals.showAlert(
                                Alert.AlertType.ERROR,
                                String.format(AlertTexts.GENERIC_ERROR_HEADER, "Entfernen"),
                                "Datenbankfehler",
                                String.format(AlertTexts.GENERIC_ERROR_MESSAGE, "Entfernung", "Objekts"),
                                alert -> alert.close(),
                                ButtonType.OK
                        );
                    } else {
                        lstObjektEntities.getItems().remove(objekt);
                    }
                });

                deleteButton.disableProperty().bind(isEntfernenDisabled);
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

        //endregion

        lstObjektEntities.setItems(foundEntities); //Bindung der TableView an ObservableCollection<BenutzerEntity> foundEntities

        //Algorithmus zum Deaktivieren des "Suchen"-Knopfes bei leeren Filterfeldern
        btnSearchObjekte.disableProperty().bind(searchButtonDisabled);
        searchButtonDisabled.bind(txtInventarnummer.textProperty().isEmpty()
                .and(txtHersteller.textProperty().isEmpty())
                .and(txtModell.textProperty().isEmpty())
                .and(cbKaufdatum.valueProperty().isNull())
                .and(txtEinzelpreis.textProperty().isEmpty())
                .and(cbTyp.valueProperty().isNull())
                .and(cbAblageort.valueProperty().isNull())
        );

        //Algorithmus zum Verhindern von nicht-numerischen Eingaben in diversen numerischen Feldern
        txtInventarnummer.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (!"0123456789".contains(event.getCharacter())) {
                event.consume();
            }
        });
        txtEinzelpreis.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (!".0123456789".contains(event.getCharacter())) {
                event.consume();
            }
        });

        logger.info("Initialization complete");
    }

    private BooleanProperty searchButtonDisabled = new SimpleBooleanProperty(true);

    //region FXML-Entities

    @FXML
    private Button btnNewObjekt;

    @FXML
    private Button btnSearchObjekte;

    @FXML
    private ComboBox<AblageortEntity> cbAblageort;

    @FXML
    private DatePicker cbKaufdatum;

    @FXML
    private TableColumn<ObjektEntity, Void> tcObjektAktion;

    @FXML
    private TableColumn<ObjektEntity, Boolean> tcObjektAusgeliehen;

    @FXML
    private TableColumn<ObjektEntity, String> tcObjektHersteller;

    @FXML
    private TableColumn<ObjektEntity, Integer> tcObjektInventarnummer;

    @FXML
    private TableColumn<ObjektEntity, String> tcObjektModell;

    @FXML
    private TableColumn<ObjektEntity, TypEntity> tcObjektTyp;

    @FXML
    private ComboBox<TypEntity> cbTyp;

    @FXML
    private Label lblFoundObjektEntities;

    @FXML
    private TableView<ObjektEntity> lstObjektEntities;

    @FXML
    private TextField txtEinzelpreis;

    @FXML
    private TextField txtHersteller;

    @FXML
    private TextField txtInventarnummer;

    @FXML
    private TextField txtModell;

    //endregion

    @FXML
    void newObjektButtonTapped(ActionEvent event){
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/ObjektdatenDetailView.fxml"));
            ObjektdatenDetailViewController controller = new ObjektdatenDetailViewController();
            loader.setController(controller);
            Node node = loader.load();
            ViewNavigation.push(2, node);

            NavigationViewController navigationController = NavigationViewController.getInstance();
            AnchorPane ap = navigationController.getAnchorPane();
            ap.setLeftAnchor(node, 0.0);
            ap.setRightAnchor(node, 0.0);
            ap.setTopAnchor(node, 0.0);
            ap.setBottomAnchor(node, 0.0);

            ap.getChildren().setAll(node);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void sucheStartenButtonTapped(ActionEvent event) {
        logger.info("Search initiated...");
        int invnr = 0;
        LocalDate kaufdatum = LocalDate.of(1900, 1, 1);
        float einzelpreis = -1;
        TypEntity typ = new TypEntity(); typ.setID(-1);
        AblageortEntity ablageort = new AblageortEntity(); ablageort.setID(-1);

        if(!txtInventarnummer.getText().equals(""))
            invnr = Integer.parseInt(txtInventarnummer.getText());
        if(!(cbKaufdatum.getValue() == null))
            kaufdatum = cbKaufdatum.getValue();
        if(!txtEinzelpreis.getText().equals(""))
            einzelpreis = Float.parseFloat(txtEinzelpreis.getText());
        if(!(cbTyp.getValue() == null))
            typ = cbTyp.getValue();
        if(!(cbAblageort.getValue() == null))
            ablageort = cbAblageort.getValue();


        foundEntities.setAll(o_dao.filterObjekt(invnr,txtHersteller.getText(),txtModell.getText(),kaufdatum,einzelpreis,typ,ablageort));
        lblFoundObjektEntities.setText(String.valueOf(foundEntities.size()));
        logger.info("Search finished!");
    }

}
