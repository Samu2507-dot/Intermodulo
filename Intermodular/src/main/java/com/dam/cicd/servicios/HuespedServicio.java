package com.dam.cicd.servicios;

import com.dam.cicd.entidades.Alojamiento;
import com.dam.cicd.entidades.Huesped;
import com.dam.cicd.entidades.Resena;
import com.dam.cicd.entidades.Reserva;
import com.dam.cicd.excepciones.ReservaInvalidaException;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Servicio encargado de gestionar las acciones de negocio propias del rol de Huésped.
 * Proporciona la lógica necesaria para realizar reservas en los alojamientos del sistema
 * y publicar valoraciones o reseñas tras las estancias dentro de Roomly, controlando
 * la persistencia y la integridad relacional de los datos mediante JPA.
 */
public class HuespedServicio {

    private EntityManager em;

    /**
     * Construye una nueva instancia del servicio de huéspedes.
     * * @param em El manejador de entidades (EntityManager) de JPA que se utilizará para las operaciones de persistencia.
     */
    public HuespedServicio(EntityManager em) {
        this.em = em;
    }

    /**
     * Registra y formaliza una nueva reserva de estancia en el sistema Roomly.
     * Valida la coherencia cronológica de las fechas e inspecciona la existencia previa
     * del huésped y el inmueble antes de persistir la operación de forma transaccional.
     * * @param idHuesped     Identificador único del huésped que solicita la reserva.
     * @param idAlojamiento Identificador único del alojamiento que se desea reservar.
     * @param entrada       Fecha de inicio del periodo de estancia (check-in).
     * @param salida        Fecha de finalización del periodo de estancia (check-out).
     * @param precioTotal   Importe financiero total presupuestado para toda la reserva.
     * @return El objeto {@link Reserva} creado, enlazado correctamente y persistido con éxito en la base de datos.
     * @throws ReservaInvalidaException Si la fecha de check-out resulta previa al check-in, si no se localiza
     * al huésped o al alojamiento en los registros, o ante cualquier error crítico en la base de datos.
     */
    public Reserva realizarReserva(Integer idHuesped, Integer idAlojamiento, LocalDate entrada, LocalDate salida, BigDecimal precioTotal) throws ReservaInvalidaException {

        if (salida != null && entrada != null && salida.isBefore(entrada)) {
            throw new ReservaInvalidaException("La fecha de salida no puede ser anterior a la fecha de entrada.");
        }

        em.getTransaction().begin();
        try {
            Huesped huesped = em.find(Huesped.class, idHuesped);
            Alojamiento alojamiento = em.find(Alojamiento.class, idAlojamiento);

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

    /**
     * Publica una reseña con comentario y puntuación numérica vinculada a una reserva específica.
     * Asegura de forma estricta que la valoración se encuentre dentro de los límites de calidad
     * estipulados por el negocio y estampa de forma automática la fecha actual del sistema.
     * * @param idReserva   Identificador único de la reserva sobre la que se emite la valoración.
     * @param puntuacion  Escala numérica obligatoria del 1 al 5 para calificar la estancia.
     * @param comentario  Texto descriptivo u opinión opcional redactada por el huésped.
     * @return El objeto {@link Resena} generado, vinculado de forma unívoca a la reserva y persistido.
     * @throws ReservaInvalidaException Si la puntuación se sale de los márgenes admisibles (1 a 5), si la
     * reserva indicada no existe o ante fallos imprevistos en la persistencia del objeto.
     */
    public Resena publicarResena(Integer idReserva, Integer puntuacion, String comentario) throws ReservaInvalidaException {

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