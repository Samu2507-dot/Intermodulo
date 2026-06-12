package com.dam.cicd.servicios;

import com.dam.cicd.entidades.Anfitrion;
import com.dam.cicd.entidades.Huesped;
import com.dam.cicd.entidades.OperarioMantenimiento;
import com.dam.cicd.excepciones.AutenticacionException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Servicio centralizado de seguridad encargado de la gestión de usuarios en Roomly.
 * Proporciona la lógica de negocio necesaria para la autenticación unificada (login)
 * de múltiples perfiles (Anfitriones, Huéspedes y Operarios) y el registro seguro
 * de cuentas mediante algoritmos de encriptación y hashing robustos (BCrypt).
 */
public class UsuarioServicio {

    private EntityManager em;

    /**
     * Construye una nueva instancia del servicio de usuarios.
     * @param em El manejador de entidades (EntityManager) de JPA que se utilizará para las operaciones de persistencia y consultas.
     */
    public UsuarioServicio(EntityManager em) {
        this.em = em;
    }

    /**
     * Realiza una autenticación unificada en el sistema comprobando secuencialmente
     * las credenciales en cada rol del modelo de negocio.
     * Verifica la existencia del nombre de usuario y valida la contraseña plana
     * contrastándola contra el hash Bcrypt almacenado de forma segura en la base de datos.
     * * @param usuario   Nombre de usuario único de la cuenta que intenta acceder.
     * @param passPlana Contraseña en texto plano introducida en el formulario de inicio de sesión.
     * @return El objeto de la entidad correspondiente al usuario autenticado (instancia de {@link Anfitrion},
     * {@link Huesped} o {@link OperarioMantenimiento}).
     * @throws AutenticacionException Si las credenciales no son correctas o el usuario no existe en ningún rol.
     */
    public Object login(String usuario, String passPlana) throws AutenticacionException {

        // 1. INTENTAR LOGIN COMO ANFITRIÓN
        try {
            Anfitrion anfi = em.createQuery("SELECT a FROM Anfitrion a WHERE a.usuario = :user", Anfitrion.class)
                    .setParameter("user", usuario)
                    .setMaxResults(1)
                    .getSingleResult();

            if (BCrypt.checkpw(passPlana, anfi.getPass())) {
                return anfi;
            }
        } catch (NoResultException e) {
            // Continuar con el siguiente rol si no se encuentra en este
        }

        // 2. INTENTAR LOGIN COMO HUÉSPED
        try {
            Huesped huesped = em.createQuery("SELECT h FROM Huesped h WHERE h.usuario = :user", Huesped.class)
                    .setParameter("user", usuario)
                    .setMaxResults(1)
                    .getSingleResult();

            if (BCrypt.checkpw(passPlana, huesped.getPass())) {
                return huesped;
            }
        } catch (NoResultException e) {
            // Continuar con el siguiente rol si no se encuentra en este
        }

        // 3. INTENTAR LOGIN COMO OPERARIO
        try {
            OperarioMantenimiento ope = em.createQuery("SELECT o FROM OperarioMantenimiento o WHERE o.usuario = :user", OperarioMantenimiento.class)
                    .setParameter("user", usuario)
                    .setMaxResults(1)
                    .getSingleResult();

            if (BCrypt.checkpw(passPlana, ope.getPass())) {
                return ope;
            }
        } catch (NoResultException e) {
            // Absorber la excepción para permitir lanzar la alerta global de credenciales erróeanas
        }

        throw new AutenticacionException("Error de autenticación: El nombre de usuario o la contraseña introducidos no coinciden con ningún registro.");
    }

