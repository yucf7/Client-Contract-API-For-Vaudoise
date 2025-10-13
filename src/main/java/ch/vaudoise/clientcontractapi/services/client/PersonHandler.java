package ch.vaudoise.clientcontractapi.services.client;

import ch.vaudoise.clientcontractapi.dtos.client.ClientDTO;
import ch.vaudoise.clientcontractapi.dtos.client.PersonDTO;
import ch.vaudoise.clientcontractapi.mappers.PersonMapper;
import ch.vaudoise.clientcontractapi.models.entities.client.Person;
import ch.vaudoise.clientcontractapi.models.enums.ClientType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Strategy implementation of {@link ClientHandler} for managing {@link Person} clients.
 * <p>
 * This handler performs CRUD operations specific to the {@link ClientType#PERSON} type,
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
    public PersonDTO getById(Long id) {
        return personService.getPersonById(id)
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
     * Updates an existing {@link Person} with the given ID using the provided {@link PersonDTO}.
     *
     * @param id  the ID of the person to update
     * @param dto the DTO containing updated data
     * @return the updated {@link PersonDTO}, or {@code null} if not found
     */
    @Override
    public PersonDTO update(Long id, PersonDTO dto) {
        return personService.getPersonById(id)
                .map(existing -> personMapper.toDTO(
                        personService.updatePerson(existing, personMapper.toEntity(dto))
                ))
                .orElse(null);
    }

    /**
     * Deletes a {@link Person} client by ID.
     *
     * @param id the ID of the person to delete
     */
    @Override
    public void delete(Long id) {
        personService.getPersonById(id).ifPresent(personService::deletePerson);
    }

    /**
     * Converts a generic {@link ClientDTO} to a {@link PersonDTO}.
     *
     * @param genericDto the generic DTO to convert
     * @return the specific {@link PersonDTO}
     * @throws IllegalArgumentException if the DTO is not an instance of {@link PersonDTO}
     */
    @Override
    public PersonDTO convertToSpecificDto(ClientDTO genericDto) {
        if (!(genericDto instanceof PersonDTO)) {
            throw new IllegalArgumentException("Expected PersonDTO but got: " + genericDto.getClass().getSimpleName());
        }
        return (PersonDTO) genericDto;
    }
}
