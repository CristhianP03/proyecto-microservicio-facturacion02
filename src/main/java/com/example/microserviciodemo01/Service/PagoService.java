package com.example.microserviciodemo01.Service;

import com.example.microserviciodemo01.Repository.PagoRepository;
import com.example.microserviciodemo01.Repository.FacturaRepository;
import com.example.microserviciodemo01.models.Pago;
import com.example.microserviciodemo01.models.Factura;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PagoService {

    private final PagoRepository pagoRepository;
    private final FacturaRepository facturaRepository;

    public PagoService(PagoRepository pagoRepository, FacturaRepository facturaRepository) {
        this.pagoRepository = pagoRepository;
        this.facturaRepository = facturaRepository;
    }

    /**
     * Registra un pago y vincula la lógica con el estado de la factura.
     */
    @Transactional
    public Pago registrarPago(Integer idFactura, Pago pago) {
        // 1. Verificación de integridad: Factura debe existir
        Factura factura = facturaRepository.findById(idFactura)
                .orElseThrow(() -> new RuntimeException("Error: La factura " + idFactura + " no existe."));

        // 2. Vinculación manual de IDs
        pago.setIdFactura(idFactura);

        // 3. Auditoría automática
        pago.setFechaPago(LocalDateTime.now());
        if (pago.getEstado() == null) {
            pago.setEstado("COMPLETADO");
        }

        // 4. LÓGICA DE NEGOCIO ESTRICTA:
        // Validación de cambio de estado basado en montos
        if (pago.getMonto() != null && pago.getMonto().compareTo(factura.getTotal()) >= 0) {
            factura.setEstado("PAGADA");
            facturaRepository.save(factura);
        }

        return pagoRepository.save(pago);
    }

    /**
     * Retorna todos los pagos registrados en el sistema.
     * Requerido por PagoServiceTest.
     */
    @Transactional(readOnly = true)
    public List<Pago> obtenerTodos() {
        return pagoRepository.findAll();
    }

    /**
     * Retorna pagos filtrados por factura.
     */
    @Transactional(readOnly = true)
    public List<Pago> obtenerPorFactura(Integer idFactura) {
        return pagoRepository.findByIdFactura(idFactura);
    }
}