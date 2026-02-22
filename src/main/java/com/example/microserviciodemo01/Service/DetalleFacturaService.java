package com.example.microserviciodemo01.Service;

import com.example.microserviciodemo01.Repository.DetalleFacturaRepository;
import com.example.microserviciodemo01.Repository.FacturaRepository;
import com.example.microserviciodemo01.models.DetalleFactura;
import com.example.microserviciodemo01.models.Factura;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

    @Transactional(readOnly = true)
    public List<DetalleFactura> obtenerPorFactura(Integer idFactura) {
        return detalleFacturaRepository.findByFactura_IdFactura(idFactura);
    }

    @Transactional
    public DetalleFactura crearDetalle(Integer idFactura, DetalleFactura detalle) {
        // 1. Validar Factura
        Factura factura = facturaRepository.findById(idFactura)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada: ID " + idFactura));

        // 2. Validación Estricta: Precio y Cantidad
        if (detalle.getPrecioUnitario() == null || detalle.getPrecioUnitario().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("El precio unitario debe ser mayor a cero.");
        }
        if (detalle.getCantidad() == null || detalle.getCantidad() <= 0) {
            throw new RuntimeException("La cantidad debe ser al menos 1.");
        }

        // 3. Calcular Total del Detalle (Precio * Cantidad)
        BigDecimal totalDetalle = detalle.getPrecioUnitario().multiply(new BigDecimal(detalle.getCantidad()));
        detalle.setTotal(totalDetalle);
        detalle.setFactura(factura);

        DetalleFactura guardado = detalleFacturaRepository.save(detalle);

        // 4. RECALCULO CRÍTICO: Actualizar la Factura padre
        actualizarTotalesFactura(factura);

        return guardado;
    }

    @Transactional
    public void eliminarDetalle(Integer idFactura, Integer idDetalle) {
        DetalleFactura detalle = detalleFacturaRepository.findById(idDetalle)
                .orElseThrow(() -> new RuntimeException("Detalle no encontrado: ID " + idDetalle));

        // Validación de pertenencia
        if (!detalle.getFactura().getIdFactura().equals(idFactura)) {
            throw new RuntimeException("El detalle no pertenece a la factura indicada.");
        }

        Factura factura = detalle.getFactura();
        detalleFacturaRepository.delete(detalle);

        // RECALCULO CRÍTICO tras eliminación
        actualizarTotalesFactura(factura);
    }

    /**
     * Mantiene la integridad financiera de la factura.
     * Suma todos los detalles y actualiza la cabecera.
     */
    private void actualizarTotalesFactura(Factura factura) {
        List<DetalleFactura> detalles = detalleFacturaRepository.findByFactura_IdFactura(factura.getIdFactura());

        BigDecimal nuevoSubtotal = detalles.stream()
                .map(DetalleFactura::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Suponiendo IVA del 15% (ajustar según tu lógica de FacturaService)
        BigDecimal impuestos = nuevoSubtotal.multiply(new BigDecimal("0.15"));
        BigDecimal nuevoTotal = nuevoSubtotal.add(impuestos);

        factura.setSubtotal(nuevoSubtotal);
        factura.setImpuestos(impuestos);
        factura.setTotal(nuevoTotal);

        facturaRepository.save(factura);
    }
}