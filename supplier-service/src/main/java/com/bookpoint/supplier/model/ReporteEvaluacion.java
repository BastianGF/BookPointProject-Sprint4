package com.bookpoint.supplier.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reportes_evaluacion")
public class ReporteEvaluacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date fechaReporte;

    @Column(length = 500)
    private String contenidoReporte;

    @ManyToOne
    @JoinColumn(name = "evaluacion_id")
    private EvaluacionProveedor evaluacionProveedor;
}
