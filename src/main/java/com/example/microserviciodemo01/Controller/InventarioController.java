package com.example.microserviciodemo01.Controller;

import com.example.microserviciodemo01.Service.VentaService;
import com.example.microserviciodemo01.models.Venta;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/inventario")
public class InventarioController {

    private final VentaService ventaService;

    public InventarioController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    // Solo ventas REALES. Si es modo prueba devuelve 400.
    @GetMapping("/ventas/{idVenta}/productos-vendidos")
    public ResponseEntity<?> getProductosVendidos(@PathVariable Integer idVenta) {

        Venta venta = ventaService.obtenerPorId(idVenta);

        if (Boolean.TRUE.equals(venta.getEsModoTest())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error",
                            "Venta de modo prueba. No afecta inventario."));
        }

        List<Map<String, Object>> resultado = venta.getDetalles().stream()
                .map(d -> Map.<String, Object>of(
                        "idProducto",      d.getIdProducto(),
                        "nombreProducto",  d.getNombreProducto(),
                        "cantidadVendida", d.getCantidad(),
                        "precioUnitario",  d.getPrecioUnitario(),
                        "totalLinea",      d.getTotal()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(resultado);
    }

    // Solo ventas reales, jamás de modo prueba
    @GetMapping("/ventas-con-productos")
    public ResponseEntity<List<Map<String, Object>>> getTodasVentasReales() {

        List<Map<String, Object>> resultado = ventaService.listarVentas().stream()
                .filter(v -> !Boolean.TRUE.equals(v.getEsModoTest()))
                .map(v -> Map.<String, Object>of(
                        "idVenta",         v.getIdVenta(),
                        "numeroRegistro",  v.getNumeroRegistro(),
                        "fechaVenta",      v.getFechaVenta().toString(),
                        "estado",          v.getEstado(),
                        "productos",       v.getDetalles().stream()
                                .map(d -> Map.<String, Object>of(
                                        "idProducto",      d.getIdProducto(),
                                        "cantidadVendida", d.getCantidad()
                                ))
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(resultado);
    }
}