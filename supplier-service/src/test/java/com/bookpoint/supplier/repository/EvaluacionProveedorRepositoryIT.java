package com.bookpoint.supplier.repository;

import com.bookpoint.supplier.model.EvaluacionProveedor;
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
class EvaluacionProveedorRepositoryIT {

    @Autowired
    private EvaluacionProveedorRepository evaluacionRepository;

    @BeforeEach
    void cleanDb() {
        evaluacionRepository.deleteAll();
    }

    private EvaluacionProveedor crearEvaluacionBase() {
        EvaluacionProveedor evaluacion = new EvaluacionProveedor();
        evaluacion.setFechaEvaluacion(new Date());
        evaluacion.setPuntaje(85);
        evaluacion.setObservacionEvaluacion("Evaluación de prueba");
        return evaluacion;
    }

    @Test
    void testGuardarEvaluacion() {
        EvaluacionProveedor evaluacion = crearEvaluacionBase();
        EvaluacionProveedor guardado = evaluacionRepository.save(evaluacion);

        assertThat(guardado).isNotNull();
        assertThat(guardado.getId()).isNotNull();
        assertThat(guardado.getPuntaje()).isEqualTo(85);
        assertThat(guardado.getObservacionEvaluacion()).isEqualTo("Evaluación de prueba");
    }

    @Test
    void testBuscarPorId() {
        EvaluacionProveedor evaluacion = crearEvaluacionBase();
        EvaluacionProveedor guardado = evaluacionRepository.save(evaluacion);

        Optional<EvaluacionProveedor> encontrado = evaluacionRepository.findById(guardado.getId());

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getPuntaje()).isEqualTo(85);
    }

    @Test
    void testBuscarPorIdInexistente() {
        Optional<EvaluacionProveedor> encontrado = evaluacionRepository.findById(9999L);
        assertThat(encontrado).isEmpty();
    }

    @Test
    void testListarTodos() {
        evaluacionRepository.save(crearEvaluacionBase());
        evaluacionRepository.save(crearEvaluacionBase());

        List<EvaluacionProveedor> todos = evaluacionRepository.findAll();

        assertThat(todos).hasSize(2);
    }

    @Test
    void testActualizar() {
        EvaluacionProveedor evaluacion = crearEvaluacionBase();
        EvaluacionProveedor guardado = evaluacionRepository.save(evaluacion);

        guardado.setPuntaje(95);
        guardado.setObservacionEvaluacion("Actualizado");
        EvaluacionProveedor actualizado = evaluacionRepository.save(guardado);

        assertThat(actualizado.getPuntaje()).isEqualTo(95);
        assertThat(actualizado.getObservacionEvaluacion()).isEqualTo("Actualizado");
    }

    @Test
    void testEliminar() {
        EvaluacionProveedor evaluacion = crearEvaluacionBase();
        EvaluacionProveedor guardado = evaluacionRepository.save(evaluacion);

        evaluacionRepository.deleteById(guardado.getId());
        Optional<EvaluacionProveedor> encontrado = evaluacionRepository.findById(guardado.getId());

        assertThat(encontrado).isEmpty();
    }

    @Test
    void testCount() {
        evaluacionRepository.save(crearEvaluacionBase());
        evaluacionRepository.save(crearEvaluacionBase());

        long count = evaluacionRepository.count();

        assertThat(count).isEqualTo(2);
    }

    @Test
    void testExistsById() {
        EvaluacionProveedor evaluacion = crearEvaluacionBase();
        EvaluacionProveedor guardado = evaluacionRepository.save(evaluacion);

        boolean existe = evaluacionRepository.existsById(guardado.getId());
        boolean noExiste = evaluacionRepository.existsById(9999L);

        assertThat(existe).isTrue();
        assertThat(noExiste).isFalse();
    }
}
