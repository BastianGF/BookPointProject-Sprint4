package com.bookpoint.logistic.service;

import com.bookpoint.logistic.model.Ruta;
import com.bookpoint.logistic.model.Transportista;
import com.bookpoint.logistic.repository.RutaRepository;
import com.bookpoint.logistic.repository.TransportistaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RutaServiceTest {

    @Mock
    private RutaRepository rutaRepository;

    @Mock
    private TransportistaRepository transportistaRepository;

    @InjectMocks
    private RutaService rutaService;

    private Ruta rutaBase;
    private Transportista transportistaBase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        rutaBase = new Ruta();
        rutaBase.setId(1L);
        rutaBase.setOrigen("Santiago");
        rutaBase.setDestino("Valparaíso");
        rutaBase.setDescripcionRuta("Ruta costera");
        rutaBase.setEstado("PENDIENTE");
        rutaBase.setSucursalId(1L);

        transportistaBase = new Transportista();
        transportistaBase.setId(1L);
        transportistaBase.setNombre("Transportista Test");
        transportistaBase.setDisponible(true);
    }

    // TEST PARA LISTAR RUTAS
    @Test
    void testListarRutas() {
        List<Ruta> rutas = new ArrayList<>();
        rutas.add(rutaBase);
        when(rutaRepository.findAll()).thenReturn(rutas);

        List<Ruta> resultado = rutaService.listarRutas();

        assertThat(resultado).hasSize(1);
        verify(rutaRepository).findAll();
    }

    @Test
    void testListarRutasVacio() {
        when(rutaRepository.findAll()).thenReturn(new ArrayList<>());

        List<Ruta> resultado = rutaService.listarRutas();

        assertThat(resultado).isEmpty();
        verify(rutaRepository).findAll();
    }

    // TEST PARA BUSCAR POR ID
    @Test
    void testBuscarPorIdExistente() {
        when(rutaRepository.findById(1L)).thenReturn(Optional.of(rutaBase));

        Optional<Ruta> resultado = rutaService.buscarPorId(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(1L);
        verify(rutaRepository).findById(1L);
    }

    @Test
    void testBuscarPorIdInexistente() {
        when(rutaRepository.findById(9999L)).thenReturn(Optional.empty());

        Optional<Ruta> resultado = rutaService.buscarPorId(9999L);

        assertThat(resultado).isEmpty();
        verify(rutaRepository).findById(9999L);
    }

    // TEST PARA ASIGNAR RUTA A TRANSPORTISTA
    @Test
    void testAsignarRutaATransportistaExitoso() {
        when(transportistaRepository.findById(1L)).thenReturn(Optional.of(transportistaBase));
        when(rutaRepository.save(any(Ruta.class))).thenReturn(rutaBase);

        Ruta resultado = rutaService.asignarRutaATransportista(1L, rutaBase);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getTransportista()).isEqualTo(transportistaBase);
        verify(transportistaRepository).findById(1L);
        verify(rutaRepository).save(any(Ruta.class));
    }

    @Test
    void testAsignarRutaATransportistaNoExiste() {
        when(transportistaRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> rutaService.asignarRutaATransportista(9999L, rutaBase))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Transportista con ID 9999 no existe");

        verify(transportistaRepository).findById(9999L);
        verify(rutaRepository, never()).save(any(Ruta.class));
    }

    // TEST PARA ELIMINAR RUTA
    @Test
    void testEliminarRutaExistente() {
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
        verify(rutaRepository, never()).deleteById(anyLong());
    }

    // TEST PARA ACTUALIZAR RUTA
    @Test
    void testActualizarRutaExitoso() {
        Ruta datosNuevos = new Ruta();
        datosNuevos.setOrigen("Rancagua");
        datosNuevos.setDestino("San Fernando");

        when(rutaRepository.findById(1L)).thenReturn(Optional.of(rutaBase));
        when(rutaRepository.save(any(Ruta.class))).thenReturn(rutaBase);

        Ruta resultado = rutaService.actualizarRuta(1L, datosNuevos);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getOrigen()).isEqualTo("Rancagua");
        assertThat(resultado.getDestino()).isEqualTo("San Fernando");
        verify(rutaRepository).findById(1L);
        verify(rutaRepository).save(any(Ruta.class));
    }

    @Test
    void testActualizarRutaNoExiste() {
        when(rutaRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> rutaService.actualizarRuta(9999L, new Ruta()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ruta no existe");

        verify(rutaRepository).findById(9999L);
        verify(rutaRepository, never()).save(any(Ruta.class));
    }

    @Test
    void testActualizarRutaEstadoNoPendiente() {
        rutaBase.setEstado("ACTIVA");
        when(rutaRepository.findById(1L)).thenReturn(Optional.of(rutaBase));

        assertThatThrownBy(() -> rutaService.actualizarRuta(1L, new Ruta()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ruta solo editable en estado PENDIENTE");

        verify(rutaRepository).findById(1L);
        verify(rutaRepository, never()).save(any(Ruta.class));
    }

    // TEST PARA LISTAR POR SUCURSAL
    @Test
    void testListarPorSucursal() {
        List<Ruta> rutas = new ArrayList<>();
        rutas.add(rutaBase);
        when(rutaRepository.findBySucursalId(1L)).thenReturn(rutas);

        List<Ruta> resultado = rutaService.listarPorSucursal(1L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getSucursalId()).isEqualTo(1L);
        verify(rutaRepository).findBySucursalId(1L);
    }

    @Test
    void testListarPorSucursalVacio() {
        when(rutaRepository.findBySucursalId(9999L)).thenReturn(new ArrayList<>());

        List<Ruta> resultado = rutaService.listarPorSucursal(9999L);

        assertThat(resultado).isEmpty();
        verify(rutaRepository).findBySucursalId(9999L);
    }
}
