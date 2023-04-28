package com.golkov.inventv.model.daos;

import javafx.collections.ObservableList;

public interface IEntityDAO<T> {
    public void addEntity(T entity);
    public void updateEntity(T entity);
    public void deleteEntity(int id);
    public T getEntityById(int id);
    public ObservableList<T> getAllEntities();
}
