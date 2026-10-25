package com.duoc.seguridadcalidad.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Evento gamer publicado en el sitio.
 */
public class Evento {

    private Long id;
    private String nombre;
    private String categoria;
    private String descripcion;
    private LocalDateTime fechaHora;
    private String direccion;
    private String ciudad;
    private List<String> organizadores;
    private List<String> expositores;
    private boolean estacionamiento;
    private boolean locomocionColectiva;
    private boolean servicioComidas;
    private boolean alojamiento;
    private boolean destacado;
    private String precio;

    public Evento(Long id, String nombre, String categoria, String descripcion, LocalDateTime fechaHora,
                  String direccion, String ciudad, List<String> organizadores, List<String> expositores,
                  boolean estacionamiento, boolean locomocionColectiva, boolean servicioComidas,
                  boolean alojamiento, boolean destacado, String precio) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.fechaHora = fechaHora;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.organizadores = organizadores;
        this.expositores = expositores;
        this.estacionamiento = estacionamiento;
        this.locomocionColectiva = locomocionColectiva;
        this.servicioComidas = servicioComidas;
        this.alojamiento = alojamiento;
        this.destacado = destacado;
        this.precio = precio;
    }

    private static final DateTimeFormatter FORMATO_LARGO =
            DateTimeFormatter.ofPattern("EEEE d 'de' MMMM 'de' yyyy", Locale.forLanguageTag("es-CL"));

    /** Fecha en formato largo en espanol, por ejemplo "sabado 14 de noviembre de 2026". */
    public String getFechaLarga() {
        return fechaHora == null ? "" : fechaHora.format(FORMATO_LARGO);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }

    public String getCategoria() { return categoria; }

    public String getDescripcion() { return descripcion; }

    public LocalDateTime getFechaHora() { return fechaHora; }

    public String getDireccion() { return direccion; }

    public String getCiudad() { return ciudad; }

    public List<String> getOrganizadores() { return organizadores; }

    public List<String> getExpositores() { return expositores; }

    public boolean isEstacionamiento() { return estacionamiento; }

    public boolean isLocomocionColectiva() { return locomocionColectiva; }

    public boolean isServicioComidas() { return servicioComidas; }

    public boolean isAlojamiento() { return alojamiento; }

    public boolean isDestacado() { return destacado; }

    public String getPrecio() { return precio; }
}
