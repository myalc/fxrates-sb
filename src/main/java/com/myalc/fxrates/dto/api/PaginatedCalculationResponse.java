package com.myalc.fxrates.dto.api;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class PaginatedCalculationResponse implements Serializable {
    
    private List<CalculationResponse> calculations;
    private Integer currentPage;
    private Long totalItems;
    private Integer totalPages;

    public PaginatedCalculationResponse() {
    }

    public PaginatedCalculationResponse(List<CalculationResponse> calculations, Integer currentPage, Long totalItems, Integer totalPages) {
        this.calculations = calculations;
        this.currentPage = currentPage;
        this.totalItems = totalItems;
        this.totalPages = totalPages;
    }

    public List<CalculationResponse> getCalculations() {
        return this.calculations;
    }

    public void setCalculations(List<CalculationResponse> calculations) {
        this.calculations = calculations;
    }

    public Integer getCurrentPage() {
        return this.currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Long getTotalItems() {
        return this.totalItems;
    }

    public void setTotalItems(Long totalItems) {
        this.totalItems = totalItems;
    }

    public Integer getTotalPages() {
        return this.totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    @Override
    public String toString() {
        return "{" +
            " calculations='" + getCalculations() + "'" +
            ", currentPage='" + getCurrentPage() + "'" +
            ", totalItems='" + getTotalItems() + "'" +
            ", totalPages='" + getTotalPages() + "'" +
            "}";
    }

    public String toShortString() {
        return "{" +
            " calculations.size='" + getCalculations().size() + "'" +
            ", currentPage='" + getCurrentPage() + "'" +
            ", totalItems='" + getTotalItems() + "'" +
            ", totalPages='" + getTotalPages() + "'" +
            "}";
    }

}
