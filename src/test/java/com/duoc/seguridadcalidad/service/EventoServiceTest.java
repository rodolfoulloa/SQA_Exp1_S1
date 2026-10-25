package com.duoc.seguridadcalidad.service;

import com.duoc.seguridadcalidad.model.Evento;
import com.duoc.seguridadcalidad.repository.EventoRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EventoServiceTest {

    private final EventoService service = new EventoService(new EventoRepository());

    @Test
    void sinFiltrosDevuelveTodosOrdenadosPorFecha() {
        List<Evento> eventos = service.buscar(null, null, null, "");
        assertThat(eventos).hasSize(8);
        for (int i = 1; i < eventos.size(); i++) {
            assertThat(eventos.get(i).getFechaHora()).isAfterOrEqualTo(eventos.get(i - 1).getFechaHora());
        }
    }

    @Test
    void filtraPorCategoria() {
        assertThat(service.buscar("eSports", null, null, ""))
                .isNotEmpty()
                .allMatch(e -> e.getCategoria().equals("eSports"));
    }

    @Test
    void filtraPorUbicacionSinImportarTildesNiMayusculas() {
        assertThat(service.buscar(null, null, null, "CONCEPCION"))
                .extracting(Evento::getCiudad)
                .containsOnly("Concepción");
    }

    @Test
    void filtraPorRangoDeFechas() {
        LocalDate desde = LocalDate.now().plusDays(10);
        LocalDate hasta = LocalDate.now().plusDays(21);
        assertThat(service.buscar(null, desde, hasta, ""))
                .isNotEmpty()
                .allMatch(e -> !e.getFechaHora().toLocalDate().isBefore(desde)
                        && !e.getFechaHora().toLocalDate().isAfter(hasta));
    }

    @Test
    void proximosRespetaElLimite() {
        assertThat(service.proximos(3)).hasSize(3);
    }
}
