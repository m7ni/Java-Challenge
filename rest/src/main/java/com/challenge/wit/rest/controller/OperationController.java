package com.challenge.wit.rest.controller;

import com.challenge.wit.rest.exception.InvalidOperationException;
import com.challenge.wit.rest.service.ICalculationService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController
@RequestMapping("/calculate")
public class OperationController {
    private final ICalculationService calculationService;

    @Autowired
    public OperationController(ICalculationService calculationService) {
        this.calculationService = calculationService;
    }

    @PostMapping ("/sum")
    public ResponseEntity<BigDecimal> sum(@RequestParam @NotNull Double a, @RequestParam @NotNull Double b) {
        BigDecimal result = calculationService.calculate("sum", a, b);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/subtract")
    public ResponseEntity<BigDecimal> subtract(@RequestParam @NotNull Double a, @RequestParam @NotNull Double b) {
        BigDecimal result = calculationService.calculate("subtract", a, b);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/multiply")
    public ResponseEntity<BigDecimal> multiply(@RequestParam @NotNull Double a, @RequestParam @NotNull Double b) {
        BigDecimal result = calculationService.calculate("multiply", a, b);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/divide")
    public ResponseEntity<BigDecimal> divide(@RequestParam @NotNull Double a, @RequestParam @NotNull Double b) {
        BigDecimal result = calculationService.calculate("divide", a, b);
        return ResponseEntity.ok(result);
    }

    // Handler for /calculate without operation
    @PostMapping
    public ResponseEntity<Void> handleMissingOperation() {
        throw new InvalidOperationException("Operation not specified. Please provide a valid operation in the URL path.");
    }
}
