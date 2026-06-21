package com.bookpoint.logistic.service;

import com.bookpoint.logistic.model.Transportista;
import com.bookpoint.logistic.repository.TransportistaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import jakarta.persistence.EntityNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TransportistaServiceTest {

    @Mock
    private TransportistaRepository transportistaRepository;

    @InjectMocks
    private TransportistaService transportistaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // TEST 1: Para guardar transportista válido
    @Test
    void testGuardarTransportista() {
        Transportista transportista = new Transportista();
        transportista.setNombre("Juan Pérez");
        transportista.setRut("12345678-9");
        transportista.setTelefono("987654321");
        transportista.setDisponible(true);

        Transportista transportistaGuardado = new Transportista();
        transportistaGuardado.setId(1L);
        transportistaGuardado.setNombre("Juan Pérez");
        transportistaGuardado.setRut("12345678-9");
        transportistaGuardado.setTelefono("987654321");
        transportistaGuardado.setDisponible(true);

        when(transportistaRepository.save(any(Transportista.class))).thenReturn(transportistaGuardado);

        Transportista resultado = transportistaService.registerTransportista(transportista);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNombre()).isEqualTo("Juan Pérez");
        verify(transportistaRepository).save(any(Transportista.class));
    }

    // TEST 2: Para obtener transportista por ID existente y validar disponibilidad (Fusión Test 2 y 12)
    @Test
    void testObtenerTransportistaPorIdExistente() {
        Transportista transportista = new Transportista();
        transportista.setId(1L);
        transportista.setNombre("Carlos Rodríguez");
        transportista.setDisponible(true);

        when(transportistaRepository.findById(1L)).thenReturn(Optional.of(transportista));

        Transportista resultado = transportistaService.obtainPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getDisponible()).isTrue();
        verify(transportistaRepository).findById(1L);
    }

    // TEST 3: Para obbtener transportista por ID inexistente
    @Test
    void testObtenerTransportistaPorIdInexistente() {
        when(transportistaRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transportistaService.obtainPorId(9999L))
            .isInstanceOf(EntityNotFoundException.class);
            
        verify(transportistaRepository).findById(9999L);
    }

    // TEST 4: Para listar transportistas
    @Test
    void testListarTransportistas() {
        List<Transportista> transportistas = new ArrayList<>();
        transportistas.add(new Transportista(1L, "Juan", "22967384-4", "777777777", true));
        when(transportistaRepository.findAll()).thenReturn(transportistas);

        List<Transportista> resultado = transportistaService.listarTransportistas();
        assertThat(resultado).hasSize(1);
        verify(transportistaRepository).findAll();
    }

    // TEST 5: Para actualizar transportista válido
    @Test
    void testActualizarTransportista() {
        Long id = 1L;
        Transportista transportistaExistente = new Transportista();
        transportistaExistente.setId(id);
        transportistaExistente.setNombre("Juan Antiguo");

        Transportista transportistaActualizado = new Transportista();
        transportistaActualizado.setNombre("Juan Nuevo");

        when(transportistaRepository.findById(id)).thenReturn(Optional.of(transportistaExistente));
        when(transportistaRepository.save(any(Transportista.class))).thenAnswer(i -> i.getArgument(0));

        Transportista resultado = transportistaService.updateTransportista(id, transportistaActualizado);

        assertThat(resultado.getNombre()).isEqualTo("Juan Nuevo");
        verify(transportistaRepository).save(any(Transportista.class));
    }

    // TEST 6: Para ctualizar transportista que no existe
    @Test
    void testActualizarTransportistaInexistente() {
        when(transportistaRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transportistaService.updateTransportista(9999L, new Transportista()))
            .isInstanceOf(EntityNotFoundException.class);
            
        verify(transportistaRepository, never()).save(any(Transportista.class));
    }

    // TEST 7: Para eliminar transportista ahora si con borrado logico
    @Test
    void testEliminarTransportistaLogico() {
        Long id = 1L;
        Transportista transportista = new Transportista();
        transportista.setId(id);
        transportista.setDisponible(true);

        when(transportistaRepository.findById(id)).thenReturn(Optional.of(transportista));
        when(transportistaRepository.save(any(Transportista.class))).thenAnswer(i -> i.getArgument(0));

        transportistaService.deleteTransportista(id);

        assertThat(transportista.getDisponible()).isFalse();
        verify(transportistaRepository).findById(id);
        verify(transportistaRepository).save(transportista);
    }

    // TEST 8: Para listar transportistas disponibles
    @Test
    void testListarTransportistasDisponibles() {
        List<Transportista> disponibles = new ArrayList<>();
        disponibles.add(new Transportista(1L, "Disponible1", "11362745-8", "888888888", true));
        
        when(transportistaRepository.findByDisponible(true)).thenReturn(disponibles);

        List<Transportista> resultado = transportistaService.listTransportistasDisponibles();
        assertThat(resultado).isNotEmpty();
        verify(transportistaRepository).findByDisponible(true);
    }
}