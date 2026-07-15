package com.bookpoint.logistic.service;

import com.bookpoint.logistic.client.SucursalClient;
import com.bookpoint.logistic.dto.SucursalDTO;
import com.bookpoint.logistic.model.Ruta;
import com.bookpoint.logistic.model.Transportista;
import com.bookpoint.logistic.repository.RutaRepository;
import com.bookpoint.logistic.repository.TransportistaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RutaServiceTest {

    @Mock
    private RutaRepository rutaRepository;

    @Mock
    private TransportistaRepository transportistaRepository;

    @Mock
    private SucursalClient sucursalClient;

    @InjectMocks
    private RutaService rutaService;

    private Transportista transportistaBase;
    private Ruta rutaBase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        transportistaBase = new Transportista();
        transportistaBase.setId(1L);
        transportistaBase.setNombre("Transportista Test");
        transportistaBase.setDisponible(true);

        rutaBase = new Ruta();
        rutaBase.setId(1L);
        rutaBase.setOrigen("Santiago");
        rutaBase.setDestino("Valparaíso");
        rutaBase.setEstado("PENDIENTE");
        rutaBase.setSucursalId(1L);
    }

    @Test
    void testAsignarRutaATransportistaExitoso() {
        SucursalDTO sucursalMock = new SucursalDTO();
        sucursalMock.setId(1L);
        sucursalMock.setNombre("Sucursal Test");
        when(sucursalClient.obtenerSucursalPorId(1L)).thenReturn(sucursalMock);
        
        when(transportistaRepository.findById(1L)).thenReturn(Optional.of(transportistaBase));
        when(rutaRepository.save(any(Ruta.class))).thenReturn(rutaBase);

        Ruta resultado = rutaService.asignarRutaATransportista(1L, rutaBase);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getTransportista()).isEqualTo(transportistaBase);
        verify(transportistaRepository).findById(1L);
        verify(rutaRepository).save(any(Ruta.class));
        verify(sucursalClient).obtenerSucursalPorId(1L);
    }

    @Test
    void testAsignarRutaATransportistaConSucursalInvalida() {
        when(sucursalClient.obtenerSucursalPorId(999L)).thenReturn(null);
        
        when(transportistaRepository.findById(1L)).thenReturn(Optional.of(transportistaBase));
        
        rutaBase.setSucursalId(999L);

        try {
            rutaService.asignarRutaATransportista(1L, rutaBase);
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).contains("Sucursal con ID 999 no existe");
        }
    }

    @Test
    void testEliminarRutaExitoso() {
        when(rutaRepository.existsById(1L)).thenReturn(true);
        doNothing().when(rutaRepository).deleteById(1L);
        
        boolean resultado = rutaService.eliminarRuta(1L);
        
        assertThat(resultado).isTrue();
        verify(rutaRepository).existsById(1L);
        verify(rutaRepository).deleteById(1L);
    }

    @Test
    void testEliminarRutaInexistente() {
        when(rutaRepository.existsById(9999L)).thenReturn(false);
        
        boolean resultado = rutaService.eliminarRuta(9999L);
        
        assertThat(resultado).isFalse();
        verify(rutaRepository).existsById(9999L);
        verify(rutaRepository, never()).deleteById(9999L);
    }
}