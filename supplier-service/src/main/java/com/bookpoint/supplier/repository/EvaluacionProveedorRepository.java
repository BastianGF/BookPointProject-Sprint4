package com.bookpoint.supplier.repository;

import com.bookpoint.supplier.model.EvaluacionProveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EvaluacionProveedorRepository extends JpaRepository<EvaluacionProveedor, Long> {
}
