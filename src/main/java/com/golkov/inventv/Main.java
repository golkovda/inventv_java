package com.golkov.inventv;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


public class Main extends Application {

    private static final Logger logger = LogManager.getLogger(Main.class);

    @Override
    public void start(Stage stage) throws IOException {
        try {
            logger.info("Initialising Scene...");
            logger.debug("Loading StartbildschirmView.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("views/StartbildschirmView.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
            stage.setTitle("Hello!");
            stage.setScene(scene);
            stage.show();
            logger.info("Finished loading Scene");
        }catch(Exception e){
            logger.fatal("Initialization failed");
            logger.error(e.getMessage());
        }
    }

    public static void main(String[] args) {
        logger.info("Initialising program...");
        launch();
    }
}