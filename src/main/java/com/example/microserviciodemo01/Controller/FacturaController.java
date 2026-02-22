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

    @GetMapping("/{idFactura}")
    public ResponseEntity<Factura> obtenerFacturaPorId(@PathVariable Integer idFactura) {
        // Análisis Estricto: El service lanza excepción si no existe, aquí retornamos 200 OK
        return ResponseEntity.ok(facturaService.obtenerPorId(idFactura));
    }

    @GetMapping
    public ResponseEntity<List<Factura>> obtenerTodas() {
        return ResponseEntity.ok(facturaService.obtenerTodas());
    }

    // Nota: El método de generar factura se encuentra en VentaController para mantener el flujo de origen.
}