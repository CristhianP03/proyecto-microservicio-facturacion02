package com.example.microserviciodemo01.Controller;

import com.example.microserviciodemo01.models.EventoFactura;
import com.example.microserviciodemo01.Service.EventoFacturaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/eventos")
public class EventoFacturaController {

    private final EventoFacturaService eventoFacturaService;

    public EventoFacturaController(EventoFacturaService eventoFacturaService) {
        this.eventoFacturaService = eventoFacturaService;
    }

    @GetMapping
    public List<EventoFactura> obtenerEventos() {
        return eventoFacturaService.obtenerTodos();
    }
}
