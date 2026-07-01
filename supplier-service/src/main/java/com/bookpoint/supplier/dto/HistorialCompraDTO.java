package com.bookpoint.supplier.dto;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class HistorialCompraDTO {
    private Long id;
    private Long proveedorId;
    private Date fechaCompra;
    private Double montoTotal;
    private String descripcionCompra;
    private List<ProductoCompraDTO> productos;
}

@Data
class ProductoCompraDTO {
    private String nombre;
    private Integer cantidad;
    private Double precioUnitario;
}