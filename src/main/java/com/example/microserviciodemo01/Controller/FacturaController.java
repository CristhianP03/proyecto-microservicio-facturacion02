package com.example.microserviciodemo01.Controller;

import com.example.microserviciodemo01.models.Factura;
import com.example.microserviciodemo01.Service.FacturaService;
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
    public List<Factura> obtenerFacturas() {
        return facturaService.obtenerTodas();
    }
}
