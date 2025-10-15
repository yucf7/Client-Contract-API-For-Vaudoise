package ch.vaudoise.clientcontractapi.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Custom exception for handling validation errors.
 * This exception is used to signal a validation failure with a specific message, field, and error code.
 */
@Getter
@AllArgsConstructor
public class CustomValidationException extends RuntimeException {

    /**
     * The message associated with the validation failure.
     */
    private final String message;

    /**
     * The field that caused the validation failure.
     */
    private final String field;

    /**
     * The error code representing the type of validation failure.
     */
    private final String errorCode;
}
