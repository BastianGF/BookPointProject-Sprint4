package com.bookpoint.supplier.repository;

import com.bookpoint.supplier.model.RecepcionMercaderia;
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
class RecepcionMercaderiaRepositoryIT {

    @Autowired
    private RecepcionMercaderiaRepository recepcionRepository;

    @BeforeEach
    void cleanDb() {
        recepcionRepository.deleteAll();
    }

    private RecepcionMercaderia crearRecepcionBase() {
        RecepcionMercaderia recepcion = new RecepcionMercaderia();
        recepcion.setFechaRecepcion(new Date());
        recepcion.setCantidadRecibida(50);
        recepcion.setObservacionRecepcion("Recepción de prueba");
        return recepcion;
    }

    @Test
    void testGuardarRecepcion() {
        RecepcionMercaderia recepcion = crearRecepcionBase();
        RecepcionMercaderia guardado = recepcionRepository.save(recepcion);

        assertThat(guardado).isNotNull();
        assertThat(guardado.getId()).isNotNull();
        assertThat(guardado.getCantidadRecibida()).isEqualTo(50);
        assertThat(guardado.getObservacionRecepcion()).isEqualTo("Recepción de prueba");
    }

    @Test
    void testBuscarPorId() {
        RecepcionMercaderia recepcion = crearRecepcionBase();
        RecepcionMercaderia guardado = recepcionRepository.save(recepcion);

        Optional<RecepcionMercaderia> encontrado = recepcionRepository.findById(guardado.getId());

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getCantidadRecibida()).isEqualTo(50);
    }

    @Test
    void testBuscarPorIdInexistente() {
        Optional<RecepcionMercaderia> encontrado = recepcionRepository.findById(9999L);
        assertThat(encontrado).isEmpty();
    }

    @Test
    void testListarTodos() {
        recepcionRepository.save(crearRecepcionBase());
        recepcionRepository.save(crearRecepcionBase());

        List<RecepcionMercaderia> todos = recepcionRepository.findAll();

        assertThat(todos).hasSize(2);
    }

    @Test
    void testActualizar() {
        RecepcionMercaderia recepcion = crearRecepcionBase();
        RecepcionMercaderia guardado = recepcionRepository.save(recepcion);

        guardado.setCantidadRecibida(100);
        guardado.setObservacionRecepcion("Actualizado");
        RecepcionMercaderia actualizado = recepcionRepository.save(guardado);

        assertThat(actualizado.getCantidadRecibida()).isEqualTo(100);
        assertThat(actualizado.getObservacionRecepcion()).isEqualTo("Actualizado");
    }

    @Test
    void testEliminar() {
        RecepcionMercaderia recepcion = crearRecepcionBase();
        RecepcionMercaderia guardado = recepcionRepository.save(recepcion);

        recepcionRepository.deleteById(guardado.getId());
        Optional<RecepcionMercaderia> encontrado = recepcionRepository.findById(guardado.getId());

        assertThat(encontrado).isEmpty();
    }

    @Test
    void testCount() {
        recepcionRepository.save(crearRecepcionBase());
        recepcionRepository.save(crearRecepcionBase());

        long count = recepcionRepository.count();

        assertThat(count).isEqualTo(2);
    }

    @Test
    void testExistsById() {
        RecepcionMercaderia recepcion = crearRecepcionBase();
        RecepcionMercaderia guardado = recepcionRepository.save(recepcion);

        boolean existe = recepcionRepository.existsById(guardado.getId());
        boolean noExiste = recepcionRepository.existsById(9999L);

        assertThat(existe).isTrue();
        assertThat(noExiste).isFalse();
    }
}
