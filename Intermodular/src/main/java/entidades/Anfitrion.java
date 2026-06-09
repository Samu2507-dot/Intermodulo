package entidades;

import jakarta.persistence.*;

/**
 * Entidad que representa a un Anfitrión en el sistema Roomly.
 * Almacena los datos personales, de contacto y credenciales de acceso de los usuarios
 * propietarios que publican y gestionan alojamientos, mapeados con la tabla 'anfitriones'.
 */
@Entity
@Table(name = "anfitriones")
public class Anfitrion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_anfitrion", nullable = false)
    private Integer idAnfitrion;

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

    public Integer getIdAnfitrion() { return idAnfitrion; }
    public void setIdAnfitrion(Integer idAnfitrion) { this.idAnfitrion = idAnfitrion; }

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