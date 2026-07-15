package com.bookpoint.logistic.client;

import com.bookpoint.logistic.dto.SucursalDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class SucursalClient {

    @Autowired
    private RestTemplate restTemplate;

    private static final String SUCURSAL_URL = "http://localhost:8083/api/sucursales";

    public SucursalDTO obtenerSucursalPorId(Long id) {
        try {
            return restTemplate.getForObject(SUCURSAL_URL + "/" + id, SucursalDTO.class);
        } catch (Exception e) {
            return null;
        }
    }
}