package com.example.microserviciodemo01.Repository;

import com.example.microserviciodemo01.models.Reembolso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReembolsoRepository extends JpaRepository<Reembolso, Integer> {

    // Análisis Estricto: Aseguramos el uso de alias 'r' y 'p' para claridad
    @Query("""
        SELECT r FROM Reembolso r 
        WHERE r.idPago IN (SELECT p.idPago FROM Pago p WHERE p.idFactura = :idFactura)
    """)
    List<Reembolso> obtenerPorFactura(@Param("idFactura") Integer idFactura);
}