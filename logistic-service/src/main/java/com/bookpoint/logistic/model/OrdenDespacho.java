package com.bookpoint.logistic.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ordenes_despacho")
public class OrdenDespacho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date fechaCreacion;

    @NotBlank(message = "El estado no puede estar vacío")
    @Column(length = 50, nullable = false)
    private String estadoDespacho;

    @Size(max = 200, message = "La observación no puede superar 200 caracteres")
    @Column(length = 200)
    private String observacionDespacho;

    @ManyToOne
    @JoinColumn(name = "transportista_id")
    private Transportista transportista;

    @Column(length = 50)
    private String tipo; // DOMICILIO, BODEGA_SUCURSAL COMO FUSION.

    private Long sucursalDestinoId;

    @Column(length = 200)
    private String ubicacionBodega;

    @Min(value = 1)
    private Integer cantidadSolicitada;

    @Min(value = 1)
    private Integer cantidadConfirmada;

    @Min(value = 1)
    private Integer cantidadFinal;

    @Column(columnDefinition = "TEXT")
    private String productos; // arvhivo JSON con la lista de productos
}