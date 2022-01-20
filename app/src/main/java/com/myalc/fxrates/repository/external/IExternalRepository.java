package com.myalc.fxrates.repository.external;

import com.myalc.fxrates.model.LatestRates;

public interface IExternalRepository {
    
    public LatestRates getLatestExchangeRate(String sourceCurrency, String targetCurrency);

    public LatestRates getLatestExchangeRate(String sourceCurrency);

    // optional
    //public Calculation getCalculation(Double amount, String sourceCurrency, String targetCurrency);
}
