package com.example.microserviciodemo01.Controller;

import com.example.microserviciodemo01.models.Reembolso;
import com.example.microserviciodemo01.Service.ReembolsoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reembolsos")
public class ReembolsoController {

    private final ReembolsoService reembolsoService;

    public ReembolsoController(ReembolsoService reembolsoService) {
        this.reembolsoService = reembolsoService;
    }

    @GetMapping
    public List<Reembolso> obtenerReembolsos() {
        return reembolsoService.obtenerTodos();
    }
}
