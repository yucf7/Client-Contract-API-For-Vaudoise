package ch.vaudoise.clientcontractapi.dtos.client;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import ch.vaudoise.clientcontractapi.models.enums.ClientType;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = PersonDTO.class, name = "PERSON"),
    @JsonSubTypes.Type(value = CompanyDTO.class, name = "COMPANY")
})
public abstract class ClientDTO {

  private ClientType type;

  @NotBlank(message = "Name is required")
  private String name;

  @Email(message = "Invalid email format")
  @NotBlank(message = "Email is required")
  private String email;

  @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "Invalid phone number")
  private String phone;
}
