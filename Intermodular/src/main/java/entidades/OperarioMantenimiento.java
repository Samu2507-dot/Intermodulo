package entidades;

import jakarta.persistence.*;

@Entity
@Table(name = "operarios_mantenimiento")
public class OperarioMantenimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_operario")
    private Integer idOperario;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, unique = true, length = 50)
    private String usuario;

    @Column(nullable = false, length = 60)
    private String pass;

    // Constructores
    public OperarioMantenimiento() {}

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