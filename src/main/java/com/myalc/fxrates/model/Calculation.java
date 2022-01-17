package com.myalc.fxrates.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "T_CALCULATION")
public class Calculation implements Serializable {

    @Id
    @GeneratedValue
    private UUID transactionId; 

    @Column(name = "amount", nullable = false)
    private Double amount;
    
    @Column(name = "sourceCurrency", length = 3, nullable = false)
    private String sourceCurrency;

    @Column(name = "targetCurrency", length = 3, nullable = false)
    private String targetCurrency;

    @Column(name = "targetAmount", nullable = false)
    private Double targetAmount;

	@Column(name = "sqlDate", nullable = false)
    private Date sqlDate;
    
    @Column(name = "sqlTimestamp", nullable = false)
    private Timestamp sqlTimestamp;

    public Calculation() {
    }

    public Calculation(Double amount, String sourceCurrency, String targetCurrency, Double targetAmount, Date date, Timestamp timestamp) {
        this.amount = amount;
        this.sourceCurrency = sourceCurrency;
        this.targetCurrency = targetCurrency;
        this.targetAmount = targetAmount;
        
        this.sqlDate = date;
        this.sqlTimestamp = timestamp;
    }

    public UUID getTransactionId() {
        return this.transactionId;
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

    public Double getTargetAmount() {
        return this.targetAmount;
    }

    public void setTargetAmount(Double targetAmount) {
        this.targetAmount = targetAmount;
    }

    public Date getSqlDate() {
        return this.sqlDate;
    }

    public void setSqlDate(Date sqlDate) {
        this.sqlDate = sqlDate;
    }

    public Timestamp getSqlTimestamp() {
        return this.sqlTimestamp;
    }

    public void setSqlTimestamp(Timestamp sqlTimestamp) {
        this.sqlTimestamp = sqlTimestamp;
    }

    @Override
    public String toString() {
        return "{" +
            " transactionId='" + getTransactionId() + "'" +
            ", amount='" + getAmount() + "'" +
            ", sourceCurrency='" + getSourceCurrency() + "'" +
            ", targetCurrency='" + getTargetCurrency() + "'" +
            ", targetAmount='" + getTargetAmount() + "'" +
            ", sqlDate='" + getSqlDate() + "'" +
            ", sqlTimestamp='" + getSqlTimestamp() + "'" +
            "}";
    }
}
