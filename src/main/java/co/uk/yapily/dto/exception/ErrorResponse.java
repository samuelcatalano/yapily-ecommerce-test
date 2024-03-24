package co.uk.yapily.dto.exception;

/**
 * A class that represents an Error Response.
 *
 * @param status the error status
 * @param message the error message
 * @param code the HTTP status code
 *
 * @author Samuel Catalano
 * @since 1.0.0
 */
public record ErrorResponse(String status, String message, Integer code) {}