package com.myalc.fxrates.repository.db;

import com.myalc.fxrates.model.Calculation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalculationRepository extends JpaRepository<Calculation, java.util.UUID> {
    Page<Calculation> findBySqlDate(java.sql.Date sqlDate, Pageable pageable);
    Page<Calculation> findByTransactionId(java.util.UUID transactionId, Pageable pageable);
}
