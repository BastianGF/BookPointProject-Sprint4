package com.bookpoint.supplier.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "recepciones_mercaderia")
public class RecepcionMercaderia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La fecha de recepción es requerida")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date fechaRecepcion;

    @NotNull(message = "La cantidad recibida es requerida")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    @Column(nullable = false)
    private Integer cantidadRecibida;

    @Column(length = 200)
    private String observacionRecepcion;

    @ManyToOne
    @JoinColumn(name = "proveedor_id")
    private Proveedor proveedor;

    @ManyToOne
    @JoinColumn(name = "historial_id")
    private HistorialCompras historialCompras;
}