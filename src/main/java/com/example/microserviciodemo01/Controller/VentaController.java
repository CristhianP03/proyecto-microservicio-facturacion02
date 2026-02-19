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

    // Crear venta
    @PostMapping
    public ResponseEntity<Venta> crearVenta(@RequestBody Venta venta) {
        Venta nueva = ventaService.crearVenta(venta);
        return new ResponseEntity<>(nueva, HttpStatus.CREATED);
    }

    // Generar factura desde venta
    @PostMapping("/{idVenta}/generar-factura")
    public ResponseEntity<Factura> generarFactura(
            @PathVariable Integer idVenta) {

        Factura factura = ventaService.generarFactura(idVenta);
        return new ResponseEntity<>(factura, HttpStatus.CREATED);
    }

    // Obtener facturas de una venta
    @GetMapping("/{idVenta}/facturas")
    public List<Factura> obtenerFacturasPorVenta(
            @PathVariable Integer idVenta) {

        return ventaService.obtenerFacturasPorVenta(idVenta);
    }

    // Listar ventas
    @GetMapping
    public List<Venta> listarVentas() {
        return ventaService.listarVentas();
    }

    // Obtener venta por ID
    @GetMapping("/{idVenta}")
    public Venta obtenerVentaPorId(@PathVariable Integer idVenta) {
        return ventaService.obtenerVentaPorId(idVenta);
    }

    // Eliminar venta
    @DeleteMapping("/{idVenta}")
    public ResponseEntity<Void> eliminarVenta(@PathVariable Integer idVenta) {
        ventaService.eliminarVenta(idVenta);
        return ResponseEntity.noContent().build();
    }
}
