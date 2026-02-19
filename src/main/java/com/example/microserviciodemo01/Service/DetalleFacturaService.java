package com.example.microserviciodemo01.Service;

import com.example.microserviciodemo01.Repository.DetalleFacturaRepository;
import com.example.microserviciodemo01.Repository.FacturaRepository;
import com.example.microserviciodemo01.models.DetalleFactura;
import com.example.microserviciodemo01.models.Factura;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DetalleFacturaService {

    private final DetalleFacturaRepository detalleFacturaRepository;
    private final FacturaRepository facturaRepository;

    public DetalleFacturaService(
            DetalleFacturaRepository detalleFacturaRepository,
            FacturaRepository facturaRepository) {

        this.detalleFacturaRepository = detalleFacturaRepository;
        this.facturaRepository = facturaRepository;
    }

    // Obtener detalles por factura
    public List<DetalleFactura> obtenerPorFactura(Integer idFactura) {
        return detalleFacturaRepository.findByFactura_IdFactura(idFactura);
    }

    // Crear detalle
    @Transactional
    public DetalleFactura crearDetalle(Integer idFactura, DetalleFactura detalle) {

        Factura factura = facturaRepository.findById(idFactura)
                .orElseThrow(() ->
                        new RuntimeException("Factura no encontrada: id=" + idFactura)
                );

        detalle.setFactura(factura);

        return detalleFacturaRepository.save(detalle);
    }

    // Eliminar detalle validando que pertenezca a la factura
    @Transactional
    public void eliminarDetalle(Integer idFactura, Integer idDetalle) {

        DetalleFactura detalle = detalleFacturaRepository.findById(idDetalle)
                .orElseThrow(() ->
                        new RuntimeException("Detalle no encontrado: id=" + idDetalle)
                );

        if (!detalle.getFactura().getIdFactura().equals(idFactura)) {
            throw new RuntimeException("El detalle no pertenece a la factura indicada");
        }

        detalleFacturaRepository.delete(detalle);
    }
}
