package ch.vaudoise.clientcontractapi.models.entities.client;

import jakarta.persistence.*;
import jakarta.validation.constraints.Past;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "person_client")
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person extends Client {

    @Past(message = "Birthdate must be in the past")
    private LocalDate birthdate;
}
