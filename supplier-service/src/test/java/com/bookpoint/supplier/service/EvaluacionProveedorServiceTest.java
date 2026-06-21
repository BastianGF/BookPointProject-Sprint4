package com.bookpoint.supplier.service;

import com.bookpoint.supplier.model.EvaluacionProveedor;
import com.bookpoint.supplier.model.ReporteEvaluacion;
import com.bookpoint.supplier.repository.EvaluacionProveedorRepository;
import com.bookpoint.supplier.repository.ReporteEvaluacionRepository;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class EvaluacionProveedorServiceTest {

    @Mock
    private EvaluacionProveedorRepository evaluacionRepository;

    @Mock
    private ReporteEvaluacionRepository reporteRepository;

    @InjectMocks
    private EvaluacionProveedorService evaluacionService;

    private EvaluacionProveedor evaluacionBase;
    private ReporteEvaluacion reporteBase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        evaluacionBase = new EvaluacionProveedor();
        evaluacionBase.setId(1L);
        evaluacionBase.setFechaEvaluacion(new Date());
        evaluacionBase.setPuntaje(85);
        evaluacionBase.setObservacionEvaluacion("Evaluación de prueba");

        reporteBase = new ReporteEvaluacion();
        reporteBase.setId(1L);
        reporteBase.setFechaReporte(new Date());
        reporteBase.setContenidoReporte("Reporte de prueba");
        reporteBase.setEvaluacionProveedor(evaluacionBase);
    }

    // TESTS PARA PROBAR EVALUACIONPROVEEDOR

    @Test
    void testListarEvaluaciones() {
        List<EvaluacionProveedor> evaluaciones = new ArrayList<>();
        evaluaciones.add(evaluacionBase);
        when(evaluacionRepository.findAll()).thenReturn(evaluaciones);

        List<EvaluacionProveedor> resultado = evaluacionService.listarEvaluaciones();

        assertThat(resultado).hasSize(1);
        verify(evaluacionRepository).findAll();
    }

    @Test
    void testListarEvaluacionesVacio() {
        when(evaluacionRepository.findAll()).thenReturn(new ArrayList<>());

        List<EvaluacionProveedor> resultado = evaluacionService.listarEvaluaciones();

        assertThat(resultado).isEmpty();
        verify(evaluacionRepository).findAll();
    }

    @Test
    void testRegistrarEvaluacion() {
        EvaluacionProveedor nueva = new EvaluacionProveedor();
        nueva.setPuntaje(90);
        nueva.setObservacionEvaluacion("Nueva evaluación");

        when(evaluacionRepository.save(any(EvaluacionProveedor.class))).thenReturn(evaluacionBase);

        EvaluacionProveedor resultado = evaluacionService.registrarEvaluacion(nueva);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getFechaEvaluacion()).isNotNull();
        verify(evaluacionRepository).save(any(EvaluacionProveedor.class));
    }

    @Test
    void testBuscarEvaluacionPorIdExistente() {
        when(evaluacionRepository.findById(1L)).thenReturn(Optional.of(evaluacionBase));

        EvaluacionProveedor resultado = evaluacionService.buscarEvaluacionPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        verify(evaluacionRepository).findById(1L);
    }

    @Test
    void testBuscarEvaluacionPorIdInexistente() {
        when(evaluacionRepository.findById(9999L)).thenReturn(Optional.empty());

        EvaluacionProveedor resultado = evaluacionService.buscarEvaluacionPorId(9999L);

        assertThat(resultado).isNull();
        verify(evaluacionRepository).findById(9999L);
    }

    @Test
    void testListarReportesEvaluacion() {
        List<ReporteEvaluacion> reportes = new ArrayList<>();
        reportes.add(reporteBase);
        when(reporteRepository.findAll()).thenReturn(reportes);

        List<ReporteEvaluacion> resultado = evaluacionService.listarReportesEvaluacion();

        assertThat(resultado).hasSize(1);
        verify(reporteRepository).findAll();
    }

    @Test
    void testListarReportesEvaluacionVacio() {
        when(reporteRepository.findAll()).thenReturn(new ArrayList<>());

        List<ReporteEvaluacion> resultado = evaluacionService.listarReportesEvaluacion();

        assertThat(resultado).isEmpty();
        verify(reporteRepository).findAll();
    }

    @Test
    void testGenerarReporteEvaluacion() {
        when(reporteRepository.save(any(ReporteEvaluacion.class))).thenReturn(reporteBase);

        ReporteEvaluacion resultado = evaluacionService.generarReporteEvaluacion(reporteBase);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getFechaReporte()).isNotNull();
        verify(reporteRepository).save(any(ReporteEvaluacion.class));
    }

    @Test
    void testGenerarReporteParaEvaluacionExitoso() {
        when(evaluacionRepository.findById(1L)).thenReturn(Optional.of(evaluacionBase));
        when(reporteRepository.save(any(ReporteEvaluacion.class))).thenReturn(reporteBase);

        ReporteEvaluacion resultado = evaluacionService.generarReporteParaEvaluacion(1L, reporteBase);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getEvaluacionProveedor()).isEqualTo(evaluacionBase);
        assertThat(resultado.getFechaReporte()).isNotNull();
        verify(evaluacionRepository).findById(1L);
        verify(reporteRepository).save(any(ReporteEvaluacion.class));
    }

    @Test
    void testGenerarReporteParaEvaluacionInexistente() {
        when(evaluacionRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> evaluacionService.generarReporteParaEvaluacion(9999L, reporteBase))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Evaluación con ID 9999 no existe");

        verify(evaluacionRepository).findById(9999L);
        verify(reporteRepository, never()).save(any(ReporteEvaluacion.class));
    }
}
