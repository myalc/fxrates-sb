package com.myalc.fxrates.repository.db;

import com.myalc.fxrates.model.LatestRates;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LatestRatesRepository extends JpaRepository<LatestRates, Long> {
}
