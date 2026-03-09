package com.example.microserviciodemo01.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "cajeros")
@Data
public class Cajero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cajero")
    private Integer idCajero;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "nombre_completo", nullable = false)
    private String nombreCompleto;

    @Column(name = "numero_caja", nullable = false)
    private Integer numeroCaja;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;
}