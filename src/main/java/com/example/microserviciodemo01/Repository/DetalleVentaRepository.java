package com.example.microserviciodemo01.Repository;

import com.example.microserviciodemo01.models.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Integer> {

    List<DetalleVenta> findByVenta_IdVenta(Integer idVenta);
}