package com.dam.cicd.servicios;

import com.dam.cicd.entidades.Alojamiento;
import com.dam.cicd.entidades.Anfitrion;
import com.dam.cicd.excepciones.MantenimientoException;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;

/**
 * Servicio encargado de gestionar las acciones de negocio propias del rol de Anfitrión.
 * Proporciona la lógica necesaria para publicar nuevos alojamientos, modificar anuncios
 * existentes y consultar el rendimiento financiero dentro del sistema Roomly, controlando
 * las transacciones con la base de datos a través de JPA.
 */
public class AnfitrionServicio {

    private EntityManager em;

    /**
     * Construye una nueva instancia del servicio de anfitriones.
     * @param em El manejador de entidades (EntityManager) de JPA que se utilizará para las operaciones de persistencia.
     */
    public AnfitrionServicio(EntityManager em) {
        this.em = em;
    }

    /**
     * Publica un nuevo alojamiento en el sistema vinculándolo a un anfitrión existente.
     * Valida que los datos económicos sean correctos y maneja la transacción de forma segura.
     * * @param idAnfitrion Identificador único del anfitrión.
     * @param nombre Nombre del alojamiento.
     * @param direccion Dirección física.
     * @param precioDia Precio por día de estancia.
     * @param fotoUrl URL de la imagen del alojamiento.
     * @return El objeto {@link Alojamiento} persistido.
     * @throws MantenimientoException Si los datos son inválidos o falla la transacción.
     */
    public Alojamiento publicarAlojamiento(Integer idAnfitrion, String nombre, String direccion, BigDecimal precioDia, String fotoUrl) throws MantenimientoException {

        if (precioDia == null || precioDia.compareTo(BigDecimal.ZERO) <= 0) {
            throw new MantenimientoException("El precio por día debe ser mayor que cero.");
        }

        em.getTransaction().begin();
        try {
            Anfitrion anfitrion = em.find(Anfitrion.class, idAnfitrion);
            if (anfitrion == null) {
                throw new MantenimientoException("Anfitrión no encontrado.");
            }

            Alojamiento alojamiento = new Alojamiento();
            alojamiento.setNombre(nombre);
            alojamiento.setDireccion(direccion);
            alojamiento.setPrecioDia(precioDia);
            alojamiento.setFotoUrl(fotoUrl);
            alojamiento.setAnfitrion(anfitrion);

            em.persist(alojamiento);
            em.getTransaction().commit();
            return alojamiento;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new MantenimientoException("Error al publicar", e);
        }
    }

    /**
     * Modifica los datos básicos (nombre comercial y precio diario) de un alojamiento ya existente.
     * * @param idAlojamiento ID del alojamiento a actualizar.
     * @param nuevoNombre Nuevo nombre para el anuncio.
     * @param nuevoPrecio Nuevo precio por día.
     * @throws MantenimientoException Si el alojamiento no existe o falla la actualización.
     */
    public void modificarAnuncio(Integer idAlojamiento, String nuevoNombre, String nuevaDireccion,BigDecimal nuevoPrecio) throws MantenimientoException {
        em.getTransaction().begin();
        try {
            Alojamiento alojamiento = em.find(Alojamiento.class, idAlojamiento);

            if (alojamiento == null) {
                throw new MantenimientoException("No se puede modificar: El alojamiento con ID " + idAlojamiento + " no existe.");
            }

            alojamiento.setNombre(nuevoNombre);
            alojamiento.setDireccion(nuevaDireccion);
            alojamiento.setPrecioDia(nuevoPrecio);
            em.merge(alojamiento);

            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new MantenimientoException("Fallo crítico en la base de datos al modificar el anuncio", e);
        }
    }

    /**
     * Calcula el total facturado sumando los importes de todas las reservas
     * asociadas a los alojamientos del anfitrión.
     * * @param idAnfitrion ID del anfitrión a consultar.
     * @return BigDecimal con la suma total facturada, o {@link BigDecimal#ZERO} si no hay ingresos.
     */
    public BigDecimal obtenerTotalFacturado(Integer idAnfitrion) {
        try {

            String jpql = "SELECT SUM(r.precioTotal) FROM Reserva r WHERE r.alojamiento.anfitrion.id = :id";
            BigDecimal total = em.createQuery(jpql, BigDecimal.class)
                    .setParameter("id", idAnfitrion)
                    .getSingleResult();
            return (total != null) ? total : BigDecimal.ZERO;
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }
}