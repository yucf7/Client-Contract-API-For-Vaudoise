package ch.vaudoise.clientcontractapi.services.handlers;

import ch.vaudoise.clientcontractapi.dtos.client.ClientDTO;
import ch.vaudoise.clientcontractapi.dtos.client.ClientUpdateDTO;
import ch.vaudoise.clientcontractapi.dtos.client.PersonDTO;
import ch.vaudoise.clientcontractapi.dtos.client.PersonUpdateDTO;
import ch.vaudoise.clientcontractapi.mappers.PersonMapper;
import ch.vaudoise.clientcontractapi.models.entities.client.Person;
import ch.vaudoise.clientcontractapi.models.enums.ClientType;
import ch.vaudoise.clientcontractapi.services.client.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Strategy implementation of {@link ClientHandler} for managing {@link Person}
 * clients.
 * <p>
 * This handler performs CRUD operations specific to the
 * {@link ClientType#PERSON} type,
 * delegating business logic to {@link PersonService} and mapping between
 * {@link Person} entities and {@link PersonDTO} DTOs via {@link PersonMapper}.
 */
@Service
@RequiredArgsConstructor
public class PersonHandler implements ClientHandler<Person, PersonDTO> {

    private final PersonService personService;
    private final PersonMapper personMapper;

    @Override
    public ClientType getSupportedClientType() {
        return ClientType.PERSON;
    }

    /**
     * Returns the client type handled by this implementation.
     *
     * @return {@link ClientType#PERSON}
     */
    @Override
    public ClientType getClientType() {
        return ClientType.PERSON;
    }

    /**
     * Retrieves all {@link Person} clients and maps them to {@link PersonDTO}.
     *
     * @return list of {@link PersonDTO}
     */
    @Override
    public List<PersonDTO> getAll() {
        return personService.getAllPersons().stream()
                .map(personMapper::toDTO)
                .toList();
    }

    /**
     * Retrieves a {@link Person} by ID and maps it to {@link PersonDTO}.
     *
     * @param id the ID of the person
     * @return the corresponding {@link PersonDTO}, or {@code null} if not found
     */
    @Override
    public PersonDTO getById(String id) {
        UUID uuid = toUUID(id);
        return personService.getPersonById(uuid)
                .map(personMapper::toDTO)
                .orElse(null);
    }

    /**
     * Creates a new {@link Person} from the provided {@link PersonDTO}.
     *
     * @param dto the DTO containing person data
     * @return the created {@link PersonDTO}
     */
    @Override
    public PersonDTO create(PersonDTO dto) {
        Person entity = personMapper.toEntity(dto);
        return personMapper.toDTO(personService.createPerson(entity));
    }

    /**
     * Deletes a {@link Person} client by ID.
     *
     * @param id the ID of the person to delete
     */
    @Override
    public void delete(String id) {
        UUID uuid = toUUID(id);
        personService.getPersonById(uuid).ifPresent(personService::deletePerson);
    }

    /**
     * Converts a generic {@link ClientDTO} to a {@link PersonDTO}.
     *
     * @param genericDto the generic DTO to convert
     * @return the specific {@link PersonDTO}
     * @throws IllegalArgumentException if the DTO is not an instance of
     *                                  {@link PersonDTO}
     */
    @Override
    public PersonDTO convertToSpecificDto(ClientDTO genericDto) {
        if (!(genericDto instanceof PersonDTO)) {
            throw new IllegalArgumentException("Expected PersonDTO but got: " + genericDto.getClass().getSimpleName());
        }
        return (PersonDTO) genericDto;
    }

    /**
     * Utility method to safely convert String to UUID.
     * Throws IllegalArgumentException if invalid.
     *
     * @param id the string representation of UUID
     * @return the UUID object
     */
    private UUID toUUID(String id) {
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid UUID string: " + id, ex);
        }
    }

    @Override
    public PersonDTO update(String id, PersonDTO personDTO) {
        UUID uuid = UUID.fromString(id);
        Person existing = personService.getPersonById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Person not found with id: " + id));

        // Update only allowed fields - explicitly ignore birthdate from incoming DTO
        existing.setName(personDTO.getName());
        existing.setEmail(personDTO.getEmail());
        existing.setPhone(personDTO.getPhone());
        // Birthdate remains unchanged - we completely ignore the incoming birthdate
        // value

        Person updated = personService.updatePerson(existing, existing);
        return personMapper.toDTO(updated);
    }

    /**
     * Overloaded method for PersonUpdateDTO
     */
    public PersonDTO update(String id, PersonUpdateDTO updateDTO) {
        // Convert to full PersonDTO for processing (birthdate will be handled by
        // service)
        PersonDTO personDTO = new PersonDTO();
        personDTO.setId(updateDTO.getId());
        personDTO.setType(updateDTO.getType());
        personDTO.setName(updateDTO.getName());
        personDTO.setEmail(updateDTO.getEmail());
        personDTO.setPhone(updateDTO.getPhone());
        // birthdate remains null - will be ignored in service

        return update(id, personDTO);
    }

    @Override
    public PersonDTO convertToSpecificDto(ClientUpdateDTO genericDto) {

        PersonDTO personDTO = new PersonDTO();

        personDTO.setName(genericDto.getName());
        personDTO.setEmail(genericDto.getEmail());
        personDTO.setPhone(genericDto.getPhone());

        return personDTO;
    }
}
