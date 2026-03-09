package com.example.microserviciodemo01;

import com.example.microserviciodemo01.Service.CajeroService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class MicroservicioDemo01Application {

    public static void main(String[] args) {
        SpringApplication.run(MicroservicioDemo01Application.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public CommandLineRunner inicializar(CajeroService cajeroService) {
        return args -> cajeroService.inicializarCajeros();
    }
}