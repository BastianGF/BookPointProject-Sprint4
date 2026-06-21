package com.bookpoint.logistic.service;

import com.bookpoint.logistic.controller.EnvioController;
import com.bookpoint.logistic.model.Envio;
import com.bookpoint.logistic.model.Incidencia;
import com.bookpoint.logistic.repository.EnvioRepository;
import com.bookpoint.logistic.repository.IncidenciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

@Service
public class IncidenciaService {
    private static final Logger logger = LoggerFactory.getLogger(IncidenciaService.class);

    @Autowired
    private IncidenciaRepository incidenciaRepository;
    @Autowired
    private EnvioRepository envioRepository;
    
    public List<Incidencia> listarIncidencias() {
        return incidenciaRepository.findAll();
    }

    public Incidencia registrarIncidencia(Incidencia incidencia) {
        incidencia.setFecha(new Date());
        return incidenciaRepository.save(incidencia);
    }
    
    @Transactional
    public Incidencia registrarIncidenciaEnEnvio(Long envioId, Incidencia incidencia) {
        logger.info("Registrando incidencia en envío {}", envioId);
        Envio envio = envioRepository.findById(envioId)
            .orElseThrow(() -> new RuntimeException("Envío con ID " + envioId + " no existe"));
        incidencia.setEnvio(envio);
        incidencia.setFecha(new Date());
        return incidenciaRepository.save(incidencia);
    }

}
