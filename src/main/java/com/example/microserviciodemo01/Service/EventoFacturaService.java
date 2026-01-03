package com.example.microserviciodemo01.Service;

import com.example.microserviciodemo01.models.EventoFactura;
import com.example.microserviciodemo01.Repository.EventoFacturaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventoFacturaService {

    private final EventoFacturaRepository eventoFacturaRepository;

    public EventoFacturaService(EventoFacturaRepository eventoFacturaRepository) {
        this.eventoFacturaRepository = eventoFacturaRepository;
    }

    public List<EventoFactura> obtenerTodos() {
        return eventoFacturaRepository.findAll();
    }
}
