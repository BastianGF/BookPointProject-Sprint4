package com.bookpoint.logistic.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "envios")
public class Envio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El estado del envío es requerido")
    @Column(length = 50, nullable = false)
    private String estadoEnvio;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaActualizacion;

    @Column(length = 100)
    private String ubicacionActual;

    @OneToOne
    @JoinColumn(name = "orden_despacho_id")
    private OrdenDespacho ordenDespacho;
}
