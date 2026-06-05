import entidades.*;
import servicios.*;
import excepciones.*; // IMPORTAMOS TUS EXCEPCIONES PERSONALIZADAS
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class Principal {
    public static void main(String[] args) {

        try (EntityManagerFactory emf = Persistence.createEntityManagerFactory("RoomlyPU");
             EntityManager em = emf.createEntityManager()) {

            System.out.println("Conexión correcta!");


            UsuarioServicio usuarioServ = new UsuarioServicio(em);
            AnfitrionServicio anfitrionServ = new AnfitrionServicio(em);
            HuespedServicio huespedServ = new HuespedServicio(em);
            MantenimientoServicio mantenimientoServ = new MantenimientoServicio(em);


            // CREAR UN ANFITRION
            // actualizado para que la contraseña se encripte automáticamente
            Anfitrion a1 = usuarioServ.registrarAnfitrion(
                    "Juan",
                    "Rodriguez Fernandez",
                    "juana1@gmail.com",
                    "666111777",
                    "juan_21",
                    "juan_password_plana" // Aquí pasas la contraseña limpia, el servicio la encripta
            );


            //CREAR ALOJAMIENTO
            Alojamiento al1 = anfitrionServ.publicarAlojamiento(
                    a1.getIdAnfitrion(),
                    "Apartamento Centro Sol",
                    "Calle Mayor 14, Madrid",
                    new BigDecimal("85.00")
            );


            //CREAR HUESPED
            Huesped h1 = usuarioServ.registrarHuesped(
                    "Ana",
                    "Sánchez Ruiz",
                    "ana.sanchez@email.com",
                    "677111111",
                    "ana_huesped",
                    "ana_password_plana" // Contraseña en texto plano
            );


            // CREAR RESERVA
            Reserva r1 = huespedServ.realizarReserva(
                    h1.getIdHuesped(),
                    al1.getIdAlojamiento(),
                    LocalDate.of(2026, 6, 10),
                    LocalDate.of(2026, 6, 15),
                    new BigDecimal("425.00")
            );


            //CREAR OPERARIO MANTENIMIENTO
            OperarioMantenimiento op1 = new OperarioMantenimiento();
            op1.setNombre("Técnico Sol");
            op1.setUsuario("tecnico_sol");
            op1.setPass("$2a$12$e0MbgS/a8U7N9fA3f5.bkuU81E7sIscMvQhks2mIqGZfWbN5Z7pQy");

            em.getTransaction().begin();
            em.persist(op1);
            em.getTransaction().commit();


            //CREAR MANTENIMIENTO
            Mantenimiento m1 = mantenimientoServ.solicitarMantenimiento(
                    al1.getIdAlojamiento(),
                    op1.getIdOperario(),
                    "Arreglo de la cisterna del baño principal"
            );


            //EJEMPLO DE CREACIÓN DE RESEÑA
            Resena res1 = huespedServ.publicarResena(
                    r1.getIdReserva(),
                    5,
                    "Excelente ubicación y todo muy limpio."
            );


            //BUSCAR/MODIFICAR MANTENIMIENTO
            mantenimientoServ.actualizarEstadoMantenimiento(m1.getIdMantenimiento(), "Completado");


            //CONSULTAS JPQL


            //Buscar alojamientos económicos
            TypedQuery<Alojamiento> q = em.createQuery(
                    "SELECT a FROM Alojamiento a WHERE a.precioDia <= :precioMaximo", Alojamiento.class);
            q.setParameter("precioMaximo", new BigDecimal("100.00"));

            List<Alojamiento> resultado = q.getResultList();

            System.out.println("\n--- LISTADO DE ALOJAMIENTOS ECONÓMICOS ---");
            for (Alojamiento a : resultado) {
                System.out.println("Alojamiento: " + a.getNombre() +
                        " | Precio: " + a.getPrecioDia() + "€" +
                        " | Anfitrión: " + a.getAnfitrion().getNombre());
            }

            //buscar reserva y ver datos conectados
            Reserva reservaConsultada = em.find(Reserva.class, r1.getIdReserva());

            if (reservaConsultada != null) {
                System.out.println("\n--- DETALLE DE RESERVA OPTIMIZADA ---");
                // ¡Ojo a esto! Accedemos al huésped y al anfitrión sin hacer ninguna query extra
                System.out.println("El Huésped " + reservaConsultada.getHuesped().getNombre() +
                        " ha reservado el alojamiento '" + reservaConsultada.getAlojamiento().getNombre() + "'");
                System.out.println("El dueño de este alojamiento es: " + reservaConsultada.getAlojamiento().getAnfitrion().getNombre());
            }

            //Controlar mantenimientos pendientes
            TypedQuery<Mantenimiento> qAlertas = em.createQuery(
                    "SELECT m FROM Mantenimiento m " +
                            "WHERE m.estado IN ('Pendiente', 'En progreso')", Mantenimiento.class);

            List<Mantenimiento> alertas = qAlertas.getResultList();

            System.out.println("\n--- ALERTAS DE MANTENIMIENTO REQUERIDO ---");
            for (Mantenimiento mantPendiente : alertas) {
                System.out.println("¡ATENCIÓN! El alojamiento '" + mantPendiente.getAlojamiento().getNombre() +
                        "' tiene el siguiente problema: " + mantPendiente.getDescripcion() +
                        " [Estado: " + mantPendiente.getEstado() + "]");
            }


        } catch (AutenticacionException e) {
            System.out.println("Error de Autenticación: " + e.getMessage());
        } catch (ReservaInvalidaException e) {
            System.out.println("Error en la Reserva/Reseña: " + e.getMessage());
        } catch (MantenimientoException e) {
            System.out.println("Error de Mantenimiento o Anuncios: " + e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println("Error de configuración de persistencia: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Fallo inesperado del sistema: " + e.getMessage());
        }
    }
}