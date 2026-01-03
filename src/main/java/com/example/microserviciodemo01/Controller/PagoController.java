package com.example.microserviciodemo01.Controller;

import com.example.microserviciodemo01.models.Pago;
import com.example.microserviciodemo01.Service.PagoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pagos")
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @GetMapping
    public List<Pago> obtenerPagos() {
        return pagoService.obtenerTodos();
    }
}
