package entidades;

import jakarta.persistence.*;

/**
 * Entidad que representa a un Operario de Mantenimiento en el sistema Roomly.
 * Almacena los datos de identidad, nombre del técnico y credenciales de acceso de los operarios
 * encargados de revisar y reparar desperfectos en los alojamientos, mapeados con la tabla 'operarios_mantenimiento'.
 */
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

    public Integer getIdOperario() { return idOperario; }
    public void setIdOperario(Integer idOperario) { this.idOperario = idOperario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getPass() { return pass; }
    public void setPass(String pass) { this.pass = pass; }
}