package com.golkov.inventv;

import com.golkov.inventv.controller.AdministratorValidierungViewController;
import com.golkov.inventv.controller.ServerConnectionViewController;
import com.golkov.inventv.model.DatabaseConnectivityChecker;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import static com.golkov.inventv.InventVPreferences.saveServerCredentials;


public class Main extends Application {

    private static final Logger logger = LogManager.getLogger(Main.class);
    private static Stage primaryStage;
    private static final DatabaseConnectivityChecker checker = new DatabaseConnectivityChecker();

    public interface ServerConnectedCallback {
        void onServerConnected() throws IOException;
    }

    public interface AdministratorValidatedCallback {
        void onAdministratorValidated() throws IOException;
    }

    @Override
    public void start(Stage stage) throws IOException {
        URL url = this.getClass().getResource("/images/logo.png");
        stage.getIcons().add(new Image(url.openStream()));
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                checker.stopChecking();
            }
        });
        Main.primaryStage = stage;
        initializeWithServerInfo(primaryStage);
    }

    public static void restartApplication() {
        Platform.runLater(() -> {
            try {
                // Schließe das aktuelle Fenster
                primaryStage.close();

                // Starte die Anwendung erneut
                Platform.runLater(() -> {
                    try {
                        Main app = new Main();
                        Stage newStage = new Stage();
                        app.start(newStage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                //Platform.exit();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void initializeWithServerInfo(Stage stage) {
        try {
            logger.info("Initialising Scene...");
            logger.debug("Loading ServerConnectionView.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("views/ServerConnectionView.fxml"));
            Parent root = fxmlLoader.load();
            ServerConnectionViewController controller = fxmlLoader.getController();
            controller.setOnServerConnectedCallback(() -> {
                checker.setDaemon(true);
                checker.start();
                logger.debug("Loading AdministratorValidierungView.fxml");
                FXMLLoader fxmlLoader2 = new FXMLLoader(Main.class.getResource("views/AdministratorValidierungView.fxml"));
                Parent root2 = fxmlLoader2.load();
                AdministratorValidierungViewController controller2 = fxmlLoader2.getController();
                controller2.setOnAdministratorValidatedCallback(() -> {
                    logger.debug("Loading NavigationWindowView.fxml");
                    FXMLLoader fxmlLoader3 = new FXMLLoader(Main.class.getResource("views/NavigationWindowView.fxml"));
                    Scene scene = new Scene(fxmlLoader3.load(), 1280, 720);
                    scene.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("views/style.css")).toExternalForm());
                    stage.setTitle("InventV");
                    stage.setScene(scene);
                    stage.setResizable(true);
                    stage.show();
                });
                Scene scene = new Scene(root2, 600, 250);
                //scene.getStylesheets().add(Main.class..getResource("com.golkov.inventv.css/style.css").toExternalForm());
                stage.setTitle("InventV");
                stage.setScene(scene);
                stage.setResizable(false);
                stage.show();

            });
            Scene scene = new Scene(root, 600, 340);
            //scene.getStylesheets().add(Main.class..getResource("com.golkov.inventv.css/style.css").toExternalForm());
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
        //saveServerCredentials("", "", "", "", false);
        launch();

    }
}