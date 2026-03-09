package com.example.microserviciodemo01.Service;

import com.example.microserviciodemo01.Repository.DetalleVentaRepository;
import com.example.microserviciodemo01.Repository.FacturaRepository;
import com.example.microserviciodemo01.Repository.VentaRepository;
import com.example.microserviciodemo01.models.DetalleVenta;
import com.example.microserviciodemo01.models.Factura;
import com.example.microserviciodemo01.models.Venta;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final FacturaRepository facturaRepository;

    private static final BigDecimal TASA_IVA = new BigDecimal("0.15");

    // CORRECCIÓN: se agrega FacturaRepository para poder eliminar
    // facturas asociadas cuando se borran ventas de prueba.
    public VentaService(VentaRepository ventaRepository,
                        DetalleVentaRepository detalleVentaRepository,
                        FacturaRepository facturaRepository) {
        this.ventaRepository = ventaRepository;
        this.detalleVentaRepository = detalleVentaRepository;
        this.facturaRepository = facturaRepository;
    }

    @Transactional
    public Venta crearVenta(Venta venta) {
        if (venta.getDetalles() == null || venta.getDetalles().isEmpty()) {
            throw new RuntimeException("La venta debe tener al menos un producto.");
        }
        if (venta.getValorPagado() == null ||
                venta.getValorPagado().compareTo(venta.getTotal()) < 0) {
            throw new RuntimeException("El valor pagado no puede ser menor al total.");
        }

        BigDecimal subtotal = venta.getDetalles().stream()
                .map(d -> d.getPrecioUnitario()
                        .multiply(new BigDecimal(d.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal impuestos = subtotal
                .multiply(TASA_IVA)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal total = subtotal.add(impuestos)
                .setScale(2, RoundingMode.HALF_UP);

        venta.setSubtotal(subtotal);
        venta.setImpuestos(impuestos);
        venta.setTotal(total);
        venta.setFechaVenta(LocalDateTime.now());
        venta.setEstado("COMPLETADA");
        venta.setNumeroRegistro(generarNumeroRegistro());

        for (DetalleVenta detalle : venta.getDetalles()) {
            detalle.setVenta(venta);
            BigDecimal totalDetalle = detalle.getPrecioUnitario()
                    .multiply(new BigDecimal(detalle.getCantidad()))
                    .setScale(2, RoundingMode.HALF_UP);
            detalle.setTotal(totalDetalle);
        }

        return ventaRepository.save(venta);
    }

    private String generarNumeroRegistro() {
        String ultimo = ventaRepository.findUltimoNumeroRegistro().orElse(null);
        int siguiente = 1;
        if (ultimo != null) {
            try {
                siguiente = Integer.parseInt(ultimo) + 1;
            } catch (NumberFormatException e) {
                siguiente = 1;
            }
        }
        return String.format("%09d", siguiente);
    }

    @Transactional(readOnly = true)
    public List<Venta> listarVentas() {
        return ventaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Venta obtenerPorId(Integer idVenta) {
        return ventaRepository.findById(idVenta)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada: " + idVenta));
    }

    /**
     * Elimina todas las ventas marcadas como esModoTest = true,
     * junto con sus facturas y detalles asociados.
     * Este método solo es llamado desde TestController (modo prueba).
     */
    @Transactional
    public void eliminarVentasPrueba() {
        List<Venta> ventasPrueba = ventaRepository.findAll().stream()
                .filter(v -> Boolean.TRUE.equals(v.getEsModoTest()))
                .collect(Collectors.toList());

        for (Venta v : ventasPrueba) {
            // 1. Eliminar factura asociada si existe (los DetalleFactura
            //    se eliminan en cascada si la entidad Factura tiene
            //    CascadeType.ALL en su relación con DetalleFactura).
            Optional<Factura> factura = facturaRepository.findByVenta_IdVenta(v.getIdVenta());
            factura.ifPresent(facturaRepository::delete);

            // 2. Eliminar los detalles de la venta
            List<DetalleVenta> detalles = detalleVentaRepository.findByVenta_IdVenta(v.getIdVenta());
            detalleVentaRepository.deleteAll(detalles);
        }

        // 3. Eliminar las ventas de prueba
        ventaRepository.deleteAll(ventasPrueba);
    }
}