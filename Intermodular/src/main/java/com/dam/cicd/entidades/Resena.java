package com.dam.cicd.entidades;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Entidad que representa una Reseña o valoración en el sistema Roomly.
 * Almacena la puntuación, comentarios y fecha de la opinión emitida por un huésped
 * sobre su estancia, mapeada con la tabla 'resenas'.
 */
@Entity
@Table(name = "resenas")
public class Resena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_resena", nullable = false)
    private Integer idResena;

    /**
     * Reserva a la que pertenece la reseña.
     * Establece una relación uno a uno bidireccional o unidireccional estricta con la entidad Reserva.
     */
    @OneToOne
    @JoinColumn(name = "id_reserva", unique = true, nullable = false)
    private Reserva reserva;

    @Column(name = "puntuacion", nullable = false)
    private Integer puntuacion;

    @Column(name = "comentario")
    private String comentario;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

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