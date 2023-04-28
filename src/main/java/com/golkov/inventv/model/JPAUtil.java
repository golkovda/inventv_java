package com.golkov.inventv.model;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.io.File;
import java.net.MalformedURLException;

public class JPAUtil {
    private static EntityManagerFactory entityManagerFactory;

    public static EntityManagerFactory getEntityManagerFactory() throws MalformedURLException {
        if (entityManagerFactory == null) {
            entityManagerFactory = Persistence.createEntityManagerFactory("test"); //TODO: Hibernate statt JPA verwenden
        }
        return entityManagerFactory;
    }
}
