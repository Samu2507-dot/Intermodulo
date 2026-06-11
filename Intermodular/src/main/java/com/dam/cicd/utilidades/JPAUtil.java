package com.dam.cicd.utilidades;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public class JPAUtil {
    private static final EntityManagerFactory emf;
    private static final EntityManager em;

    static {
        try {

            Map<String, String> propiedades = new HashMap<>();


            propiedades.put("jakarta.persistence.jdbc.url", "jdbc:mariadb://localhost:3306/gestion_alojamientos_ROOMLY_");
            propiedades.put("jakarta.persistence.jdbc.user", "root");
            propiedades.put("jakarta.persistence.jdbc.password", "1234");
            propiedades.put("jakarta.persistence.jdbc.driver", "org.mariadb.jdbc.Driver");


            emf = Persistence.createEntityManagerFactory("RoomlyPU", propiedades);
            em = emf.createEntityManager();
        } catch (Throwable ex) {
            System.err.println("🚨 Error crítico al conectar con AWS: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static EntityManager getEntityManager() {
        return em;
    }

    public static void shutdown() {
        if (em.isOpen()) em.close();
        if (emf.isOpen()) emf.close();
    }
}