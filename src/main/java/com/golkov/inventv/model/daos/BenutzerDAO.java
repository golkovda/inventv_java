package com.golkov.inventv.model.daos;

import com.golkov.inventv.controller.NavigationViewController;
import com.golkov.inventv.model.HibernateUtil;
import com.golkov.inventv.model.entities.AusleihEntity;
import com.golkov.inventv.model.entities.BenutzerEntity;
import com.golkov.inventv.model.entities.ObjektEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
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

public class BenutzerDAO implements IEntityDAO<BenutzerEntity> {
    private static Logger logger = LogManager.getLogger(NavigationViewController.class);

    private SessionFactory sessionFactory;

    private AusleiheDAO a_dao = new AusleiheDAO();


    public BenutzerDAO() {
        sessionFactory = HibernateUtil.getSessionFactory();
    }

    public void setAusleiheDAO(AusleiheDAO newDAO){
        a_dao = newDAO;
    }

    public void setSessionFactory(SessionFactory newSessionFactory){
        sessionFactory = newSessionFactory;
    }

    public void setLogger(Logger logger){
        BenutzerDAO.logger = logger;
    }

    public BenutzerEntity getEntityByKennung(String kennung){
       return filterBenutzer(0, kennung, "","").size() > 0 ? filterBenutzer(0, kennung, "","").get(0) : null;
    }

    public boolean hatOffeneAusleihen(BenutzerEntity benutzer) {
        ObjektEntity null_objekt = new ObjektEntity();
        null_objekt.setID(-1);
        ObservableList<AusleihEntity> ausleihen = a_dao.filterAusleihe(benutzer, null_objekt, LocalDate.of(1900, 1, 1));

        for (AusleihEntity a : ausleihen) {
            if (!a.isAbgegeben())
                return true;
        }
        return false;
    }

    public BenutzerEntity getBenutzerById(int id){
        return filterBenutzer(id, "","","").get(0);
    }


    public ObservableList<BenutzerEntity> filterBenutzer(Integer benutzerId, String kennung, String vorname, String nachname) {
        logger.info("Getting BenutzerEntities from Database and filtering for: ID=" + benutzerId.toString() + ", kennung=" + kennung + ", vorname=" + vorname + ", nachname=" + nachname);
        ObservableList<BenutzerEntity> benutzerList = FXCollections.observableArrayList();
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<BenutzerEntity> query = builder.createQuery(BenutzerEntity.class);
            Root<BenutzerEntity> root = query.from(BenutzerEntity.class);

            List<Predicate> predicates = new ArrayList<>();
            if (kennung != null && !kennung.isEmpty()) {
                predicates.add(builder.like(root.get("kennung"), kennung.replace('*', '%')));
            }
            if (vorname != null && !vorname.isEmpty()) {
                predicates.add(builder.like(root.get("vorname"), vorname.replace('*', '%')));
            }
            if (nachname != null && !nachname.isEmpty()) {
                predicates.add(builder.like(root.get("nachname"), nachname.replace('*', '%')));
            }
            if (benutzerId != 0) {
                predicates.add(builder.equal(root.get("ID"), benutzerId));
            }

            query.where(builder.and(predicates.toArray(new Predicate[0])));
            List<BenutzerEntity> resultList = session.createQuery(query).getResultList();
            benutzerList.addAll(resultList);
            tx.commit();
        } catch (HibernateException e) {
            logger.error("Exception occured while filtering data: " + Arrays.toString(e.getStackTrace()));
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        logger.info(benutzerList.stream().count() + " Element(s) found");
        return benutzerList;
    }


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
        return benutzerList;
    }

    @Override
    public int updateEntity(BenutzerEntity oldEntity, BenutzerEntity newEntity) { //0 = keine Fehler, 1 = Kennung fehler, 2 = sonstige
        logger.info("Trying to update BenutzerEntity in the database");
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            // Check if another user with the same kennung already exists
            Query<Long> countQuery = session.createQuery("select count(*) from BenutzerEntity where kennung = :kennung and ID != :id", Long.class);
            countQuery.setParameter("kennung", newEntity.getKennung());
            countQuery.setParameter("id", newEntity.getID());
            long count = countQuery.getSingleResult();
            if (count > 0) {
                logger.error("Another user with the same kennung already exists. Update operation aborted.");
                return 1;
            }

            session.merge(newEntity);
            transaction.commit();
            logger.debug("Successfully updated BenutzerEntity in the database");
        } catch (Exception e) {
            logger.error("Failed to update BenutzerEntity in the database: " + Arrays.toString(e.getStackTrace()));
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
    public int removeEntity(BenutzerEntity entityToRemove) { //0 = keine Fehler, 1 = Entfernen fehler, 2 = sonstige
        logger.info("Trying to remove BenutzerEntity from the database");
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            //Redundant: Siehe ObjektDAO.java an selber Stelle
            //if(a_dao.hatOffeneAusleihen(entityToRemove))
                //return 1;

            session.remove(entityToRemove);
            transaction.commit();
            logger.debug("Successfully removed BenutzerEntity from the database");
        } catch (Exception e) {
            logger.error("Failed to remove BenutzerEntity from the database: " + Arrays.toString(e.getStackTrace()));
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
    public int insertEntity(BenutzerEntity entityToInsert) {
        logger.info("Trying to insert BenutzerEntity into the database");
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            // Check if another user with the same kennung already exists
            Query<Long> countQuery = session.createQuery("select count(*) from BenutzerEntity where kennung = :kennung", Long.class);
            countQuery.setParameter("kennung", entityToInsert.getKennung());
            long count = countQuery.getSingleResult();
            if (count > 0) {
                logger.error("Another user with the same kennung already exists. Update operation aborted.");
                return 1;
            }

            session.persist(entityToInsert);
            transaction.commit();
            logger.debug("Successfully inserted BenutzerEntity into the database");
        } catch (Exception e) {
            logger.error("Failed to insert BenutzerEntity in the database: " + Arrays.toString(e.getStackTrace()));
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
