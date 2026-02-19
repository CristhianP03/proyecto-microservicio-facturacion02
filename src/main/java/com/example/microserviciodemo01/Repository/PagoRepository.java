package com.example.microserviciodemo01.Repository;

import com.example.microserviciodemo01.models.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagoRepository
        extends JpaRepository<Pago, Integer> {

    List<Pago> findByIdFactura(Integer idFactura);
}
