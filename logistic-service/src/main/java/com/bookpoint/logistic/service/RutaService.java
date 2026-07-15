package com.bookpoint.logistic.service;

import com.bookpoint.logistic.client.SucursalClient;
import com.bookpoint.logistic.dto.SucursalDTO;
import com.bookpoint.logistic.model.Ruta;
import com.bookpoint.logistic.model.Transportista;
import com.bookpoint.logistic.repository.RutaRepository;
import com.bookpoint.logistic.repository.TransportistaRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
public class RutaService {

    private static final Logger logger = LoggerFactory.getLogger(RutaService.class);

    @Autowired
    private RutaRepository rutaRepository;
    
    @Autowired
    private TransportistaRepository transportistaRepository;

    @Autowired
    private SucursalClient sucursalClient;

    public List<Ruta> listarRutas() {
        return rutaRepository.findAll();
    }

    public Optional<Ruta> buscarPorId(Long id) {
        return rutaRepository.findById(id);
    }

    @Transactional
    public Ruta asignarRutaATransportista(Long transportistaId, Ruta ruta) {
        logger.info("Asignando ruta a transportista {}", transportistaId);

        Transportista transportista = transportistaRepository.findById(transportistaId)
            .orElseThrow(() -> new RuntimeException("Transportista con ID " + transportistaId + " no existe"));
        
        if (ruta.getSucursalId() != null) {
            SucursalDTO sucursal = sucursalClient.obtenerSucursalPorId(ruta.getSucursalId());
            if (sucursal == null) {
                throw new RuntimeException("Sucursal con ID " + ruta.getSucursalId() + " no existe");
            }
            logger.info("Sucursal encontrada: {} - {}", sucursal.getId(), sucursal.getNombre());
        }
        
        ruta.setTransportista(transportista);
        return rutaRepository.save(ruta);
    }

    public boolean eliminarRuta(Long id) {
        if (!rutaRepository.existsById(id)) return false;
        rutaRepository.deleteById(id);
        return true;
    }

    @Transactional
    public Ruta actualizarRuta(Long id, Ruta datosNuevos) {
        Ruta ruta = rutaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ruta no existe"));
        if (!"PENDIENTE".equals(ruta.getEstado())) {
            throw new RuntimeException("Ruta solo editable en estado PENDIENTE");
        }
        ruta.setOrigen(datosNuevos.getOrigen());
        ruta.setDestino(datosNuevos.getDestino());
        return rutaRepository.save(ruta);
    }

    public List<Ruta> listarPorSucursal(Long sucursalId) {
        return rutaRepository.findBySucursalId(sucursalId);
    }
}