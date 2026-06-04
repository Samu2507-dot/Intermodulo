package entidades;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "resenas")
public class Resena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_resena")
    private Integer idResena;

    // Relación OneToOne porque tu SQL dice que el id_reserva es UNIQUE en la tabla reseñas.
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_reserva", unique = true, nullable = false)
    private Reserva reserva;

    @Column(nullable = false)
    private Integer puntuacion;

    @Column(columnDefinition = "TEXT")
    private String comentario;

    @Column(nullable = false)
    private LocalDate fecha;

    // Constructores
    public Resena() {}

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