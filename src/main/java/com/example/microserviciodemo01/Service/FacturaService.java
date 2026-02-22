package com.example.microserviciodemo01.Service;

import com.example.microserviciodemo01.Repository.FacturaRepository;
import com.example.microserviciodemo01.Repository.VentaRepository;
import com.example.microserviciodemo01.models.Factura;
import com.example.microserviciodemo01.models.Venta;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class FacturaService {

    private final FacturaRepository facturaRepository;
    private final VentaRepository ventaRepository;

    // Constante para impuestos (Ejemplo: 15% de IVA)
    private static final BigDecimal TASA_IMPUESTO = new BigDecimal("0.15");

    public FacturaService(FacturaRepository facturaRepository,
                          VentaRepository ventaRepository) {
        this.facturaRepository = facturaRepository;
        this.ventaRepository = ventaRepository;
    }

    /**
     * Genera una factura vinculada a una venta real.
     * Realiza cálculos financieros estrictos y hereda datos de la venta.
     */
    @Transactional
    public Factura generarFacturaDesdeVenta(Integer idVenta) {
        // 1. Validación de existencia
        Venta venta = ventaRepository.findById(idVenta)
                .orElseThrow(() -> new RuntimeException("Error Crítico: Venta no encontrada con ID: " + idVenta));

        // 2. Orquestación del objeto Factura
        Factura factura = new Factura();
        factura.setVenta(venta);

        // El idPedido se hereda de la lógica de negocio (asumiendo 0 si es venta directa)
        factura.setIdPedido(0);

        // Generación de número correlativo legible: FAC-YYYYMMDD-ID
        factura.setNumeroFactura(generarNumeroFactura(idVenta));

        // Enlace de datos del cliente (Heredados de la venta para evitar inconsistencia)
        factura.setIdCliente(venta.getIdCliente());
        factura.setNombreCliente("Cliente Ref: " + venta.getIdCliente());
        factura.setEmailCliente("cliente_" + venta.getIdCliente() + "@facturacion.com");
        factura.setIdentificacionFiscalCliente("ID-FISCAL-" + venta.getIdCliente());

        factura.setFechaEmision(LocalDateTime.now());
        factura.setFechaVencimiento(LocalDateTime.now().plusDays(30)); // Plazo estándar
        factura.setEstado("PENDIENTE_PAGO"); // Estado inicial para flujo de Pagos
        factura.setMoneda("USD");

        // 3. Cálculos Financieros Estrictos
        calcularMontos(factura, venta.getTotal());

        return facturaRepository.save(factura);
    }

    private void calcularMontos(Factura factura, BigDecimal totalVenta) {
        // En un escenario estricto: Subtotal = TotalVenta / (1 + Tasa)
        BigDecimal divisor = BigDecimal.ONE.add(TASA_IMPUESTO);
        BigDecimal subtotal = totalVenta.divide(divisor, 2, RoundingMode.HALF_UP);
        BigDecimal impuestos = totalVenta.subtract(subtotal);

        factura.setSubtotal(subtotal);
        factura.setImpuestos(impuestos);
        factura.setTotal(totalVenta); // El total debe coincidir exactamente con la Venta
    }

    private String generarNumeroFactura(Integer idVenta) {
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "FAC-" + fecha + "-" + idVenta;
    }

    // --- Consultas ---

    @Transactional(readOnly = true)
    public List<Factura> obtenerTodas() {
        return facturaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Factura obtenerPorId(Integer idFactura) {
        return facturaRepository.findById(idFactura)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada: ID " + idFactura));
    }

    @Transactional(readOnly = true)
    public List<Factura> obtenerPorVenta(Integer idVenta) {
        return facturaRepository.findByVenta_IdVenta(idVenta);
    }
}