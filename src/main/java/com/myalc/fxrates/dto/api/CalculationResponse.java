package com.myalc.fxrates.dto.api;

import java.io.Serializable;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class CalculationResponse implements Serializable {
    
    @NotBlank
    @DecimalMin(value = "0")
    private Double targetAmount;

    @NotBlank
    @Size(min = 10, max=50)
    private String transactionId; 

    @Size(min = 3, max=3)
    private String sourceCurrency;

    @Size(min = 3, max=3)
    private String targetCurrency;

    public CalculationResponse() {
    }

    public CalculationResponse(Double targetAmount, String transactionId) {
        this.targetAmount = targetAmount;
        this.transactionId = transactionId;
    }

    public CalculationResponse(Double targetAmount, String transactionId, String sourceCurrency, String targetCurrency) {
        this.targetAmount = targetAmount;
        this.transactionId = transactionId;
        this.sourceCurrency = sourceCurrency;
        this.targetCurrency = targetCurrency;
    }

    public Double getTargetAmount() {
        return this.targetAmount;
    }

    public void setTargetAmount(Double targetAmount) {
        this.targetAmount = targetAmount;
    }

    public String getTransactionId() {
        return this.transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
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
            " targetAmount='" + getTargetAmount() + "'" +
            ", transactionId='" + getTransactionId() + "'" +
            "}";
    }

}
