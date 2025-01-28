package com.challenge.wit.rest.service;


import com.challenge.wit.shared.dto.CalculationResult;

public interface ICalculationService {

    CalculationResult calculate(String operation, double a, double b);
}
