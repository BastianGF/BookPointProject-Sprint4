package com.bookpoint.supplier.service;

import com.bookpoint.supplier.dto.HistorialCompraDTO;
import com.bookpoint.supplier.model.HistorialCompras;
import com.bookpoint.supplier.model.Proveedor;
import com.bookpoint.supplier.repository.HistorialComprasRepository;
import com.bookpoint.supplier.repository.ProveedorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.Date;

import java.util.List;
import java.util.Optional;

@Service
public class ProveedorService {

    private static final Logger logger = LoggerFactory.getLogger(ProveedorService.class);

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Autowired
    private HistorialComprasRepository historialComprasRepository;

    @Autowired
    private HistorialComprasSimuladoService historialSimuladoService;

    public Page<Proveedor> listarProveedores(PageRequest pageRequest) {
        logger.info("Listando proveedores activos - página {}, tamaño {}", 
            pageRequest.getPageNumber(), pageRequest.getPageSize());
        return proveedorRepository.findByActivo(true, pageRequest);
    }

    public List<Proveedor> listarProveedores() {
        logger.info("Listando todos los proveedores activos");
        return proveedorRepository.findByActivo(true);
    }

    public Optional<Proveedor> buscarPorId(Long id) {
        logger.info("Buscando proveedor con id: {}", id);
        return proveedorRepository.findById(id);
    }

    public Proveedor registrarProveedor(Proveedor proveedor) {
        logger.info("Registrando proveedor: {}", proveedor.getNombre());
        return proveedorRepository.save(proveedor);
    }

    public Proveedor editarProveedor(Long id, Proveedor datos) {
        logger.info("Editando proveedor con id: {}", id);
        Proveedor existing = proveedorRepository.findById(id).orElse(null);
        if (existing == null) {
            logger.warn("Proveedor con id {} no encontrado", id);
            return null;
        }
        existing.setNombre(datos.getNombre());
        existing.setRut(datos.getRut());
        existing.setTelefono(datos.getTelefono());
        existing.setEmail(datos.getEmail());
        return proveedorRepository.save(existing);
    }

    public boolean eliminarProveedor(Long id) {
        logger.info("Inactivando proveedor con id: {}", id);
        Proveedor proveedor = proveedorRepository.findById(id).orElse(null);
        if (proveedor == null) {
            logger.warn("Proveedor con id {} no encontrado para inactivar", id);
            return false;
        }
        proveedor.setActivo(false);
        proveedorRepository.save(proveedor);
        logger.info("Proveedor {} inactivado exitosamente", id);
        return true;
    }

    public List<HistorialCompraDTO> obtenerHistorialComprasPorProveedor(Long proveedorId) {
        logger.info("Obteniendo historial de compras para proveedor {}", proveedorId);
        if (!proveedorRepository.existsById(proveedorId)) {
            throw new RuntimeException("Proveedor con ID " + proveedorId + " no existe");
        }
        return historialSimuladoService.obtenerPorProveedor(proveedorId);
    }

    public HistorialCompraDTO registrarCompraAProveedor(Long proveedorId, HistorialCompraDTO compraDTO) {
        logger.info("Registrando compra para proveedor {}", proveedorId);
        if (!proveedorRepository.existsById(proveedorId)) {
            throw new RuntimeException("Proveedor con ID " + proveedorId + " no existe");
        }
        compraDTO.setProveedorId(proveedorId);
        compraDTO.setFechaCompra(new Date());
        return historialSimuladoService.registrarCompra(compraDTO);
    }

    public HistorialCompras obtenerHistorialCompraPorId(Long id) {
        logger.info("Buscando historial de compra con id: {}", id);
        return historialComprasRepository.findById(id).orElse(null);
    }
}