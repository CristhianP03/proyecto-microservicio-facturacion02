package com.example.microserviciodemo01.Service;

import com.example.microserviciodemo01.models.Reembolso;
import com.example.microserviciodemo01.Repository.ReembolsoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReembolsoService {

    private final ReembolsoRepository reembolsoRepository;

    public ReembolsoService(ReembolsoRepository reembolsoRepository) {
        this.reembolsoRepository = reembolsoRepository;
    }

    public List<Reembolso> obtenerTodos() {
        return reembolsoRepository.findAll();
    }
}
