package servicios;

import entidades.*;
import excepciones.*; // IMPORTAMOS TUS EXCEPCIONES PERSONALIZADAS
import jakarta.persistence.EntityManager;
import java.time.LocalDate;

public class MantenimientoServicio {

    private EntityManager em;

    public MantenimientoServicio(EntityManager em) {
        this.em = em;
    }

    // SOLICITAR MANTENIMIENTO
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

    // ACTUALIZAR ESTADO MANTENIMIENTO
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