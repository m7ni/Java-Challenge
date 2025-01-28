package com.challenge.wit.rest.controller;

import com.challenge.wit.rest.exception.InvalidOperationException;
import com.challenge.wit.rest.service.ICalculationService;
import com.challenge.wit.shared.dto.CalculationResult;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/calculate")
public class OperationController {
    private final ICalculationService calculationService;

    @Autowired
    public OperationController(ICalculationService calculationService) {
        this.calculationService = calculationService;
    }

    @PostMapping("/{operation}")
    public ResponseEntity<CalculationResult> calculate(
            @PathVariable String operation,
            @RequestParam @NotNull Double a,
            @RequestParam @NotNull Double b) {
        CalculationResult result = calculationService.calculate(operation, a, b);
        return ResponseEntity.ok(result);
    }
    @PostMapping
    public ResponseEntity<Void> handleMissingOperation() {
        throw new InvalidOperationException("Operation not specified. Please provide a valid operation in the URL path.");
    }
}
