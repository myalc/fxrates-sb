package com.myalc.fxrates.dto.external.fixerio;

import java.io.Serializable;
import java.util.Map;

public class FixerioLatestRates implements Serializable {
    
    private Boolean success;
    private Long timestamp;
    private String base;
    private String date;
    private Map<String, Double> rates;
    private FixerioError error;

    public FixerioLatestRates() {
    }

    public FixerioLatestRates(Boolean success, Long timestamp, String base, String date, Map<String,Double> rates, FixerioError error) {
        this.success = success;
        this.timestamp = timestamp;
        this.base = base;
        this.date = date;
        this.rates = rates;
        this.error = error;
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

    public Long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getBase() {
        return this.base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Map<String,Double> getRates() {
        return this.rates;
    }

    public void setRates(Map<String,Double> rates) {
        this.rates = rates;
    }

    public FixerioError getError() {
        return this.error;
    }

    public void setError(FixerioError error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "{" +
            " success='" + isSuccess() + "'" +
            ", timestamp='" + getTimestamp() + "'" +
            ", base='" + getBase() + "'" +
            ", date='" + getDate() + "'" +
            ", rates='" + getRates() + "'" +
            ", error='" + getError() + "'" +
            "}";
    }

}
