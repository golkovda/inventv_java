package com.golkov.inventv.model;

import com.golkov.inventv.model.entities.*;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import static com.golkov.inventv.InventVPreferences.*;

public class HibernateUtil {
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration().configure();

                configuration.setProperty("hibernate.connection.url", getServerUrl());
                configuration.setProperty("hibernate.connection.username", getServerUsername());
                configuration.setProperty("hibernate.connection.password", getServerPassword());

                configuration.addAnnotatedClass(BenutzerEntity.class);
                configuration.addAnnotatedClass(ObjektEntity.class);
                configuration.addAnnotatedClass(AblageortEntity.class);
                configuration.addAnnotatedClass(TypEntity.class);
                configuration.addAnnotatedClass(AusleihEntity.class);

                ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties()).build();
                sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sessionFactory;
    }
}