package com.bookpoint.logistic.service;

import com.bookpoint.logistic.model.Envio;
import com.bookpoint.logistic.model.Incidencia;
import com.bookpoint.logistic.repository.EnvioRepository;
import com.bookpoint.logistic.repository.IncidenciaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class IncidenciaServiceTest {

    @Mock
    private IncidenciaRepository incidenciaRepository;

    @Mock
    private EnvioRepository envioRepository;

    @InjectMocks
    private IncidenciaService incidenciaService;

    private Incidencia incidenciaBase;
    private Envio envioBase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        incidenciaBase = new Incidencia();
        incidenciaBase.setId(1L);
        incidenciaBase.setDescripcionIncidencia("Problema con el envío");
        incidenciaBase.setFecha(new Date());
        incidenciaBase.setTipo("RETRASO");

        envioBase = new Envio();
        envioBase.setId(1L);
        envioBase.setEstadoEnvio("PENDIENTE");
        envioBase.setFechaActualizacion(new Date());
        envioBase.setUbicacionActual("Bodega Central");
    }

    // TEST LISTAR LAS INCIDENCIAS
    @Test
    void testListarIncidencias() {
        List<Incidencia> incidencias = new ArrayList<>();
        incidencias.add(incidenciaBase);
        when(incidenciaRepository.findAll()).thenReturn(incidencias);

        List<Incidencia> resultado = incidenciaService.listarIncidencias();

        assertThat(resultado).hasSize(1);
        verify(incidenciaRepository).findAll();
    }

    @Test
    void testListarIncidenciasVacio() {
        when(incidenciaRepository.findAll()).thenReturn(new ArrayList<>());

        List<Incidencia> resultado = incidenciaService.listarIncidencias();

        assertThat(resultado).isEmpty();
        verify(incidenciaRepository).findAll();
    }

    // TEST PARA REGISTRAR INCIDENCIA
    @Test
    void testRegistrarIncidencia() {
        when(incidenciaRepository.save(any(Incidencia.class))).thenReturn(incidenciaBase);

        Incidencia resultado = incidenciaService.registrarIncidencia(incidenciaBase);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getFecha()).isNotNull();
        verify(incidenciaRepository).save(any(Incidencia.class));
    }

    // TEST PARA REGISTRAR INCIDENCIA EN ENVIO
    @Test
    void testRegistrarIncidenciaEnEnvioExitoso() {
        when(envioRepository.findById(1L)).thenReturn(Optional.of(envioBase));
        when(incidenciaRepository.save(any(Incidencia.class))).thenReturn(incidenciaBase);

        Incidencia resultado = incidenciaService.registrarIncidenciaEnEnvio(1L, incidenciaBase);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getEnvio()).isEqualTo(envioBase);
        assertThat(resultado.getFecha()).isNotNull();
        verify(envioRepository).findById(1L);
        verify(incidenciaRepository).save(any(Incidencia.class));
    }

    @Test
    void testRegistrarIncidenciaEnEnvioNoExiste() {
        when(envioRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> incidenciaService.registrarIncidenciaEnEnvio(9999L, incidenciaBase))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Envío con ID 9999 no existe");

        verify(envioRepository).findById(9999L);
        verify(incidenciaRepository, never()).save(any(Incidencia.class));
    }
}