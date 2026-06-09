package entidades;

import jakarta.persistence.*;

/**
 * Entidad que representa a un Huésped en el sistema Roomly.
 * Almacena los datos personales, de contacto y credenciales de acceso de los usuarios
 * clientes que realizan reservas de alojamientos, mapeados con la tabla 'huespedes'.
 */
@Entity
@Table(name = "huespedes")
public class Huesped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_huesped", nullable = false)
    private Integer idHuesped;

    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    @Column(name = "apellidos", nullable = false, length = 100)
    private String apellidos;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "telefono", nullable = false, length = 15)
    private String telefono;

    @Column(name = "usuario", nullable = false, unique = true, length = 50)
    private String usuario;

    @Column(name = "pass", nullable = false, length = 60)
    private String pass;

    public Integer getIdHuesped() { return idHuesped; }
    public void setIdHuesped(Integer idHuesped) { this.idHuesped = idHuesped; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getPass() { return pass; }
    public void setPass(String pass) { this.pass = pass; }
}