package com.bookpoint.logistic.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rutas")
public class Ruta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 200)
    private String descripcionRuta;

    @Column(length = 100, nullable = false)
    private String origen;

    @Column(length = 100, nullable = false)
    private String destino;

    @Column(length = 20, nullable = false)
    private String estado = "PENDIENTE";

    @Column(name = "sucursal_id")
    private Long sucursalId;

    @ManyToOne
    @JoinColumn(name = "transportista_id")
    private Transportista transportista;
}
