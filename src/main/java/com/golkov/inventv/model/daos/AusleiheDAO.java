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

    @Override
    public void addEntity(AusleihEntity entity) {
        //TODO
    }

    @Override
    public void updateEntity(AusleihEntity entity) {
        //TODO
    }

    @Override
    public void deleteEntity(int id) {
        //TODO
    }

    @Override
    public AusleihEntity getEntityById(int id) {
        //TODO
        return null;
    }

    public ObservableList<AusleihEntity> getAusleihenByBenutzer(BenutzerEntity benutzer){
        ObjektEntity null_entity = new ObjektEntity();
        null_entity.setID(-1);
        return filterAusleihe(benutzer, null_entity, LocalDate.of(1900,1,1));
    }

    public ObservableList<AusleihEntity> filterAusleihe(BenutzerEntity benutzer, ObjektEntity objekt, LocalDate ausleihdatum) {
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
}
