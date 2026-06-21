package com.bookpoint.supplier.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "historial_compras")
public class HistorialCompras {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date fechaCompra;

    @Column(nullable = false)
    private Double montoTotal;

    @Column(length = 200)
    private String descripcionCompra;

    @ManyToOne
    @JoinColumn(name = "proveedor_id")
    private Proveedor proveedor;
}
