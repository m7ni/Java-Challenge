package com.challenge.wit.rest.exception;

import com.challenge.wit.shared.logging.LoggingConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Collections;

/**
 * Handles exceptions globally across all controllers in the REST module.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles CalculationException and returns a BAD_REQUEST response.
     *
     * @param ex The CalculationException thrown.
     * @return A ResponseEntity containing the ErrorResponse.
     */
    @ExceptionHandler(CalculationException.class)
    public ResponseEntity<ErrorResponse> handleCalculationException(CalculationException ex) {
        logger.error(LoggingConstants.LOG_ERROR, "CALCULATION_ERROR", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                Collections.singletonList(ex.getMessage()),
                "CALCULATION_ERROR"
        );
        return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
    }

    /**
     * Handles TimeoutException and returns a REQUEST_TIMEOUT response.
     *
     * @param ex The TimeoutException thrown.
     * @return A ResponseEntity containing the ErrorResponse.
     */
    @ExceptionHandler(TimeoutException.class)
    public ResponseEntity<ErrorResponse> handleTimeoutException(TimeoutException ex) {
        logger.error(LoggingConstants.LOG_ERROR, "TIMEOUT_ERROR", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.REQUEST_TIMEOUT,
                ex.getMessage(),
                Collections.singletonList(ex.getMessage()),
                "TIMEOUT_ERROR"
        );
        return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
    }

    /**
     * Handles InvalidOperationException and returns a BAD_REQUEST response.
     *
     * @param ex The InvalidOperationException thrown.
     * @return A ResponseEntity containing the ErrorResponse.
     */
    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<ErrorResponse> handleInvalidOperationException(InvalidOperationException ex) {
        logger.error(LoggingConstants.LOG_ERROR, "INVALID_OPERATION", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                Collections.singletonList(ex.getMessage()),
                "INVALID_OPERATION"
        );
        return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
    }

    /**
     * Handles generic exceptions and returns an INTERNAL_SERVER_ERROR response.
     *
     * @param ex The Exception thrown.
     * @return A ResponseEntity containing the ErrorResponse.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
        logger.error(LoggingConstants.LOG_ERROR, "INTERNAL_SERVER_ERROR", "An unexpected error occurred.");

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred.",
                Collections.singletonList("Please contact support."),
                "INTERNAL_SERVER_ERROR"
        );
        return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
    }

    /**
     * Handles type mismatches in method arguments and returns a BAD_REQUEST response.
     *
     * @param ex The MethodArgumentTypeMismatchException thrown.
     * @return A ResponseEntity containing the ErrorResponse.
     */
    @ExceptionHandler(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid parameter: " + ex.getName(),
                Collections.singletonList("Expected type: " + (ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "Unknown")),
                "VALIDATION_ERROR"
        );
        return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
    }

    /**
     * Handles missing request parameters and returns a BAD_REQUEST response.
     *
     * @param ex The MissingServletRequestParameterException thrown.
     * @return A ResponseEntity containing the ErrorResponse.
     */
    @ExceptionHandler(org.springframework.web.bind.MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParams(org.springframework.web.bind.MissingServletRequestParameterException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Missing required parameter: " + ex.getParameterName(),
                Collections.singletonList("Parameter is required but not provided."),
                "VALIDATION_ERROR"
        );
        return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
    }
}
