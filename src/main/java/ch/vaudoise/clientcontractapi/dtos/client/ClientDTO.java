package ch.vaudoise.clientcontractapi.dtos.client;

import ch.vaudoise.clientcontractapi.models.enums.ClientType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PersonDTO.class, name = "PERSON"),
        @JsonSubTypes.Type(value = CompanyDTO.class, name = "COMPANY")
})
@Schema(description = "Base DTO for client creation", discriminatorProperty = "type", oneOf = { PersonDTO.class,
        CompanyDTO.class })
public abstract class ClientDTO {

    @NotNull(message = "Client ID is required")
    private String id;

    @Schema(description = "Type of client: PERSON or COMPANY. Determines required fields.", example = "PERSON", required = true)
    private ClientType type;

    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "Invalid phone number")
    private String phone;
}