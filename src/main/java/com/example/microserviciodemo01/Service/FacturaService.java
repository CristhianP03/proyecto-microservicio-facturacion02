package com.example.microserviciodemo01.Service;

import com.example.microserviciodemo01.Repository.FacturaRepository;
import com.example.microserviciodemo01.Repository.VentaRepository;
import com.example.microserviciodemo01.models.Factura;
import com.example.microserviciodemo01.models.Venta;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class FacturaService {

    private final FacturaRepository facturaRepository;
    private final VentaRepository ventaRepository;

    public FacturaService(FacturaRepository facturaRepository,
                          VentaRepository ventaRepository) {
        this.facturaRepository = facturaRepository;
        this.ventaRepository = ventaRepository;
    }

    @Transactional
    public Factura generarFacturaDesdeVenta(Integer idVenta) {

        Venta venta = ventaRepository.findById(idVenta)
                .orElseThrow(() ->
                        new RuntimeException("Venta no encontrada: id=" + idVenta)
                );

        Factura factura = new Factura();
        factura.setVenta(venta);

        factura.setIdPedido(0);
        factura.setNumeroFactura("FAC-" + UUID.randomUUID());

        factura.setIdCliente(venta.getIdCliente());
        factura.setNombreCliente("CLIENTE SIMULADO");
        factura.setEmailCliente("cliente@email.com");

        factura.setFechaEmision(LocalDateTime.now());
        factura.setEstado("EMITIDA");

        factura.setSubtotal(venta.getTotal());
        factura.setImpuestos(BigDecimal.ZERO);
        factura.setTotal(venta.getTotal());
        factura.setMoneda("USD");

        return facturaRepository.save(factura);
    }


    // 2️⃣ Obtener todas las facturas
    public List<Factura> obtenerTodas() {
        return facturaRepository.findAll();
    }

    // 3️⃣ Obtener factura por ID
    public Factura obtenerPorId(Integer idFactura) {
        return facturaRepository.findById(idFactura)
                .orElseThrow(() ->
                        new RuntimeException("Factura no encontrada: id=" + idFactura)
                );
    }

    // 4️⃣ Obtener facturas por venta
    public List<Factura> obtenerPorVenta(Integer idVenta) {
        return facturaRepository.findByVenta_IdVenta(idVenta);
    }
}
