package com.dam.cicd;

import com.dam.cicd.entidades.*;
import com.dam.cicd.entidades.*;
import com.dam.cicd.excepciones.AutenticacionException;
import com.dam.cicd.excepciones.MantenimientoException;
import com.dam.cicd.excepciones.ReservaInvalidaException;
import com.dam.cicd.servicios.AnfitrionServicio;
import com.dam.cicd.servicios.HuespedServicio;
import com.dam.cicd.servicios.MantenimientoServicio;
import com.dam.cicd.servicios.UsuarioServicio;
import com.dam.cicd.servicios.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class Principal {

    private static final Logger log = LoggerFactory.getLogger(Principal.class);

    public static void main(String[] args) {

        log.info("Iniciando la aplicación Roomly...");
        log.info("Conectando con el servidor AWS a través del puerto remoto 3308...");

        try (EntityManagerFactory emf = Persistence.createEntityManagerFactory("RoomlyPU");
             EntityManager em = emf.createEntityManager()) {

            log.info("✅ Conexión con JPA/Hibernate establecida con éxito en la nube de AWS.");

            UsuarioServicio usuarioServ = new UsuarioServicio(em);
            AnfitrionServicio anfitrionServ = new AnfitrionServicio(em);
            HuespedServicio huespedServ = new HuespedServicio(em);
            MantenimientoServicio mantenimientoServ = new MantenimientoServicio(em);

            log.info("👤 Registrando nuevo Anfitrión...");
            Anfitrion a1 = usuarioServ.registrarAnfitrion(
                    "Alba",
                    "Rodriguez Fernandez",
                    "alba1@gmail.com",
                    "666111777",
                    "albiita_21",
                    "alba_password_plana"
            );
            log.info("🔹 Anfitrión creado con ID: {}", a1.getIdAnfitrion());

            log.info("🏠 Publicando nuevo alojamiento para el Anfitrión ID: {}", a1.getIdAnfitrion());
            Alojamiento al1 = anfitrionServ.publicarAlojamiento(
                    a1.getIdAnfitrion(),
                    "Apartamento esquina Fernandez",
                    "Calle Mayor 14, Madrid",
                    new BigDecimal("85.00")
            );
            log.info("🔹 Alojamiento publicado con ID: {}", al1.getIdAlojamiento());

            log.info("🧳 Registrando nuevo Huésped...");
            Huesped h1 = usuarioServ.registrarHuesped(
                    "Ana",
                    "Sánchez Ruiz",
                    "ana.sanchez@email.com",
                    "677111111",
                    "ana_huesped",
                    "ana_password_plana"
            );
            log.info("🔹 Huésped creado con ID: {}", h1.getIdHuesped());

            log.info("📅 Procesando solicitud de reserva para el Huésped ID: {}", h1.getIdHuesped());
            Reserva r1 = huespedServ.realizarReserva(
                    h1.getIdHuesped(),
                    al1.getIdAlojamiento(),
                    LocalDate.of(2026, 6, 10),
                    LocalDate.of(2026, 6, 15),
                    new BigDecimal("425.00")
            );
            log.info("🔹 Reserva confirmada con ID: {}", r1.getIdReserva());

            log.info("🔧 Creando operario de mantenimiento en el sistema...");
            OperarioMantenimiento op1 = new OperarioMantenimiento();
            op1.setNombre("Técnico Sol");
            op1.setUsuario("tecnico_sol");
            op1.setPass("$2a$12$e0MbgS/a8U7N9fA3f5.bkuU81E7sIscMvQhks2mIqGZfWbN5Z7pQy");

            em.getTransaction().begin();
            em.persist(op1);
            em.getTransaction().commit();
            log.info("🔹 Operario persistido con ID: {}", op1.getIdOperario());

            log.info("🛠️ Abriendo parte de mantenimiento para el alojamiento ID: {}", al1.getIdAlojamiento());
            Mantenimiento m1 = mantenimientoServ.solicitarMantenimiento(
                    al1.getIdAlojamiento(),
                    op1.getIdOperario(),
                    "Arreglo de la cisterna del baño principal"
            );

            log.info("⭐ Publicando reseña para la reserva ID: {}", r1.getIdReserva());
            Resena res1 = huespedServ.publicarResena(
                    r1.getIdReserva(),
                    5,
                    "Excellent location and very clean."
            );

            log.info("🔍 Ejecutando consultas analíticas JPQL...");

            TypedQuery<Alojamiento> q = em.createQuery(
                    "SELECT a FROM Alojamiento a WHERE a.precioDia <= :precioMaximo", Alojamiento.class);
            q.setParameter("precioMaximo", new BigDecimal("100.00"));

            List<Alojamiento> resultado = q.getResultList();
            log.info("📊 Consulta: Alojamientos económicos encontrados: {}", resultado.size());
            for (Alojamiento a : resultado) {
                log.info("[JPQL - Económico] Alojamiento: {} | Precio: {}€ | Anfitrión: {}",
                        a.getNombre(), a.getPrecioDia(), a.getAnfitrion().getNombre());
            }

            Reserva reservaConsultada = em.find(Reserva.class, r1.getIdReserva());
            if (reservaConsultada != null) {
                log.info("[JPQL - Detalle Reserva] Huésped '{}' -> Alojamiento '{}' [Dueño: {}]",
                        reservaConsultada.getHuesped().getNombre(),
                        reservaConsultada.getAlojamiento().getNombre(),
                        reservaConsultada.getAlojamiento().getAnfitrion().getNombre());
            }

            TypedQuery<Mantenimiento> qAlertas = em.createQuery(
                    "SELECT m FROM Mantenimiento m " +
                            "WHERE m.estado IN ('Pendiente', 'En progreso')", Mantenimiento.class);

            List<Mantenimiento> alertas = qAlertas.getResultList();
            for (Mantenimiento mantPendiente : alertas) {
                log.warn("⚠️ [ALERTA] Mantenimiento requerido en '{}': {} [Estado: {}]",
                        mantPendiente.getAlojamiento().getNombre(),
                        mantPendiente.getDescripcion(),
                        mantPendiente.getEstado());
            }

            log.info("🔄 Actualizando estado del mantenimiento ID: {} a 'Completado'...", m1.getIdMantenimiento());
            mantenimientoServ.actualizarEstadoMantenimiento(m1.getIdMantenimiento(), "Completado");

            log.info("🏁 Ejecución de la simulación completada con éxito. Todos los datos han sido sincronizados.");

        } catch (AutenticacionException e) {
            log.warn("🚨 Error de Autenticación controlado: {}", e.getMessage());
        } catch (ReservaInvalidaException e) {
            log.warn("🚨 Error en la Reserva/Reseña controlado: {}", e.getMessage());
        } catch (MantenimientoException e) {
            log.warn("🚨 Error de Mantenimiento controlado: {}", e.getMessage());
        } catch (IllegalStateException e) {
            log.error("💥 Error crítico de configuración de persistencia: {}", e.getMessage());
        } catch (Exception e) {
            log.error("💥 Fallo imprevisto y crítico del sistema: ", e);
        }
    }
}