package com.bookpoint.logistic.repository;

import com.bookpoint.logistic.model.Traslado;
import com.bookpoint.logistic.model.Transportista;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class TrasladoRepositoryIT {

    @Autowired
    private TrasladoRepository trasladoRepository;

    @Autowired
    private TransportistaRepository transportistaRepository;

    @BeforeEach
    void cleanDb() {
        trasladoRepository.deleteAll();
        transportistaRepository.deleteAll();
    }

    private Traslado crearTrasladoBase() {
        Traslado traslado = new Traslado();
        traslado.setOrigenId(1L);
        traslado.setDestinoId(2L);
        traslado.setEstado("PENDIENTE");
        traslado.setFechaRegistro(new Date());
        traslado.setProductos("{\"producto\": \"libro\", \"cantidad\": 10}");
        traslado.setUbicacionActual("Bodega Central");
        return traslado;
    }

    @Test
    void testGuardarTraslado() {
        Traslado traslado = crearTrasladoBase();
        Traslado guardado = trasladoRepository.save(traslado);

        assertThat(guardado).isNotNull();
        assertThat(guardado.getId()).isNotNull();
        assertThat(guardado.getEstado()).isEqualTo("PENDIENTE");
    }

    @Test
    void testBuscarPorId() {
        Traslado traslado = crearTrasladoBase();
        Traslado guardado = trasladoRepository.save(traslado);

        Optional<Traslado> encontrado = trasladoRepository.findById(guardado.getId());

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getEstado()).isEqualTo("PENDIENTE");
    }

    @Test
    void testBuscarPorIdInexistente() {
        Optional<Traslado> encontrado = trasladoRepository.findById(9999L);
        assertThat(encontrado).isEmpty();
    }

    @Test
    void testListarTodos() {
        trasladoRepository.save(crearTrasladoBase());
        trasladoRepository.save(crearTrasladoBase());

        List<Traslado> todos = trasladoRepository.findAll();

        assertThat(todos).hasSize(2);
    }

    @Test
    void testActualizar() {
        Traslado traslado = crearTrasladoBase();
        Traslado guardado = trasladoRepository.save(traslado);

        guardado.setEstado("ASIGNADO");
        Traslado actualizado = trasladoRepository.save(guardado);

        assertThat(actualizado.getEstado()).isEqualTo("ASIGNADO");
    }

    @Test
    void testEliminar() {
        Traslado traslado = crearTrasladoBase();
        Traslado guardado = trasladoRepository.save(traslado);

        trasladoRepository.deleteById(guardado.getId());
        Optional<Traslado> encontrado = trasladoRepository.findById(guardado.getId());

        assertThat(encontrado).isEmpty();
    }

    @Test
    void testGuardarConTransportistaAsociado() {
        Transportista transportista = new Transportista(null, "Transportista Test", "12345678-9", "987654321", true);
        Transportista tGuardado = transportistaRepository.save(transportista);

        Traslado traslado = crearTrasladoBase();
        traslado.setTransportista(tGuardado);
        Traslado oGuardado = trasladoRepository.save(traslado);

        assertThat(oGuardado.getTransportista()).isNotNull();
        assertThat(oGuardado.getTransportista().getId()).isEqualTo(tGuardado.getId());
    }

    @Test
    void testCount() {
        trasladoRepository.save(crearTrasladoBase());
        trasladoRepository.save(crearTrasladoBase());

        long count = trasladoRepository.count();

        assertThat(count).isEqualTo(2);
    }

    @Test
    void testExistById() {
        Traslado traslado = crearTrasladoBase();
        Traslado guardado = trasladoRepository.save(traslado);

        boolean existe = trasladoRepository.existsById(guardado.getId());
        boolean noExiste = trasladoRepository.existsById(9999L);

        assertThat(existe).isTrue();
        assertThat(noExiste).isFalse();
    }
}
