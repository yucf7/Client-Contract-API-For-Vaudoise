package ch.vaudoise.clientcontractapi.dtos;

import lombok.*;

/**
 * DTO for error response, used to structure error details in the API responses.
 * This class provides all the necessary information about an error, including
 * the error type, message, and additional context such as the field and error code.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponseDTO {

    /**
     * The type of the error (e.g., validation failure, internal server error).
     */
    private String errorType;

    /**
     * A human-readable message describing the error.
     */
    private String errorMessage;

    /**
     * A more detailed message, typically used to provide specific details about the error.
     */
    private String message;

    /**
     * The specific field that caused the error, if applicable.
     */
    private String field;

    /**
     * A unique error code representing the type of error.
     */
    private String errorCode;
}
