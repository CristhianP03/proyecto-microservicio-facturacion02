package com.example.microserviciodemo01.models;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reembolsos")
@Data
public class Reembolso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reembolso")
    private Integer idReembolso;

    @Column(name = "id_pago", nullable = false)
    private Integer idPago;

    @Column(nullable = false)
    private BigDecimal monto;

    @Column
    private String motivo;

    @Column(name = "fecha_reembolso", nullable = false)
    private LocalDateTime fechaReembolso;

    @Column(nullable = false)
    private String estado;
}

