package ch.vaudoise.clientcontractapi.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractDTO {

    @NotNull(message = "Contract ID is required")
    private String id;

    @NotNull(message = "Client ID is required")
    private String clientId;

    private LocalDate startDate;
    private LocalDate endDate;

    @NotNull(message = "Cost amount is required")
    @DecimalMin(value = "0.0", message = "Cost amount must be positive")
    private Double costAmount;
}
