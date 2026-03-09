package com.example.microserviciodemo01.Controller;

import com.example.microserviciodemo01.Service.CajeroService;
import com.example.microserviciodemo01.models.Cajero;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/cajeros")
public class CajeroController {

    private final CajeroService cajeroService;

    public CajeroController(CajeroService cajeroService) {
        this.cajeroService = cajeroService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {
        String username = credenciales.get("username");
        String password = credenciales.get("password");

        if (username == null || username.isBlank() ||
                password == null || password.isBlank()) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Usuario y contraseña son requeridos."));
        }

        Optional<Cajero> cajero = cajeroService.login(username, password);

        if (cajero.isPresent()) {
            return ResponseEntity.ok(cajero.get());
        }

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Usuario o contraseña incorrectos."));
    }
}