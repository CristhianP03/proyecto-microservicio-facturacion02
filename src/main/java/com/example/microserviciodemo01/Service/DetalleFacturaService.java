package com.example.microserviciodemo01.Service;

import com.example.microserviciodemo01.Repository.DetalleFacturaRepository;
import com.example.microserviciodemo01.models.DetalleFactura;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DetalleFacturaService {

    private final DetalleFacturaRepository detalleFacturaRepository;

    public DetalleFacturaService(DetalleFacturaRepository detalleFacturaRepository) {
        this.detalleFacturaRepository = detalleFacturaRepository;
    }

    @Transactional(readOnly = true)
    public List<DetalleFactura> obtenerPorFactura(Integer idFactura) {
        return detalleFacturaRepository.findByFactura_IdFactura(idFactura);
    }
}