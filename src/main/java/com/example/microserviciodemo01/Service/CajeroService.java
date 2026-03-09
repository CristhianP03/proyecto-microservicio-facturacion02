package com.example.microserviciodemo01.Service;

import com.example.microserviciodemo01.Repository.CajeroRepository;
import com.example.microserviciodemo01.models.Cajero;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CajeroService {

    private final CajeroRepository cajeroRepository;

    public CajeroService(CajeroRepository cajeroRepository) {
        this.cajeroRepository = cajeroRepository;
    }

    @Transactional(readOnly = true)
    public Optional<Cajero> login(String username, String password) {
        Optional<Cajero> cajero = cajeroRepository.findByUsernameAndActivoTrue(username);
        if (cajero.isPresent() && cajero.get().getPassword().equals(password)) {
            return cajero;
        }
        return Optional.empty();
    }

    @Transactional
    public void inicializarCajeros() {
        // CORRECCIÓN: verifica cada usuario individualmente.
        // Con count() == 0 nunca se creaba cajero_prueba si ya existían cajero1 y cajero2.

        if (!cajeroRepository.existsByUsername("cajero1")) {
            Cajero c1 = new Cajero();
            c1.setUsername("cajero1");
            c1.setPassword("1234");
            c1.setNombreCompleto("Juan Pérez");
            c1.setNumeroCaja(1);
            c1.setActivo(true);
            cajeroRepository.save(c1);
        }

        if (!cajeroRepository.existsByUsername("cajero2")) {
            Cajero c2 = new Cajero();
            c2.setUsername("cajero2");
            c2.setPassword("1234");
            c2.setNombreCompleto("María González");
            c2.setNumeroCaja(2);
            c2.setActivo(true);
            cajeroRepository.save(c2);
        }

        // Cajero exclusivo del modo prueba.
        // numeroCaja = 1 igual que cajero1, pero la secuencia de facturas
        // se aísla por el campo esModoTest en FacturaRepository.
        if (!cajeroRepository.existsByUsername("cajero_prueba")) {
            Cajero cp = new Cajero();
            cp.setUsername("cajero_prueba");
            cp.setPassword("1234");
            cp.setNombreCompleto("Cajero Prueba");
            cp.setNumeroCaja(1);
            cp.setActivo(true);
            cajeroRepository.save(cp);
        }
    }
}