package com.example.microservicioconsultafacturacion.DTO;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class VentaDTO {
    private Integer idVenta;
    private Integer idCliente;
    private BigDecimal total;
    private LocalDateTime fechaVenta;
}
