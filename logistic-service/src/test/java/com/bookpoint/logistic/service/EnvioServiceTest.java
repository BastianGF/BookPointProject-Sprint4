package com.bookpoint.logistic.service;

import com.bookpoint.logistic.model.Envio;
import com.bookpoint.logistic.repository.EnvioRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EnvioServiceTest {

    @Mock
    private EnvioRepository envioRepository;

    @InjectMocks
    private EnvioService envioService;

    private Envio envioBase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        envioBase = new Envio();
        envioBase.setId(1L);
        envioBase.setEstadoEnvio("PENDIENTE");
        envioBase.setFechaActualizacion(new Date());
        envioBase.setUbicacionActual("Bodega Central");
    }

    // TEST PARA LISTAR ENVIOS
    @Test
    void testListarEnvios() {
        List<Envio> envios = new ArrayList<>();
        envios.add(envioBase);
        when(envioRepository.findAll()).thenReturn(envios);

        List<Envio> resultado = envioService.listarEnvios();

        assertThat(resultado).hasSize(1);
        verify(envioRepository).findAll();
    }

    @Test
    void testListarEnviosVacio() {
        when(envioRepository.findAll()).thenReturn(new ArrayList<>());

        List<Envio> resultado = envioService.listarEnvios();

        assertThat(resultado).isEmpty();
        verify(envioRepository).findAll();
    }

    // TEST BUSCAR ENVIO POR ID
    @Test
    void testBuscarPorIdExistente() {
        when(envioRepository.findById(1L)).thenReturn(Optional.of(envioBase));

        Optional<Envio> resultado = envioService.buscarPorId(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(1L);
        verify(envioRepository).findById(1L);
    }

    @Test
    void testBuscarPorIdInexistente() {
        when(envioRepository.findById(9999L)).thenReturn(Optional.empty());

        Optional<Envio> resultado = envioService.buscarPorId(9999L);

        assertThat(resultado).isEmpty();
        verify(envioRepository).findById(9999L);
    }

    // TEST CREAR ENVIO
    @Test
    void testCrearEnvio() {
        Envio nuevo = new Envio();
        nuevo.setEstadoEnvio("PENDIENTE");
        nuevo.setUbicacionActual("Bodega Central");

        when(envioRepository.save(any(Envio.class))).thenReturn(envioBase);

        Envio resultado = envioService.crearEnvio(nuevo);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getFechaActualizacion()).isNotNull();
        verify(envioRepository).save(any(Envio.class));
    }

    // TEST PARA ACTUALIZAR ESTADO DE ENVIO
    @Test
    void testActualizarEstadoEnvioExistente() {
        when(envioRepository.findById(1L)).thenReturn(Optional.of(envioBase));
        when(envioRepository.save(any(Envio.class))).thenReturn(envioBase);

        Envio resultado = envioService.actualizarEstadoEnvio(1L, "EN_TRANSITO");

        assertThat(resultado).isNotNull();
        assertThat(resultado.getEstadoEnvio()).isEqualTo("EN_TRANSITO");
        assertThat(resultado.getFechaActualizacion()).isNotNull();
        verify(envioRepository).findById(1L);
        verify(envioRepository).save(any(Envio.class));
    }

    @Test
    void testActualizarEstadoEnvioInexistente() {
        when(envioRepository.findById(9999L)).thenReturn(Optional.empty());

        Envio resultado = envioService.actualizarEstadoEnvio(9999L, "EN_TRANSITO");

        assertThat(resultado).isNull();
        verify(envioRepository).findById(9999L);
        verify(envioRepository, never()).save(any(Envio.class));
    }

    @Test
    void testActualizarEstadoEnvioConEstadoNulo() {
        when(envioRepository.findById(1L)).thenReturn(Optional.of(envioBase));
        when(envioRepository.save(any(Envio.class))).thenReturn(envioBase);

        Envio resultado = envioService.actualizarEstadoEnvio(1L, null);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getEstadoEnvio()).isNull();
        verify(envioRepository).save(any(Envio.class));
    }
}
