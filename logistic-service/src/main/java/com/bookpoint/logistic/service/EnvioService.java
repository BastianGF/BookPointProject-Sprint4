package com.bookpoint.logistic.service;

import com.bookpoint.logistic.model.Envio;
import com.bookpoint.logistic.repository.EnvioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class EnvioService {

    @Autowired
    private EnvioRepository envioRepository;

    public List<Envio> listarEnvios() {
        return envioRepository.findAll();
    }

    public Optional<Envio> buscarPorId(Long id) {
        return envioRepository.findById(id);
    }

    public Envio crearEnvio(Envio envio) {
        envio.setFechaActualizacion(new Date());
        return envioRepository.save(envio);
    }

    public Envio actualizarEstadoEnvio(Long id, String nuevoEstado) {
        Envio envio = envioRepository.findById(id).orElse(null);
        if (envio == null) return null;
        envio.setEstadoEnvio(nuevoEstado);
        envio.setFechaActualizacion(new Date());
        return envioRepository.save(envio);
    }
}
