package com.myalc.fxrates.model.mapper;

import java.util.Map;

import com.myalc.fxrates.exception.CustomException;
import com.myalc.fxrates.model.LatestRates;
import com.myalc.fxrates.model.Rate;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class RateMapper {
    
    public Rate mapToRate(LatestRates latestRates, String targetCurrency) {
        for (Map.Entry<String, Double> entry : latestRates.getRates().entrySet()) {
            if (targetCurrency.equals(entry.getKey())) {
                Rate rate = new Rate(latestRates.getBase(), targetCurrency, entry.getValue(), latestRates.getDate(), latestRates.getTimestamp());
                return rate;
            }
        }
        throw new CustomException(HttpStatus.NOT_FOUND, "target currency {} cannot find", targetCurrency);
    }

}
