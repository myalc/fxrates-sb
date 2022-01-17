package com.myalc.fxrates.repository.db;

import com.myalc.fxrates.model.Rate;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RateRepository extends JpaRepository<Rate, Long> {
}
