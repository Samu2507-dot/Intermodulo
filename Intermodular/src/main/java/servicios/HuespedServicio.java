package servicios;

import entidades.*;
import excepciones.*;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;

public class HuespedServicio {

    private EntityManager em;

    public HuespedServicio(EntityManager em) {
        this.em = em;
    }

    // REALIZAR RESERVA
    public Reserva realizarReserva(Integer idHuesped, Integer idAlojamiento, LocalDate entrada, LocalDate salida, BigDecimal precioTotal) throws ReservaInvalidaException {

        if (salida != null && entrada != null && salida.isBefore(entrada)) {
            throw new ReservaInvalidaException("La fecha de salida no puede ser anterior a la fecha de entrada.");
        }

        em.getTransaction().begin();
        try {
            Huesped huesped = em.find(Huesped.class, idHuesped);
            Alojamiento alojamiento = em.find(Alojamiento.class, idAlojamiento);

            // Validamos que existan los dos registros
            if (huesped == null) {
                throw new ReservaInvalidaException("No se puede realizar la reserva: El huésped con ID " + idHuesped + " no existe.");
            }
            if (alojamiento == null) {
                throw new ReservaInvalidaException("No se puede realizar la reserva: El alojamiento con ID " + idAlojamiento + " no existe.");
            }

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
            throw new ReservaInvalidaException("Fallo crítico al procesar la reserva en la base de datos", e);
        }
    }

    // PUBLICAR RESEÑA
    public Resena publicarResena(Integer idReserva, Integer puntuacion, String comentario) throws ReservaInvalidaException {

        // Validación del 1 al 5
        if (puntuacion == null || puntuacion < 1 || puntuacion > 5) {
            throw new ReservaInvalidaException("La puntuación de la reseña debe estar entre 1 y 5 estrellas.");
        }

        em.getTransaction().begin();
        try {
            Reserva reserva = em.find(Reserva.class, idReserva);

            if (reserva == null) {
                throw new ReservaInvalidaException("No se puede publicar la reseña: La reserva con ID " + idReserva + " no existe.");
            }

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
            throw new ReservaInvalidaException("Fallo crítico al registrar la reseña en la base de datos", e);
        }
    }
}