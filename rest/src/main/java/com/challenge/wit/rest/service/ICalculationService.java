package com.challenge.wit.rest.service;

import org.springframework.http.ResponseEntity;

public interface ICalculationService {

    ResponseEntity<?> calculate(String operation, double a, double b);
}
