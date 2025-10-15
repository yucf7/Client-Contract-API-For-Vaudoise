package ch.vaudoise.clientcontractapi.dtos.client;

import ch.vaudoise.clientcontractapi.models.enums.ClientType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Base Data Transfer Object (DTO) for client creation.
 * This abstract class is extended by {@link PersonDTO} and {@link CompanyDTO} and provides the common fields
 * and validation rules for client-related data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
                @JsonSubTypes.Type(value = PersonDTO.class, name = "PERSON"),
                @JsonSubTypes.Type(value = CompanyDTO.class, name = "COMPANY")
})
@Schema(description = "Base DTO for client creation", discriminatorProperty = "type", oneOf = { PersonDTO.class, CompanyDTO.class })
public abstract class ClientDTO {

    /**
     * The unique identifier for the client (hidden in the schema).
     * This field is typically used by the backend to identify the client.
     */
    @Schema(hidden = true)
    private String id;

    /**
     * The type of the client, either PERSON or COMPANY.
     * This field determines which specific fields are required for the client and is used for deserialization.
     *
     * @see ClientType
     */
    @Schema(description = "Type of client: PERSON or COMPANY. Determines required fields.", example = "PERSON", required = true)
    @NotNull(message = "Client type is required")
    private ClientType type;

    /**
     * The name of the client.
     * This field is mandatory.
     */
    @NotBlank(message = "Name is required")
    private String name;

    /**
     * The email address of the client.
     * It must be a valid email format and cannot be blank.
     */
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    /**
     * The phone number of the client.
     * It must match the specific phone number format: +XX X XX XX XX XX.
     */
    @Pattern(regexp = "^\\+\\d{1,4}([\\s\\-]?\\d{1,4}){5}$", message = "Invalid phone number format. The correct format is +XX X XX XX XX XX.")
    private String phone;
}
