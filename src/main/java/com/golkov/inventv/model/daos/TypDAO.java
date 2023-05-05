package com.golkov.inventv.model.daos;

import com.golkov.inventv.controller.NavigationViewController;
import com.golkov.inventv.model.HibernateUtil;
import com.golkov.inventv.model.entities.TypEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TypDAO implements IEntityDAO<TypEntity>{
    private static final Logger logger = LogManager.getLogger(NavigationViewController.class);

    private final SessionFactory sessionFactory;

    public TypDAO(){
        sessionFactory = HibernateUtil.getSessionFactory();
    }

    @Override
    public void addEntity(TypEntity entity) {
        //TODO
    }

    @Override
    public void updateEntity(TypEntity entity) {
        //TODO
    }

    @Override
    public void deleteEntity(int id) {
        //TODO
    }

    @Override
    public TypEntity getEntityById(int id) {
        //TODO
        return null;
    }

    public ObservableList<TypEntity> filterTyp(Integer typId, String bezeichnung) {
        logger.info("Getting TypEntities from Database and filtering for: ID="+typId.toString()+", bezeichnung="+bezeichnung);
        ObservableList<TypEntity> typList = FXCollections.observableArrayList();
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<TypEntity> query = builder.createQuery(TypEntity.class);
            Root<TypEntity> root = query.from(TypEntity.class);

            List<Predicate> predicates = new ArrayList<>();
            if (bezeichnung != null && !bezeichnung.isEmpty()) {
                predicates.add(builder.like(root.get("bezeichnung"), bezeichnung.replace('*', '%')));
            }
            if (typId != 0) {
                predicates.add(builder.equal(root.get("ID"), typId));
            }

            query.where(builder.and(predicates.toArray(new Predicate[0])));
            List<TypEntity> resultList = session.createQuery(query).getResultList();
            typList.addAll(resultList);
            tx.commit();
        } catch (HibernateException e) {
            logger.error("Exception occured while filtering data: "+ Arrays.toString(e.getStackTrace()));
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        logger.info(typList.stream().count()+" Element(s) found");
        return typList;
    }


    public ObservableList<TypEntity> getAllEntities() {
        logger.info("Trying to load all Entries from 'Typ' in an ObservableList<TypEntity>");
        ObservableList<TypEntity> typList = FXCollections.observableArrayList();
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            Query<TypEntity> query = session.createQuery("from TypEntity", TypEntity.class);
            List<TypEntity> resultList = query.getResultList();
            typList.addAll(resultList);
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
        logger.debug("Successfully loaded Typ-type Objects from Database");
        return typList;
    }
}
