package com.challenge.wit.shared.logging;

public class LoggingConstants {
    public static final String MDC_REQUEST_ID_KEY = "requestId";

    // Log messages templates without requestId (handled by MDC)
    public static final String LOG_HTTP_REQUEST = "HTTP Request - Operation: {}, Parameters: a={}, b={}";
    public static final String LOG_HTTP_RESPONSE = "HTTP Response - Result: {}";
    public static final String LOG_CALCULATION_REQUEST = "Calculation Request - Operation: {}, OperandA: {}, OperandB: {}";
    public static final String LOG_CALCULATION_RESPONSE = "Calculation Response - Result: {}, Error: {}";
    public static final String LOG_CALCULATION_RESULT = "{} result: {}";
    public static final String LOG_KAFKA_SEND = "Kafka Message Sent - Topic: {}";
    public static final String LOG_KAFKA_RECEIVE = "Kafka Message Received - Topic: {}";
    public static final String LOG_ERROR = "Error processing request - Type: {}, Message: {}";
    public static final String LOG_UNSUPPORTED_OPERATION = "Unsupported operation: {}";
}