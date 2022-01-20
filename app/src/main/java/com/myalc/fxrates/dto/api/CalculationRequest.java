package com.myalc.fxrates.dto.api;

import java.io.Serializable;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.validation.annotation.Validated;

@Validated
public class CalculationRequest implements Serializable {

    @DecimalMin(value = "0")
    private Double amount; 

    @NotBlank
    @Size(min = 3, max=3)
    private String sourceCurrency;

    @NotBlank
    @Size(min = 3, max=3)
    private String targetCurrency;

    public CalculationRequest(Double amount, String sourceCurrency, String targetCurrency) {
        this.amount = amount;
        this.sourceCurrency = sourceCurrency;
        this.targetCurrency = targetCurrency;
    }

    public Double getAmount() {
        return this.amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getSourceCurrency() {
        return this.sourceCurrency;
    }

    public void setSourceCurrency(String sourceCurrency) {
        this.sourceCurrency = sourceCurrency;
    }

    public String getTargetCurrency() {
        return this.targetCurrency;
    }

    public void setTargetCurrency(String targetCurrency) {
        this.targetCurrency = targetCurrency;
    }

    @Override
    public String toString() {
        return "{" +
            " amount='" + getAmount() + "'" +
            ", sourceCurrency='" + getSourceCurrency() + "'" +
            ", targetCurrency='" + getTargetCurrency() + "'" +
            "}";
    }
    
}
