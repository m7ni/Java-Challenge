package com.challenge.wit.rest.exception;

import com.challenge.wit.shared.logging.LoggingConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Collections;

@ControllerAdvice
public class GlobalExceptionHandler{

    // Initialize logger with GlobalExceptionHandler.class
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Handle CalculationException
    @ExceptionHandler(CalculationException.class)
    public ResponseEntity<ErrorResponse> handleCalculationException(CalculationException ex) {
        logger.error(LoggingConstants.LOG_ERROR, "CALCULATION_ERROR", ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                Collections.singletonList(ex.getMessage()),
                "CALCULATION_ERROR"
        );
        return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
    }

    // Handle TimeoutException
    @ExceptionHandler(TimeoutException.class)
    public ResponseEntity<ErrorResponse> handleTimeoutException(TimeoutException ex) {
        logger.error(LoggingConstants.LOG_ERROR, "TIMEOUT_ERROR", ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.REQUEST_TIMEOUT,
                ex.getMessage(),
                Collections.singletonList(ex.getMessage()),
                "TIMEOUT_ERROR"
        );
        return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
    }

    // Handle InvalidOperationException
    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<ErrorResponse> handleInvalidOperationException(InvalidOperationException ex) {
        logger.error(LoggingConstants.LOG_ERROR, "INVALID_OPERATION", ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                Collections.singletonList(ex.getMessage()),
                "INVALID_OPERATION"
        );
        return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
    }

    // Handle generic exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
        logger.error(LoggingConstants.LOG_ERROR, "INTERNAL_SERVER_ERROR", "An unexpected error occurred.", ex);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred.",
                Collections.singletonList("Please contact support."),
                "INTERNAL_SERVER_ERROR"
        );
        return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
    }
}
