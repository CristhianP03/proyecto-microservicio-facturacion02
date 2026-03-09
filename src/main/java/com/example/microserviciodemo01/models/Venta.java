package com.example.microserviciodemo01.models;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ventas")
@Data
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venta")
    private Integer idVenta;

    @Column(name = "numero_registro", nullable = false, unique = true)
    private String numeroRegistro;

    @Column(name = "id_cajero", nullable = false)
    private Integer idCajero;

    @Column(name = "cedula_cliente", nullable = false)
    private String cedulaCliente;

    @Column(name = "nombre_cliente", nullable = false)
    private String nombreCliente;

    @Column(name = "direccion_cliente")
    private String direccionCliente;

    @Column(name = "telefono_cliente")
    private String telefonoCliente;

    @Column(name = "subtotal", nullable = false, precision = 15, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "impuestos", nullable = false, precision = 15, scale = 2)
    private BigDecimal impuestos;

    @Column(name = "total", nullable = false, precision = 15, scale = 2)
    private BigDecimal total;

    @Column(name = "forma_pago", nullable = false)
    private String formaPago;

    @Column(name = "valor_pagado", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorPagado;

    @Column(name = "fecha_venta", nullable = false)
    private LocalDateTime fechaVenta;

    @Column(name = "estado", nullable = false)
    private String estado;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleVenta> detalles;

    @Column(name = "es_modo_test", nullable = false, columnDefinition = "boolean default false")
    private Boolean esModoTest = false;
}