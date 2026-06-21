package com.bookpoint.supplier.repository;

import com.bookpoint.supplier.model.RecepcionMercaderia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecepcionMercaderiaRepository extends JpaRepository<RecepcionMercaderia, Long> {
}
