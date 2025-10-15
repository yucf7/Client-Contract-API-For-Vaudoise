package ch.vaudoise.clientcontractapi.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for the {@link ValidCompanyIdentifier} annotation.
 * This ensures that the company identifier is in the correct format: 'aaa-123',
 * where 'aaa' is a string of letters, and '123' is a numeric value.
 */
public class CompanyIdentifierValidator implements ConstraintValidator<ValidCompanyIdentifier, String> {

    private static final String COMPANY_IDENTIFIER_PATTERN = "^[a-zA-Z]{3}-\\d{3}$";  // Matches aaa-123

    /**
     * Initializes the validator.
     * 
     * @param constraintAnnotation the annotation instance for this validation
     */
    @Override
    public void initialize(ValidCompanyIdentifier constraintAnnotation) {
        // No initialization needed for this simple regex validation.
    }

    /**
     * Validates if the company identifier matches the expected pattern 'aaa-123'.
     *
     * @param companyIdentifier the company identifier to be validated
     * @param context           the context in which the constraint is evaluated
     * @return true if the company identifier matches the pattern; false otherwise
     */
    @Override
    public boolean isValid(String companyIdentifier, ConstraintValidatorContext context) {
        if (companyIdentifier == null) {
            return true;  // Let other validation annotations (like @NotNull) handle null checks
        }

        return companyIdentifier.matches(COMPANY_IDENTIFIER_PATTERN);
    }
}
