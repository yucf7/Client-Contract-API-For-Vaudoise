package ch.vaudoise.clientcontractapi.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation to validate the company identifier in the format 'aaa-123',
 * where 'aaa' is a string of letters, and '123' is a numeric value.
 */
@Constraint(validatedBy = CompanyIdentifierValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCompanyIdentifier {

    /**
     * The error message to be displayed when the validation fails.
     * 
     * @return the error message
     */
    String message() default "Company Identifier must follow the format 'aaa-123'";

    /**
     * Allows for the grouping of constraints. Can be used to specify validation
     * groups when performing validations.
     * 
     * @return the groups associated with the constraint
     */
    Class<?>[] groups() default {};

    /**
     * Carries additional data that can be used by the validation logic.
     * 
     * @return the payload associated with the constraint
     */
    Class<? extends Payload>[] payload() default {};
}
