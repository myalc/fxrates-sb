package com.myalc.fxrates.service;

import com.myalc.fxrates.dto.api.PaginatedCalculationResponse;
import com.myalc.fxrates.model.Calculation;
import com.myalc.fxrates.model.Rate;

public interface IFxRatesService {
    
    public Rate getLatestExchangeRate(String sourceCurrency, String targetCurrency);

    public Calculation getCalculation(Double amount, String sourceCurrency, String targetCurrency);

    public PaginatedCalculationResponse getCalculations(String date, String transactionId, Integer page, Integer size);

    default Double calculate(Double amount, Double rate) {
        return amount * rate;
    }

}
