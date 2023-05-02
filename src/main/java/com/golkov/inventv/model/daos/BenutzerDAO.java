package com.golkov.inventv.model.daos;

import com.golkov.inventv.controller.NavigationViewController;
import com.golkov.inventv.model.HibernateUtil;
import com.golkov.inventv.model.entities.BenutzerEntity;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BenutzerDAO implements IEntityDAO<BenutzerEntity>{
    private static final Logger logger = LogManager.getLogger(NavigationViewController.class);

    private final SessionFactory sessionFactory;

    public BenutzerDAO(){
        sessionFactory = HibernateUtil.getSessionFactory();
    }

    @Override
    public void addEntity(BenutzerEntity entity) {
        //TODO
    }

    @Override
    public void updateEntity(BenutzerEntity entity) {
        //TODO
    }

    @Override
    public void deleteEntity(int id) {
        //TODO
    }

    @Override
    public BenutzerEntity getEntityById(int id) {
        //TODO
        return null;
    }

    //@Override
    public ObservableList<BenutzerEntity> getAllEntities() {
        logger.info("Trying to load all Entries from 'Benutzer' in an ObservableList<BenutzerEntity>");
        ObservableList<BenutzerEntity> benutzerList = FXCollections.observableArrayList();
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            Query<BenutzerEntity> query = session.createQuery("from BenutzerEntity", BenutzerEntity.class);
            List<BenutzerEntity> resultList = query.getResultList();
            benutzerList.addAll(resultList);
            transaction.commit();
        } catch (Exception e) {
            logger.error("Failed to load Entries from Database: "+ Arrays.toString(e.getStackTrace()));
            if (transaction != null) {
                logger.info("Rolling back transaction...");
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        logger.debug("Successfully loaded Benutzer-type Objects from Database");
        return benutzerList;
    }
}
