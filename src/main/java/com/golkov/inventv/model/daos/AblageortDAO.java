package com.golkov.inventv.model.daos;

import com.golkov.inventv.controller.NavigationViewController;
import com.golkov.inventv.model.HibernateUtil;
import com.golkov.inventv.model.entities.AblageortEntity;
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

public class AblageortDAO implements IEntityDAO<AblageortEntity>{
    private static final Logger logger = LogManager.getLogger(NavigationViewController.class);

    private final SessionFactory sessionFactory;

    public AblageortDAO() {
        sessionFactory = HibernateUtil.getSessionFactory();
    }

    public ObservableList<AblageortEntity> filterAblageort(Integer ablageortId, String bezeichnung) {
        logger.info("Getting AblageortEntities from Database and filtering for: ID="+ablageortId.toString()+", bezeichnung="+bezeichnung);
        ObservableList<AblageortEntity> ablageortList = FXCollections.observableArrayList();
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<AblageortEntity> query = builder.createQuery(AblageortEntity.class);
            Root<AblageortEntity> root = query.from(AblageortEntity.class);

            List<Predicate> predicates = new ArrayList<>();
            if (bezeichnung != null && !bezeichnung.isEmpty()) {
                predicates.add(builder.like(root.get("bezeichnung"), bezeichnung.replace('*', '%')));
            }
            if (ablageortId != 0) {
                predicates.add(builder.equal(root.get("ID"), ablageortId));
            }

            query.where(builder.and(predicates.toArray(new Predicate[0])));
            List<AblageortEntity> resultList = session.createQuery(query).getResultList();
            ablageortList.addAll(resultList);
            tx.commit();
        } catch (HibernateException e) {
            logger.error("Exception occured while filtering data: "+ Arrays.toString(e.getStackTrace()));
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        logger.info(ablageortList.stream().count()+" Element(s) found");
        return ablageortList;
    }


    public ObservableList<AblageortEntity> getAllEntities() {
        logger.info("Trying to load all Entries from 'Ablageort' in an ObservableList<AblageortEntity>");
        ObservableList<AblageortEntity> ablageortList = FXCollections.observableArrayList();
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            Query<AblageortEntity> query = session.createQuery("from AblageortEntity", AblageortEntity.class);
            List<AblageortEntity> resultList = query.getResultList();
            ablageortList.addAll(resultList);
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
        logger.debug("Successfully loaded Ablageort-type Objects from Database");
        return ablageortList;
    }

    @Override
    public int updateEntity(AblageortEntity oldEntity, AblageortEntity newEntity) {
        return 0;

    }

    @Override
    public int removeEntity(AblageortEntity entityToRemove) {
        return 0;

    }

    @Override
    public int insertEntity(AblageortEntity entityToInsert) {
        return 0;

    }
}
