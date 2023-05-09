package com.golkov.inventv.controller.detailcontroller;

import com.golkov.inventv.controller.listcontroller.BenutzerdatenListeViewController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DetailViewControllerBase<T> {

    private static final Logger logger = LogManager.getLogger(DetailViewControllerBase.class);


    public T getUnchangedEntity() {
        return UnchangedEntity;
    }

    public void setUnchangedEntity(T unchangedEntity) {
        if(unchangedEntity != this.UnchangedEntity) {
            this.UnchangedEntity = unchangedEntity;
            logger.debug("CurrentEntity changed to '"+unchangedEntity.toString()+"'");
        }else{
            logger.debug("CurrentEntity remains unchanged");
        }
    }

    protected T UnchangedEntity;
}
