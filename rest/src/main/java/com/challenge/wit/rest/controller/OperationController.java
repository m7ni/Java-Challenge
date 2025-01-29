package com.challenge.wit.rest.controller;

import com.challenge.wit.rest.exception.InvalidOperationException;
import com.challenge.wit.rest.service.ICalculationService;
import com.challenge.wit.shared.dto.CalculationResult;
import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/calculate")
@Validated
public class OperationController {

    private static final Logger logger = LoggerFactory.getLogger(OperationController.class);

    private final ICalculationService calculationService;

    @Autowired
    public OperationController(ICalculationService calculationService) {
        this.calculationService = calculationService;
    }

    @PostMapping("/{operation}")
    public ResponseEntity<CalculationResult> calculate(
            @PathVariable String operation,
            @RequestParam Double a,
            @RequestParam Double b) {
        logger.info("Received HTTP request: Operation={}, a={}, b={}", operation, a, b);
        CalculationResult result = calculationService.calculate(operation, a, b);
        logger.info("Sending HTTP response: {}", result);
        return ResponseEntity.ok(result);
    }

    // Handler for /calculate without operation
    @PostMapping
    public ResponseEntity<Void> handleMissingOperation() {
        logger.warn("Received calculation request without specifying an operation.");
        throw new InvalidOperationException("Operation not specified. Please provide a valid operation in the URL path.");
    }
}
