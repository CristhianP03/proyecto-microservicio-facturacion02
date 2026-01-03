package com.example.microserviciodemo01.Service;

import com.example.microserviciodemo01.models.Factura;
import com.example.microserviciodemo01.Repository.FacturaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FacturaService {

    private final FacturaRepository facturaRepository;

    public FacturaService(FacturaRepository facturaRepository) {
        this.facturaRepository = facturaRepository;
    }

    public List<Factura> obtenerTodas() {
        return facturaRepository.findAll();
    }
}
