package com.example.microserviciodemo01.Controller;

import com.example.microserviciodemo01.Service.VentaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {

    private final VentaService ventaService;

    public TestController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    // Borra todas las ventas del modo prueba
    // Solo accesible desde el frontend en modo prueba
    @DeleteMapping("/limpiar-ventas")
    public ResponseEntity<?> limpiarVentasPrueba() {
        ventaService.eliminarVentasPrueba();
        return ResponseEntity.ok(
                Map.of("mensaje", "Ventas de modo prueba eliminadas correctamente."));
    }
}