package servicios;

import entidades.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.mindrot.jbcrypt.BCrypt;

public class UsuarioServicio {

    private EntityManager em;

    public UsuarioServicio(EntityManager em) {
        this.em = em;
    }

    // MÉTODO DE AUTENTICACIÓN
    public Object login(String usuario, String passPlana) {

        // 1. INTENTAR LOGIN COMO ANFITRIÓN
        try {
            Anfitrion anfi = em.createQuery("SELECT a FROM Anfitrion a WHERE a.usuario = :user", Anfitrion.class)
                    .setParameter("user", usuario)
                    .setMaxResults(1)
                    .getSingleResult();

            // Comparación con el hash almacenado en la base de datos
            if (BCrypt.checkpw(passPlana, anfi.getPass())) {
                return anfi; // Login correcto
            }
        } catch (NoResultException e) {

        }

        // 2. INTENTAR LOGIN COMO HUÉSPED
        try {
            Huesped huesped = em.createQuery("SELECT h FROM Huesped h WHERE h.usuario = :user", Huesped.class)
                    .setParameter("user", usuario)
                    .setMaxResults(1) // Controlamos el máximo de 1 resultado
                    .getSingleResult();

            if (BCrypt.checkpw(passPlana, huesped.getPass())) {
                return huesped; // Login correcto
            }
        } catch (NoResultException e) {

        }

        // 3. INTENTAR LOGIN COMO OPERARIO
        try {
            OperarioMantenimiento ope = em.createQuery("SELECT o FROM OperarioMantenimiento o WHERE o.usuario = :user", OperarioMantenimiento.class)
                    .setParameter("user", usuario)
                    .setMaxResults(1)
                    .getSingleResult();

            if (BCrypt.checkpw(passPlana, ope.getPass())) {
                return ope; // Login correcto
            }
        } catch (NoResultException e) {

        }

        return null; // ¡AQUÍ ESTÁ LA SOLUCIÓN! Si ningún login es correcto, devolvemos null
    }

    // REGISTRO Y ENCRIPTACIÓN ANFITRIÓN
    public Anfitrion registrarAnfitrion(String nombre, String apellidos, String email, String telefono, String usuario, String passPlana) {
        Anfitrion a = new Anfitrion();
        a.setNombre(nombre);
        a.setApellidos(apellidos);
        a.setEmail(email);
        a.setTelefono(telefono);
        a.setUsuario(usuario);


        String hashEncriptado = BCrypt.hashpw(passPlana, BCrypt.gensalt(12));
        a.setPass(hashEncriptado);

        em.getTransaction().begin();
        em.persist(a);
        em.getTransaction().commit();
        return a;
    }

    // REGISTRO Y ENCRIPTACIÓN HUÉSPED
    public Huesped registrarHuesped(String nombre, String apellidos, String email, String telefono, String usuario, String passPlana) {
        Huesped h = new Huesped();
        h.setNombre(nombre);
        h.setApellidos(apellidos);
        h.setEmail(email);
        h.setTelefono(telefono);
        h.setUsuario(usuario);


        String hashEncriptado = BCrypt.hashpw(passPlana, BCrypt.gensalt(12));
        h.setPass(hashEncriptado);

        em.getTransaction().begin();
        em.persist(h);
        em.getTransaction().commit();
        return h;
    }
}