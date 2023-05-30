package com.golkov.inventv.model_tests.dao_tests;

import com.golkov.inventv.model.daos.AusleiheDAO;
import com.golkov.inventv.model.daos.BenutzerDAO;
import com.golkov.inventv.model.entities.AusleihEntity;
import com.golkov.inventv.model.entities.BenutzerEntity;
import com.golkov.inventv.model.entities.ObjektEntity;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.hibernate.query.criteria.JpaCriteriaQuery;
import org.hibernate.query.criteria.JpaRoot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.golkov.inventv.TestBase;

public class BenutzerDAO_Tests {

    TestBase testBase = new TestBase();

    private BenutzerDAO benutzerDAO;
    private AusleiheDAO ausleiheDAO;
    private ObservableList<BenutzerEntity> benutzer_mock;
    private ObservableList<AusleihEntity> ausleih_mock;
    private ObservableList<ObjektEntity> objekt_mock;

    private SessionFactory mock_sf;
    private Logger mock_logger;

    @BeforeEach
    public void setUp() {
        // Ein Spy-Objekt ermöglicht es, die tatsächliche Implementierung der Methode beizubehalten, aber dennoch bestimmte Methodenaufrufe zu überwachen und zu steuern
        benutzerDAO = spy(BenutzerDAO.class);
        ausleiheDAO = spy(AusleiheDAO.class);

        mock_sf = Mockito.mock(SessionFactory.class);
        mock_logger = Mockito.mock(Logger.class);

        benutzerDAO.setAusleiheDAO(ausleiheDAO);
        benutzerDAO.setSessionFactory(mock_sf); // Setzen Sie die SessionFactory in den BenutzerDAO
        benutzerDAO.setLogger(mock_logger); // Setzen Sie den Logger in den BenutzerDAO

        benutzer_mock = testBase.getBenutzer_mock();
        ausleih_mock = testBase.getAusleih_mock();
        objekt_mock = testBase.getObjekt_mock();
    }

    @Test
    public void testGetEntityByKennung() {
        // Arrange
        String benutzerKennung = "golkovda"; //Erwartete Benutzerkennung

        // Mocking der filterBenutzer-Methode
        ObservableList<BenutzerEntity> mockList = FXCollections.observableArrayList();
        mockList.add(benutzer_mock.get(0)); //MockList soll erstes Objekt der benutzer_mock-Liste enthalten (golkovda)
        doReturn(mockList) //Gib MockList zurück, wenn filterBenutzer aufgerufen wird
                .when(benutzerDAO)
                .filterBenutzer(0, benutzerKennung, "", "");

        // Act
        BenutzerEntity actualEntity = benutzerDAO.getEntityByKennung(benutzerKennung);

        // Assert
        assertNotNull(actualEntity); //actualEntity darf nicht null sein
        assertEquals(benutzerKennung, actualEntity.getKennung());
        verify(benutzerDAO, times(2))
                .filterBenutzer(0, benutzerKennung, "", "");
    }

    @Test
    public void testHatOffeneAusleihen(){
        boolean expectedValue = true; //Erwarteter Wert
        boolean expectedValue2 = false;
        BenutzerEntity golkovda_benutzer = benutzer_mock.get(0);
        BenutzerEntity golkovma_benutzer = benutzer_mock.get(1);

        ObservableList<AusleihEntity> mockList = FXCollections.observableArrayList();
        mockList.add(ausleih_mock.get(0));
        mockList.add(ausleih_mock.get(1));
        doReturn(mockList) //Gib MockList zurück, wenn filterBenutzer aufgerufen wird
                .when(ausleiheDAO)
                .filterAusleihe(eq(golkovda_benutzer), any(ObjektEntity.class), any(LocalDate.class));

        doReturn(FXCollections.observableArrayList()) //Gib MockList zurück, wenn filterBenutzer aufgerufen wird
                .when(ausleiheDAO)
                .filterAusleihe(eq(golkovma_benutzer), any(ObjektEntity.class), any(LocalDate.class));

        boolean actualValue = benutzerDAO.hatOffeneAusleihen(golkovda_benutzer);
        boolean actualValue2 = benutzerDAO.hatOffeneAusleihen(golkovma_benutzer);

        assertEquals(expectedValue, actualValue); //golkovda hat eine offene Ausleihe, also muss true sein
        assertEquals(expectedValue2, actualValue2); //golkovma hat keine offene Ausleihe, also muss false sein

        verify(ausleiheDAO, times(2)) //filterAusleihe muss 2 mal aufgerufen werden
                .filterAusleihe(any(BenutzerEntity.class), any(ObjektEntity.class), any(LocalDate.class));
    }

