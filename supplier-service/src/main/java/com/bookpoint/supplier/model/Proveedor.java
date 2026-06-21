package com.bookpoint.supplier.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "proveedores")
public class Proveedor {

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

    @Email(message = "Email inválido")
    @Column(length = 100)
    private String email;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @Min(value = 1, message = "El puntaje debe ser mínimo 1")
    @Max(value = 100, message = "El puntaje debe ser máximo 100")
    @Column(name = "puntaje")
    private Integer puntaje;
}
