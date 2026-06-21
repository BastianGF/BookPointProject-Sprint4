package com.bookpoint.logistic.repository;

import com.bookpoint.logistic.model.Ruta;
import com.bookpoint.logistic.model.Transportista;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class RutaRepositoryIT {

    @Autowired
    private RutaRepository rutaRepository;

    @Autowired
    private TransportistaRepository transportistaRepository;

    @BeforeEach
    void cleanDb() {
        rutaRepository.deleteAll();
        transportistaRepository.deleteAll();
    }

    private Ruta crearRutaBase() {
        Ruta ruta = new Ruta();
        ruta.setOrigen("Santiago");
        ruta.setDestino("Valparaíso");
        ruta.setDescripcionRuta("Ruta costera");
        ruta.setEstado("PENDIENTE");
        ruta.setSucursalId(1L);
        return ruta;
    }

    private Transportista crearTransportistaBase() {
        return new Transportista(null, "Transportista Test", "12345678-9", "987654321", true);
    }

    @Test
    void testGuardarRuta() {
        Ruta ruta = crearRutaBase();
        Ruta guardado = rutaRepository.save(ruta);

        assertThat(guardado).isNotNull();
        assertThat(guardado.getId()).isNotNull();
        assertThat(guardado.getOrigen()).isEqualTo("Santiago");
        assertThat(guardado.getEstado()).isEqualTo("PENDIENTE");
    }

    @Test
    void testBuscarPorId() {
        Ruta ruta = crearRutaBase();
        Ruta guardado = rutaRepository.save(ruta);

        Optional<Ruta> encontrado = rutaRepository.findById(guardado.getId());

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getDestino()).isEqualTo("Valparaíso");
    }

    @Test
    void testBuscarPorIdInexistente() {
        Optional<Ruta> encontrado = rutaRepository.findById(9999L);
        assertThat(encontrado).isEmpty();
    }

    @Test
    void testListarTodos() {
        rutaRepository.save(crearRutaBase());
        rutaRepository.save(crearRutaBase());

        List<Ruta> todos = rutaRepository.findAll();

        assertThat(todos).hasSize(2);
    }

    @Test
    void testActualizar() {
        Ruta ruta = crearRutaBase();
        Ruta guardado = rutaRepository.save(ruta);

        guardado.setDestino("Concepción");
        Ruta actualizado = rutaRepository.save(guardado);

        assertThat(actualizado.getDestino()).isEqualTo("Concepción");
    }

    @Test
    void testEliminar() {
        Ruta ruta = crearRutaBase();
        Ruta guardado = rutaRepository.save(ruta);

        rutaRepository.deleteById(guardado.getId());
        Optional<Ruta> encontrado = rutaRepository.findById(guardado.getId());

        assertThat(encontrado).isEmpty();
    }

    @Test
    void testGuardarConTransportistaAsociado() {
        Transportista transportista = crearTransportistaBase();
        Transportista tGuardado = transportistaRepository.save(transportista);

        Ruta ruta = crearRutaBase();
        ruta.setTransportista(tGuardado);
        Ruta rGuardado = rutaRepository.save(ruta);

        assertThat(rGuardado.getTransportista()).isNotNull();
        assertThat(rGuardado.getTransportista().getId()).isEqualTo(tGuardado.getId());
    }

    @Test
    void testFindBySucursalId() {
        Ruta ruta1 = crearRutaBase();
        ruta1.setSucursalId(1L);
        rutaRepository.save(ruta1);

        Ruta ruta2 = crearRutaBase();
        ruta2.setSucursalId(2L);
        ruta2.setOrigen("Concepción");
        rutaRepository.save(ruta2);

        List<Ruta> rutasSucursal1 = rutaRepository.findBySucursalId(1L);

        assertThat(rutasSucursal1).hasSize(1);
        assertThat(rutasSucursal1.get(0).getOrigen()).isEqualTo("Santiago");
    }

    @Test
    void testCount() {
        rutaRepository.save(crearRutaBase());
        rutaRepository.save(crearRutaBase());

        long count = rutaRepository.count();

        assertThat(count).isEqualTo(2);
    }

    @Test
    void testExistById() {
        Ruta ruta = crearRutaBase();
        Ruta guardado = rutaRepository.save(ruta);

        boolean existe = rutaRepository.existsById(guardado.getId());
        boolean noExiste = rutaRepository.existsById(9999L);

        assertThat(existe).isTrue();
        assertThat(noExiste).isFalse();
    }
}
