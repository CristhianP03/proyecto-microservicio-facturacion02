package com.example.microserviciodemo01.Controller;

import com.example.microserviciodemo01.models.Pago;
import com.example.microserviciodemo01.Service.PagoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/facturas/{idFactura}/pagos")
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    // Este es el método que "crea" el pago en la base de datos
    @PostMapping
    public ResponseEntity<Pago> registrarPago(
            @PathVariable Integer idFactura,
            @RequestBody Pago pago) {

        // Llamamos al service pasándole el ID de la factura y los datos del pago (monto, método, etc.)
        Pago nuevoPago = pagoService.registrarPago(idFactura, pago);
        return new ResponseEntity<>(nuevoPago, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Pago>> obtenerPorFactura(@PathVariable Integer idFactura) {
        return ResponseEntity.ok(pagoService.obtenerPorFactura(idFactura));
    }
}