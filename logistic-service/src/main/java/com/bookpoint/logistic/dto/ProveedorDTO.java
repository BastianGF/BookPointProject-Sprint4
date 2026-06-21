package com.bookpoint.logistic.dto;

import lombok.Data;

@Data
public class ProveedorDTO {
    private Long id;
    private String nombre;
    private String rut;
    private String telefono;
    private String email;
}