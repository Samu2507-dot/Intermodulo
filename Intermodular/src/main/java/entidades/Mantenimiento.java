package entidades;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "mantenimientos")
public class Mantenimiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mantenimiento", nullable = false)
    private Integer idMantenimiento;

    @ManyToOne
    @JoinColumn(name = "id_alojamiento", nullable = false)
    private Alojamiento alojamiento;

    @ManyToOne
    @JoinColumn(name = "id_operario", nullable = false)
    private OperarioMantenimiento operario;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "descripcion", nullable = false, length = 255)
    private String descripcion;

    @Column(name = "estado")
    private String estado;

    // Getters y Setters
    public Integer getIdMantenimiento() { return idMantenimiento; }
    public void setIdMantenimiento(Integer idMantenimiento) { this.idMantenimiento = idMantenimiento; }

    public Alojamiento getAlojamiento() { return alojamiento; }
    public void setAlojamiento(Alojamiento alojamiento) { this.alojamiento = alojamiento; }

    public OperarioMantenimiento getOperario() { return operario; }
    public void setOperario(OperarioMantenimiento operario) { this.operario = operario; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}