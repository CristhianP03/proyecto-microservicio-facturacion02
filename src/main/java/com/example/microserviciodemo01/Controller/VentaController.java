package com.example.microserviciodemo01.Controller;

import com.example.microserviciodemo01.Service.FacturaService;
import com.example.microserviciodemo01.Service.VentaService;
import com.example.microserviciodemo01.models.Factura;
import com.example.microserviciodemo01.models.Venta;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ventas")
public class VentaController {

    private final VentaService ventaService;
    private final FacturaService facturaService;

    public VentaController(VentaService ventaService,
                           FacturaService facturaService) {
        this.ventaService = ventaService;
        this.facturaService = facturaService;
    }

    // Crea la venta con todos sus detalles de una sola vez
    @PostMapping
    public ResponseEntity<Venta> crearVenta(@RequestBody Venta venta) {
        Venta nueva = ventaService.crearVenta(venta);
        return new ResponseEntity<>(nueva, HttpStatus.CREATED);
    }

    // Genera la factura a partir de una venta ya creada
    @PostMapping("/{idVenta}/generar-factura")
    public ResponseEntity<Factura> generarFactura(
            @PathVariable Integer idVenta) {
        Factura factura = facturaService.generarFacturaDesdeVenta(idVenta);
        return new ResponseEntity<>(factura, HttpStatus.CREATED);
    }

    // Obtiene la factura de una venta específica
    @GetMapping("/{idVenta}/factura")
    public ResponseEntity<Factura> obtenerFacturaPorVenta(
            @PathVariable Integer idVenta) {
        Factura factura = facturaService.obtenerPorVenta(idVenta);
        return ResponseEntity.ok(factura);
    }

    @GetMapping
    public ResponseEntity<List<Venta>> listarVentas() {
        return ResponseEntity.ok(ventaService.listarVentas());
    }

    @GetMapping("/{idVenta}")
    public ResponseEntity<Venta> obtenerVentaPorId(
            @PathVariable Integer idVenta) {
        return ResponseEntity.ok(ventaService.obtenerPorId(idVenta));
    }
}