package com.example.microserviciodemo01.Controller;

import com.example.microserviciodemo01.Service.DetalleFacturaService;
import com.example.microserviciodemo01.models.DetalleFactura;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/facturas/{idFactura}/detalles")
public class DetalleFacturaController {

    private final DetalleFacturaService detalleFacturaService;

    public DetalleFacturaController(
            DetalleFacturaService detalleFacturaService) {
        this.detalleFacturaService = detalleFacturaService;
    }

    @GetMapping
    public ResponseEntity<List<DetalleFactura>> obtenerPorFactura(
            @PathVariable Integer idFactura) {
        return ResponseEntity.ok(
                detalleFacturaService.obtenerPorFactura(idFactura));
    }
}