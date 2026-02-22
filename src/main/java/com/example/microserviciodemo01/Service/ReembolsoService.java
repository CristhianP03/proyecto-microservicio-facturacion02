package com.example.microserviciodemo01.Service;

import com.example.microserviciodemo01.Repository.ReembolsoRepository;
import com.example.microserviciodemo01.Repository.PagoRepository;
import com.example.microserviciodemo01.models.Reembolso;
import com.example.microserviciodemo01.models.Pago;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReembolsoService {

    private final ReembolsoRepository reembolsoRepository;
    private final PagoRepository pagoRepository;

    public ReembolsoService(ReembolsoRepository reembolsoRepository, PagoRepository pagoRepository) {
        this.reembolsoRepository = reembolsoRepository;
        this.pagoRepository = pagoRepository;
    }

    /**
     * Registra un reembolso validando la existencia del pago y el monto.
     */
    @Transactional
    public Reembolso registrarReembolso(Reembolso reembolso) {
        // 1. Validar que el pago exista
        Pago pago = pagoRepository.findById(reembolso.getIdPago())
                .orElseThrow(() -> new RuntimeException("Error: El pago ID " + reembolso.getIdPago() + " no existe."));

        // 2. Validación de Monto: No se puede reembolsar más de lo que se pagó
        if (reembolso.getMonto() == null || reembolso.getMonto().compareTo(pago.getMonto()) > 0) {
            throw new RuntimeException("Error: El monto del reembolso excede el monto del pago original.");
        }

        // 3. Auditoría y Estado
        reembolso.setFechaReembolso(LocalDateTime.now());
        if (reembolso.getEstado() == null) {
            reembolso.setEstado("PROCESADO");
        }

        // 4. Lógica de Conexión: Actualizar el estado del pago a REEMBOLSADO
        pago.setEstado("REEMBOLSADO");
        pagoRepository.save(pago);

        return reembolsoRepository.save(reembolso);
    }

    @Transactional(readOnly = true)
    public List<Reembolso> obtenerTodos() {
        return reembolsoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Reembolso> obtenerPorFactura(Integer idFactura) {
        // Utiliza la Query personalizada que definimos en el Repository
        return reembolsoRepository.obtenerPorFactura(idFactura);
    }
}