package ch.vaudoise.clientcontractapi.dtos.client;

import ch.vaudoise.clientcontractapi.validators.ValidCompanyIdentifier;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Data Transfer Object (DTO) representing a company.
 * Extends {@link ClientDTO} and adds company-specific validation and fields.
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDTO extends ClientDTO {

    /**
     * The company identifier, which must follow a specific format (e.g., 'aaa-123').
     * This field is validated to ensure it is not blank and that it follows the correct format
     * via the {@link ValidCompanyIdentifier} annotation.
     */
    @NotBlank(message = "Company identifier is required")
    @ValidCompanyIdentifier(message = "Company Identifier must follow the format 'aaa-123'")
    private String companyIdentifier;
}