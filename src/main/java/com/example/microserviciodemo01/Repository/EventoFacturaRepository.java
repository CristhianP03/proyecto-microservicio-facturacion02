package com.example.microserviciodemo01.Repository;

import com.example.microserviciodemo01.models.EventoFactura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventoFacturaRepository extends JpaRepository<EventoFactura, Integer> {
}

