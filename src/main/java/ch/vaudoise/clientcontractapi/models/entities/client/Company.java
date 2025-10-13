package ch.vaudoise.clientcontractapi.models.entities.client;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "company_client")
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Company extends Client {

    @Column(name = "company_identifier", unique = true, nullable = false)
    @NotBlank(message = "Company identifier is required")
    private String companyIdentifier;
}
