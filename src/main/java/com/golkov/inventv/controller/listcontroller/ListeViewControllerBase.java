package com.golkov.inventv.controller.listcontroller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;
import java.util.function.Supplier;

public class ListeViewControllerBase<T> {

    private static final Logger logger = LogManager.getLogger(BenutzerdatenListeViewController.class);

    //region Supplier<T> for creating instances of new T()
    private final Supplier<? extends T> ctor;
    ListeViewControllerBase(Supplier<? extends T> ctor){
        this.ctor = Objects.requireNonNull(ctor);
    }

    //endregion

    //region getters and setters

    public T getCurrentEntity() {
        if(currentEntity == null)
            currentEntity = ctor.get();
        return currentEntity;
    }

    public void setCurrentEntity(T currentEntity) {
        if(currentEntity != this.currentEntity) {
            this.currentEntity = currentEntity;
            logger.debug("CurrentEntity changed to '"+currentEntity.toString()+"'");
        }else{
            logger.debug("CurrentEntity remains unchanged");
        }
    }

    public ObservableList<T> getFoundEntities() {
        if(foundEntities == null) {
            logger.warn("getFoundEntities is null");
            return FXCollections.observableArrayList();
        }
        logger.debug("Returning foundEntities");
        return foundEntities;
    }

    public void setFoundEntities(ObservableList<T> foundEntities) {
        if(foundEntities != this.foundEntities){
            this.foundEntities = foundEntities;
            logger.debug("foundEntities changed");
        }else{
            logger.debug("foundEntities remain unchanged");
        }
    }

    //endregion

    private T currentEntity;
    private ObservableList<T> foundEntities;

}
