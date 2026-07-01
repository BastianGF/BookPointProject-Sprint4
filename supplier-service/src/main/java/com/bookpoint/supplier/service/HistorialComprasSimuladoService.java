package com.bookpoint.supplier.service;

import com.bookpoint.supplier.dto.HistorialCompraDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HistorialComprasSimuladoService {

    private static final Logger logger = LoggerFactory.getLogger(HistorialComprasSimuladoService.class);
    private List<HistorialCompraDTO> historialCompras = null;
    private final ObjectMapper objectMapper;

    public HistorialComprasSimuladoService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public HistorialComprasSimuladoService() {
        this.objectMapper = new ObjectMapper();
    }

    private List<HistorialCompraDTO> cargarHistorial() {
        if (historialCompras == null) {
            try {
                InputStream inputStream = getClass().getResourceAsStream("/historial-compras.json");
                if (inputStream == null) {
                    logger.error("No se encontró historial-compras.json");
                    historialCompras = new ArrayList<>();
                    return historialCompras;
                }
                historialCompras = objectMapper.readValue(inputStream, new TypeReference<List<HistorialCompraDTO>>() {});
                logger.info("Historial de compras cargado: {} registros", historialCompras.size());
            } catch (Exception e) {
                logger.error("Error al cargar historial de compras: {}", e.getMessage());
                historialCompras = new ArrayList<>();
            }
        }
        return historialCompras;
    }

    public List<HistorialCompraDTO> obtenerPorProveedor(Long proveedorId) {
        return cargarHistorial().stream()
                .filter(c -> c.getProveedorId().equals(proveedorId))
                .collect(Collectors.toList());
    }

    public HistorialCompraDTO registrarCompra(HistorialCompraDTO nuevaCompra) {
        List<HistorialCompraDTO> historial = cargarHistorial();
        nuevaCompra.setId((long) (historial.size() + 1));
        historial.add(nuevaCompra);
        return nuevaCompra;
    }
}