package ch.vaudoise.clientcontractapi.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import ch.vaudoise.clientcontractapi.dtos.ErrorResponseDTO;

import org.springframework.web.bind.MethodArgumentNotValidException;

/**
 * Global exception handler to catch and process different types of exceptions thrown
 * within the application.
 * This class provides centralized handling of validation and database errors, 
 * returning consistent error responses to the client.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles custom validation exceptions, typically thrown by custom validators.
     * Returns a detailed error response with the validation failure message and relevant details.
     *
     * @param ex the {@link CustomValidationException} that was thrown
     * @return a {@link ResponseEntity} containing the error response
     */
    @ExceptionHandler(CustomValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponseDTO> handleCustomValidationException(CustomValidationException ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                "VALIDATION_FAILED",
                "Validation failed for the provided data",
                ex.getMessage(),
                ex.getField(),
                ex.getErrorCode());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles general method argument validation errors, which occur when the @Valid annotation fails.
     * Returns the error message associated with the first field validation failure.
     *
     * @param ex the {@link MethodArgumentNotValidException} that was thrown
     * @return a {@link ResponseEntity} containing the error response
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponseDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        // Get the first field error (you can iterate over all errors if needed)
        FieldError fieldError = ex.getBindingResult().getFieldError();
        if (fieldError != null) {
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                    "VALIDATION_FAILED",
                    "Validation failed for the provided data",
                    fieldError.getDefaultMessage(),
                    fieldError.getField(),
                    fieldError.getCode());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        // If no field error is found, return a generic validation error
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                "VALIDATION_FAILED",
                "Validation failed for the provided data",
                "Unknown validation error",
                null,
                null);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles any other generic exception (e.g., internal server errors).
     * Returns a generic error message with status 500.
     *
     * @param ex the {@link Exception} that was thrown
     * @return a {@link ResponseEntity} containing the error response
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred",
                ex.getMessage(),
                null,
                null);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles database integrity violations, such as unique constraint violations.
     * Specifically, it checks for the case where an email address already exists in the database.
     *
     * @param ex the {@link DataIntegrityViolationException} that was thrown
     * @return a {@link ResponseEntity} containing the error response
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        // Check if the error message contains a constraint violation for the email key
        if (ex.getMessage().contains("client_email_key")) {
            // Custom message for duplicate email error
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                "EMAIL_ALREADY_EXISTS",
                "The provided email address already exists in the system. Please choose a different one.",
                "Email is already in use",
                "email",
                "client_email_key"
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST); // Return bad request status
        }

        // If another constraint violation occurs, return a generic internal server error
        ErrorResponseDTO response = new ErrorResponseDTO(
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred while processing the request.",
                ex.getMessage(),
                null,
                null
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
