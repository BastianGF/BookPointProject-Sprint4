package com.bookpoint.supplier.service;

import com.bookpoint.supplier.model.SolicitudReposicion;
import com.bookpoint.supplier.repository.SolicitudReposicionRepository;
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

class SolicitudReposicionServiceTest {

    @Mock
    private SolicitudReposicionRepository solicitudRepository;

    @InjectMocks
    private SolicitudReposicionService solicitudService;

    private SolicitudReposicion solicitudBase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        solicitudBase = new SolicitudReposicion();
        solicitudBase.setId(1L);
        solicitudBase.setFechaSolicitud(new Date());
        solicitudBase.setEstadoSolicitud("PENDIENTE");
        solicitudBase.setCantidadSolicitada(10);
    }

    // TEST PARA LISTAR SOLICITUDES
    
    @Test
    void testListarSolicitudes() {
        List<SolicitudReposicion> solicitudes = new ArrayList<>();
        solicitudes.add(solicitudBase);
        when(solicitudRepository.findAll()).thenReturn(solicitudes);

        List<SolicitudReposicion> resultado = solicitudService.listarSolicitudes();

        assertThat(resultado).hasSize(1);
        verify(solicitudRepository).findAll();
    }

    @Test
    void testListarSolicitudesVacio() {
        when(solicitudRepository.findAll()).thenReturn(new ArrayList<>());

        List<SolicitudReposicion> resultado = solicitudService.listarSolicitudes();

        assertThat(resultado).isEmpty();
        verify(solicitudRepository).findAll();
    }

    // TEST PARA BUSCAR POR ID

    @Test
    void testBuscarPorIdExistente() {
        when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitudBase));

        Optional<SolicitudReposicion> resultado = solicitudService.buscarPorId(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(1L);
        verify(solicitudRepository).findById(1L);
    }

    @Test
    void testBuscarPorIdInexistente() {
        when(solicitudRepository.findById(9999L)).thenReturn(Optional.empty());

        Optional<SolicitudReposicion> resultado = solicitudService.buscarPorId(9999L);

        assertThat(resultado).isEmpty();
        verify(solicitudRepository).findById(9999L);
    }

    // TEST PARA CREAR SOLICITUD solo son test por la integracion y edicion que hice antes

    @Test
    void testCrearSolicitud() {
        SolicitudReposicion nueva = new SolicitudReposicion();
        nueva.setCantidadSolicitada(15);

        when(solicitudRepository.save(any(SolicitudReposicion.class))).thenReturn(solicitudBase);

        SolicitudReposicion resultado = solicitudService.crearSolicitud(nueva);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getEstadoSolicitud()).isEqualTo("PENDIENTE");
        assertThat(resultado.getFechaSolicitud()).isNotNull();
        verify(solicitudRepository).save(any(SolicitudReposicion.class));
    }

    @Test
    void testCrearSolicitudConCantidadCero() {
        SolicitudReposicion nueva = new SolicitudReposicion();
        nueva.setCantidadSolicitada(0);

        when(solicitudRepository.save(any(SolicitudReposicion.class))).thenReturn(solicitudBase);

        SolicitudReposicion resultado = solicitudService.crearSolicitud(nueva);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getEstadoSolicitud()).isEqualTo("PENDIENTE");
        verify(solicitudRepository).save(any(SolicitudReposicion.class));
    }
}
