package com.example.microserviciodemo01.Controller;

import com.example.microserviciodemo01.Service.DetalleFacturaService;
import com.example.microserviciodemo01.models.DetalleFactura;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/facturas/{idFactura}/detalles")
public class DetalleFacturaController {

    private final DetalleFacturaService detalleFacturaService;

    public DetalleFacturaController(DetalleFacturaService detalleFacturaService) {
        this.detalleFacturaService = detalleFacturaService;
    }

    @GetMapping
    public ResponseEntity<List<DetalleFactura>> obtenerPorFactura(@PathVariable Integer idFactura) {
        List<DetalleFactura> detalles = detalleFacturaService.obtenerPorFactura(idFactura);
        return ResponseEntity.ok(detalles);
    }

    @PostMapping
    public ResponseEntity<DetalleFactura> crearDetalle(
            @PathVariable Integer idFactura,
            @RequestBody DetalleFactura detalle) {

        // El Service debe encargarse de que el detalle se vincule al ID de la factura del path.
        DetalleFactura nuevo = detalleFacturaService.crearDetalle(idFactura, detalle);
        return new ResponseEntity<>(nuevo, HttpStatus.CREATED);
    }

    @DeleteMapping("/{idDetalle}")
    public ResponseEntity<Void> eliminarDetalle(
            @PathVariable Integer idFactura,
            @PathVariable Integer idDetalle) {

        // Análisis Estricto: Se pasan ambos IDs para validar que el detalle realmente pertenezca a esa factura.
        detalleFacturaService.eliminarDetalle(idFactura, idDetalle);
        return ResponseEntity.noContent().build();
    }
}