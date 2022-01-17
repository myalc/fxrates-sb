package com.myalc.fxrates.model.mapper;

import com.myalc.fxrates.model.Calculation;
import com.myalc.fxrates.model.Rate;

import org.springframework.stereotype.Component;

@Component
public class CalculationMapper {
    
    public Calculation mapTCalculation(Rate rate, Double amount, Double targetAmount) {
        return new Calculation(amount, rate.getSourceCurrency(), rate.getTargetCurrency(), targetAmount, rate.getSqlDate(), rate.getSqlTimestamp());
    }
    
}
