package com.bookpoint.logistic.service;

import com.bookpoint.logistic.client.SupplierClient;
import com.bookpoint.logistic.dto.ProveedorDTO;
import com.bookpoint.logistic.model.OrdenDespacho;
import com.bookpoint.logistic.model.Transportista;
import com.bookpoint.logistic.repository.OrdenDespachoRepository;
import com.bookpoint.logistic.repository.TransportistaRepository;

import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class OrdenDespachoService {

    private static final Logger logger = LoggerFactory.getLogger(OrdenDespachoService.class);

    @Autowired
    private OrdenDespachoRepository ordenDespachoRepository;

    @Autowired
    private SupplierClient supplierClient;

    @Autowired
    private TransportistaRepository transportistaRepository;

    public List<OrdenDespacho> listarOrdenes() {
        logger.info("Listando todas las órdenes de despacho");
        return ordenDespachoRepository.findAll();
    }

    public Optional<OrdenDespacho> buscarPorId(Long id) {
        logger.info("Buscando orden de despacho con id: {}", id);
        return ordenDespachoRepository.findById(id);
    }

    public OrdenDespacho crearOrden(OrdenDespacho orden, Long proveedorId) {
        logger.info("Creando orden de despacho, consultando proveedor id: {} en SupplierService", proveedorId);
        ProveedorDTO proveedor = supplierClient.obtenerProveedorPorId(proveedorId);
        if (proveedor == null) {
            logger.error("Proveedor con id {} no encontrado en SupplierService", proveedorId);
            throw new RuntimeException("Proveedor no encontrado en SupplierService");
        }
        logger.info("Proveedor encontrado: {}. Creando orden.", proveedor.getNombre());
        orden.setFechaCreacion(new Date());
        orden.setEstadoDespacho("PENDIENTE");
        orden.setObservacionDespacho("Proveedor asignado: " + proveedor.getNombre());
        return ordenDespachoRepository.save(orden);
    }

    public OrdenDespacho confirmarDespacho(Long id) {
        logger.info("Confirmando despacho con id: {}", id);
        OrdenDespacho orden = ordenDespachoRepository.findById(id).orElse(null);
        if (orden == null) {
            logger.warn("Orden con id {} no encontrada para confirmar", id);
            return null;
        }
        orden.setEstadoDespacho("CONFIRMADO");
        return ordenDespachoRepository.save(orden);
    }

    public OrdenDespacho cancelarDespacho(Long id) {
        logger.info("Cancelando despacho con id: {}", id);
        OrdenDespacho orden = ordenDespachoRepository.findById(id).orElse(null);
        if (orden == null) {
            logger.warn("Orden con id {} no encontrada para cancelar", id);
            return null;
        }
        orden.setEstadoDespacho("CANCELADO");
        return ordenDespachoRepository.save(orden);
    }

    @Transactional
    public OrdenDespacho prepararMercaderia(Long ordenId, String ubicacionBodega, Integer cantidadConfirmada) {
        logger.info("Preparando mercadería para orden {}", ordenId);
        OrdenDespacho orden = ordenDespachoRepository.findById(ordenId)
            .orElseThrow(() -> new RuntimeException("Orden con ID " + ordenId + " no existe"));
        if (!"PENDIENTE".equals(orden.getEstadoDespacho())) 
            throw new RuntimeException("Orden no en estado PENDIENTE");
        orden.setUbicacionBodega(ubicacionBodega);
        orden.setCantidadConfirmada(cantidadConfirmada);
        orden.setEstadoDespacho("PREPARADO");
        return ordenDespachoRepository.save(orden);
    }

    @Transactional
    public OrdenDespacho registrarSalidaBodega(Long ordenId, Long transportistaId, Integer cantidadFinal) {
        logger.info("Registrando salida de orden {}", ordenId);
        OrdenDespacho orden = ordenDespachoRepository.findById(ordenId)
            .orElseThrow(() -> new RuntimeException("Orden con ID " + ordenId + " no existe"));
        if (!"PREPARADO".equals(orden.getEstadoDespacho())) 
            throw new RuntimeException("Orden no en estado PREPARADO");
        
        Transportista transportista = transportistaRepository.findById(transportistaId)
            .orElseThrow(() -> new RuntimeException("Transportista con ID " + transportistaId + " no existe"));
        
        orden.setTransportista(transportista);
        orden.setCantidadFinal(cantidadFinal);
        orden.setEstadoDespacho("DESPACHADO");
        return ordenDespachoRepository.save(orden);
    }

}