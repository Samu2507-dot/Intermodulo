package servicios;

import entidades.*;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;

public class HuespedServicio {

    private EntityManager em;

    public HuespedServicio(EntityManager em) {
        this.em = em;
    }

    // REALIZAR RESERVA
    public Reserva realizarReserva(Integer idHuesped, Integer idAlojamiento, LocalDate entrada, LocalDate salida, BigDecimal precioTotal) {
        em.getTransaction().begin();
        try {
            Huesped huesped = em.find(Huesped.class, idHuesped);
            Alojamiento alojamiento = em.find(Alojamiento.class, idAlojamiento);

            Reserva reserva = new Reserva();
            reserva.setHuesped(huesped);
            reserva.setAlojamiento(alojamiento);
            reserva.setFechaEntrada(entrada);
            reserva.setFechaSalida(salida);
            reserva.setPrecioTotal(precioTotal);

            em.persist(reserva);
            em.getTransaction().commit();
            return reserva;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }

    // PUBLICAR RESEÑA
    public Resena publicarResena(Integer idReserva, Integer puntuacion, String comentario) {
        em.getTransaction().begin();
        try {
            Reserva reserva = em.find(Reserva.class, idReserva);

            Resena resena = new Resena();
            resena.setReserva(reserva);
            resena.setPuntuacion(puntuacion);
            resena.setComentario(comentario);
            resena.setFecha(LocalDate.now());

            em.persist(resena);
            em.getTransaction().commit();
            return resena;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }
}