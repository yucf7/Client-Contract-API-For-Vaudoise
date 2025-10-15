package ch.vaudoise.clientcontractapi.services;

import ch.vaudoise.clientcontractapi.models.entities.client.Person;
import ch.vaudoise.clientcontractapi.repositories.client.PersonRepository;
import ch.vaudoise.clientcontractapi.services.client.PersonService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link PersonService}.
 * This test class verifies the functionality of the {@link PersonService} class, ensuring correct 
 * behavior when interacting with the {@link PersonRepository} and {@link ContractService}.
 */
@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;

    @Mock
    private ContractService contractService;

    @InjectMocks
    private PersonService personService;

    private Person person;

    /**
     * Setup method to initialize common data for all tests.
     * This method initializes a sample {@link Person} entity with predefined values.
     */
    @BeforeEach
    void setUp() {
        person = new Person();
        person.setId(UUID.randomUUID());
        person.setName("Jane Doe");
        person.setEmail("jane@example.com");
        person.setPhone("+41234567890");
        person.setBirthdate(LocalDate.of(1990, 1, 1));
    }

    /**
     * Test to verify the creation of a person.
     * This test ensures that the {@link PersonService#createPerson(Person)} method
     * successfully saves and returns the person entity.
     */
    @Test
    void createPerson_savesAndReturns() {
        when(personRepository.save(any(Person.class))).thenReturn(person);

        Person created = personService.createPerson(person);

        // Assertions to verify that the created person has the correct values
        assertNotNull(created);
        assertEquals("Jane Doe", created.getName());
        verify(personRepository, times(1)).save(person);
    }

    /**
     * Test to verify retrieval of all persons.
     * This test ensures that the {@link PersonService#getAllPersons()} method correctly calls the 
     * repository to fetch all person records.
     */
    @Test
    void getAllPersons_callsRepository() {
        when(personRepository.findAll()).thenReturn(List.of(person));

        var all = personService.getAllPersons();

        // Assertions to verify that the retrieved list contains the expected person
        assertEquals(1, all.size());
        verify(personRepository).findAll();
    }

    /**
     * Test to verify updating a person's details.
     * This test ensures that the fields of the person are updated correctly and saved back 
     * to the repository.
     */
    @Test
    void updatePerson_updatesFieldsAndSaves() {
        Person updatedData = new Person();
        updatedData.setName("Jane Updated");
        updatedData.setEmail("jane2@example.com");
        updatedData.setPhone("+41987654321");

        // Mocking the save method to return the updated person
        when(personRepository.save(any(Person.class))).thenAnswer(inv -> inv.getArgument(0));

        Person result = personService.updatePerson(person, updatedData);

        // Assertions to verify the updated fields
        assertEquals("Jane Updated", result.getName());
        assertEquals("jane2@example.com", result.getEmail());
        assertEquals("+41987654321", result.getPhone());
        assertNotNull(result.getUpdatedAt());  // Ensure the updated timestamp is set
        verify(personRepository).save(person);
    }

    /**
     * Test to verify that a person’s deletion closes associated contracts and deletes the person.
     * This test ensures that the system correctly handles the deletion of a person, 
     * including closing any associated contracts before removal.
     */
    @Test
    void deletePerson_closesContracts_thenDeletes() {
        // Mocking the close contracts behavior and deletion of the person
        doNothing().when(contractService).closeContractsOnClientDeletion(person);
        doNothing().when(personRepository).delete(person);

        // Deleting the person and verifying the actions
        personService.deletePerson(person);

        // Verifying that the contracts were closed and the person was deleted
        verify(contractService).closeContractsOnClientDeletion(person);
        verify(personRepository).delete(person);
    }

    /**
     * Test to verify that retrieving a person by their ID returns the correct person.
     * This test ensures that the {@link PersonService#getPersonById(UUID)} method 
     * returns the correct {@link Person} when provided with a valid ID.
     */
    @Test
    void getPersonById_returnsOptional() {
        UUID id = person.getId();
        when(personRepository.findById(id)).thenReturn(Optional.of(person));

        Optional<Person> found = personService.getPersonById(id);

        // Assertions to verify that the correct person was found
        assertTrue(found.isPresent());
        assertEquals(person.getName(), found.get().getName());
    }
}