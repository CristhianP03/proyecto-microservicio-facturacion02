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

    // Obtener todos los detalles de una factura
    @GetMapping
    public List<DetalleFactura> obtenerPorFactura(
            @PathVariable Integer idFactura) {

        return detalleFacturaService.obtenerPorFactura(idFactura);
    }

    // Crear detalle
    @PostMapping
    public ResponseEntity<DetalleFactura> crearDetalle(
            @PathVariable Integer idFactura,
            @RequestBody DetalleFactura detalle) {

        DetalleFactura nuevo =
                detalleFacturaService.crearDetalle(idFactura, detalle);

        return new ResponseEntity<>(nuevo, HttpStatus.CREATED);
    }

    // Eliminar detalle
    @DeleteMapping("/{idDetalle}")
    public ResponseEntity<Void> eliminarDetalle(
            @PathVariable Integer idFactura,
            @PathVariable Integer idDetalle) {

        detalleFacturaService.eliminarDetalle(idFactura, idDetalle);
        return ResponseEntity.noContent().build();
    }
}
