package com.golkov.inventv.model.daos;

import com.golkov.inventv.controller.NavigationViewController;
import com.golkov.inventv.model.HibernateUtil;
import com.golkov.inventv.model.entities.*;
import com.golkov.inventv.model.entities.ObjektEntity;
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

public class ObjektDAO implements IEntityDAO<ObjektEntity> {
    private static final Logger logger = LogManager.getLogger(NavigationViewController.class);

    private final SessionFactory sessionFactory;

    public ObjektDAO() {
        sessionFactory = HibernateUtil.getSessionFactory();
    }

    public boolean istAusgeliehen(ObjektEntity objekt) {
        AusleiheDAO a_dao = new AusleiheDAO();
        BenutzerEntity null_benutzer = new BenutzerEntity();
        null_benutzer.setID(-1);
        ObservableList<AusleihEntity> ausleihen = a_dao.filterAusleihe(null_benutzer, objekt, LocalDate.of(1900, 1, 1));

        for (AusleihEntity a : ausleihen) {
            if (!a.isAbgegeben())
                return true;
        }
        return false;
    }

    public ObjektEntity getObjektById(int id){
        TypEntity null_entity = new TypEntity();
        AblageortEntity null_entity2 = new AblageortEntity();
        null_entity.setID(-1);
        null_entity2.setID(-1);
        return filterObjekt(id,0, "", "", LocalDate.of(1900, 1, 1), -1, null_entity, null_entity2).get(0);
    }

    public int getAnzahlObjekteByTyp(TypEntity typ) {
        AblageortEntity null_entity = new AblageortEntity();
        null_entity.setID(-1);
        return filterObjekt(0, "", "", LocalDate.of(1900, 1, 1), -1, typ, null_entity).size();
    }

    public int getAnzahlObjekteByAblageort(AblageortEntity aort) {
        TypEntity null_entity = new TypEntity();
        null_entity.setID(-1);
        return filterObjekt(0, "", "", LocalDate.of(1900, 1, 1), -1, null_entity, aort).size();
    }

    public ObservableList<ObjektEntity> filterObjekt(int id, Integer invnr, String hersteller, String modell, LocalDate kaufdatum, float einzelpreis, TypEntity typ, AblageortEntity ablageort) {
        logger.info("Getting ObjektEntities from Database and filtering for: inv.nr.=" + invnr.toString() + ", hersteller=" + hersteller + ", modell=" + modell + ", kaufdatum=" + kaufdatum.toString() + ", einzelpreis=" + einzelpreis + ", typ=" + typ.getBezeichnung() + ", ablageort=" + ablageort.getBezeichnung());
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
            if (kaufdatum.getYear() != 1900) {
                predicates.add(builder.equal(root.get("kaufdatum"), kaufdatum));
            }
            if (einzelpreis != -1) {
                predicates.add(builder.equal(root.get("einzelpreis"), einzelpreis));
            }
            if (typ.getID() != -1) {
                predicates.add(builder.equal(root.get("typ"), typ));
            }
            if (ablageort.getID() != -1) {
                predicates.add(builder.equal(root.get("ablageort"), ablageort));
            }
            if(id != -1){
                predicates.add(builder.equal(root.get("ID"), id));
            }

            query.where(builder.and(predicates.toArray(new Predicate[0])));
            List<ObjektEntity> resultList = session.createQuery(query).getResultList();
            objektList.addAll(resultList);
            tx.commit();
        } catch (HibernateException e) {
            logger.error("Exception occured while filtering data: " + Arrays.toString(e.getStackTrace()));
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        logger.info(objektList.stream().count() + " Element(s) found");
        return objektList;
    }

    public ObservableList<ObjektEntity> filterObjekt(Integer invnr, String hersteller, String modell, LocalDate kaufdatum, float einzelpreis, TypEntity typ, AblageortEntity ablageort) {
        return filterObjekt(-1, invnr, hersteller, modell, kaufdatum, einzelpreis, typ, ablageort);
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
            logger.error("Failed to load Entries from Database: " + Arrays.toString(e.getStackTrace()));
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

    public ObservableList<ObjektEntity> getAvailableObjektEntities() {
        AusleiheDAO a_dao = new AusleiheDAO();
        ObservableList<ObjektEntity> objektList = FXCollections.observableArrayList();

        for (AusleihEntity entity : a_dao.getAllEntities()) {
            if(entity.isAbgegeben())
                objektList.add(entity.getObjekt());
        }

        return objektList;

    }

    @Override
    public int updateEntity(ObjektEntity oldEntity, ObjektEntity newEntity) {
        logger.info("Trying to update ObjektEntity in the database");
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.merge(newEntity);
            transaction.commit();
            logger.debug("Successfully updated ObjektEntity in the database");
        } catch (Exception e) {
            logger.error("Failed to update ObjektEntity in the database: " + Arrays.toString(e.getStackTrace()));
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
    public int removeEntity(ObjektEntity entityToRemove) {
        logger.info("Trying to remove ObjektEntity from the database");
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            AusleiheDAO a_dao = new AusleiheDAO();

            if (a_dao.getAusleihenByObjekt(entityToRemove).stream().anyMatch(x -> !x.isAbgegeben()))
                return 1;

            session.remove(entityToRemove);
            transaction.commit();
            logger.debug("Successfully removed ObjektEntity from the database");
        } catch (Exception e) {
            logger.error("Failed to remove ObjektEntity from the database: " + Arrays.toString(e.getStackTrace()));
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
    public int insertEntity(ObjektEntity entityToInsert) {
        logger.info("Trying to insert ObjektEntity into the database");
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            // Check if another user with the same inventarnummer already exists
            Query<Long> countQuery = session.createQuery("select count(*) from ObjektEntity where inventarnummer = :invnr", Long.class);
            countQuery.setParameter("invnr", entityToInsert.getInventarnummer());
            long count = countQuery.getSingleResult();
            if (count > 0) {
                logger.error("Another objekt with the same inventarnummer already exists. Update operation aborted.");
                return 1;
            }

            // Überprüfe die Beziehungen und speichere sie gegebenenfalls separat
            TypEntity typ = entityToInsert.getTyp();
            if (typ != null && typ.getID() != 0) {
                typ = session.merge(typ);
                entityToInsert.setTyp(typ);
            } else
                return 2;

            AblageortEntity ablageort = entityToInsert.getAblageort();
            if (ablageort != null && ablageort.getID() != 0) {
                ablageort = session.merge(ablageort);
                entityToInsert.setAblageort(ablageort);
            } else
                return 2;

            // Speichere das Objekt
            session.persist(entityToInsert);

            transaction.commit();
            logger.debug("Successfully inserted ObjektEntity into the database");
        } catch (Exception e) {
            logger.error("Failed to insert ObjektEntity in the database: " + Arrays.toString(e.getStackTrace()));
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
