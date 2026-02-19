package com.example.microserviciodemo01.Repository;

import com.example.microserviciodemo01.models.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VentaRepository
        extends JpaRepository<Venta, Integer> {
}
