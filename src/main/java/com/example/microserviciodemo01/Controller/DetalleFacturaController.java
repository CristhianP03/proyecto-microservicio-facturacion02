package com.example.microserviciodemo01.Controller;

import com.example.microserviciodemo01.models.DetalleFactura;
import com.example.microserviciodemo01.Service.DetalleFacturaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/detalles")
public class DetalleFacturaController {

    private final DetalleFacturaService detalleFacturaService;

    public DetalleFacturaController(DetalleFacturaService detalleFacturaService) {
        this.detalleFacturaService = detalleFacturaService;
    }

    @GetMapping
    public List<DetalleFactura> obtenerDetalles() {
        return detalleFacturaService.obtenerTodos();
    }
}
