package com.golkov.inventv.model.daos;

import com.golkov.inventv.controller.NavigationViewController;
import com.golkov.inventv.model.HibernateUtil;
import com.golkov.inventv.model.entities.AblageortEntity;
import com.golkov.inventv.model.entities.ObjektEntity;
import com.golkov.inventv.model.entities.ObjektEntity;
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

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ObjektDAO implements IEntityDAO<ObjektEntity>{
    private static final Logger logger = LogManager.getLogger(NavigationViewController.class);

    private final SessionFactory sessionFactory;

    public ObjektDAO(){
        sessionFactory = HibernateUtil.getSessionFactory();
    }

    @Override
    public void addEntity(ObjektEntity entity) {
        //TODO
    }

    @Override
    public void updateEntity(ObjektEntity entity) {
        //TODO
    }

    @Override
    public void deleteEntity(int id) {
        //TODO
    }

    @Override
    public ObjektEntity getEntityById(int id) {
        //TODO
        return null;
    }

    public ObservableList<ObjektEntity> filterObjekt(Integer objektId, Integer invnr, String hersteller, String modell, LocalDate kaufdatum, float einzelpreis, TypEntity typ, AblageortEntity ablageort) {
        logger.info("Getting ObjektEntities from Database and filtering for: ID="+objektId.toString()+", inv.nr.="+invnr.toString()+", hersteller="+hersteller+", modell="+modell+", kaufdatum="+kaufdatum.toString()+", einzelpreis="+einzelpreis+", typ="+typ.getBezeichnung()+", ablageort="+ablageort.getBezeichnung());
        ObservableList<ObjektEntity> objektList = FXCollections.observableArrayList();
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<ObjektEntity> query = builder.createQuery(ObjektEntity.class);
            Root<ObjektEntity> root = query.from(ObjektEntity.class);

            List<Predicate> predicates = new ArrayList<>();
            if (invnr != 0) {
                predicates.add(builder.equal(root.get("inventarnummer"), invnr));
            }
            if (hersteller != null && !hersteller.isEmpty()) {
                predicates.add(builder.like(root.get("hersteller"), hersteller.replace('*', '%')));
            }
            if (modell != null && !modell.isEmpty()) {
                predicates.add(builder.like(root.get("modell"), modell.replace('*', '%')));
            }
            if (kaufdatum.getYear() != 1900 ) {
                predicates.add(builder.equal(root.get("kaufdatum"), kaufdatum));
            }
            if (einzelpreis != -1 ) {
                predicates.add(builder.equal(root.get("einzelpreis"), einzelpreis));
            }
            if(typ.getID() != -1){
                predicates.add(builder.equal(root.get("typ"), typ));
            }
            if(ablageort.getID() != -1){
                predicates.add(builder.equal(root.get("ablageort"), ablageort));
            }
            if (objektId != 0) {
                predicates.add(builder.equal(root.get("ID"), objektId));
            }

            query.where(builder.and(predicates.toArray(new Predicate[0])));
            List<ObjektEntity> resultList = session.createQuery(query).getResultList();
            objektList.addAll(resultList);
            tx.commit();
        } catch (HibernateException e) {
            logger.error("Exception occured while filtering data: "+ Arrays.toString(e.getStackTrace()));
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        logger.info(objektList.stream().count()+" Element(s) found");
        return objektList;
    }


    public ObservableList<ObjektEntity> getAllEntities() {
        logger.info("Trying to load all Entries from 'Objekt' in an ObservableList<ObjektEntity>");
        ObservableList<ObjektEntity> objektList = FXCollections.observableArrayList();
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            Query<ObjektEntity> query = session.createQuery("from ObjektEntity", ObjektEntity.class);
            List<ObjektEntity> resultList = query.getResultList();
            objektList.addAll(resultList);
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
        return objektList;
    }
}
