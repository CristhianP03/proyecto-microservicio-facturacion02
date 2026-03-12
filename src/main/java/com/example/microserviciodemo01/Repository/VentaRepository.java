package com.example.microserviciodemo01.Repository;

import com.example.microserviciodemo01.models.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Integer> {

    /**
     * Obtiene el ultimo numero de registro filtrando por modo.
     *
     * esModoTest = false → secuencia de ventas reales
     * esModoTest = true  → secuencia de ventas de prueba
     *
     * Al borrar ventas de prueba con /test/limpiar-ventas,
     * el MAX vuelve a null y el contador reinicia en 000000001.
     * La secuencia real nunca se ve afectada.
     */
    @Query("SELECT MAX(v.numeroRegistro) FROM Venta v WHERE v.esModoTest = :esModoTest")
    Optional<String> findUltimoNumeroRegistroPorModo(@Param("esModoTest") Boolean esModoTest);
}