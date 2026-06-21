package com.bookpoint.supplier.service;

import com.bookpoint.supplier.model.SolicitudReposicion;
import com.bookpoint.supplier.repository.SolicitudReposicionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class SolicitudReposicionService {

    @Autowired
    private SolicitudReposicionRepository solicitudRepository;

    public List<SolicitudReposicion> listarSolicitudes() {
        return solicitudRepository.findAll();
    }

    public Optional<SolicitudReposicion> buscarPorId(Long id) {
        return solicitudRepository.findById(id);
    }

    public SolicitudReposicion crearSolicitud(SolicitudReposicion solicitud) {
        solicitud.setFechaSolicitud(new Date());
        solicitud.setEstadoSolicitud("PENDIENTE");
        return solicitudRepository.save(solicitud);
    }
}
