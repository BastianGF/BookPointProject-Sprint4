package com.bookpoint.logistic.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "incidencias")
public class Incidencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 200)
    private String descripcionIncidencia;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;

    @Column(length = 50)
    private String tipo;

    @ManyToOne
    @JoinColumn(name = "envio_id")
    private Envio envio;
}
