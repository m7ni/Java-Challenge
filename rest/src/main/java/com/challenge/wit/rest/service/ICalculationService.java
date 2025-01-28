package com.challenge.wit.rest.service;


import java.math.BigDecimal;

public interface ICalculationService {

    BigDecimal calculate(String operation, double a, double b);
}
