package com.example.microserviciodemo01.models;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "facturas")
@Data
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_factura")
    private Integer idFactura;

    // 🔹 RELACIÓN CON VENTA
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_venta", nullable = false)
    private Venta venta;

    // 🔹 RELACIÓN CON DETALLES
    @OneToMany(mappedBy = "factura",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<DetalleFactura> detalles;

    @Column(name = "id_pedido", nullable = false)
    private Integer idPedido;

    @Column(name = "numero_factura", nullable = false, unique = true)
    private String numeroFactura;

    @Column(name = "id_cliente", nullable = false)
    private Integer idCliente;

    @Column(name = "nombre_cliente", nullable = false)
    private String nombreCliente;

    @Column(name = "email_cliente", nullable = false)
    private String emailCliente;

    @Column(name = "identificacion_fiscal_cliente")
    private String identificacionFiscalCliente;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDateTime fechaEmision;

    @Column(name = "fecha_vencimiento")
    private LocalDateTime fechaVencimiento;

    @Column(name = "estado", nullable = false)
    private String estado;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal subtotal;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal impuestos;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal total;

    @Column(nullable = false, length = 3)
    private String moneda;

    @Column(name = "fecha_creacion", insertable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion", insertable = false)
    private LocalDateTime fechaActualizacion;
}
