package com.bookpoint.logistic.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "traslados")
public class Traslado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "El origen es requerido")
    private Long origenId;
    @NotNull(message = "El destino es requerido")
    private Long destinoId;
    @NotBlank(message = "El estado es requerido")
    @Column(length = 50)
    private String estado;
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaRegistro;
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaActualizacion;
    @ManyToOne
    @JoinColumn(name = "transportista_id")
    private Transportista transportista;
    @Column(length = 1000)
    private String productos; // JSON
    @Column(length = 200)
    private String ubicacionActual;
    @Column(length = 500)
    private String observaciones;
}