package com.dam.cicd.entidades;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Entidad que representa un alojamiento en el sistema Roomly.
 * Mapea los datos de los inmuebles disponibles para alquiler con la tabla 'alojamientos' en la base de datos.
 */
@Entity
@Table(name = "alojamientos")
public class Alojamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alojamiento", nullable = false)
    private Integer idAlojamiento;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "direccion", nullable = false, length = 255)
    private String direccion;

    @Column(name = "precio_dia", nullable = false)
    private BigDecimal precioDia;

    /**
     * Anfitrión propietario del alojamiento.
     * Establece una relación de muchos a uno con la entidad Anfitrion.
     */
    @ManyToOne
    @JoinColumn(name = "id_anfitrion", nullable = false)
    private Anfitrion anfitrion;

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