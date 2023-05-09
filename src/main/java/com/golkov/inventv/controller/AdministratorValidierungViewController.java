package com.golkov.inventv.controller;

import com.golkov.inventv.InventVPreferences;
import com.golkov.inventv.Main;
import com.golkov.inventv.model.HibernateUtil;
import com.golkov.inventv.model.daos.BenutzerDAO;
import com.golkov.inventv.model.entities.BenutzerEntity;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

import static com.golkov.inventv.InventVPreferences.saveServerCredentials;

public class AdministratorValidierungViewController implements Initializable {

    private Main.AdministratorValidatedCallback callback;

    private final String notAnAdmin = "Sie haben keine Berechtigung um das Programm zu bedienen. Bitte wenden Sie sich an einen Administrator.";
    private final String userNotFound = "Es wurde kein Benutzer mit der angegebenen Kennung gefunden. Bitte geben Sie eine gültige Benutzerkennung ein.";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    private Button btnCheckAccess;

    @FXML
    private Label lblNoAccess;

    @FXML
    private ProgressIndicator piAdministratorValidierung;

    @FXML
    private TextField txtBenutzerKennung;

    @FXML
    private PasswordField txtBenutzerPasswort;

    @FXML
    void checkAccessButtonTapped(ActionEvent event) {
        lblNoAccess.setVisible(false);
        piAdministratorValidierung.setVisible(true);

        BenutzerDAO b_dao = new BenutzerDAO();
        BenutzerEntity cu = b_dao.getEntityByKennung(txtBenutzerKennung.getText());
        if (cu != null) {
            if(cu.isAdministrator()) {
                if(callback != null) {
                    try {
                        InventVPreferences.saveUserCredentials(txtBenutzerKennung.getText(), txtBenutzerPasswort.getText());
                        callback.onAdministratorValidated();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }else{
                lblNoAccess.setText(notAnAdmin);
                lblNoAccess.setVisible(true);
            }
        }else{
            lblNoAccess.setText(userNotFound);
            lblNoAccess.setVisible(true);
        }
        piAdministratorValidierung.setVisible(false);

    }


    public void setOnAdministratorValidatedCallback(Main.AdministratorValidatedCallback callback) {
        this.callback = callback;
    }


}
