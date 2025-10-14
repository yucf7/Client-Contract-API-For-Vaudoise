package ch.vaudoise.clientcontractapi.dtos.client;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonDTO extends ClientDTO {
    @Past(message = "Birthdate must be in the past")
    private LocalDate birthdate;
}