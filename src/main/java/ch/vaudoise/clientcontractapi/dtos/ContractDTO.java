package ch.vaudoise.clientcontractapi.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

import ch.vaudoise.clientcontractapi.validators.ValidEndDate;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ValidEndDate(message = "End date must be after the start date and today")
public class ContractDTO {

    @Schema(hidden = true)
    private String id;

    @NotNull(message = "Client ID is required")
    private String clientId;

    private LocalDate startDate;

    private LocalDate endDate;

    @NotNull(message = "Cost amount is required")
    @DecimalMin(value = "0.01", message = "Cost amount must be positive")
    private Double costAmount;
}
