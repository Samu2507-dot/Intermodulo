package com.dam.cicd.utilidades;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public class JPAUtil {
    // La fábrica (EMF) debe ser única y vivir toda la aplicación
    private static final EntityManagerFactory emf;

    static {
        try {
            Map<String, String> propiedades = new HashMap<>();
            propiedades.put("jakarta.persistence.jdbc.url", "jdbc:mariadb://localhost:3306/gestion_alojamientos_ROOMLY_");
            propiedades.put("jakarta.persistence.jdbc.user", "root");
            propiedades.put("jakarta.persistence.jdbc.password", "1234");
            propiedades.put("jakarta.persistence.jdbc.driver", "org.mariadb.jdbc.Driver");

            emf = Persistence.createEntityManagerFactory("RoomlyPU", propiedades);
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    // AHORA: Cada vez que necesites un EM, esta clase te entrega uno nuevo
    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public static void shutdown() {
        if (emf.isOpen()) emf.close();
    }
}