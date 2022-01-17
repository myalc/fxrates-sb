package com.myalc.fxrates.dto.mapper;

import java.util.ArrayList;
import java.util.List;

import com.myalc.fxrates.dto.api.CalculationResponse;
import com.myalc.fxrates.dto.api.PaginatedCalculationResponse;
import com.myalc.fxrates.model.Calculation;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class PaginatedCalculationMapper {
    
    public PaginatedCalculationResponse mapToPaginatedCalculation(Page<Calculation> pages) {
        
        List<CalculationResponse> calcs = new ArrayList<>();
        for (Calculation c : pages.getContent()) {
            calcs.add(new CalculationResponse(c.getTargetAmount(), c.getTransactionId().toString(), c.getSourceCurrency(), c.getTargetCurrency()));
            //calcs.add(new CalculationResponse(c.getTargetAmount(), c.getTransactionId().toString()));
        }

        PaginatedCalculationResponse response = new PaginatedCalculationResponse(calcs, pages.getNumber(), pages.getTotalElements(), pages.getTotalPages());
        return response;
    }
}
