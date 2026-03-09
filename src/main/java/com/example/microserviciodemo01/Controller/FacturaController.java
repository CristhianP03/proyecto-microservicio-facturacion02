package com.example.microserviciodemo01.Controller;

import com.example.microserviciodemo01.Service.FacturaService;
import com.example.microserviciodemo01.models.Factura;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/facturas")
public class FacturaController {

    private final FacturaService facturaService;

    public FacturaController(FacturaService facturaService) {
        this.facturaService = facturaService;
    }

    @GetMapping
    public ResponseEntity<List<Factura>> obtenerTodas() {
        return ResponseEntity.ok(facturaService.obtenerTodas());
    }

    @GetMapping("/{idFactura}")
    public ResponseEntity<Factura> obtenerPorId(
            @PathVariable Integer idFactura) {
        return ResponseEntity.ok(facturaService.obtenerPorId(idFactura));
    }
}