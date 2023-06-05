package com.golkov.inventv.controller.listcontroller;

import com.golkov.inventv.AlertTexts;
import com.golkov.inventv.Globals;
import com.golkov.inventv.Main;
import com.golkov.inventv.controller.DataObserver;
import com.golkov.inventv.controller.detailcontroller.TypAblageortDetailViewController;
import com.golkov.inventv.model.daos.AblageortDAO;
import com.golkov.inventv.model.daos.ObjektDAO;
import com.golkov.inventv.model.daos.TypDAO;
import com.golkov.inventv.model.entities.AblageortEntity;
import com.golkov.inventv.model.entities.TAEntity;
import com.golkov.inventv.model.entities.TypEntity;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class TypAblageortListeViewController implements Initializable, DataObserver {


    private ObservableList<TAEntity> foundTypEntities = FXCollections.observableArrayList();
    private ObservableList<TAEntity> foundAblageortEntities = FXCollections.observableArrayList();
    private final ObjektDAO o_dao = new ObjektDAO();
    private final TypDAO t_dao = new TypDAO();
    private final AblageortDAO ao_dao = new AblageortDAO();
    ObservableList<String> options = FXCollections.observableArrayList("Typ", "Ablageort");

    String typbezeichnung_temp = "";
    String aortbezeichnung_temp = "";

    @Override
    public void updateData() {
        sucheStartenButtonTapped(new ActionEvent());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        choiceSuchart.setItems(options);
        choiceSuchart.setValue(options.get(0));
        lblButtonText.setText("Neuen "+options.get(0)+" hinzufügen");


        // Erstellen Sie die Spalten für Ansicht 1
        TableColumn<TAEntity, String> column1 = new TableColumn<>("Bezeichnung");
        TableColumn<TAEntity, Integer> column2 = new TableColumn<>("Objekte des Typs");
        TableColumn<TAEntity, Void> column3 = new TableColumn<>("Aktion");

        // Erstellen Sie die Spalten für Ansicht 2
        TableColumn<TAEntity, String> column1Alt = new TableColumn<>("Bezeichnung");
        TableColumn<TAEntity, Integer> column2Alt = new TableColumn<>("Objekte in Ablage");
        TableColumn<TAEntity, Void> column3Alt = new TableColumn<>("Aktion");

        // Setzen Sie die CellValueFactories für die Spalten
        column1.setCellValueFactory(new PropertyValueFactory<>("bezeichnung"));

        column2.setCellValueFactory(cellData -> {
            TypEntity entity = (TypEntity) cellData.getValue();
            int objekteAnzahl = o_dao.getAnzahlObjekteByTyp(entity);
            return new SimpleIntegerProperty(objekteAnzahl).asObject();
        });

        column3.setCellFactory(column -> new TableCell<>() {
            private final Button editButton = new Button();
            private final Button deleteButton = new Button();


            {
                // Setze das Bild für den Edit-Button
                ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/images/edit.png")));
                imageView.setFitHeight(15);
                imageView.setFitWidth(15);
                editButton.setGraphic(imageView);

                // Setze das Bild für den Delete-Button
                imageView = new ImageView(new Image(getClass().getResourceAsStream("/images/delete.png")));
                imageView.setFitHeight(15);
                imageView.setFitWidth(15);
                deleteButton.setGraphic(imageView);

                // Add action listeners to the buttons
                editButton.setOnAction(event -> {
                    TypEntity typ = (TypEntity) getTableView().getItems().get(getIndex());
                    try {
                        Stage stage = new Stage();
                        FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/TypAblageortDetailView.fxml"));
                        TypAblageortDetailViewController controller = new TypAblageortDetailViewController(typ, stage);
                        controller.setObserver(TypAblageortListeViewController.this);
                        loader.setController(controller);
                        Parent root = loader.load();


                        stage.setTitle(choiceSuchart.getValue() + "bearbeiten: " + typ.getBezeichnung());
                        stage.setScene(new Scene(root, 450, 125));
                        stage.show();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    //lstEntities.getItems().set(getIndex(), t_dao.getTypById(typ.getID()));
                });

                BooleanBinding isEntfernenDisabled = new BooleanBinding() {
                    {
                        super.bind(emptyProperty()); // Bindung an den aktuellen Eintrag
                    }

                    @Override
                    protected boolean computeValue() {
                        if(isEmpty())
                            return true;
                        TAEntity typ = getTableRow().getItem();
                        return o_dao.getAnzahlObjekteByTyp((TypEntity) typ) > 0; // true, wenn deaktiviert, false, wenn aktiviert
                    }
                };

                deleteButton.setOnAction(event -> {
                    TypEntity typ = (TypEntity) getTableView().getItems().get(getIndex());
                    int error = t_dao.removeEntity(typ);

                    if (error == 2) {
                        Globals.showAlert(
                                Alert.AlertType.ERROR,
                                String.format(AlertTexts.GENERIC_ERROR_HEADER, "Entfernen"),
                                "Datenbankfehler",
                                String.format(AlertTexts.GENERIC_ERROR_MESSAGE, "Entfernung", "Ablageorts"),
                                alert -> alert.close(),
                                ButtonType.OK
                        );
                    } else {
                        lstEntities.getItems().remove(typ);
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

        column3Alt.setCellFactory(column -> new TableCell<>() {
            private final Button editButton = new Button();
            private final Button deleteButton = new Button();

            {
                // Setze das Bild für den Edit-Button
                ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/images/edit.png")));
                imageView.setFitHeight(15);
                imageView.setFitWidth(15);
                editButton.setGraphic(imageView);

                // Setze das Bild für den Delete-Button
                imageView = new ImageView(new Image(getClass().getResourceAsStream("/images/delete.png")));
                imageView.setFitHeight(15);
                imageView.setFitWidth(15);
                deleteButton.setGraphic(imageView);

                // Add action listeners to the buttons
                editButton.setOnAction(event -> {
                    AblageortEntity aort = (AblageortEntity) getTableView().getItems().get(getIndex());
                    try {
                        Stage stage = new Stage();
                        FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/TypAblageortDetailView.fxml"));
                        TypAblageortDetailViewController controller = new TypAblageortDetailViewController(aort, stage);
                        controller.setObserver(TypAblageortListeViewController.this);
                        loader.setController(controller);
                        Parent root = loader.load();

                        stage.setTitle(choiceSuchart.getValue() + "bearbeiten: " + aort.getBezeichnung());
                        stage.setScene(new Scene(root, 450, 125));
                        stage.show();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    lstEntities.getItems().set(getIndex(), ao_dao.getAblageortById(aort.getID()));
                });

                BooleanBinding isEntfernenDisabled = new BooleanBinding() {
                    {
                        super.bind(emptyProperty()); // Bindung an den aktuellen Eintrag
                    }

                    @Override
                    protected boolean computeValue() {
                        if(isEmpty())
                            return true;
                        TAEntity typ = getTableRow().getItem();
                        return o_dao.getAnzahlObjekteByAblageort((AblageortEntity) typ) > 0; // true, wenn deaktiviert, false, wenn aktiviert
                    }
                };

                deleteButton.setOnAction(event -> {
                    AblageortEntity aort = (AblageortEntity) getTableView().getItems().get(getIndex());
                    int error = ao_dao.removeEntity(aort);

                    if (error == 2) {
                        Globals.showAlert(
                                Alert.AlertType.ERROR,
                                String.format(AlertTexts.GENERIC_ERROR_HEADER, "Entfernen"),
                                "Datenbankfehler",
                                String.format(AlertTexts.GENERIC_ERROR_MESSAGE, "Entfernung", "Ablageorts"),
                                alert -> alert.close(),
                                ButtonType.OK
                        );
                    } else {
                        lstEntities.getItems().remove(aort);
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

        column1Alt.setCellValueFactory(new PropertyValueFactory<>("bezeichnung"));
        column2Alt.setCellValueFactory(cellData -> {
            AblageortEntity entity = (AblageortEntity) cellData.getValue();
            int objekteAnzahl = o_dao.getAnzahlObjekteByAblageort(entity);
            return new SimpleIntegerProperty(objekteAnzahl).asObject();
        });
        column3Alt.setCellValueFactory(new PropertyValueFactory<>("aktion"));

        // Fügen Sie die Spalten zur TableView hinzu
        lstEntities.getColumns().addAll(column1, column2, column3);
        lstEntities.setItems(foundTypEntities);

// Fügen Sie den Listener zur ChoiceBox hinzu
        choiceSuchart.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(options.get(0))) {
                // Entfernen Sie die Spalten für Ansicht 2
                lstEntities.getColumns().removeAll(column1Alt, column2Alt, column3Alt);

                // Fügen Sie die Spalten für Ansicht 1 hinzu
                lstEntities.getColumns().addAll(column1, column2, column3);

                // Binden Sie die Tabelle an die Entitäten für Ansicht 1
                aortbezeichnung_temp = txtBezeichnung.getText();
                txtBezeichnung.setText(typbezeichnung_temp);
                lstEntities.setItems(foundTypEntities);
            } else if (newValue.equals(options.get(1))) {
                // Entfernen Sie die Spalten für Ansicht 1
                lstEntities.getColumns().removeAll(column1, column2, column3);

                // Fügen Sie die Spalten für Ansicht 2 hinzu
                lstEntities.getColumns().addAll(column1Alt, column2Alt, column3Alt);

                // Binden Sie die Tabelle an die Entitäten für Ansicht 2
                typbezeichnung_temp = txtBezeichnung.getText();
                txtBezeichnung.setText(aortbezeichnung_temp);
                lstEntities.setItems(foundAblageortEntities);
            }
            lblButtonText.setText("Neuen "+newValue+" hinzufügen");
        });
    }

    private SimpleBooleanProperty isColumn1Edited = new SimpleBooleanProperty(false);


    //region FXML Controls

    @FXML
    private Label lblButtonText;

    @FXML
    private Button btnNewTypAblageort;

    @FXML
    private Button btnSearchTypAblageort;

    @FXML
    private ChoiceBox<String> choiceSuchart;

    @FXML
    private Label lblFoundEntities;

    @FXML
    private TableView<TAEntity> lstEntities;

    @FXML
    private TextField txtBezeichnung;

    @FXML
    void newTypAblageortButtonTapped(ActionEvent event) {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/TypAblageortDetailView.fxml"));
            TypAblageortDetailViewController controller;
            if(Objects.equals(choiceSuchart.getValue(), options.get(0))){
                controller = new TypAblageortDetailViewController(0, stage);
            }else{
                controller = new TypAblageortDetailViewController(1, stage);
            }

            controller.setObserver(TypAblageortListeViewController.this);
            loader.setController(controller);
            Parent root = loader.load();

            stage.setTitle("Neuer "+choiceSuchart.getValue());
            stage.setScene(new Scene(root, 450, 125));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void sucheStartenButtonTapped(ActionEvent event) {
        if (Objects.equals(choiceSuchart.getValue(), options.get(0))) { // Wenn Typ ausgewählt
            ObservableList<TypEntity> typEntities = t_dao.filterTyp(0, txtBezeichnung.getText());
            // Erstellen Sie eine neue ObservableList<TAEntity>
            ObservableList<TAEntity> taEntities = FXCollections.observableArrayList();
            // Fügen Sie die Elemente aus der typEntities-Liste als TAEntity-Objekte zur taEntities-Liste hinzu
            taEntities.addAll(typEntities);
            foundTypEntities.setAll(taEntities);
            lstEntities.setItems(foundTypEntities);
        } else {
            ObservableList<AblageortEntity> ablageortEntities = ao_dao.filterAblageort(0, txtBezeichnung.getText());
            // Erstellen Sie eine neue ObservableList<TAEntity>
            ObservableList<TAEntity> taEntities = FXCollections.observableArrayList();
            // Fügen Sie die Elemente aus der typEntities-Liste als TAEntity-Objekte zur taEntities-Liste hinzu
            taEntities.addAll(ablageortEntities);
            foundAblageortEntities.setAll(taEntities);
            lstEntities.setItems(foundAblageortEntities);
        }
    }

    //endregion
}
