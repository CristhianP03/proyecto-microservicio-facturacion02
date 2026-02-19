package com.example.microserviciodemo01.Service;

import com.example.microserviciodemo01.models.Pago;
import com.example.microserviciodemo01.Repository.PagoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PagoService {

    private final PagoRepository pagoRepository;

    public PagoService(PagoRepository pagoRepository) {
        this.pagoRepository = pagoRepository;
    }

    public List<Pago> obtenerTodos() {
        return pagoRepository.findAll();
    }

    public List<Pago> obtenerPorFactura(Integer idFactura) {
        return pagoRepository.findByIdFactura(idFactura);
    }
}
