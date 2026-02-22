package com.example.microserviciodemo01.Controller;

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

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @PostMapping
    public ResponseEntity<Venta> crearVenta(@RequestBody Venta venta) {
        // Análisis Estricto: El service debe validar que el objeto no sea nulo.
        Venta nueva = ventaService.crearVenta(venta);
        return new ResponseEntity<>(nueva, HttpStatus.CREATED);
    }

    @PostMapping("/{idVenta}/generar-factura")
    public ResponseEntity<Factura> generarFactura(@PathVariable Integer idVenta) {
        // Este endpoint es la conexión manual entre el proceso de venta y facturación.
        Factura factura = ventaService.generarFactura(idVenta);
        return new ResponseEntity<>(factura, HttpStatus.CREATED);
    }

    @GetMapping("/{idVenta}/facturas")
    public ResponseEntity<List<Factura>> obtenerFacturasPorVenta(@PathVariable Integer idVenta) {
        return ResponseEntity.ok(ventaService.obtenerFacturasPorVenta(idVenta));
    }

    @GetMapping
    public ResponseEntity<List<Venta>> listarVentas() {
        return ResponseEntity.ok(ventaService.listarVentas());
    }

    @GetMapping("/{idVenta}")
    public ResponseEntity<Venta> obtenerVentaPorId(@PathVariable Integer idVenta) {
        return ResponseEntity.ok(ventaService.obtenerVentaPorId(idVenta));
    }

    @DeleteMapping("/{idVenta}")
    public ResponseEntity<Void> eliminarVenta(@PathVariable Integer idVenta) {
        ventaService.eliminarVenta(idVenta);
        return ResponseEntity.noContent().build();
    }
}