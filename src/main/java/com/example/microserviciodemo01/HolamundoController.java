package com.example.microserviciodemo01;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class HolamundoController {

    @GetMapping("/hola")
    public String decirhola(){
        return "!Hola¡ Esta es la conexión de la base de datos con Springboot";
    }
}
