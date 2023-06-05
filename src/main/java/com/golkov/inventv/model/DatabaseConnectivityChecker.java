package com.golkov.inventv.model;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class DatabaseConnectivityChecker extends Thread {
    private boolean running = true;
    private long interval = 5000; // Überprüfungsintervall in Millisekunden

    public void run() {
        while (running) {
            System.out.print("Connection Test: ");
            try {
                // Überprüfen Sie hier die Datenbankverbindung
                // Verwenden Sie eine geeignete Methode, um die Verbindung zu testen
                boolean isConnected = testDatabaseConnection();

                if (!isConnected) {
                    System.out.println("fail");
                    Platform.runLater(this::showConnectionErrorDialog);

                    // Warten Sie, bis der Benutzer "Erneut Verbinden" auswählt
                    synchronized (this) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }else{
                    System.out.println("success");
                    interval = 5000;
                }

                Thread.sleep(interval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void showConnectionErrorDialog() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Verbindungsfehler");
        alert.setHeaderText("Datenbankverbindung getrennt");
        alert.setContentText("Die Datenbankverbindung wurde getrennt. Bitte prüfen Sie ihre Verbindung und verbinden Sie sich erneut.");

        ButtonType erneutVerbinden = new ButtonType("Erneut Verbinden");
        ButtonType beenden = new ButtonType("Beenden");

        alert.getButtonTypes().setAll(erneutVerbinden, beenden);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == erneutVerbinden) {
            interval = 500;
            synchronized (this) {
                notify();
            }
        } else {
            System.exit(0);
        }
    }

    public void stopChecking() {
        running = false;
        Thread.currentThread().interrupt();
    }

    private boolean testDatabaseConnection() {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        if(sessionFactory != null && !sessionFactory.isClosed()){
            Session session = sessionFactory.openSession();
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                session.close();
                return true;
            }catch  (Exception e) {
                session.close();
                return false;
            }
        }
        return false;
    }
}

