package entidades;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "anfitriones")
public class Anfitrion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_anfitrion")
    private Integer idAnfitrion;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellidos;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 15)
    private String telefono;

    @Column(nullable = false, unique = true, length = 50)
    private String usuario;

    @Column(nullable = false, length = 60)
    private String pass;

    @OneToMany(mappedBy = "anfitrion")
    private List<Alojamiento> alojamientos;

    // Constructores
    public Anfitrion() {}

    // Getters y Setters
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

    public List<Alojamiento> getAlojamientos() { return alojamientos; }
    public void setAlojamientos(List<Alojamiento> alojamientos) { this.alojamientos = alojamientos; }
}