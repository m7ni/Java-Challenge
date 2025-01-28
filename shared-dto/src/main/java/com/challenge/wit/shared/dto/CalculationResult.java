package com.challenge.wit.shared.dto;

import java.math.BigDecimal;

public class CalculationResult {
    private BigDecimal result;

    // Constructors
    public CalculationResult() {}

    public CalculationResult(BigDecimal result) {
        this.result = result;
    }

    // Getter and Setter
    public BigDecimal getResult() {
        return result;
    }

    public void setResult(BigDecimal result) {
        this.result = result;
    }
}
