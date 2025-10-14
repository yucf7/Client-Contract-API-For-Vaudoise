package ch.vaudoise.clientcontractapi.dtos.client;

import ch.vaudoise.clientcontractapi.models.enums.ClientType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class CompanyUpdateDTO extends ClientDTO {
    // No companyIdentifier field - cannot be updated!
    // Inherits all updatable fields: id, type, name, email, phone
    
    public CompanyUpdateDTO(String id, ClientType type, String name, String email, String phone) {
        this.setId(id);
        this.setType(type);
        this.setName(name);
        this.setEmail(email);
        this.setPhone(phone);
    }
}