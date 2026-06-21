package com.bookpoint.supplier.service;

import com.bookpoint.supplier.model.RecepcionMercaderia;
import com.bookpoint.supplier.repository.RecepcionMercaderiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class RecepcionMercaderiaService {

    @Autowired
    private RecepcionMercaderiaRepository recepcionRepository;

    public List<RecepcionMercaderia> listarRecepciones() {
        return recepcionRepository.findAll();
    }

    public RecepcionMercaderia registrarRecepcion(RecepcionMercaderia recepcion) {
        recepcion.setFechaRecepcion(new Date());
        return recepcionRepository.save(recepcion);
    }
}
