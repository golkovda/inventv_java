package com.golkov.inventv.controller.detailcontroller;

import com.golkov.inventv.model.daos.AblageortDAO;
import com.golkov.inventv.model.daos.TypDAO;
import com.golkov.inventv.model.entities.AblageortEntity;
import com.golkov.inventv.model.entities.TAEntity;
import com.golkov.inventv.model.entities.TypEntity;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class TypAblageortDetailViewController implements Initializable {

    private TAEntity currentEntity;
    private int index;
    private Stage stage;

    public TypAblageortDetailViewController(AblageortEntity aoentity, Stage stage){
        currentEntity = aoentity;
        this.stage = stage;
    }

    public TypAblageortDetailViewController(TypEntity typentity, Stage stage){
        currentEntity = typentity;
        this.stage = stage;
    }

    public TypAblageortDetailViewController(int index, Stage stage){
        currentEntity = null;
        this.index = index;
        this.stage = stage;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnSaveBezeichnung.disableProperty().bind(saveButtonDisabled);

        if(currentEntity != null){
            txtBezeichnung.setText(currentEntity.getBezeichnung());

            //Algorithmus zum Deaktivieren des "Speichern"-Knopfes wenn UnchangedEntity = CurrentEntity
            BooleanBinding saveButtonDisabled2 = new BooleanBinding() {
                {
                    super.bind(txtBezeichnung.textProperty());
                }

                @Override
                protected boolean computeValue() {
                    return txtBezeichnung.getText().equals(currentEntity.getBezeichnung());
                }
            };
            saveButtonDisabled.bind(saveButtonDisabled2);
        }else{
            saveButtonDisabled.bind(txtBezeichnung.textProperty().isEmpty());
        }
    }

    private BooleanProperty saveButtonDisabled = new SimpleBooleanProperty(true);

    @FXML
    private Button btnSaveBezeichnung;

    @FXML
    private TextField txtBezeichnung;

    @FXML
    void speichernButtonTapped(ActionEvent event) {
        int error = 2;
        String typeString = "";
        if(currentEntity == null){
            if(index == 0){
                TypDAO t_dao = new TypDAO();
                TypEntity newEntity = new TypEntity();
                newEntity.setBezeichnung(txtBezeichnung.getText());
                error = t_dao.insertEntity(newEntity);
                typeString = "Typ";
            }else{
                AblageortDAO ao_dao = new AblageortDAO();
                AblageortEntity newEntity = new AblageortEntity();
                newEntity.setBezeichnung(txtBezeichnung.getText());
                error = ao_dao.insertEntity(newEntity);
                typeString = "Ablageort";
            }
        }else{
            if(currentEntity instanceof TypEntity){
                TypDAO t_dao = new TypDAO();
                TypEntity newEntity = new TypEntity();
                newEntity.setBezeichnung(txtBezeichnung.getText());
                newEntity.setID(((TypEntity) currentEntity).getID());
                error = t_dao.updateEntity((TypEntity) currentEntity, newEntity);
                typeString = "Typ";
            }else{
                AblageortDAO ao_dao = new AblageortDAO();
                AblageortEntity newEntity = new AblageortEntity();
                newEntity.setID(((AblageortEntity) currentEntity).getID());
                newEntity.setBezeichnung(txtBezeichnung.getText());
                error = ao_dao.updateEntity((AblageortEntity) currentEntity, newEntity);
                typeString = "Ablageort";
            }
        }

        if (error == 1) { //TODO: Alerts in separate Klasse auslagern
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Fehler beim Speichern");
            alert.setHeaderText("Bezeichnung existiert bereits");
            alert.setContentText("Bei der Verarbeitung der "+typeString+"daten ist ein Fehler aufgetreten: Die Bezeichnung '" + txtBezeichnung.getText() + "' existiert bereits in der Datenbank. Bitte geben Sie eine gültige Bezeichnung ein.");
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    alert.close();
                }
            });
        } else if (error == 2) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Fehler beim Speichern");
            alert.setHeaderText("Datenbankfehler");
            alert.setContentText("Bei der Verarbeitung der "+typeString+"daten ist ein Fehler aufgetreten. Bitte wenden Sie sich an den Administrator.");
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    alert.close();
                }
            });
        }else{
            stage.close();
        }
    }


}
