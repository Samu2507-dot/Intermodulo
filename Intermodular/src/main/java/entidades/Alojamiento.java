package entidades;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "alojamientos")
public class Alojamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alojamiento")
    private Integer idAlojamiento;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 255)
    private String direccion;

    @Column(name = "precio_dia", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioDia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_anfitrion", nullable = false)
    private Anfitrion anfitrion;

    // Constructores
    public Alojamiento() {}

    // Getters y Setters
    public Integer getIdAlojamiento() { return idAlojamiento; }
    public void setIdAlojamiento(Integer idAlojamiento) { this.idAlojamiento = idAlojamiento; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public BigDecimal getPrecioDia() { return precioDia; }
    public void setPrecioDia(BigDecimal precioDia) { this.precioDia = precioDia; }

    public Anfitrion getAnfitrion() { return anfitrion; }
    public void setAnfitrion(Anfitrion anfitrion) { this.anfitrion = anfitrion; }
}