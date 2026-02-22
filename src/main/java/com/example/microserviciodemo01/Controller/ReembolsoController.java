package com.example.microserviciodemo01.Controller;

import com.example.microserviciodemo01.models.Reembolso;
import com.example.microserviciodemo01.Service.ReembolsoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/facturas/{idFactura}/reembolsos")
public class ReembolsoController {

    private final ReembolsoService reembolsoService;

    public ReembolsoController(ReembolsoService reembolsoService) {
        this.reembolsoService = reembolsoService;
    }

    /**
     * POST /facturas/{idFactura}/reembolsos
     * Registra un nuevo reembolso para un pago asociado a la factura.
     */
    @PostMapping
    public ResponseEntity<Reembolso> registrarReembolso(
            @PathVariable Integer idFactura,
            @RequestBody Reembolso reembolso) {

        // Análisis Estricto: El service procesa la lógica de vinculación por Pago ID
        Reembolso nuevo = reembolsoService.registrarReembolso(reembolso);
        return new ResponseEntity<>(nuevo, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Reembolso>> obtenerPorFactura(@PathVariable Integer idFactura) {
        return ResponseEntity.ok(reembolsoService.obtenerPorFactura(idFactura));
    }
}