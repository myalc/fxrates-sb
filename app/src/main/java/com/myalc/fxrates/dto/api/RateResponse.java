package com.myalc.fxrates.dto.api;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class RateResponse implements Serializable {

    @NotBlank
    @Size(min = 3, max=3)
    private String sourceCurrency;

    @NotBlank
    @Size(min = 3, max=3)
    private String targetCurrency;
    
    @NotBlank
    @Min(0)
    private Long timestamp;

    @NotBlank
    @Pattern(regexp = "^[0-9]{4}-((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01])|(0[469]|11)-(0[1-9]|[12][0-9]|30)|(02)-(0[1-9]|[12][0-9]))$", message = "Please provide a valid ISO date")
    private String date;

    @NotBlank
    @DecimalMin(value = "0")
    private Double rate;

    public RateResponse(String sourceCurrency, String targetCurrency, java.sql.Timestamp timestamp, java.sql.Date date, Double rate) {
        this.sourceCurrency = sourceCurrency;
        this.targetCurrency = targetCurrency;
        this.timestamp = timestamp.getTime();   // milliseconds epoch
        this.rate = rate;

        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getDefault());
        this.date = sdf.format(date);
    }
    
    public Double getRate() {
        return this.rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
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

    public Long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "{" +
            " sourceCurrency='" + getSourceCurrency() + "'" +
            ", targetCurrency='" + getTargetCurrency() + "'" +
            ", timestamp='" + getTimestamp() + "'" +
            ", date='" + getDate() + "'" +
            ", rate='" + getRate() + "'" +
            "}";
    }
    
}
