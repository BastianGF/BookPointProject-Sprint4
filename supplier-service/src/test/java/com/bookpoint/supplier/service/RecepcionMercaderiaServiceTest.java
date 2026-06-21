package com.bookpoint.supplier.service;

import com.bookpoint.supplier.model.RecepcionMercaderia;
import com.bookpoint.supplier.repository.RecepcionMercaderiaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RecepcionMercaderiaServiceTest {

    @Mock
    private RecepcionMercaderiaRepository recepcionRepository;

    @InjectMocks
    private RecepcionMercaderiaService recepcionService;

    private RecepcionMercaderia recepcionBase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        recepcionBase = new RecepcionMercaderia();
        recepcionBase.setId(1L);
        recepcionBase.setFechaRecepcion(new Date());
        recepcionBase.setCantidadRecibida(50);
        recepcionBase.setObservacionRecepcion("Recepción de prueba");
    }

    // TEST PARA LISTAR RECEPCIONES

    @Test
    void testListarRecepciones() {
        List<RecepcionMercaderia> recepciones = new ArrayList<>();
        recepciones.add(recepcionBase);
        when(recepcionRepository.findAll()).thenReturn(recepciones);

        List<RecepcionMercaderia> resultado = recepcionService.listarRecepciones();

        assertThat(resultado).hasSize(1);
        verify(recepcionRepository).findAll();
    }

    @Test
    void testListarRecepcionesVacio() {
        when(recepcionRepository.findAll()).thenReturn(new ArrayList<>());

        List<RecepcionMercaderia> resultado = recepcionService.listarRecepciones();

        assertThat(resultado).isEmpty();
        verify(recepcionRepository).findAll();
    }

    // TEST PARA TEST REGISTRAR RECEPCION

    @Test
    void testRegistrarRecepcion() {
        RecepcionMercaderia nueva = new RecepcionMercaderia();
        nueva.setCantidadRecibida(30);
        nueva.setObservacionRecepcion("Nueva recepción");

        when(recepcionRepository.save(any(RecepcionMercaderia.class))).thenReturn(recepcionBase);

        RecepcionMercaderia resultado = recepcionService.registrarRecepcion(nueva);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getFechaRecepcion()).isNotNull();
        verify(recepcionRepository).save(any(RecepcionMercaderia.class));
    }

    @Test
    void testRegistrarRecepcionSinObservacion() {
        RecepcionMercaderia nueva = new RecepcionMercaderia();
        nueva.setCantidadRecibida(20);

        when(recepcionRepository.save(any(RecepcionMercaderia.class))).thenReturn(recepcionBase);

        RecepcionMercaderia resultado = recepcionService.registrarRecepcion(nueva);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getFechaRecepcion()).isNotNull();
        verify(recepcionRepository).save(any(RecepcionMercaderia.class));
    }
}
