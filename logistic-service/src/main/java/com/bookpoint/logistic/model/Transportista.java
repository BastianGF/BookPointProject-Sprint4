package com.bookpoint.logistic.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transportistas")
public class Transportista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    @Column(length = 100, nullable = false)
    private String nombre;

    @NotBlank(message = "El RUT no puede estar vacío")
    @Size(max = 12, message = "RUT inválido")
    @Column(length = 12, nullable = false, unique = true)
    private String rut;

    @Size(max = 20, message = "Teléfono inválido")
    @Column(length = 20)
    private String telefono;

    @NotNull(message = "La disponibilidad es requerida")
    @Column(nullable = false)
    private Boolean disponible;
}