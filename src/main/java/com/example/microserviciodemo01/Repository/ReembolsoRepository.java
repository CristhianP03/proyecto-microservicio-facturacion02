package com.example.microserviciodemo01.Repository;

import com.example.microserviciodemo01.models.Reembolso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReembolsoRepository extends JpaRepository<Reembolso, Integer> {
}

