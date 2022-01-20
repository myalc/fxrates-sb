package com.myalc.fxrates.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "T_LATESTRATES")
public class LatestRates implements Serializable {
    
    @Id
    @GeneratedValue
    private Long id;
    
    @Column(name = "provider", length = 50, nullable = false)
    private String provider;

    @Column(name = "success", nullable = false)
    private Boolean success;
    
    @Column(name = "base", length = 3, nullable = false)
    private String base;

    @Column(name = "timestamp", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    private Timestamp timestamp;
    
    @Column(name = "date", nullable = false)
    private Date date;

    @ElementCollection
    @CollectionTable(name = "T_LATESTRATES_RATES")
    @Column(name = "rates", nullable = false)
    private Map<String, Double> rates;
        
    public LatestRates() {
    }

    public LatestRates(String provider, Boolean success, Long timestamp, String base, String date, Map<String,Double> rates) {
        this.provider = provider;
        this.success = success;
        this.base = base;
        this.rates = rates;
        // date and timestamp
        this.date = java.sql.Date.valueOf(date);
        this.timestamp = new Timestamp(timestamp);
    }

    public Long getId() {
        return this.id;
    }

    public String getProvider() {
        return this.provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public Boolean isSuccess() {
        return this.success;
    }

    public Boolean getSuccess() {
        return this.success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Timestamp getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getBase() {
        return this.base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Map<String,Double> getRates() {
        return this.rates;
    }

    public void setRates(Map<String,Double> rates) {
        this.rates = rates;
    }

    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", provider='" + getProvider() + "'" +
            ", success='" + isSuccess() + "'" +
            ", base='" + getBase() + "'" +
            ", timestamp='" + getTimestamp() + "'" +
            ", date='" + getDate() + "'" +
            ", rates='" + getRates() + "'" +
            "}";
    }   

}
