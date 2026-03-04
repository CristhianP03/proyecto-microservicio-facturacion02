package com.example.microservicioconsultafacturacion.DTO;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PagoDTO {
    private Integer idPago;
    private Integer idFactura;
    private String metodoPago;
    private BigDecimal monto;
    private String estado;
    private LocalDateTime fechaPago;
}
