package com.golkov.inventv.model.daos;

import com.golkov.inventv.model.JPAUtil;
import com.golkov.inventv.model.entities.BenutzerEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import javafx.collections.ObservableList;

import java.net.MalformedURLException;

public class BenutzerDAO implements IEntityDAO<BenutzerEntity>{

    private EntityManager entityManager;

    public BenutzerDAO() throws MalformedURLException {
        entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
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

    @Override
    public ObservableList<BenutzerEntity> getAllEntities() {
        String jpql = "SELECT e FROM BenutzerEntity e";
        TypedQuery<BenutzerEntity> query = entityManager.createQuery(jpql, BenutzerEntity.class);
        return (ObservableList<BenutzerEntity>) query.getResultList();
    }
}
