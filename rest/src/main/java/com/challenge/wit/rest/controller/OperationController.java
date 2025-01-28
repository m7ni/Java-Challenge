package com.challenge.wit.rest.controller;

import com.challenge.wit.rest.service.CalculationService;
import com.challenge.wit.rest.service.ICalculationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OperationController {
    private final ICalculationService calculationService;

    public OperationController(CalculationService calculationService) {
        this.calculationService = calculationService;
    }

    @GetMapping("/sum")
    public ResponseEntity<?> sum(@RequestParam double a, @RequestParam double b) {
        return calculationService.calculate("sum", a, b);
    }

    @GetMapping("/subtract")
    public ResponseEntity<?> subtract(@RequestParam double a, @RequestParam double b) {
        return calculationService.calculate("subtract", a, b);
    }

    @GetMapping("/multiply")
    public ResponseEntity<?> multiply(@RequestParam double a, @RequestParam double b) {
        return calculationService.calculate("multiply", a, b);
    }

    @GetMapping("/divide")
    public ResponseEntity<?> divide(@RequestParam double a, @RequestParam double b) {
        return calculationService.calculate("divide", a, b);
    }

}
