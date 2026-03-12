package com.example.microserviciodemo01.Service;

import com.example.microserviciodemo01.Repository.DetalleVentaRepository;
import com.example.microserviciodemo01.Repository.FacturaRepository;
import com.example.microserviciodemo01.Repository.VentaRepository;
import com.example.microserviciodemo01.models.DetalleVenta;
import com.example.microserviciodemo01.models.Factura;
import com.example.microserviciodemo01.models.Venta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VentaService {

    private static final Logger log = LoggerFactory.getLogger(VentaService.class);

    private final VentaRepository         ventaRepository;
    private final DetalleVentaRepository  detalleVentaRepository;
    private final FacturaRepository       facturaRepository;
    private final RestTemplate            restTemplate;

    private static final BigDecimal TASA_IVA = new BigDecimal("0.15");

    @Value("${microservicio.productos.reducir-stock.url:http://20.221.105.223}")
    private String productosReducirStockBaseUrl;

    public VentaService(VentaRepository ventaRepository,
                        DetalleVentaRepository detalleVentaRepository,
                        FacturaRepository facturaRepository,
                        RestTemplate restTemplate) {
        this.ventaRepository          = ventaRepository;
        this.detalleVentaRepository   = detalleVentaRepository;
        this.facturaRepository        = facturaRepository;
        this.restTemplate             = restTemplate;
    }

    // ----------------------------------------------------------------
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

        // Contador independiente por modo:
        // - ventas reales  -> su propia secuencia
        // - ventas prueba  -> su propia secuencia (reinicia al borrarlas)
        boolean esPrueba = Boolean.TRUE.equals(venta.getEsModoTest());
        venta.setNumeroRegistro(generarNumeroRegistro(esPrueba));

        for (DetalleVenta detalle : venta.getDetalles()) {
            detalle.setVenta(venta);
            BigDecimal totalDetalle = detalle.getPrecioUnitario()
                    .multiply(new BigDecimal(detalle.getCantidad()))
                    .setScale(2, RoundingMode.HALF_UP);
            detalle.setTotal(totalDetalle);
        }

        Venta ventaGuardada = ventaRepository.save(venta);

        // Reducir stock solo para ventas reales.
        // esModoTest = true nunca afecta el stock.
        if (!esPrueba) {
            reducirStockProductos(ventaGuardada);
        }

        return ventaGuardada;
    }

    // ----------------------------------------------------------------
    /**
     * Genera numeroRegistro con secuencia separada por modo.
     * Al borrar las ventas de prueba el MAX queda null
     * y la proxima venta de prueba empieza en 000000001.
     */
    private String generarNumeroRegistro(boolean esModoTest) {
        String ultimo = ventaRepository
                .findUltimoNumeroRegistroPorModo(esModoTest)
                .orElse(null);
        int siguiente = 1;
        if (ultimo != null) {
            try { siguiente = Integer.parseInt(ultimo) + 1; }
            catch (NumberFormatException e) { siguiente = 1; }
        }
        return String.format("%09d", siguiente);
    }

    // ----------------------------------------------------------------
    /**
     * PUT /api/productos/reducir-stock
     * Body: [{ "idProducto": int, "cantidad": int, "precioVenta": double }]
     * Metodo PUT confirmado por Postman y Swagger del equipo de productos.
     *
     * Si falla (timeout, servicio caido) la venta NO se revierte.
     * El error queda en el log para revision manual.
     */
    private void reducirStockProductos(Venta venta) {
        try {
            List<Map<String, Object>> payload = venta.getDetalles().stream()
                    .map(d -> Map.<String, Object>of(
                            "idProducto",  d.getIdProducto(),
                            "cantidad",    d.getCantidad(),
                            "precioVenta", d.getPrecioUnitario().doubleValue()
                    ))
                    .collect(Collectors.toList());

            String url = productosReducirStockBaseUrl + "/api/productos/reducir-stock";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<List<Map<String, Object>>> request = new HttpEntity<>(payload, headers);

            restTemplate.exchange(url, HttpMethod.PUT, request, Void.class);
            log.info("Stock reducido correctamente para venta #{}", venta.getNumeroRegistro());

        } catch (Exception e) {
            log.error("ERROR al reducir stock para venta #{}: {}",
                    venta.getNumeroRegistro(), e.getMessage());
        }
    }

    // ----------------------------------------------------------------
    @Transactional(readOnly = true)
    public List<Venta> listarVentas() {
        return ventaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Venta obtenerPorId(Integer idVenta) {
        return ventaRepository.findById(idVenta)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada: " + idVenta));
    }

    // ----------------------------------------------------------------
    /**
     * Borra todas las ventas marcadas como esModoTest = true,
     * junto con sus facturas y detalles.
     * Despues del borrado el MAX de numeroRegistro para prueba es null,
     * por lo que la proxima venta de prueba reinicia en 000000001.
     */
    @Transactional
    public void eliminarVentasPrueba() {
        List<Venta> ventasPrueba = ventaRepository.findAll().stream()
                .filter(v -> Boolean.TRUE.equals(v.getEsModoTest()))
                .collect(Collectors.toList());

        for (Venta v : ventasPrueba) {
            Optional<Factura> factura =
                    facturaRepository.findByVenta_IdVenta(v.getIdVenta());
            factura.ifPresent(facturaRepository::delete);

            List<DetalleVenta> detalles =
                    detalleVentaRepository.findByVenta_IdVenta(v.getIdVenta());
            detalleVentaRepository.deleteAll(detalles);
        }

        ventaRepository.deleteAll(ventasPrueba);
    }
}