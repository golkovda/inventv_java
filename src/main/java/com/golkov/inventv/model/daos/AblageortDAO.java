package com.golkov.inventv.model.daos;

import com.golkov.inventv.controller.NavigationViewController;
import com.golkov.inventv.model.HibernateUtil;
import com.golkov.inventv.model.entities.AblageortEntity;
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

public class AblageortDAO implements IEntityDAO<AblageortEntity>{
    private static final Logger logger = LogManager.getLogger(NavigationViewController.class);

    private final SessionFactory sessionFactory;

    public AblageortDAO() {
        sessionFactory = HibernateUtil.getSessionFactory();
    }

    public AblageortEntity getAblageortById(int id){
        return filterAblageort(id,"").get(0);
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

    public long getEntityCount() {
        logger.info("Trying to get the count of all entries in 'Ablageort' table");
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        long count = 0;
        try {
            transaction = session.beginTransaction();
            Query<Long> query = session.createQuery("select count(*) from AblageortEntity", Long.class);
            count = query.uniqueResult();
            transaction.commit();
        } catch (Exception e) {
            logger.error("Failed to get count of entries from 'Ablageort' table: " + Arrays.toString(e.getStackTrace()));
            if (transaction != null) {
                logger.info("Rolling back transaction...");
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        logger.debug("Successfully retrieved count of entries from 'Ablageort' table");
        return count;
    }

    @Override
    public int updateEntity(AblageortEntity oldEntity, AblageortEntity newEntity) {
        logger.info("Trying to update AblageortEntity in the database");
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            // Check if another user with the same kennung already exists
            Query<Long> countQuery = session.createQuery("select count(*) from AblageortEntity where bezeichnung = :bez and ID != :id", Long.class);
            countQuery.setParameter("bez", newEntity.getBezeichnung());
            countQuery.setParameter("id", newEntity.getID());
            long count = countQuery.getSingleResult();
            if (count > 0) {
                logger.error("Another ablageort with the same bezeichnung already exists. Update operation aborted.");
                return 1;
            }

            session.merge(newEntity);
            transaction.commit();
            logger.debug("Successfully updated AblageortEntity in the database");
        } catch (Exception e) {
            logger.error("Failed to update AblageortEntity in the database: " + Arrays.toString(e.getStackTrace()));
            if (transaction != null) {
                logger.info("Rolling back transaction...");
                transaction.rollback();
            }
            e.printStackTrace();
            return 2;
        } finally {
            session.close();
        }
        return 0;
    }

    @Override
    public int removeEntity(AblageortEntity entityToRemove) {
        logger.info("Trying to remove AblageortEntity from the database");
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            ObjektDAO o_dao = new ObjektDAO();

            if(o_dao.getAnzahlObjekteByAblageort(entityToRemove) > 0)
                return 1;

            session.remove(entityToRemove);
            transaction.commit();
            logger.debug("Successfully removed AblageortEntity from the database");
        } catch (Exception e) {
            logger.error("Failed to remove AblageortEntity from the database: " + Arrays.toString(e.getStackTrace()));
            if (transaction != null) {
                logger.info("Rolling back transaction...");
                transaction.rollback();
            }
            e.printStackTrace();
            return 2;
        } finally {
            session.close();
        }
        return 0;
    }

    @Override
    public int insertEntity(AblageortEntity entityToInsert) {
        logger.info("Trying to insert AblageortEntity into the database");
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            // Check if another user with the same kennung already exists
            Query<Long> countQuery = session.createQuery("select count(*) from AblageortEntity where bezeichnung = :bez", Long.class);
            countQuery.setParameter("bez", entityToInsert.getBezeichnung());
            long count = countQuery.getSingleResult();
            if (count > 0) {
                logger.error("Another Ablageort with the same bezeichnung already exists. Update operation aborted.");
                return 1;
            }

            session.persist(entityToInsert);
            transaction.commit();
            logger.debug("Successfully inserted AblageortEntity into the database");
        } catch (Exception e) {
            logger.error("Failed to insert AblageortEntity in the database: " + Arrays.toString(e.getStackTrace()));
            if (transaction != null) {
                logger.info("Rolling back transaction...");
                transaction.rollback();
            }
            e.printStackTrace();
            return 2;
        } finally {
            session.close();
        }
        return 0;
    }
}
