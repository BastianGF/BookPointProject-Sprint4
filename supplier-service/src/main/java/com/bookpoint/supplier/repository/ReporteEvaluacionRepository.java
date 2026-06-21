package com.bookpoint.supplier.repository;

import com.bookpoint.supplier.model.ReporteEvaluacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReporteEvaluacionRepository extends JpaRepository<ReporteEvaluacion, Long> {
}
