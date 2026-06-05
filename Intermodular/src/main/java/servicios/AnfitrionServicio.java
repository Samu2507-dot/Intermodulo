package servicios;

import entidades.*;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;

public class AnfitrionServicio {

    private EntityManager em;

    public AnfitrionServicio(EntityManager em) {
        this.em = em;
    }

    // PUBLICAR ALOJAMIENTO
    public Alojamiento publicarAlojamiento(Integer idAnfitrion, String nombre, String direccion, BigDecimal precioDia) {
        em.getTransaction().begin();
        try {
            Anfitrion anfitrion = em.find(Anfitrion.class, idAnfitrion);

            Alojamiento alojamiento = new Alojamiento();
            alojamiento.setNombre(nombre);
            alojamiento.setDireccion(direccion);
            alojamiento.setPrecioDia(precioDia);
            alojamiento.setAnfitrion(anfitrion);

            em.persist(alojamiento);
            em.getTransaction().commit();
            return alojamiento;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }

    // MODIFICAR ANUNCIO
    public void modificarAnuncio(Integer idAlojamiento, String nuevoNombre, BigDecimal nuevoPrecio) {
        em.getTransaction().begin();
        try {
            Alojamiento alojamiento = em.find(Alojamiento.class, idAlojamiento);
            if (alojamiento != null) {
                alojamiento.setNombre(nuevoNombre);
                alojamiento.setPrecioDia(nuevoPrecio);
                em.merge(alojamiento);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }
}