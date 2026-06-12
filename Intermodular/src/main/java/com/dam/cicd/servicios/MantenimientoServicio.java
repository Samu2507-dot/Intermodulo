package com.dam.cicd.servicios;

import com.dam.cicd.entidades.Alojamiento;
import com.dam.cicd.entidades.Mantenimiento;
import com.dam.cicd.entidades.OperarioMantenimiento;
import com.dam.cicd.excepciones.MantenimientoException;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;

/**
 * Servicio encargado de gestionar las operaciones asociadas al soporte técnico y reparaciones.
 * Proporciona la lógica de negocio para abrir nuevas solicitudes de mantenimiento en los alojamientos,
 * asignar técnicos cualificados y actualizar el progreso de las revisiones dentro de Roomly,
 * garantizando el control transaccional mediante JPA.
 */
public class MantenimientoServicio {

    private EntityManager em;

    /**
     * Construye una nueva instancia del servicio de mantenimientos.
     * @param em El manejador de entidades (EntityManager) de JPA que se utilizará para las operaciones de persistencia.
     */
    public MantenimientoServicio(EntityManager em) {
        this.em = em;
    }

    /**
     * Registra un nuevo parte de mantenimiento para un alojamiento asignándolo a un operario específico.
     * La solicitud toma automáticamente la fecha actual del sistema y se inicializa en estado "Pendiente".
     * * @param idAlojamiento Identificador único del alojamiento que presenta la incidencia.
     * @param idOperario    Identificador único del técnico encargado de realizar la reparación.
     * @param descripcion   Explicación detallada del desperfecto físico o avería a reparar.
     * @return El objeto {@link Mantenimiento} creado, correctamente enlazado y persistido.
     * @throws MantenimientoException Si el alojamiento o el operario especificados no existen en el sistema,
     * o ante cualquier fallo crítico en la base de datos al abrir el parte.
     */
    public Mantenimiento solicitarMantenimiento(Integer idAlojamiento, Integer idOperario, String descripcion) throws MantenimientoException {
        em.getTransaction().begin();
        try {
            Alojamiento alojamiento = em.find(Alojamiento.class, idAlojamiento);
            OperarioMantenimiento operario = em.find(OperarioMantenimiento.class, idOperario);

            if (alojamiento == null) {
                throw new MantenimientoException("No se puede solicitar mantenimiento: El alojamiento con ID " + idAlojamiento + " no existe.");
            }
            if (operario == null) {
                throw new MantenimientoException("No se puede solicitar mantenimiento: El operario con ID " + idOperario + " no existe.");
            }

            Mantenimiento mantenimiento = new Mantenimiento();
            mantenimiento.setAlojamiento(alojamiento);
            mantenimiento.setOperario(operario);
            mantenimiento.setFechaInicio(LocalDate.now());
            mantenimiento.setDescripcion(descripcion);
            mantenimiento.setEstado("Pendiente");

            em.persist(mantenimiento);
            em.getTransaction().commit();
            return mantenimiento;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new MantenimientoException("Fallo crítico al crear la orden de mantenimiento", e);
        }
    }

    /**
     * Actualiza el estado actual de una orden de mantenimiento (ej. "En progreso", "Completado").
     * Modifica el registro persistido sincronizando el nuevo estado del flujo técnico en la base de datos.
     * * @param idMantenimiento Identificador único de la orden de mantenimiento que se desea modificar.
     * @param nuevoEstado     El nuevo estado que tomará el parte técnico según la revisión.
     * @throws MantenimientoException Si no se encuentra ninguna orden de mantenimiento con el ID proporcionado,
     * o ante fallos imprevistos de sincronización en la persistencia.
     */
    public void actualizarEstadoMantenimiento(Integer idMantenimiento, String nuevoEstado) throws MantenimientoException {
        em.getTransaction().begin();
        try {
            Mantenimiento m = em.find(Mantenimiento.class, idMantenimiento);
            if (m != null) {
                m.setEstado(nuevoEstado);
                em.merge(m);
            } else {
                throw new MantenimientoException("No se puede actualizar: La orden de mantenimiento con ID " + idMantenimiento + " no existe.");
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new MantenimientoException("Fallo crítico al actualizar el estado del mantenimiento", e);
        }
    }
}