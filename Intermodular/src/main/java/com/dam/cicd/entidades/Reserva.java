package com.dam.cicd.entidades;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidad que representa una Reserva en el sistema Roomly.
 * Vincula a un huésped con un alojamiento concreto para un rango de fechas determinado,
 * registrando el importe total de la estancia, mapeado con la tabla 'reservas'.
 */
@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva", nullable = false)
    private Integer idReserva;

    /**
     * Alojamiento que ha sido reservado.
     * Establece una relación de muchos a uno con la entidad Alojamiento.
     */
    @ManyToOne
    @JoinColumn(name = "id_alojamiento", nullable = false)
    private Alojamiento alojamiento;

    /**
     * Huésped que realiza y es titular de la reserva.
     * Establece una relación de muchos a uno con la entidad Huesped.
     */
    @ManyToOne
    @JoinColumn(name = "id_huesped", nullable = false)
    private Huesped huesped;

    @Column(name = "fecha_entrada", nullable = false)
    private LocalDate fechaEntrada;

    @Column(name = "fecha_salida", nullable = false)
    private LocalDate fechaSalida;

    @Column(name = "precio_total", nullable = false)
    private BigDecimal precioTotal;

    public Integer getIdReserva() { return idReserva; }
    public void setIdReserva(Integer idReserva) { this.idReserva = idReserva; }

    public Alojamiento getAlojamiento() { return alojamiento; }
    public void setAlojamiento(Alojamiento alojamiento) { this.alojamiento = alojamiento; }

    public Huesped getHuesped() { return huesped; }
    public void setHuesped(Huesped huesped) { this.huesped = huesped; }

    public LocalDate getFechaEntrada() { return fechaEntrada; }
    public void setFechaEntrada(LocalDate fechaEntrada) { this.fechaEntrada = fechaEntrada; }

    public LocalDate getFechaSalida() { return fechaSalida; }
    public void setFechaSalida(LocalDate fechaSalida) { this.fechaSalida = fechaSalida; }

    public BigDecimal getPrecioTotal() { return precioTotal; }
    public void setPrecioTotal(BigDecimal precioTotal) { this.precioTotal = precioTotal; }
}