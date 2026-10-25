package com.duoc.seguridadcalidad.repository;

import com.duoc.seguridadcalidad.model.Evento;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Repositorio en memoria de eventos. En semanas siguientes se reemplaza por JPA.
 */
@Repository
public class EventoRepository {

    private final Map<Long, Evento> eventos = new ConcurrentHashMap<>();
    private final AtomicLong secuencia = new AtomicLong();

    public EventoRepository() {
        cargarDatosIniciales();
    }

    public List<Evento> findAll() {
        return new ArrayList<>(eventos.values());
    }

    public Optional<Evento> findById(Long id) {
        return Optional.ofNullable(eventos.get(id));
    }

    public Evento save(Evento evento) {
        if (evento.getId() == null) {
            evento.setId(secuencia.incrementAndGet());
        }
        eventos.put(evento.getId(), evento);
        return evento;
    }

    /** Fechas relativas a hoy para que siempre existan "proximos eventos". */
    private static LocalDateTime en(int dias, int hora, int minuto) {
        return LocalDateTime.of(LocalDate.now().plusDays(dias), LocalTime.of(hora, minuto));
    }

    private void cargarDatosIniciales() {
        save(new Evento(null, "Santiago Gaming Fest", "Convención",
                "La convención gamer más grande de la capital: zona de lanzamientos, free play, "
                        + "charlas de la industria y torneos abiertos para todas las edades.",
                en(12, 10, 0), "Av. Libertador Bernardo O'Higgins 3349, Estación Central", "Santiago",
                List.of("Asociación Gamer de Chile", "Productora Nivel 99"),
                List.of("Estudio Indie Andino", "Comunidad Speedrun Chile", "Periodistas de videojuegos"),
                true, true, true, false, true, "$12.000"));

        save(new Evento(null, "Copa Valorant Universitaria", "eSports",
                "Torneo 5v5 entre equipos de institutos y universidades. Fase final presencial con caster en vivo.",
                en(20, 15, 30), "Av. Vicuña Mackenna 4917, San Joaquín", "Santiago",
                List.of("Liga Universitaria de eSports"),
                List.of("Caster oficial de la liga", "Coach profesional invitado"),
                true, true, true, false, true, "Gratis"));

        save(new Evento(null, "LAN Party Puerto Byte", "LAN Party",
                "48 horas de juego en red. Trae tu PC, nosotros ponemos la red, la energía y el café.",
                en(30, 18, 0), "Av. Brasil 2950, Valparaíso", "Valparaíso",
                List.of("Club Puerto Byte"),
                List.of("Fabricante de periféricos local", "Técnicos de redes voluntarios"),
                false, true, true, true, false, "$8.000"));

        save(new Evento(null, "Retro Arcade Night", "Retro",
                "Noche de máquinas arcade, consolas clásicas y torneo de puntaje en juegos de los 80 y 90.",
                en(7, 19, 0), "Barrio Lastarria, Santiago Centro", "Santiago",
                List.of("Colectivo Retro Pixel"),
                List.of("Coleccionistas de consolas", "Restauradores de arcade"),
                false, true, true, false, true, "$5.000"));

        save(new Evento(null, "Concepción Cosplay Weekend", "Cosplay",
                "Concurso de cosplay, pasarela, talleres de maquillaje y armaduras con foam.",
                en(45, 11, 0), "Av. Pedro de Valdivia 1150, Concepción", "Concepción",
                List.of("Cosplay Bío Bío", "Centro Cultural Sur"),
                List.of("Jurado de cosplay internacional", "Talleristas de props"),
                true, true, true, true, false, "$6.000"));

        save(new Evento(null, "Fight Night Chile", "Torneo Fighting",
                "Torneo de juegos de pelea en formato doble eliminación. Inscripción previa obligatoria.",
                en(16, 17, 0), "Av. Providencia 1308, Providencia", "Santiago",
                List.of("FGC Chile"),
                List.of("Jugadores ranqueados nacionales", "Comentaristas de la FGC"),
                false, true, true, false, false, "$4.000"));

        save(new Evento(null, "Torneo Rocket League Viña", "eSports",
                "Torneo 3v3 con premios en periféricos. Transmisión en vivo por streaming.",
                en(25, 14, 0), "Av. San Martín 199, Viña del Mar", "Viña del Mar",
                List.of("Viña eSports"),
                List.of("Streamer local invitado"),
                true, true, false, false, false, "$3.000"));

        save(new Evento(null, "Indie Dev Meetup Antofagasta", "Convención",
                "Encuentro de desarrolladores independientes: demos jugables, revisión de portafolios y networking.",
                en(60, 16, 0), "Av. Angamos 0610, Antofagasta", "Antofagasta",
                List.of("Comunidad Indie Norte"),
                List.of("Estudios indie del norte", "Mentores de publishing"),
                true, false, true, true, false, "Gratis"));
    }
}
