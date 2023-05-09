package com.golkov.inventv;

import com.golkov.inventv.controller.NavigationViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class Globals {

    //region Getters and setters

    public static String getCurrent_user() {
        return current_user;
    }

    public static void setCurrent_user(String current_user) {
        Globals.current_user = current_user;
    }


    //endregion

    public static String current_user;
    private static final Logger logger = LogManager.getLogger(Globals.class);


    public static void loadFreshPane(AnchorPane ap, String resource, int index){
        logger.info("Loading '"+resource+"' into AnchorPane");
        try {
            Node node;

            logger.debug("Retrieving resource: '"+resource+"'");
            node = (Node) FXMLLoader.load(Objects.requireNonNull(Main.class.getResource(resource)));
            ViewNavigation.push(index, node);

            logger.debug("Setting anchors...");
            ap.setLeftAnchor(node, 0.0);
            ap.setRightAnchor(node, 0.0);
            ap.setTopAnchor(node, 0.0);
            ap.setBottomAnchor(node, 0.0);

            ap.getChildren().setAll(node);
        }catch(Exception e){
            logger.error("Failed to load '"+resource+"' into AnchorPane");
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

    public static void loadExistingPane(AnchorPane ap, int index) {
        Node node = ViewNavigation.peek(index);
        ap.setLeftAnchor(node, 0.0);
        ap.setRightAnchor(node, 0.0);
        ap.setTopAnchor(node, 0.0);
        ap.setBottomAnchor(node, 0.0);
        ap.getChildren().setAll(node);
    }

}
