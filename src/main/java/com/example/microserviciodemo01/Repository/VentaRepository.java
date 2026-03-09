package com.example.microserviciodemo01.Repository;

import com.example.microserviciodemo01.models.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Integer> {

    @Query("SELECT MAX(v.numeroRegistro) FROM Venta v")
    Optional<String> findUltimoNumeroRegistro();
}