package com.example.microservicioconsultafacturacion.DTO;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FacturaDTO {
    private Integer idFactura;
    private String numeroFactura;
    private Integer idCliente;
    private String nombreCliente;
    private String emailCliente;
    private String estado;
    private BigDecimal subtotal;
    private BigDecimal impuestos;
    private BigDecimal total;
    private String moneda;
    private LocalDateTime fechaEmision;
}
