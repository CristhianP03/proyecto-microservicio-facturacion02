package com.example.microserviciodemo01.Service;

import com.example.microserviciodemo01.Repository.CajeroRepository;
import com.example.microserviciodemo01.Repository.FacturaRepository;
import com.example.microserviciodemo01.Repository.VentaRepository;
import com.example.microserviciodemo01.models.Cajero;
import com.example.microserviciodemo01.models.DetalleFactura;
import com.example.microserviciodemo01.models.DetalleVenta;
import com.example.microserviciodemo01.models.Factura;
import com.example.microserviciodemo01.models.Venta;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class FacturaService {

    private final FacturaRepository facturaRepository;
    private final VentaRepository ventaRepository;
    private final CajeroRepository cajeroRepository;

    public FacturaService(FacturaRepository facturaRepository,
                          VentaRepository ventaRepository,
                          CajeroRepository cajeroRepository) {
        this.facturaRepository = facturaRepository;
        this.ventaRepository = ventaRepository;
        this.cajeroRepository = cajeroRepository;
    }

    @Transactional
    public Factura generarFacturaDesdeVenta(Integer idVenta) {

        // 1. Buscar la venta
        Venta venta = ventaRepository.findById(idVenta)
                .orElseThrow(() -> new RuntimeException(
                        "Venta no encontrada: " + idVenta));

        // 2. Verificar que no tenga factura ya generada
        if (facturaRepository.findByVenta_IdVenta(idVenta).isPresent()) {
            throw new RuntimeException(
                    "Ya existe una factura para la venta: " + idVenta);
        }

        // 3. Buscar el cajero para obtener su número de caja
        Cajero cajero = cajeroRepository.findById(venta.getIdCajero())
                .orElseThrow(() -> new RuntimeException(
                        "Cajero no encontrado: " + venta.getIdCajero()));

        // 4. Generar número de factura.
        // CORRECCIÓN CRÍTICA: se pasa esModoTest para que la secuencia
        // de Cajero Prueba y Caja 1 real sean completamente independientes.
        // Ambos tienen numeroCaja = 1 (prefijo 001-001-), pero el contador
        // se calcula sobre facturas del mismo modo solamente.
        boolean esTest = Boolean.TRUE.equals(venta.getEsModoTest());
        String numeroFactura = generarNumeroFactura(cajero.getNumeroCaja(), esTest);

        // 5. Construir la factura
        Factura factura = new Factura();
        factura.setVenta(venta);
        factura.setNumeroFactura(numeroFactura);
        factura.setCedulaCliente(venta.getCedulaCliente());
        factura.setNombreCliente(venta.getNombreCliente());
        factura.setDireccionCliente(venta.getDireccionCliente());
        factura.setTelefonoCliente(venta.getTelefonoCliente());
        factura.setFechaEmision(LocalDateTime.now());
        factura.setEstado("EMITIDA");
        factura.setSubtotal(venta.getSubtotal());
        factura.setImpuestos(venta.getImpuestos());
        factura.setTotal(venta.getTotal());
        factura.setFormaPago(venta.getFormaPago());
        factura.setValorPagado(venta.getValorPagado());

        // 6. Observación según forma de pago
        String observacion = venta.getFormaPago().equalsIgnoreCase("EFECTIVO")
                ? "SIN UTILIZACION DEL SISTEMA FINANCIERO"
                : "CON UTILIZACION DEL SISTEMA FINANCIERO";
        factura.setObservacion(observacion);

        // 7. Copiar los detalles de la venta a la factura
        List<DetalleFactura> detalles = new ArrayList<>();
        for (DetalleVenta dv : venta.getDetalles()) {
            DetalleFactura df = new DetalleFactura();
            df.setFactura(factura);
            df.setIdProducto(dv.getIdProducto());
            df.setNombreProducto(dv.getNombreProducto());
            df.setPrecioUnitario(dv.getPrecioUnitario());
            df.setCantidad(dv.getCantidad());
            df.setTotal(dv.getTotal());
            detalles.add(df);
        }
        factura.setDetalles(detalles);

        return facturaRepository.save(factura);
    }

    /**
     * Genera el número de factura para una caja específica,
     * buscando el último número SOLO dentro del mismo modo (real o prueba).
     *
     * Ejemplo:
     *   Caja 1 real, 3 facturas reales    → siguiente real:  001-001-000000004
     *   Cajero Prueba, 1 factura prueba   → siguiente prueba: 001-001-000000002
     *   Los contadores nunca se mezclan.
     *
     * @param numeroCaja  número de caja del cajero que emite la factura
     * @param esModoTest  true si la venta es de modo prueba
     */
    private String generarNumeroFactura(Integer numeroCaja, boolean esModoTest) {
        String prefijoCaja = String.format("001-%03d-", numeroCaja);

        // CORRECCIÓN: usa la query que filtra por esModoTest además del prefijo
        List<Factura> ultimas = facturaRepository
                .findUltimoNumeroFacturaPorCajaYModo(prefijoCaja, esModoTest);

        int siguiente = 1;
        if (!ultimas.isEmpty()) {
            String ultimo = ultimas.get(0).getNumeroFactura();
            try {
                String secuencia = ultimo.substring(ultimo.lastIndexOf("-") + 1);
                siguiente = Integer.parseInt(secuencia) + 1;
            } catch (Exception e) {
                siguiente = 1;
            }
        }

        return prefijoCaja + String.format("%09d", siguiente);
    }

    @Transactional(readOnly = true)
    public List<Factura> obtenerTodas() {
        return facturaRepository.findAllByOrderByIdFacturaDesc();
    }

    @Transactional(readOnly = true)
    public Factura obtenerPorId(Integer idFactura) {
        return facturaRepository.findById(idFactura)
                .orElseThrow(() -> new RuntimeException(
                        "Factura no encontrada: " + idFactura));
    }

    @Transactional(readOnly = true)
    public Factura obtenerPorVenta(Integer idVenta) {
        return facturaRepository.findByVenta_IdVenta(idVenta)
                .orElseThrow(() -> new RuntimeException(
                        "Factura no encontrada para venta: " + idVenta));
    }
}