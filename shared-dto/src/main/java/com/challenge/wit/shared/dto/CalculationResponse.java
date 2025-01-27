package com.challenge.wit.shared.dto;

import java.math.BigDecimal;

public class CalculationResponse {
    private String requestId; // Matches the CalculationRequest ID
    private BigDecimal result; // The result of the calculation
    private String error; // Error message if something went wrong

    // No-argument constructor
    public CalculationResponse() {
    }

    // Parameterized constructor
    public CalculationResponse(String requestId, BigDecimal result, String error) {
        this.requestId = requestId;
        this.result = result;
        this.error = error;
    }

    // Getters and setters
    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public BigDecimal getResult() {
        return result;
    }

    public void setResult(BigDecimal result) {
        this.result = result;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
