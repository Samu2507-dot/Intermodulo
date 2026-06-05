package servicios;

import entidades.*;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;

public class MantenimientoServicio {

    private EntityManager em;

    public MantenimientoServicio(EntityManager em) {
        this.em = em;
    }

    // SOLICITAR MANTENIMIENTO
    public Mantenimiento solicitarMantenimiento(Integer idAlojamiento, Integer idOperario, String descripcion) {
        em.getTransaction().begin();
        try {
            Alojamiento alojamiento = em.find(Alojamiento.class, idAlojamiento);
            OperarioMantenimiento operario = em.find(OperarioMantenimiento.class, idOperario);

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
            throw e;
        }
    }

    // ACTUALIZAR ESTADO MANTENIMIENTO
    public void actualizarEstadoMantenimiento(Integer idMantenimiento, String nuevoEstado) {
        em.getTransaction().begin();
        try {
            Mantenimiento m = em.find(Mantenimiento.class, idMantenimiento);
            if (m != null) {
                m.setEstado(nuevoEstado);
                em.merge(m);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }
}