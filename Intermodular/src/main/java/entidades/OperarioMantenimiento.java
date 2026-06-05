package entidades;

import jakarta.persistence.*;

@Entity
@Table(name = "operarios_mantenimiento")
public class OperarioMantenimiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_operario", nullable = false)
    private Integer idOperario;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "usuario", nullable = false, unique = true, length = 50)
    private String usuario;

    @Column(name = "pass", nullable = false, length = 60)
    private String pass;

    // Getters y Setters
    public Integer getIdOperario() { return idOperario; }
    public void setIdOperario(Integer idOperario) { this.idOperario = idOperario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getPass() { return pass; }
    public void setPass(String pass) { this.pass = pass; }
}