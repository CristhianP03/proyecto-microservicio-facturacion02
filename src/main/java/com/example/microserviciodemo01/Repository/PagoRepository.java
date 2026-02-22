package com.example.microserviciodemo01.Repository;

import com.example.microserviciodemo01.models.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {

    // Usamos @Query para asegurar que no haya ambigüedad con el nombre del campo
    @Query("SELECT p FROM Pago p WHERE p.idFactura = :idFactura")
    List<Pago> findByIdFactura(@Param("idFactura") Integer idFactura);
}