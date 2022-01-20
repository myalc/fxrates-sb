package com.myalc.fxrates.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "T_RATE")
public class Rate implements Serializable {
    
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "sourceCurrency", length = 3, nullable = false)
    private String sourceCurrency;

    @Column(name = "targetCurrency", length = 3, nullable = false)
    private String targetCurrency;

    @Column(name = "sqlDate", nullable = false)
    private Date sqlDate;

    @Column(name = "sqlTimestamp", nullable = false)
    private Timestamp sqlTimestamp;

    @Column(name = "rate", nullable = false)
    private Double rate; 

    public Rate() {
    }

    public Rate(String sourceCurrency, String targetCurrency, Double rate, Date date, Timestamp timestamp) {
        this.sourceCurrency = sourceCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
        // current
        /*long millis = System.currentTimeMillis();  
        this.sqlDate = new Date(millis);
        this.sqlTimestamp = new Timestamp(millis);*/
        this.sqlDate = date;
        this.sqlTimestamp = timestamp;
    }

    public Long getId() {
        return this.id;
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

    public Double getRate() {
        return this.rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", sourceCurrency='" + getSourceCurrency() + "'" +
            ", targetCurrency='" + getTargetCurrency() + "'" +
            ", sqlDate='" + getSqlDate() + "'" +
            ", sqlTimestamp='" + getSqlTimestamp() + "'" +
            ", rate='" + getRate() + "'" +
            "}";
    }

}
