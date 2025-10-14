package ch.vaudoise.clientcontractapi.dtos.client;

import jakarta.validation.constraints.*;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDTO extends ClientDTO {
    @NotBlank(message = "Company identifier is required")
    private String companyIdentifier;
}