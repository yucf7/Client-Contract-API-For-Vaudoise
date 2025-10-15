package ch.vaudoise.clientcontractapi.validators;

import ch.vaudoise.clientcontractapi.dtos.ContractDTO;

import java.time.LocalDate;

/**
 * Validator class for {@link ValidEndDate} annotation.
 * This class implements the validation logic to ensure that the end date of a contract
 * is both after the start date and after the current date (i.e., not a past date).
 */
public class EndDateValidator implements jakarta.validation.ConstraintValidator<ValidEndDate, ContractDTO> {

    /**
     * Validates the end date of the given contract to ensure it is after the start date
     * and after the current date (today).
     *
     * @param contractDTO the contract object to be validated
     * @param context the context in which the constraint is being evaluated
     * @return true if the end date is valid (i.e., after the start date and today's date); 
     *         false otherwise
     */
    @Override
    public boolean isValid(ContractDTO contractDTO, jakarta.validation.ConstraintValidatorContext context) {
        if (contractDTO == null) {
            return true; // If the contractDTO is null, we return true so other @NotNull validations can handle it
        }

        LocalDate startDate = contractDTO.getStartDate();
        LocalDate endDate = contractDTO.getEndDate();

        // Ensure both dates are present (use @NotNull if null-check is required separately)
        if (startDate == null || endDate == null) {
            return true; // Let other validations handle null cases or add explicit null checks
        }

        // Check if the endDate is after the startDate
        if (!endDate.isAfter(startDate)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("End date must be after the start date")
                    .addPropertyNode("endDate")
                    .addConstraintViolation();
            return false;
        }

        // Check if the endDate is after today's date
        if (!endDate.isAfter(LocalDate.now())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("End date must be after today's date")
                    .addPropertyNode("endDate")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
