package com.bookpoint.supplier.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "evaluaciones_proveedor")
public class EvaluacionProveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La fecha de evaluación es requerida")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date fechaEvaluacion;

    @NotNull(message = "El puntaje es requerido")
    @Min(value = 1, message = "El puntaje debe ser mínimo 1")
    @Max(value = 100, message = "El puntaje debe ser máximo 100")
    @Column(nullable = false)
    private Integer puntaje;

    @Column(length = 200)
    private String observacionEvaluacion;

    @ManyToOne
    @JoinColumn(name = "proveedor_id")
    private Proveedor proveedor;

    @ManyToOne
    @JoinColumn(name = "historial_id")
    private HistorialCompras historialCompras;
}