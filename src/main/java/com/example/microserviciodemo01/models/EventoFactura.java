package com.example.microserviciodemo01.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "eventos_factura")
@Data
public class EventoFactura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evento")
    private Integer idEvento;

    @Column(name = "id_factura", nullable = false)
    private Integer idFactura;

    @Column(name = "tipo_evento", nullable = false)
    private String tipoEvento;

    @Column(name = "fecha_evento", nullable = false)
    private LocalDateTime fechaEvento;

    @Column(name = "datos_evento")
    private String datosEvento; // JSONB mapeado como String en Java
}
