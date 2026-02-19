package com.example.microserviciodemo01.Controller;

import com.example.microserviciodemo01.Service.FacturaService;
import com.example.microserviciodemo01.models.Factura;
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
    public Factura obtenerFacturaPorId(
            @PathVariable Integer idFactura) {

        return facturaService.obtenerPorId(idFactura);
    }

    @GetMapping
    public List<Factura> obtenerTodas() {
        return facturaService.obtenerTodas();
    }
}
