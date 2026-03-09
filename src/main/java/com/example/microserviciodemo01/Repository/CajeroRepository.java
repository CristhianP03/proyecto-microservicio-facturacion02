package com.example.microserviciodemo01.Repository;

import com.example.microserviciodemo01.models.Cajero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CajeroRepository extends JpaRepository<Cajero, Integer> {

    Optional<Cajero> findByUsernameAndActivoTrue(String username);

    boolean existsByUsername(String username);
}