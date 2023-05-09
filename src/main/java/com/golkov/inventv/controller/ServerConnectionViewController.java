package com.golkov.inventv.controller;

import com.golkov.inventv.InventVPreferences;
import com.golkov.inventv.Main;
import com.golkov.inventv.model.HibernateUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.service.spi.ServiceException;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

import static com.golkov.inventv.InventVPreferences.*;

public class ServerConnectionViewController implements Initializable {

    private Main.ServerConnectedCallback callback;
    public boolean isConnected = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (Objects.equals(isDefaultServerSet(), "true")) {
            btnServerConnect.setDisable(true);
            checkSetStandardConnection.setDisable(true);
            checkSetStandardConnection.setSelected(true);
            txtIP.setDisable(true);
            txtServerBenutzername.setDisable(true);
            txtServerPasswort.setDisable(true);

            txtIP.setText(getShortServerUrl());
            txtServerBenutzername.setText(getServerUsername());
            txtServerPasswort.setText(getServerPassword());

            tryConnection();
        }
    }

    @FXML
    private Button btnServerConnect;

    @FXML
    private CheckBox checkSetStandardConnection;

    @FXML
    private Label lblConnectionFailed;

    @FXML
    private ProgressIndicator piDatabaseConnection;

    @FXML
    private TextField txtIP;

    @FXML
    private TextField txtServerBenutzername;

    @FXML
    private PasswordField txtServerPasswort;

    @FXML
    void connectToServerButtonTapped(ActionEvent event) {
        tryConnection();
    }

    private void tryConnection() {
        lblConnectionFailed.setVisible(false);
        piDatabaseConnection.setVisible(true);
        saveServerCredentials(txtIP.getText(), "jdbc:jtds:sqlserver://" + txtIP.getText() + "/InventV2", txtServerBenutzername.getText(), txtServerPasswort.getText(), checkSetStandardConnection.selectedProperty().getValue());

        CompletableFuture.supplyAsync(HibernateUtil::getSessionFactory)
                .thenAcceptAsync(sessionFactory -> {
                    if (sessionFactory == null) {
                        lblConnectionFailed.setVisible(true);
                        piDatabaseConnection.setVisible(false);
                        btnServerConnect.setDisable(false);
                        checkSetStandardConnection.setDisable(false);
                        txtIP.setDisable(false);
                        txtServerBenutzername.setDisable(false);
                        txtServerPasswort.setDisable(false);
                        checkSetStandardConnection.setSelected(false);
                    } else {
                        if (callback != null) {
                            try {
                                callback.onServerConnected();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }, Platform::runLater)
                .exceptionally(ex -> {
                    // handle any exceptions that may occur during the sessionFactory creation
                    ex.printStackTrace();
                    return null;
                });
    }

    public void setOnServerConnectedCallback(Main.ServerConnectedCallback callback) {
        this.callback = callback;
        System.out.println("Callback initialized: " + (callback != null));
    }


}
