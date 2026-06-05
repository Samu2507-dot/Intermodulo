package entidades;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "resenas")
public class Resena {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_resena", nullable = false)
    private Integer idResena;

    @OneToOne
    @JoinColumn(name = "id_reserva", unique = true, nullable = false)
    private Reserva reserva;

    @Column(name = "puntuacion", nullable = false)
    private Integer puntuacion;

    @Column(name = "comentario")
    private String comentario;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    // Getters y Setters
    public Integer getIdResena() { return idResena; }
    public void setIdResena(Integer idResena) { this.idResena = idResena; }

    public Reserva getReserva() { return reserva; }
    public void setReserva(Reserva reserva) { this.reserva = reserva; }

    public Integer getPuntuacion() { return puntuacion; }
    public void setPuntuacion(Integer puntuacion) { this.puntuacion = puntuacion; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
}