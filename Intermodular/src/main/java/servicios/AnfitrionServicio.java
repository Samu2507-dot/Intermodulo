package servicios;
import entidades.*;
import excepciones.*;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;

public class AnfitrionServicio {

    private EntityManager em;

    public AnfitrionServicio(EntityManager em) {
        this.em = em;
    }

    // PUBLICAR ALOJAMIENTO
    public Alojamiento publicarAlojamiento(Integer idAnfitrion, String nombre, String direccion, BigDecimal precioDia) throws MantenimientoException {


        if (precioDia == null || precioDia.compareTo(BigDecimal.ZERO) <= 0) {
            throw new MantenimientoException("El precio por día debe ser mayor que cero.");
        }

        em.getTransaction().begin();
        try {
            Anfitrion anfitrion = em.find(Anfitrion.class, idAnfitrion);


            if (anfitrion == null) {
                throw new MantenimientoException("No se puede publicar el alojamiento: El anfitrión con ID " + idAnfitrion + " no existe.");
            }

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
            throw new MantenimientoException("Fallo crítico en la base de datos al publicar el alojamiento", e);
        }
    }

    // MODIFICAR ANUNCIO
    public void modificarAnuncio(Integer idAlojamiento, String nuevoNombre, BigDecimal nuevoPrecio) throws MantenimientoException {
        em.getTransaction().begin();
        try {
            Alojamiento alojamiento = em.find(Alojamiento.class, idAlojamiento);


            if (alojamiento == null) {
                throw new MantenimientoException("No se puede modificar: El alojamiento con ID " + idAlojamiento + " no existe.");
            }

            alojamiento.setNombre(nuevoNombre);
            alojamiento.setPrecioDia(nuevoPrecio);
            em.merge(alojamiento);

            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new MantenimientoException("Fallo crítico en la base de datos al modificar el anuncio", e);
        }
    }
}