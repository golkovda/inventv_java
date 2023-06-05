package com.golkov.inventv.controller;

import com.golkov.inventv.Globals;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class InfoViewController implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lblVersion.setText(Globals.version);
    }

    @FXML
    private Label lblVersion;
}
