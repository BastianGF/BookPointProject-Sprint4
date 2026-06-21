package com.bookpoint.logistic.service;

import com.bookpoint.logistic.model.Traslado;
import com.bookpoint.logistic.model.Transportista;
import com.bookpoint.logistic.repository.TrasladoRepository;
import com.bookpoint.logistic.repository.TransportistaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
public class TrasladoService {
    
    @Autowired
    private TrasladoRepository trasladoRepository;
    @Autowired
    private TransportistaRepository transportistaRepository;
    
    private static final Logger logger = LoggerFactory.getLogger(TrasladoService.class);

    public Traslado registrarTraslado(Traslado traslado) {
        logger.info("Registrando traslado desde sucursal {} a {}", traslado.getOrigenId(), traslado.getDestinoId());
        traslado.setEstado("PENDIENTE");
        traslado.setFechaRegistro(new Date());
        return trasladoRepository.save(traslado);
    }

    @Transactional
    public Traslado asignarTransportista(Long trasladoId, Long transportistaId) {
        logger.info("Asignando transportista {} a traslado {}", transportistaId, trasladoId);
        Traslado traslado = trasladoRepository.findById(trasladoId)
            .orElseThrow(() -> new RuntimeException("Traslado con ID " + trasladoId + " no existe"));
        if (!"PENDIENTE".equals(traslado.getEstado())) 
            throw new RuntimeException("Traslado no en estado PENDIENTE");
        
        Transportista transportista = transportistaRepository.findById(transportistaId)
            .orElseThrow(() -> new RuntimeException("Transportista con ID " + transportistaId + " no existe"));
        if (!transportista.getDisponible()) 
            throw new RuntimeException("Transportista no disponible");
        
        traslado.setTransportista(transportista);
        traslado.setEstado("ASIGNADO");
        return trasladoRepository.save(traslado);
    }

    @Transactional
    public Traslado confirmarSalida(Long trasladoId) {
        logger.info("Confirmando salida de traslado {}", trasladoId);
        Traslado traslado = trasladoRepository.findById(trasladoId)
            .orElseThrow(() -> new RuntimeException("Traslado con ID " + trasladoId + " no existe"));
        if (!"ASIGNADO".equals(traslado.getEstado())) 
            throw new RuntimeException("Traslado no en estado ASIGNADO");
        
        traslado.setEstado("EN_TRANSITO");
        traslado.setFechaActualizacion(new Date());
        traslado.setUbicacionActual("En tránsito desde sucursal " + traslado.getOrigenId());
        return trasladoRepository.save(traslado);
    }

    @Transactional
    public Traslado confirmarRecepcion(Long trasladoId, String observaciones) {
        logger.info("Confirmando recepción de traslado {}", trasladoId);
        Traslado traslado = trasladoRepository.findById(trasladoId)
            .orElseThrow(() -> new RuntimeException("Traslado con ID " + trasladoId + " no existe"));
        if (!"EN_TRANSITO".equals(traslado.getEstado())) 
            throw new RuntimeException("Traslado no en estado EN_TRANSITO");
        
        traslado.setEstado("ENTREGADO");
        traslado.setObservaciones(observaciones);
        traslado.setFechaActualizacion(new Date());
        return trasladoRepository.save(traslado);
    }

    public Traslado obtenerPorId(Long id) {
        logger.info("Buscando traslado con id: {}", id);
        return trasladoRepository.findById(id).orElse(null);
    }
}
