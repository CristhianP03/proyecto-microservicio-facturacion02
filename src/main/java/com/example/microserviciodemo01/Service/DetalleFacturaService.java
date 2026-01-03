package com.example.microserviciodemo01.Service;

import com.example.microserviciodemo01.models.DetalleFactura;
import com.example.microserviciodemo01.Repository.DetalleFacturaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DetalleFacturaService {

    private final DetalleFacturaRepository detalleFacturaRepository;

    public DetalleFacturaService(DetalleFacturaRepository detalleFacturaRepository) {
        this.detalleFacturaRepository = detalleFacturaRepository;
    }

    public List<DetalleFactura> obtenerTodos() {
        return detalleFacturaRepository.findAll();
    }
}
