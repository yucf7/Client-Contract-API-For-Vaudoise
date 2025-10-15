package ch.vaudoise.clientcontractapi.validators;

import jakarta.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation to validate that the end date is after the start date
 * and is not a past date.
 * This annotation can be applied to fields or method parameters.
 * The actual validation logic is implemented in the {@link EndDateValidator}
 * class.
 */
@Constraint(validatedBy = EndDateValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEndDate  {

    /**
     * The error message to be displayed when the validation fails.
     * 
     * @return the error message
     */
    String message() default "End date must be after the start date and today";

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
    Class<? extends jakarta.validation.Payload>[] payload() default {};
}
