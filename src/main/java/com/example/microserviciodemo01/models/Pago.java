package com.example.microserviciodemo01.models;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
@Data
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private Integer idPago;

    @Column(name = "id_factura", nullable = false)
    private Integer idFactura;

    @Column(name = "metodo_pago", nullable = false)
    private String metodoPago;

    @Column(nullable = false)
    private BigDecimal monto;

    @Column(nullable = false)
    private String estado;

    @Column(name = "referencia_transaccion")
    private String referenciaTransaccion;

    @Column(name = "fecha_pago", nullable = false)
    private LocalDateTime fechaPago;
}
