package com.example.microserviciodemo01.models;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ventas")
@Data
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venta")
    private Integer idVenta;

    @Column(name = "id_cliente", nullable = false)
    private Integer idCliente;

    @Column(nullable = false)
    private BigDecimal total;

    @Column(name = "fecha_venta", insertable = false, updatable = false)
    private LocalDateTime fechaVenta;
}
