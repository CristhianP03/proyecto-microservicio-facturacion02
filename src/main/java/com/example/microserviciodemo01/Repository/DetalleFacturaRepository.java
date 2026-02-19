package com.example.microserviciodemo01.Repository;

import com.example.microserviciodemo01.models.DetalleFactura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleFacturaRepository
        extends JpaRepository<DetalleFactura, Integer> {

    List<DetalleFactura> findByFactura_IdFactura(Integer idFactura);
}
