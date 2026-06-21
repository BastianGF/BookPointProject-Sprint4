package com.bookpoint.logistic.client;

import com.bookpoint.logistic.dto.ProveedorDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class SupplierClient {

    @Autowired
    private RestTemplate restTemplate;

    private static final String SUPPLIER_URL = "http://localhost:8082/api/proveedores";

    public ProveedorDTO obtenerProveedorPorId(Long id) {
        try {
            return restTemplate.getForObject(SUPPLIER_URL + "/" + id, ProveedorDTO.class);
        } catch (Exception e) {
            return null;
        }
    }
}
