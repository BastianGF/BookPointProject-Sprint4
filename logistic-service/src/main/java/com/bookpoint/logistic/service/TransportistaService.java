package com.bookpoint.logistic.service;

import com.bookpoint.logistic.model.Transportista;
import com.bookpoint.logistic.repository.TransportistaRepository;

import jakarta.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransportistaService {

    private static final Logger logger = LoggerFactory.getLogger(TransportistaService.class);

    @Autowired
    private TransportistaRepository transportistaRepository;

    public List<Transportista> listarTransportistas() {
        logger.info("Listando transportistas disponibles");
        return transportistaRepository.findByDisponible(true);
    }

    public Transportista obtainPorId(Long id) {
        return transportistaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Transportista no encontrado con el ID: " + id));
    }

    public Transportista registerTransportista(Transportista transportista) {
        logger.info("Registrando transportista: {}", transportista.getNombre());
        return transportistaRepository.save(transportista);
    }

        public Transportista updateTransportista(Long id, Transportista datosNuevos) {
        Transportista existente = transportistaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Transportista no encontrado con ID: " + id));
        
        existente.setNombre(datosNuevos.getNombre());
        existente.setRut(datosNuevos.getRut());
        existente.setDisponible(datosNuevos.getDisponible());
        
        return transportistaRepository.save(existente);
    }

    public boolean deleteTransportista(Long id) {
        Transportista existente = transportistaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Transportista no encontrado con ID: " + id));
        
        existente.setDisponible(false);
        transportistaRepository.save(existente);
        return true;
    }

    public List<Transportista> listTransportistasDisponibles() {
        logger.info("Listando transportistas disponibles");
        return transportistaRepository.findByDisponible(true);
    }

    public Transportista reactivarTransportista(Long id) {
        logger.info("Reactivando transportista con id: {}", id);
        Transportista transportista = transportistaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Transportista con ID " + id + " no existe"));
        transportista.setDisponible(true);
        return transportistaRepository.save(transportista);
    }
}