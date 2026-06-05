
import entidades.*;
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


            // CREAR UN ANFITRION

            Anfitrion a1 = new Anfitrion();
            a1.setNombre("Juan");
            a1.setApellidos("Rodriguez Fernandez");
            a1.setEmail("juana1@gmail.com");
            a1.setTelefono("666111777");
            a1.setUsuario("juan_21");
            a1.setPass("$2a$12$R9h/lS7v7L3H.G5WJkXb2e2G.1bM3G9Z5E7yX8Vw2Q3nRt6Y8u2S.");

            em.getTransaction().begin();
            em.persist(a1);
            em.getTransaction().commit();

            //CREAR ALOJAMIENTO
            Alojamiento al1 = new Alojamiento();
            al1.setNombre("Apartamento Centro Sol");
            al1.setDireccion("Calle Mayor 14, Madrid");
            al1.setPrecioDia(new BigDecimal("85.00"));


            al1.setAnfitrion(a1);

            em.getTransaction().begin();
            em.persist(al1);
            em.getTransaction().commit();

            //CREAR HUESPED
            Huesped h1 = new Huesped();
            h1.setNombre("Ana");
            h1.setApellidos("Sánchez Ruiz");
            h1.setEmail("ana.sanchez@email.com");
            h1.setTelefono("677111111");
            h1.setUsuario("ana_huesped");
            h1.setPass("$2a$12$e0MbgS/a8U7N9fA3f5.bkuU81E7sIscMvQhks2mIqGZfWbN5Z7pQy");

            em.getTransaction().begin();
            em.persist(h1);
            em.getTransaction().commit();

           // CREAR RESERVA
            Reserva r1 = new Reserva();
            r1.setFechaEntrada(LocalDate.of(2026, 6, 10));
            r1.setFechaSalida(LocalDate.of(2026, 6, 15));
            r1.setPrecioTotal(new BigDecimal("425.00"));


            r1.setAlojamiento(al1);
            r1.setHuesped(h1);

            em.getTransaction().begin();
            em.persist(r1);
            em.getTransaction().commit();

            //CREAR OPERARIO MANTENIMIENTO
            OperarioMantenimiento op1 = new OperarioMantenimiento();
            op1.setNombre("Técnico Sol");
            op1.setUsuario("tecnico_sol");
            op1.setPass("$2a$12$e0MbgS/a8U7N9fA3f5.bkuU81E7sIscMvQhks2mIqGZfWbN5Z7pQy");

            em.getTransaction().begin();
            em.persist(op1);
            em.getTransaction().commit();

            //CREAR MANTENIMIENTO
            Mantenimiento m1 = new Mantenimiento();
            m1.setFechaInicio(LocalDate.of(2026, 6, 1));
            m1.setDescripcion("Arreglo de la cisterna del baño principal");
            m1.setEstado("Pendiente");


            m1.setAlojamiento(al1);
            m1.setOperario(op1);

            em.getTransaction().begin();
            em.persist(m1);
            em.getTransaction().commit();

            //EJEMPLO DE CREACIÓN DE RESEÑA
            Resena res1 = new Resena();
            res1.setPuntuacion(5);
            res1.setComentario("Excelente ubicación y todo muy limpio.");
            res1.setFecha(LocalDate.of(2026, 6, 16));


            res1.setReserva(r1);

            em.getTransaction().begin();
            em.persist(res1);
            em.getTransaction().commit();

           //BUSCAR/MODIFICAR MANTENIMIENTO
            Mantenimiento mantenimientoAEditar = em.find(Mantenimiento.class, m1.getIdMantenimiento());

            if (mantenimientoAEditar != null) {
                mantenimientoAEditar.setEstado("Completado");

                em.getTransaction().begin();
                em.merge(mantenimientoAEditar);
                em.getTransaction().commit();
            }


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



        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}