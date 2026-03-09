package com.example.microserviciodemo01.Repository;

import com.example.microserviciodemo01.models.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FacturaRepository extends JpaRepository<Factura, Integer> {

    Optional<Factura> findByVenta_IdVenta(Integer idVenta);

    List<Factura> findAllByOrderByIdFacturaDesc();

    /**
     * Devuelve las facturas cuyo número empieza con el prefijo dado
     * Y cuya venta tiene el mismo valor de esModoTest.
     *
     * Esto garantiza que Caja 1 real y Cajero Prueba (ambos con prefijo 001-001-)
     * tengan contadores completamente independientes.
     *
     * El resultado viene ordenado DESC para que el primero sea el último emitido.
     */
    @Query("SELECT f FROM Factura f " +
            "WHERE f.numeroFactura LIKE :prefijo% " +
            "AND f.venta.esModoTest = :esModoTest " +
            "ORDER BY f.idFactura DESC")
    List<Factura> findUltimoNumeroFacturaPorCajaYModo(
            @Param("prefijo") String prefijo,
            @Param("esModoTest") Boolean esModoTest);
}