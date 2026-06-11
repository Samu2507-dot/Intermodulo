package com.dam.cicd.servicios;

import com.dam.cicd.entidades.Alojamiento;
import com.dam.cicd.entidades.Anfitrion;
import com.dam.cicd.excepciones.MantenimientoException;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;

/**
 * Servicio encargado de gestionar las acciones de negocio propias del rol de Anfitrión.
 * Proporciona la lógica necesaria para publicar nuevos alojamientos y modificar la
 * información de los anuncios existentes dentro del sistema Roomly, controlando las
 * transacciones con la base de datos a través de JPA.
 */
public class AnfitrionServicio {

    private EntityManager em;

    /**
     * Construye una nueva instancia del servicio de anfitriones.
     * * @param em El manejador de entidades (EntityManager) de JPA que se utilizará para las operaciones de persistencia.
     */
    public AnfitrionServicio(EntityManager em) {
        this.em = em;
    }

    /**
     * Publica un nuevo alojamiento en el sistema vinculándolo a un anfitrión existente.
     * Valida que los datos económicos sean correctos y maneja la transacción de forma segura.
     * * @param idAnfitrion Identificador único del anfitrión propietario del alojamiento.
     * @param nombre      Nombre comercial o descriptivo del alojamiento.
     * @param direccion   Ubicación física completa del inmueble.
     * @param precioDia   Importe económico fijado por cada día de estancia.
     * @return El objeto {@link Alojamiento} que ha sido creado y persistido con éxito en la base de datos.
     * @throws MantenimientoException Si el precio es menor o igual a cero, si el anfitrión no existe
     * o si ocurre un fallo crítico durante la transacción de persistencia.
     */
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

    /**
     * Modifica los datos básicos (nombre comercial y precio diario) de un alojamiento ya existente.
     * Actualiza el estado de la entidad en la base de datos sincronizando los cambios.
     * * @param idAlojamiento Identificador único del alojamiento que se desea actualizar.
     * @param nuevoNombre   El nuevo nombre descriptivo que se asignará al anuncio.
     * @param nuevoPrecio   El nuevo importe económico por día de estancia.
     * @throws MantenimientoException Si el alojamiento especificado no existe en el sistema
     * o si se produce un error imprevisto al procesar la actualización en la base de datos.
     */
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