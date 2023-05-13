package com.golkov.inventv.model.daos;

import com.golkov.inventv.controller.NavigationViewController;
import com.golkov.inventv.model.HibernateUtil;
import com.golkov.inventv.model.entities.*;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AusleiheDAO implements IEntityDAO<AusleihEntity>{
    private static final Logger logger = LogManager.getLogger(NavigationViewController.class);

    private final SessionFactory sessionFactory;

    public AusleiheDAO(){
        sessionFactory = HibernateUtil.getSessionFactory();
    }

    public ObservableList<AusleihEntity> getAusleihenByBenutzer(BenutzerEntity benutzer){
        ObjektEntity null_entity = new ObjektEntity();
        null_entity.setID(-1);
        return filterAusleihe(benutzer, null_entity, LocalDate.of(1900,1,1));
    }

    public AusleihEntity getAusleiheByObjekt(ObjektEntity objekt, int abgegeben){
        BenutzerEntity null_entity = new BenutzerEntity();
        null_entity.setID(-1);
        ObservableList<AusleihEntity> foundEntites = filterAusleihe(null_entity, objekt, LocalDate.of(1900,1,1), abgegeben);
        if(foundEntites.size() == 1)
            return foundEntites.get(0);
        return null;
    }

    public ObservableList<AusleihEntity> getAusleihenByObjekt(ObjektEntity objekt){
        BenutzerEntity null_entity = new BenutzerEntity();
        null_entity.setID(-1);
        return filterAusleihe(null_entity, objekt, LocalDate.of(1900,1,1));
    }

    public boolean hatOffeneAusleihen(BenutzerEntity benutzer){
        ObjektEntity null_entity = new ObjektEntity();
        null_entity.setID(-1);
        ObservableList<AusleihEntity> ausleihList =  filterAusleihe(benutzer, null_entity, LocalDate.of(1900,1,1), 0); //Suche alle Ausleihen des Benutzers die nicht abgegeben wurden

        return ausleihList.size() > 0;
    }

    public ObservableList<AusleihEntity> filterAusleihe(BenutzerEntity benutzer, ObjektEntity objekt, LocalDate ausleihdatum, int abgegeben) { //abgegeben: -1 = ignore, 0 = nicht abgegeben, 1 = abgegeben
        logger.info("Getting AusleiheEntities from Database and filtering for: ausleihdatum="+ausleihdatum.toString()+", benutzer="+benutzer.getKennung()+", objekt="+objekt.getInventarnummer());
        ObservableList<AusleihEntity> ausleihList = FXCollections.observableArrayList();
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<AusleihEntity> query = builder.createQuery(AusleihEntity.class);
            Root<AusleihEntity> root = query.from(AusleihEntity.class);

            List<Predicate> predicates = new ArrayList<>();
            if (ausleihdatum.getYear() != 1900 ) {
                predicates.add(builder.equal(root.get("ausleihdatum"), ausleihdatum));
            }
            if(benutzer.getID() != -1){
                predicates.add(builder.equal(root.get("benutzer"), benutzer));
            }
            if(objekt.getID() != -1){
                predicates.add(builder.equal(root.get("objekt"), objekt));
            }
            if(abgegeben != -1){
                if(abgegeben == 0)
                    predicates.add(builder.equal(root.get("abgegeben"), false));
                else if(abgegeben == 1)
                    predicates.add(builder.equal(root.get("abgegeben"), true));
            }

            query.where(builder.and(predicates.toArray(new Predicate[0])));
            List<AusleihEntity> resultList = session.createQuery(query).getResultList();
            ausleihList.addAll(resultList);
            tx.commit();
        } catch (HibernateException e) {
            logger.error("Exception occured while filtering data: "+ Arrays.toString(e.getStackTrace()));
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        logger.info(ausleihList.stream().count()+" Element(s) found");
        return ausleihList;
    }

    public ObservableList<AusleihEntity> filterAusleihe(BenutzerEntity benutzer, ObjektEntity objekt, LocalDate ausleihdatum) {
        return filterAusleihe(benutzer, objekt, ausleihdatum, -1);
    }


    public ObservableList<AusleihEntity> getAllEntities() {
        logger.info("Trying to load all Entries from 'Ausleihe' in an ObservableList<AusleihEntity>");
        ObservableList<AusleihEntity> ausleihList = FXCollections.observableArrayList();
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            Query<AusleihEntity> query = session.createQuery("from AusleihEntity", AusleihEntity.class);
            List<AusleihEntity> resultList = query.getResultList();
            ausleihList.addAll(resultList);
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
        return ausleihList;
    }

    @Override
    public int updateEntity(AusleihEntity oldEntity, AusleihEntity newEntity) {
        logger.info("Trying to update AusleihEntity in the database");
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.merge(newEntity);
            transaction.commit();
            logger.debug("Successfully updated AusleihEntity in the database");
        } catch (Exception e) {
            logger.error("Failed to update AusleihEntity in the database: " + Arrays.toString(e.getStackTrace()));
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
    public int removeEntity(AusleihEntity entityToRemove) {
        logger.info("Trying to remove AusleihEntity from the database");
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            if (!entityToRemove.isAbgegeben())
                return 1;

            session.remove(entityToRemove);
            transaction.commit();
            logger.debug("Successfully removed AusleihEntity from the database");
        } catch (Exception e) {
            logger.error("Failed to remove AusleihEntity from the database: " + Arrays.toString(e.getStackTrace()));
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
    public int insertEntity(AusleihEntity entityToInsert) {
        logger.info("Trying to insert AusleihEntity into the database");
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            // Überprüfe die Beziehungen und speichere sie gegebenenfalls separat
            ObjektEntity objekt = entityToInsert.getObjekt();
            if (objekt != null && objekt.getID() != 0) {
                objekt = session.merge(objekt);
                entityToInsert.setObjekt(objekt);
            } else
                return 2;

            BenutzerEntity benutzer = entityToInsert.getBenutzer();
            if (benutzer != null && benutzer.getID() != 0) {
                benutzer = session.merge(benutzer);
                entityToInsert.setBenutzer(benutzer);
            } else
                return 2;

            // Speichere das Objekt
            session.persist(entityToInsert);

            transaction.commit();
            logger.debug("Successfully inserted AusleihEntity into the database");
        } catch (Exception e) {
            logger.error("Failed to insert AusleihEntity in the database: " + Arrays.toString(e.getStackTrace()));
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
