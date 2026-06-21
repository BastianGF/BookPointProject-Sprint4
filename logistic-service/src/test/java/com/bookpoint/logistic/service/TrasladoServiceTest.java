package com.bookpoint.logistic.service;

import com.bookpoint.logistic.model.Traslado;
import com.bookpoint.logistic.model.Transportista;
import com.bookpoint.logistic.repository.TrasladoRepository;
import com.bookpoint.logistic.repository.TransportistaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TrasladoServiceTest {

    @Mock
    private TrasladoRepository trasladoRepository;

    @Mock
    private TransportistaRepository transportistaRepository;

    @InjectMocks
    private TrasladoService trasladoService;

    private Traslado trasladoBase;
    private Transportista transportistaBase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        trasladoBase = new Traslado();
        trasladoBase.setId(1L);
        trasladoBase.setOrigenId(1L);
        trasladoBase.setDestinoId(2L);
        trasladoBase.setEstado("PENDIENTE");
        trasladoBase.setFechaRegistro(new Date());
        trasladoBase.setProductos("{\"producto\": \"test\"}");

        transportistaBase = new Transportista();
        transportistaBase.setId(1L);
        transportistaBase.setNombre("Transportista Test");
        transportistaBase.setDisponible(true);
    }

    // TEST PARA REGISTRAR TRASLADO
    @Test
    void testRegistrarTraslado() {
        when(trasladoRepository.save(any(Traslado.class))).thenReturn(trasladoBase);

        Traslado resultado = trasladoService.registrarTraslado(trasladoBase);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getEstado()).isEqualTo("PENDIENTE");
        assertThat(resultado.getFechaRegistro()).isNotNull();
        verify(trasladoRepository).save(any(Traslado.class));
    }

    // TEST PARA ASIGNAR TRANSPORTISTA 
    @Test
    void testAsignarTransportistaExitoso() {
        when(trasladoRepository.findById(1L)).thenReturn(Optional.of(trasladoBase));
        when(transportistaRepository.findById(1L)).thenReturn(Optional.of(transportistaBase));
        when(trasladoRepository.save(any(Traslado.class))).thenReturn(trasladoBase);

        Traslado resultado = trasladoService.asignarTransportista(1L, 1L);

        assertThat(resultado.getEstado()).isEqualTo("ASIGNADO");
        assertThat(resultado.getTransportista()).isEqualTo(transportistaBase);
        verify(trasladoRepository).save(any(Traslado.class));
    }

    @Test
    void testAsignarTransportistaTrasladoNoExiste() {
        when(trasladoRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trasladoService.asignarTransportista(9999L, 1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Traslado con ID 9999 no existe");
    }

    @Test
    void testAsignarTransportistaEstadoInvalido() {
        trasladoBase.setEstado("ASIGNADO"); // Estado incorrecto
        when(trasladoRepository.findById(1L)).thenReturn(Optional.of(trasladoBase));

        assertThatThrownBy(() -> trasladoService.asignarTransportista(1L, 1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Traslado no en estado PENDIENTE");
    }

    @Test
    void testAsignarTransportistaTransportistaNoExiste() {
        when(trasladoRepository.findById(1L)).thenReturn(Optional.of(trasladoBase));
        when(transportistaRepository.findById(9999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> trasladoService.asignarTransportista(1L, 9999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Transportista con ID 9999 no existe");
    }

    @Test
    void testAsignarTransportistaTransportistaNoDisponible() {
        transportistaBase.setDisponible(false);
        when(trasladoRepository.findById(1L)).thenReturn(Optional.of(trasladoBase));
        when(transportistaRepository.findById(1L)).thenReturn(Optional.of(transportistaBase));
        assertThatThrownBy(() -> trasladoService.asignarTransportista(1L, 1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Transportista no disponible");
    }

    // TEST PARA CONFIRMAR SALIDA
    @Test
    void testConfirmarSalidaExitoso() {
        trasladoBase.setEstado("ASIGNADO");
        when(trasladoRepository.findById(1L)).thenReturn(Optional.of(trasladoBase));
        when(trasladoRepository.save(any(Traslado.class))).thenReturn(trasladoBase);

        Traslado resultado = trasladoService.confirmarSalida(1L);

        assertThat(resultado.getEstado()).isEqualTo("EN_TRANSITO");
        assertThat(resultado.getUbicacionActual()).contains("En tránsito");
        assertThat(resultado.getFechaActualizacion()).isNotNull();
        verify(trasladoRepository).save(any(Traslado.class));
    }

    @Test
    void testConfirmarSalidaTrasladoNoExiste() {
        when(trasladoRepository.findById(9999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> trasladoService.confirmarSalida(9999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Traslado con ID 9999 no existe");
    }

    @Test
    void testConfirmarSalidaEstadoInvalido() {
        trasladoBase.setEstado("PENDIENTE"); // Estado incorrecto
        when(trasladoRepository.findById(1L)).thenReturn(Optional.of(trasladoBase));
        assertThatThrownBy(() -> trasladoService.confirmarSalida(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Traslado no en estado ASIGNADO");
    }

    // TEST PARA CONFIRMAR RECEPCION
    @Test
    void testConfirmarRecepcionExitoso() {
        trasladoBase.setEstado("EN_TRANSITO");
        when(trasladoRepository.findById(1L)).thenReturn(Optional.of(trasladoBase));
        when(trasladoRepository.save(any(Traslado.class))).thenReturn(trasladoBase);

        Traslado resultado = trasladoService.confirmarRecepcion(1L, "Recepción exitosa");

        assertThat(resultado.getEstado()).isEqualTo("ENTREGADO");
        assertThat(resultado.getObservaciones()).isEqualTo("Recepción exitosa");
        assertThat(resultado.getFechaActualizacion()).isNotNull();
        verify(trasladoRepository).save(any(Traslado.class));
    }

    @Test
    void testConfirmarRecepcionTrasladoNoExiste() {
        when(trasladoRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trasladoService.confirmarRecepcion(9999L, "Observaciones"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Traslado con ID 9999 no existe");
    }

    @Test
    void testConfirmarRecepcionEstadoInvalido() {
        trasladoBase.setEstado("PENDIENTE"); // Estado incorrecto
        when(trasladoRepository.findById(1L)).thenReturn(Optional.of(trasladoBase));

        assertThatThrownBy(() -> trasladoService.confirmarRecepcion(1L, "Observaciones"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Traslado no en estado EN_TRANSITO");
    }

    // PARA TEST DE OBTENER POR ID 
    @Test
    void testObtenerPorIdExistente() {
        when(trasladoRepository.findById(1L)).thenReturn(Optional.of(trasladoBase));

        Traslado resultado = trasladoService.obtenerPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        verify(trasladoRepository).findById(1L);
    }

    @Test
    void testObtenerPorIdInexistente() {
        when(trasladoRepository.findById(9999L)).thenReturn(Optional.empty());

        Traslado resultado = trasladoService.obtenerPorId(9999L);

        assertThat(resultado).isNull();
        verify(trasladoRepository).findById(9999L);
    }
}