    @Test
    public void testFilterBenutzer() {
        // Arrange
        Integer benutzerId = 1;
        String kennung = "john.doe";
        String vorname = "John";
        String nachname = "Doe";

        Session session = Mockito.mock(Session.class);
        Transaction tx = Mockito.mock(Transaction.class);
        CriteriaBuilder builder = Mockito.mock(HibernateCriteriaBuilder.class);
        CriteriaQuery<BenutzerEntity> query = Mockito.mock(JpaCriteriaQuery.class);
        Root<BenutzerEntity> root = Mockito.mock(JpaRoot.class);
        TypedQuery<BenutzerEntity> typedQuery = Mockito.mock(Query.class);

        ObservableList<BenutzerEntity> expectedResult = FXCollections.observableArrayList();
        BenutzerEntity benutzer = new BenutzerEntity();
        expectedResult.add(benutzer);

        when(mock_sf.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(tx);
        when(session.getCriteriaBuilder()).thenReturn((HibernateCriteriaBuilder) builder);
        when(builder.createQuery(BenutzerEntity.class)).thenReturn(query);
        when(query.from(BenutzerEntity.class)).thenReturn(root);
        when(session.createQuery(query)).thenReturn((Query<BenutzerEntity>) typedQuery);
        when(typedQuery.getResultList()).thenReturn(expectedResult);
        when(mock_sf.getCurrentSession()).thenReturn(session);

        // Act
        ObservableList<BenutzerEntity> actualResult = benutzerDAO.filterBenutzer(benutzerId, kennung, vorname, nachname);

        // Assert
        assertEquals(expectedResult, actualResult);
        verify(mock_logger).info("Getting BenutzerEntities from Database and filtering for: ID=1, kennung=john.doe, vorname=John, nachname=Doe");
        verify(tx).commit();
        verify(session).close();
        verify(mock_logger).info("1 Element(s) found");
    }

    @Test
    public void testGetAllEntites() {
        // Arrange
        Session session = Mockito.mock(Session.class);
        Transaction tx = Mockito.mock(Transaction.class);
        Query<BenutzerEntity> query = Mockito.mock(Query.class);

        ObservableList<BenutzerEntity> expectedResult = FXCollections.observableArrayList();
        BenutzerEntity benutzer1 = new BenutzerEntity();
        BenutzerEntity benutzer2 = new BenutzerEntity();
        expectedResult.add(benutzer1);
        expectedResult.add(benutzer2);

        when(mock_sf.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(tx);
        when(session.createQuery("from BenutzerEntity", BenutzerEntity.class)).thenReturn(query);
        when(query.getResultList()).thenReturn(expectedResult);

        // Act
        ObservableList<BenutzerEntity> actualResult = benutzerDAO.getAllEntities();

        // Assert
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testUpdateEntity_Successful() {
        // Arrange
        Session session = Mockito.mock(Session.class);
        Transaction tx = Mockito.mock(Transaction.class);
        Query<Long> countQuery = Mockito.mock(Query.class);

        BenutzerEntity oldEntity = new BenutzerEntity();
        BenutzerEntity newEntity = new BenutzerEntity();
        newEntity.setID(1);
        newEntity.setKennung("newKennung");

        when(mock_sf.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(tx);
        when(session.createQuery("select count(*) from BenutzerEntity where kennung = :kennung and ID != :id", Long.class)).thenReturn(countQuery);
        when(countQuery.getSingleResult()).thenReturn(0L);

        // Act
        int result = benutzerDAO.updateEntity(oldEntity, newEntity);

        // Assert
        assertEquals(0, result);
        verify(session).merge(newEntity);
        verify(tx).commit();
    }

    @Test
    public void testUpdateEntity_KennungAlreadyExists() {
        // Arrange
        Session session = Mockito.mock(Session.class);
        Transaction tx = Mockito.mock(Transaction.class);
        Query<Long> countQuery = Mockito.mock(Query.class);

        BenutzerEntity oldEntity = new BenutzerEntity();
        BenutzerEntity newEntity = new BenutzerEntity();
        newEntity.setID(1);
        newEntity.setKennung("newKennung");

        when(mock_sf.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(tx);
        when(session.createQuery("select count(*) from BenutzerEntity where kennung = :kennung and ID != :id", Long.class)).thenReturn(countQuery);
        when(countQuery.getSingleResult()).thenReturn(1L);

        // Act
        int result = benutzerDAO.updateEntity(oldEntity, newEntity);

        // Assert
        assertEquals(1, result);
        verify(mock_logger).error("Another user with the same kennung already exists. Update operation aborted.");
        verify(tx, never()).commit();
    }

    @Test
    public void testUpdateEntity_Exception() {
        // Arrange
        Session session = Mockito.mock(Session.class);
        Transaction tx = Mockito.mock(Transaction.class);
        Query<Long> countQuery = Mockito.mock(Query.class);

        BenutzerEntity oldEntity = new BenutzerEntity();
        BenutzerEntity newEntity = new BenutzerEntity();
        newEntity.setID(1);
        newEntity.setKennung("newKennung");

        when(mock_sf.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(tx);
        when(session.createQuery("select count(*) from BenutzerEntity where kennung = :kennung and ID != :id", Long.class)).thenReturn(countQuery);
        when(countQuery.getSingleResult()).thenThrow(new RuntimeException());

        // Act
        int result = benutzerDAO.updateEntity(oldEntity, newEntity);

        // Assert
        assertEquals(2, result);
        //verify(mock_logger).error("Failed to update BenutzerEntity in the database:");
        verify(tx).rollback();
    }

    @Test
    public void testRemoveEntity_Successful() {
        // Arrange
        Session session = Mockito.mock(Session.class);
        Transaction tx = Mockito.mock(Transaction.class);
        BenutzerEntity entityToRemove = new BenutzerEntity();

        when(mock_sf.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(tx);
        when(ausleiheDAO.hatOffeneAusleihen(entityToRemove)).thenReturn(false);

        // Act
        int result = benutzerDAO.removeEntity(entityToRemove);

        // Assert
        assertEquals(0, result);
        verify(session).remove(entityToRemove);
        verify(tx).commit();
    }

    @Test
    public void testRemoveEntity_HatOffeneAusleihen() {
        // Arrange
        Session session = Mockito.mock(Session.class);
        Transaction tx = Mockito.mock(Transaction.class);
        AusleiheDAO ausleiheDAO = Mockito.mock(AusleiheDAO.class);
        BenutzerEntity entityToRemove = new BenutzerEntity();

        when(mock_sf.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(tx);
        when(ausleiheDAO.hatOffeneAusleihen(entityToRemove)).thenReturn(true);

        // Act
        int result = benutzerDAO.removeEntity(entityToRemove);

        // Assert
        assertEquals(1, result);
        verify(mock_logger).error("Unable to remove BenutzerEntity. Open Ausleihen exist.");
        verify(tx, never()).commit();
    }

    @Test
    public void testRemoveEntity_Exception() {
        // Arrange
        Session session = Mockito.mock(Session.class);
        Transaction tx = Mockito.mock(Transaction.class);
        AusleiheDAO ausleiheDAO = Mockito.mock(AusleiheDAO.class);
        BenutzerEntity entityToRemove = new BenutzerEntity();

        when(mock_sf.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(tx);
        when(ausleiheDAO.hatOffeneAusleihen(entityToRemove)).thenReturn(false);
        doThrow(new RuntimeException()).when(session).remove(entityToRemove);

        // Act
        int result = benutzerDAO.removeEntity(entityToRemove);

        // Assert
        assertEquals(2, result);
        //verify(logger).error("Failed to remove BenutzerEntity from the database:");
        verify(tx).rollback();
    }

    @Test
    public void testInsertEntity_Successful() {
        // Arrange
        Session session = Mockito.mock(Session.class);
        Transaction tx = Mockito.mock(Transaction.class);
        Query<Long> countQuery = Mockito.mock(Query.class);
        TypedQuery<Long> typedQuery = Mockito.mock(Query.class);
        BenutzerEntity entityToInsert = new BenutzerEntity();
        entityToInsert.setKennung("testUser");

        when(mock_sf.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(tx);
        when(session.createQuery("select count(*) from BenutzerEntity where kennung = :kennung", Long.class)).thenReturn(countQuery);
        when(countQuery.setParameter("kennung", "testUser")).thenReturn((Query<Long>) typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(0L);

        // Act
        int result = benutzerDAO.insertEntity(entityToInsert);

        // Assert
        assertEquals(0, result);
        verify(session).persist(entityToInsert);
        verify(tx).commit();
    }

    @Test
    public void testInsertEntity_DuplicateKennung() {
        // Arrange
        Session session = Mockito.mock(Session.class);
        Transaction tx = Mockito.mock(Transaction.class);
        Query<Long> countQuery = Mockito.mock(Query.class);
        TypedQuery<Long> typedQuery = Mockito.mock(Query.class);
        BenutzerEntity entityToInsert = new BenutzerEntity();
        entityToInsert.setKennung("testUser");

        when(mock_sf.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(tx);
        when(session.createQuery("select count(*) from BenutzerEntity where kennung = :kennung", Long.class)).thenReturn(countQuery);
        when(countQuery.setParameter("kennung", "testUser")).thenReturn((Query<Long>) typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(1L);

        // Act
        int result = benutzerDAO.insertEntity(entityToInsert);

        // Assert
        assertEquals(1, result);
        verify(mock_logger).error("Another user with the same kennung already exists. Update operation aborted.");
        verify(session, never()).persist(entityToInsert);
        verify(tx, never()).commit();
    }

    @Test
    public void testInsertEntity_Failure() {
        // Arrange
        Session session = Mockito.mock(Session.class);
        Transaction tx = Mockito.mock(Transaction.class);
        Query<Long> countQuery = Mockito.mock(Query.class);
        TypedQuery<Long> typedQuery = Mockito.mock(Query.class);
        BenutzerEntity entityToInsert = new BenutzerEntity();
        entityToInsert.setKennung("testUser");

        when(mock_sf.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(tx);
        when(session.createQuery("select count(*) from BenutzerEntity where kennung = :kennung", Long.class)).thenReturn(countQuery);
        when(countQuery.setParameter("kennung", "testUser")).thenReturn((Query<Long>) typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(0L);
        doThrow(new RuntimeException("Failed to insert entity")).when(session).persist(entityToInsert);

        // Act
        int result = benutzerDAO.insertEntity(entityToInsert);

        // Assert
        assertEquals(2, result);
        //verify(logger).error("Failed to insert BenutzerEntity in the database: " + Arrays.toString(any(Exception.class).getStackTrace()));
        verify(tx).rollback();
    }

}
