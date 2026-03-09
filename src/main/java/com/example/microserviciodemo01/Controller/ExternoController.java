package com.example.microserviciodemo01.Controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/externos")
public class ExternoController {

    private final RestTemplate restTemplate;

    @Value("${microservicio.clientes.url:}")
    private String clientesUrl;

    @Value("${microservicio.productos.url:}")
    private String productosUrl;

    public ExternoController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Frontend llama: GET /externos/clientes
    // Backend llama:  GET http://4.206.202.17:8081/api/clientes
    @GetMapping("/clientes")
    public ResponseEntity<?> getClientes() {
        if (clientesUrl == null || clientesUrl.isBlank()) {
            return ResponseEntity.badRequest()
                    .body("URL del microservicio de clientes no configurada.");
        }
        try {
            // CORRECCIÓN: la ruta real es /api/clientes, no /clientes
            Object resultado = restTemplate.getForObject(
                    clientesUrl + "/api/clientes", Object.class);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(503)
                    .body("No se pudo conectar al microservicio de clientes: "
                            + e.getMessage());
        }
    }

    // Frontend llama: GET /externos/productos
    // Backend llama:  GET http://40.82.168.11:8090/api/productos
    @GetMapping("/productos")
    public ResponseEntity<?> getProductos() {
        if (productosUrl == null || productosUrl.isBlank()) {
            return ResponseEntity.badRequest()
                    .body("URL del microservicio de productos no configurada.");
        }
        try {
            // /api/productos ya es correcto ✅
            Object resultado = restTemplate.getForObject(
                    productosUrl + "/api/productos", Object.class);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(503)
                    .body("No se pudo conectar al microservicio de productos: "
                            + e.getMessage());
        }
    }
}