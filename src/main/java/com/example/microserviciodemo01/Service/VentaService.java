package com.example.microserviciodemo01.Service;

import com.example.microserviciodemo01.Repository.VentaRepository;
import com.example.microserviciodemo01.models.Factura;
import com.example.microserviciodemo01.models.Venta;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final FacturaService facturaService;

    public VentaService(VentaRepository ventaRepository,
                        FacturaService facturaService) {
        this.ventaRepository = ventaRepository;
        this.facturaService = facturaService;
    }

    @Transactional
    public Venta crearVenta(Venta venta) {
        // Validación estricta: No permitir ventas con total nulo o negativo
        if (venta.getTotal() == null || venta.getTotal().doubleValue() < 0) {
            throw new RuntimeException("El total de la venta debe ser un valor positivo.");
        }
        return ventaRepository.save(venta);
    }

    @Transactional
    public Factura generarFactura(Integer idVenta) {
        // No buscamos la venta aquí para evitar la doble consulta a la DB.
        // El FacturaService se encargará de validar la existencia.
        return facturaService.generarFacturaDesdeVenta(idVenta);
    }

    @Transactional(readOnly = true)
    public List<Factura> obtenerFacturasPorVenta(Integer idVenta) {
        // Delegamos la responsabilidad al servicio experto en facturas
        return facturaService.obtenerPorVenta(idVenta);
    }

    @Transactional(readOnly = true)
    public List<Venta> listarVentas() {
        return ventaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Venta obtenerVentaPorId(Integer idVenta) {
        return ventaRepository.findById(idVenta)
                .orElseThrow(() -> new RuntimeException("Error: Venta ID " + idVenta + " no encontrada."));
    }

    @Transactional
    public void eliminarVenta(Integer idVenta) {
        // Verificamos si tiene facturas antes de borrar para evitar errores de integridad FK
        List<Factura> facturasAsociadas = facturaService.obtenerPorVenta(idVenta);
        if (!facturasAsociadas.isEmpty()) {
            throw new RuntimeException("No se puede eliminar la venta: tiene facturas asociadas.");
        }
        ventaRepository.deleteById(idVenta);
    }
}