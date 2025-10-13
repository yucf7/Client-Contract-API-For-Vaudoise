package ch.vaudoise.clientcontractapi.services.client;

import ch.vaudoise.clientcontractapi.models.entities.client.Person;
import ch.vaudoise.clientcontractapi.repositories.client.PersonRepository;
import ch.vaudoise.clientcontractapi.services.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing operations related to {@link Person}.
 * Provides methods to create, update, retrieve, and delete persons.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PersonService implements ClientService<Person> {

    private final PersonRepository personRepository;
    private final ContractService contractService;

    /**
     * Retrieves a list of all persons from the repository.
     *
     * @return a list of all {@link Person} entities
     */
    public List<Person> getAllPersons() {
        return personRepository.findAll();
    }

    /**
     * Retrieves a person by their unique ID.
     *
     * @param id the unique identifier of the person
     * @return an {@link Optional} containing the {@link Person} if found, or empty
     *         if not found
     */
    public Optional<Person> getPersonById(Long id) {
        return personRepository.findById(id);
    }

    /**
     * Creates a new {@link Person} entity and saves it to the repository.
     *
     * @param person the {@link Person} entity to create
     * @return the created {@link Person} entity
     */
    public Person createPerson(Person person) {
        return personRepository.save(person);
    }

    /**
     * Updates an existing {@link Person} entity with new data.
     *
     * @param existing    the existing {@link Person} entity to update
     * @param updatedData the new data to update the existing entity with
     * @return the updated {@link Person} entity
     */
    public Person updatePerson(Person existing, Person updatedData) {
        existing.setName(updatedData.getName());
        existing.setEmail(updatedData.getEmail());
        existing.setPhone(updatedData.getPhone());
        existing.setUpdatedAt(LocalDate.now());
        return personRepository.save(existing);
    }

    /**
     * Deletes a {@link Person} entity and ensures related contracts are closed.
     *
     * @param person the {@link Person} entity to delete
     */
    public void deletePerson(Person person) {
        contractService.closeContractsOnClientDeletion(person);
        personRepository.delete(person);
    }

    /**
     * Retrieves a {@link Person} entity by its unique identifier.
     *
     * @param id the unique identifier of the {@link Person} entity
     * @return an {@link Optional} containing the {@link Person} if found, or empty
     *         if not found
     */
    @Override
    public Optional<Person> getEntityById(Long id) {
        return personRepository.findById(id);
    }

}
