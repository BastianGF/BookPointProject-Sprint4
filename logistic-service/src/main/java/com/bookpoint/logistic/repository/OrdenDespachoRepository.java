package com.bookpoint.logistic.repository;

import com.bookpoint.logistic.model.OrdenDespacho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdenDespachoRepository extends JpaRepository<OrdenDespacho, Long> {
}
