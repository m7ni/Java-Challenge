package com.challenge.wit.shared.dto;


import java.math.BigDecimal;
public class CalculationRequest {
    private String operation; // "sum", "subtract", "multiply", "divide"
    private BigDecimal operandA;
    private BigDecimal operandB;
    private String requestId;


    public CalculationRequest() {
    }

    private CalculationRequest(Builder builder) {
        this.operation = builder.operation;
        this.operandA = builder.operandA;
        this.operandB = builder.operandB;
        this.requestId = builder.requestId;
    }
    // Getters
    public String getOperation() {
        return operation;
    }
    public BigDecimal getOperandA() {
        return operandA;
    }
    public BigDecimal getOperandB() {
        return operandB;
    }
    public String getRequestId() {
        return requestId;
    }
    public static class Builder {
        private String operation;
        private BigDecimal operandA;
        private BigDecimal operandB;
        private String requestId;
        // Setter methods for each field, returning Builder for method chaining
        public Builder operation(String operation) {
            this.operation = operation;
            return this;
        }
        public Builder operandA(BigDecimal operandA) {
            this.operandA = operandA;
            return this;
        }
        public Builder operandB(BigDecimal operandB) {
            this.operandB = operandB;
            return this;
        }
        public Builder requestId(String requestId) {
            this.requestId = requestId;
            return this;
        }
        // Build method to create the final CalculationRequest object
        public CalculationRequest build() {
            return new CalculationRequest(this);
        }
    }
}