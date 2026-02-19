package com.example.microserviciodemo01.Repository;

import com.example.microserviciodemo01.models.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacturaRepository
        extends JpaRepository<Factura, Integer> {

    List<Factura> findByVenta_IdVenta(Integer idVenta);
}