    /**
     * Registra un nuevo Anfitrión en el sistema aplicando políticas de seguridad activa.
     * Genera un hash Bcrypt con un factor de coste adaptativo (12 rounds de sal) para ocultar la
     * contraseña en texto plano antes de persistir los datos de forma transaccional.
     * * @param nombre      Nombre de pila del anfitrión.
     * @param apellidos   Apellidos del anfitrión.
     * @param email       Dirección de correo electrónico (debe ser única).
     * @param telefono    Número telefónico de contacto.
     * @param usuario     Nombre de usuario exclusivo para el acceso al sistema.
     * @param passPlana   Contraseña original en texto plano que será encriptada.
     * @return El objeto {@link Anfitrion} creado con su credencial ya encriptada y persistida de forma exitosa.
     * @throws AutenticacionException Si ocurre un conflicto de unicidad en la base de datos o ante fallos
     * críticos en la transacción de guardado.
     */
    public Anfitrion registrarAnfitrion(String nombre, String apellidos, String email, String telefono, String usuario, String passPlana) throws AutenticacionException {
        Anfitrion a = new Anfitrion();
        a.setNombre(nombre);
        a.setApellidos(apellidos);
        a.setEmail(email);
        a.setTelefono(telefono);
        a.setUsuario(usuario);

        String hashEncriptado = BCrypt.hashpw(passPlana, BCrypt.gensalt(12));
        a.setPass(hashEncriptado);

        em.getTransaction().begin();
        try {
            em.persist(a);
            em.getTransaction().commit();
            return a;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new AutenticacionException("No se pudo registrar el anfitrión en la base de datos", e);
        }
    }

    /**
     * Registra un nuevo Huésped en el sistema aplicando políticas de seguridad activa.
     * Genera un hash Bcrypt con un factor de coste adaptativo (12 rounds de sal) para ocultar la
     * contraseña en texto plano antes de persistir los datos de forma transaccional.
     * * @param nombre      Nombre de pila del huésped.
     * @param apellidos   Apellidos del huésped.
     * @param email       Dirección de correo electrónico (debe ser única).
     * @param telefono    Número telefónico de contacto.
     * @param usuario     Nombre de usuario exclusivo para el acceso al sistema.
     * @param passPlana   Contraseña original en texto plano que será encriptada.
     * @return El objeto {@link Huesped} creado con su credencial ya encriptada y persistida de forma exitosa.
     * @throws AutenticacionException Si ocurre un conflicto de unicidad en la base de datos o ante fallos
     * críticos en la transacción de guardado.
     */
    public Huesped registrarHuesped(String nombre, String apellidos, String email, String telefono, String usuario, String passPlana) throws AutenticacionException {
        Huesped h = new Huesped();
        h.setNombre(nombre);
        h.setApellidos(apellidos);
        h.setEmail(email);
        h.setTelefono(telefono);
        h.setUsuario(usuario);

        String hashEncriptado = BCrypt.hashpw(passPlana, BCrypt.gensalt(12));
        h.setPass(hashEncriptado);

        em.getTransaction().begin();
        try {
            em.persist(h);
            em.getTransaction().commit();
            return h;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new AutenticacionException("No se pudo registrar el huésped en la base de datos", e);
        }
    }

    /**
     * Registra un nuevo Operario de Mantenimiento en el sistema.
     * Basado en la entidad OperarioMantenimiento que solo requiere nombre y usuario.
     * * @param nombre    Nombre del operario.
     * @param usuario   Nombre de usuario para el sistema.
     * @param passPlana Contraseña en texto plano.
     * @return El objeto {@link OperarioMantenimiento} persistido.
     * @throws AutenticacionException Si falla el registro en la base de datos.
     */
    public OperarioMantenimiento registrarOperario(String nombre, String usuario, String passPlana) throws AutenticacionException {
        OperarioMantenimiento o = new OperarioMantenimiento();
        o.setNombre(nombre);
        o.setUsuario(usuario);

        String hashEncriptado = BCrypt.hashpw(passPlana, BCrypt.gensalt(12));
        o.setPass(hashEncriptado);

        em.getTransaction().begin();
        try {
            em.persist(o);
            em.getTransaction().commit();
            return o;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new AutenticacionException("Error al registrar el operario en la base de datos.", e);
        }
    }
}