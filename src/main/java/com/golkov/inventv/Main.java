package com.golkov.inventv;

import com.golkov.inventv.controller.AdministratorValidierungViewController;
import com.golkov.inventv.controller.ServerConnectionViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


public class Main extends Application {

    private static final Logger logger = LogManager.getLogger(Main.class);

    public interface ServerConnectedCallback {
        void onServerConnected() throws IOException;
    }

    public interface AdministratorValidatedCallback {
        void onAdministratorValidated() throws IOException;
    }

    @Override
    public void start(Stage stage) throws IOException {
        initializeWithServerInfo(stage);
    }

    private static void initializeWithServerInfo(Stage stage) {



        try {
            logger.info("Initialising Scene...");
            logger.debug("Loading ServerConnectionView.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("views/ServerConnectionView.fxml"));
            Parent root = fxmlLoader.load();
            ServerConnectionViewController controller = fxmlLoader.getController();
            controller.setOnServerConnectedCallback(() -> {
                logger.debug("Loading AdministratorValidierungView.fxml");
                FXMLLoader fxmlLoader2 = new FXMLLoader(Main.class.getResource("views/AdministratorValidierungView.fxml"));
                Parent root2 = fxmlLoader2.load();
                AdministratorValidierungViewController controller2 = fxmlLoader2.getController();
                controller2.setOnAdministratorValidatedCallback(() -> {
                    logger.debug("Loading NavigationWindowView.fxml");
                    FXMLLoader fxmlLoader3 = new FXMLLoader(Main.class.getResource("views/NavigationWindowView.fxml"));
                    Scene scene = new Scene(fxmlLoader3.load(), 1280, 720);
                    stage.setTitle("InventV");
                    stage.setScene(scene);
                    stage.setResizable(true);
                    stage.show();
                });
                Scene scene = new Scene(root2, 600, 250);
                //scene.getStylesheets().add(Main.class..getResource("com.golkov.inventv.css/buttonstyles.css").toExternalForm());
                stage.setTitle("InventV");
                stage.setScene(scene);
                stage.setResizable(false);
                stage.show();

            });
            Scene scene = new Scene(root, 600, 340);
            //scene.getStylesheets().add(Main.class..getResource("com.golkov.inventv.css/buttonstyles.css").toExternalForm());
            stage.setTitle("InventV");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
            logger.info("Finished loading Scene");
        } catch (Exception e) {
            logger.fatal("Initialization failed");
            logger.error(e.getMessage());
        }
    }


    public static void main(String[] args) {
        logger.info("Initialising program...");
        launch();
    }
}