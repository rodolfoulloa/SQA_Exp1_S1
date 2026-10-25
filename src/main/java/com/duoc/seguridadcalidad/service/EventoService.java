package com.duoc.seguridadcalidad.service;

import com.duoc.seguridadcalidad.model.Evento;
import com.duoc.seguridadcalidad.repository.EventoRepository;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.TreeSet;

@Service
public class EventoService {

    public static final List<String> CATEGORIAS = List.of(
            "eSports", "LAN Party", "Convención", "Retro", "Cosplay", "Torneo Fighting");

    private final EventoRepository repository;

    public EventoService(EventoRepository repository) {
        this.repository = repository;
    }

    public List<Evento> proximos(int limite) {
        LocalDateTime ahora = LocalDateTime.now();
        return repository.findAll().stream()
                .filter(e -> e.getFechaHora().isAfter(ahora))
                .sorted(Comparator.comparing(Evento::getFechaHora))
                .limit(limite)
                .toList();
    }

    public List<Evento> destacados() {
        return repository.findAll().stream()
                .filter(Evento::isDestacado)
                .sorted(Comparator.comparing(Evento::getFechaHora))
                .toList();
    }

    /**
     * Busqueda por categoria, rango de fechas y ubicacion (ciudad o direccion).
     * Todos los filtros son opcionales.
     */
    public List<Evento> buscar(String categoria, LocalDate desde, LocalDate hasta, String ubicacion) {
        String ubicacionNormalizada = normalizar(ubicacion);
        return repository.findAll().stream()
                .filter(e -> categoria == null || categoria.isBlank() || e.getCategoria().equals(categoria))
                .filter(e -> desde == null || !e.getFechaHora().toLocalDate().isBefore(desde))
                .filter(e -> hasta == null || !e.getFechaHora().toLocalDate().isAfter(hasta))
                .filter(e -> ubicacionNormalizada.isEmpty()
                        || normalizar(e.getCiudad()).contains(ubicacionNormalizada)
                        || normalizar(e.getDireccion()).contains(ubicacionNormalizada))
                .sorted(Comparator.comparing(Evento::getFechaHora))
                .toList();
    }

    public Optional<Evento> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public List<String> ciudades() {
        TreeSet<String> ciudades = new TreeSet<>();
        repository.findAll().forEach(e -> ciudades.add(e.getCiudad()));
        return List.copyOf(ciudades);
    }

    /** Minusculas y sin tildes, para comparar "Concepcion" con "Concepción". */
    static String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        String sinTildes = Normalizer.normalize(texto.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return sinTildes.toLowerCase(Locale.ROOT);
    }
}
