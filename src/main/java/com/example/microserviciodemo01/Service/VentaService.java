package com.example.microserviciodemo01.Service;

import com.example.microserviciodemo01.Repository.FacturaRepository;
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
    private final FacturaRepository facturaRepository;

    public VentaService(
            VentaRepository ventaRepository,
            FacturaService facturaService,
            FacturaRepository facturaRepository) {

        this.ventaRepository = ventaRepository;
        this.facturaService = facturaService;
        this.facturaRepository = facturaRepository;
    }

    // Crear venta
    @Transactional
    public Venta crearVenta(Venta venta) {
        return ventaRepository.save(venta);
    }

    @Transactional
    public Factura generarFactura(Integer idVenta) {

        // Validar que la venta exista
        ventaRepository.findById(idVenta)
                .orElseThrow(() ->
                        new RuntimeException("Venta no encontrada: id=" + idVenta)
                );

        // Delegar a FacturaService usando el ID
        return facturaService.generarFacturaDesdeVenta(idVenta);
    }



    // Obtener facturas por venta
    public List<Factura> obtenerFacturasPorVenta(Integer idVenta) {
        return facturaRepository.findByVenta_IdVenta(idVenta);
    }

    // Listar ventas
    public List<Venta> listarVentas() {
        return ventaRepository.findAll();
    }

    // Obtener venta por ID
    public Venta obtenerVentaPorId(Integer idVenta) {
        return ventaRepository.findById(idVenta)
                .orElseThrow(() ->
                        new RuntimeException("Venta no encontrada: id=" + idVenta)
                );
    }

    // Eliminar venta
    @Transactional
    public void eliminarVenta(Integer idVenta) {
        ventaRepository.deleteById(idVenta);
    }
}
