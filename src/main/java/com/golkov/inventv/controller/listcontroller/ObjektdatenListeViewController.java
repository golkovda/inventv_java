package com.golkov.inventv.controller.listcontroller;

import com.golkov.inventv.model.daos.BenutzerDAO;
import com.golkov.inventv.model.daos.ObjektDAO;
import com.golkov.inventv.model.entities.AblageortEntity;
import com.golkov.inventv.model.entities.BenutzerEntity;
import com.golkov.inventv.model.entities.ObjektEntity;
import com.golkov.inventv.model.entities.TypEntity;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
        tcObjektID.setCellValueFactory(new PropertyValueFactory<>("ID"));
        tcObjektInventarnummer.setCellValueFactory(new PropertyValueFactory<>("inventarnummer"));
        tcObjektTyp.setCellValueFactory(new PropertyValueFactory<>("typ"));
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
                    //TODO: Bearbeiten
                });

                deleteButton.setOnAction(event -> {
                    ObjektEntity objekt = getTableView().getItems().get(getIndex());
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
        lstObjektEntities.setItems(foundEntities); //Bindung der TableView an ObservableCollection<BenutzerEntity> foundEntities

        //Algorithmus zum Deaktivieren des "Suchen"-Knopfes bei leeren Filterfeldern
        //TODO: reparieren
        btnSearchObjekte.disableProperty().bind(searchButtonDisabled);
        searchButtonDisabled.bind(txtObjektID.textProperty().isEmpty()
                .and(txtInventarnummer.textProperty().isEmpty())
                .and(txtHersteller.textProperty().isEmpty())
                .and(txtModell.textProperty().isEmpty())
                .and(cbKaufdatum.valueProperty().isNotNull())
                .and(txtEinzelpreis.textProperty().isEmpty())
                .and(cbTyp.valueProperty().isNotNull())
                .and(cbTyp.valueProperty().isNotNull())
        );

        //Algorithmus zum Verhindern von nicht-numerischen Eingaben im Feld txtBenutzerID
        txtObjektID.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (!"0123456789".contains(event.getCharacter())) {
                event.consume();
            }
        });

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
    private TableColumn<ObjektEntity, String> tcObjektTyp;

    @FXML
    private ComboBox<TypEntity> cbTyp;

    @FXML
    private Label lblFoundObjektEntities;

    @FXML
    private TableView<ObjektEntity> lstObjektEntities;

    @FXML
    private TableColumn<ObjektEntity, Integer> tcObjektID;

    @FXML
    private TextField txtEinzelpreis;

    @FXML
    private TextField txtHersteller;

    @FXML
    private TextField txtInventarnummer;

    @FXML
    private TextField txtModell;

    @FXML
    private TextField txtObjektID;

    //endregion

    @FXML
    void sucheStartenButtonTapped(ActionEvent event) {
        logger.info("Search initiated...");
        int objektid = 0;
        int invnr = 0;
        LocalDate kaufdatum = LocalDate.of(1900, 1, 1);
        float einzelpreis = -1;
        TypEntity typ = new TypEntity(); typ.setID(-1);
        AblageortEntity ablageort = new AblageortEntity(); ablageort.setID(-1);

        if(!txtObjektID.getText().equals(""))
            objektid = Integer.parseInt(txtObjektID.getText());
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


        foundEntities.setAll(o_dao.filterObjekt(objektid,invnr,txtHersteller.getText(),txtModell.getText(),kaufdatum,einzelpreis,typ,ablageort));
        lblFoundObjektEntities.setText(String.valueOf(foundEntities.size()));
        logger.info("Search finished!");
    }

}
