package com.golkov.inventv.model.daos;

import com.golkov.inventv.model.entities.BenutzerEntity;
import javafx.collections.ObservableList;

public interface IEntityDAO<T> {
    //public ObservableList<T> getAllEntities();
    public int updateEntity(T oldEntity, T newEntity);
    public int removeEntity(T entityToRemove);
    public int insertEntity(T entityToInsert);
}
