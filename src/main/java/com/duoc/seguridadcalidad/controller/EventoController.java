package com.duoc.seguridadcalidad.controller;

import com.duoc.seguridadcalidad.model.Evento;
import com.duoc.seguridadcalidad.service.EventoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Controller
@RequestMapping("/eventos")
public class EventoController {

    private final EventoService eventoService;

    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String categoria,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
                         @RequestParam(required = false) String ubicacion,
                         Model model) {
        // Solo se acepta una categoria de la lista conocida.
        // List.of(...).contains(null) lanza NPE, por eso se valida null antes.
        String categoriaValida = categoria != null && EventoService.CATEGORIAS.contains(categoria) ? categoria : null;
        String ubicacionLimpia = ubicacion == null ? "" : ubicacion.trim();
        if (ubicacionLimpia.length() > 60) {
            ubicacionLimpia = ubicacionLimpia.substring(0, 60);
        }

        model.addAttribute("eventos", eventoService.buscar(categoriaValida, desde, hasta, ubicacionLimpia));
        model.addAttribute("categorias", EventoService.CATEGORIAS);
        model.addAttribute("ciudades", eventoService.ciudades());
        model.addAttribute("categoria", categoriaValida);
        model.addAttribute("desde", desde);
        model.addAttribute("hasta", hasta);
        model.addAttribute("ubicacion", ubicacionLimpia);
        return "eventos";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        Evento evento = eventoService.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("evento", evento);
        return "evento-detalle";
    }
}
