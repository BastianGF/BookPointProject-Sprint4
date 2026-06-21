package com.bookpoint.supplier.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "solicitudes_reposicion")
public class SolicitudReposicion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date fechaSolicitud;

    @NotBlank(message = "El estado no puede estar vacío")
    @Column(length = 50, nullable = false)
    private String estadoSolicitud;

    @NotNull(message = "La cantidad solicitada es requerida")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    @Column(nullable = false)
    private Integer cantidadSolicitada;

    @ManyToOne
    @JoinColumn(name = "proveedor_id")
    private Proveedor proveedor;

    @ManyToOne
    @JoinColumn(name = "historial_id")
    private HistorialCompras historialCompras;
}