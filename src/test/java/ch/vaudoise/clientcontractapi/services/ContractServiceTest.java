package ch.vaudoise.clientcontractapi.services;

import ch.vaudoise.clientcontractapi.models.entities.Contract;
import ch.vaudoise.clientcontractapi.models.entities.client.Person;
import ch.vaudoise.clientcontractapi.repositories.ContractRepository;
import ch.vaudoise.clientcontractapi.services.client.CompanyService;
import ch.vaudoise.clientcontractapi.services.client.PersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.*;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ContractService}.
 * This test class verifies the functionality of the {@link ContractService} class,
 * ensuring correct behavior when interacting with the {@link ContractRepository}, {@link PersonService}, 
 * and {@link CompanyService}.
 */
@ExtendWith(MockitoExtension.class)
class ContractServiceTest {

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private PersonService personService;

    @Mock
    private CompanyService companyService;

    @InjectMocks
    private ContractService contractService;

    private Contract contract;
    private Person person;

    /**
     * Setup method to initialize common data for all tests.
     * This method initializes a sample {@link Person} and {@link Contract} entities 
     * with predefined values.
     */
    @BeforeEach
    void setup() {
        person = new Person();
        person.setId(UUID.randomUUID());

        contract = new Contract();
        contract.setId(UUID.randomUUID());
        contract.setClient(person);
        contract.setCostAmount(100.0);
        contract.setStartDate(LocalDate.of(2025, 1, 1));
        contract.setEndDate(null); // active
        contract.setLastModified(LocalDateTime.now().minusDays(1));
    }

    /**
     * Test to verify the creation of a contract sets a default start date and saves the contract.
     * This test ensures that if the start date is not set in the request, it defaults to the current date.
     */
    @Test
    void createContract_setsDefaultStartDateAndSaves() {
        Contract toCreate = new Contract();
        toCreate.setId(UUID.randomUUID());
        toCreate.setClient(person);
        toCreate.setCostAmount(250.0);
        toCreate.setStartDate(null); // should be set by service

        // Mocking the save method to return the contract with an auto-generated ID
        when(contractRepository.save(any(Contract.class))).thenAnswer(inv -> {
            Contract c = inv.getArgument(0);
            c.setId(UUID.randomUUID());
            return c;
        });

        Contract saved = contractService.createContract(toCreate);

        // Assertions to verify that the start date was set to the current date
        assertNotNull(saved.getStartDate());
        assertEquals(LocalDate.now(), saved.getStartDate());
        verify(contractRepository).save(any(Contract.class));
    }

    /**
     * Test to verify the update of a contract's cost.
     * This test ensures that when the contract's cost is updated, 
     * the last modified timestamp is also updated.
     */
    @Test
    void updateContractCost_updatesLastModifiedAndSaves() {
        // Mocking the save method to return the contract with updated cost
        when(contractRepository.save(any(Contract.class))).thenAnswer(inv -> inv.getArgument(0));

        Contract updated = contractService.updateContractCost(contract, 500.0);

        // Assertions to verify the updated cost and the last modified time
        assertEquals(500.0, updated.getCostAmount());
        assertTrue(updated.getLastModified().isAfter(LocalDateTime.now().minusSeconds(5)));
        verify(contractRepository).save(contract);
    }

    /**
     * Test to verify retrieval of active contracts for a client.
     * This test ensures that the {@link ContractService#getActiveContracts(Person)} method 
     * correctly delegates to the repository to fetch active contracts.
     */
    @Test
    void getActiveContracts_delegatesToRepository() {
        LocalDate today = LocalDate.now();
        when(contractRepository.findActiveByClient(person, today)).thenReturn(List.of(contract));

        List<Contract> active = contractService.getActiveContracts(person);

        // Assertions to verify the active contract is returned correctly
        assertEquals(1, active.size());
        verify(contractRepository).findActiveByClient(person, today);
    }

    /**
     * Test to verify retrieval of active contracts with an 'updatedAfter' filter.
     * This test ensures that the correct query is used when filtering active contracts 
     * by the last updated timestamp.
     */
    @Test
    void getActiveContracts_withUpdatedAfter_usesUpdatedAfterQuery() {
        OffsetDateTime updatedAfter = OffsetDateTime.now().minusDays(2);
        LocalDate today = LocalDate.now();
        when(contractRepository.findActiveContractsUpdatedAfter(person, today, updatedAfter))
                .thenReturn(List.of(contract));

        List<Contract> result = contractService.getActiveContracts(person, updatedAfter);

        // Assertions to verify the query for updated after is correctly invoked
        assertEquals(1, result.size());
        verify(contractRepository).findActiveContractsUpdatedAfter(person, today, updatedAfter);
    }

    /**
     * Test to verify retrieval of the total amount of active contracts.
     * This test ensures that the {@link ContractService#getTotalActiveContractsAmount(Person)} method 
     * correctly sums the cost of all active contracts.
     */
    @Test
    void getTotalActiveContractsAmount_returnsSumFromRepository() {
        LocalDate today = LocalDate.now();
        when(contractRepository.sumActiveCostByClient(person, today)).thenReturn(BigDecimal.valueOf(1234.56));

        BigDecimal total = contractService.getTotalActiveContractsAmount(person);

        // Assertions to verify that the total sum is returned correctly
        assertEquals(BigDecimal.valueOf(1234.56), total);
        verify(contractRepository).sumActiveCostByClient(person, today);
    }

    /**
     * Test to verify the closing of contracts when a client is deleted.
     * This test ensures that the end date of active contracts is set and saved 
     * when a client is deleted.
     */
    @Test
    void closeContractsOnClientDeletion_setsEndDateAndSavesAll() {
        when(contractRepository.findActiveByClient(person, LocalDate.now())).thenReturn(List.of(contract));
        doAnswer(inv -> {
            List<Contract> list = inv.getArgument(0);
            // Assertions to ensure the contracts are updated with end dates
            assertEquals(1, list.size());
            assertNotNull(list.get(0).getEndDate());
            return null;
        }).when(contractRepository).saveAll(anyList());

        contractService.closeContractsOnClientDeletion(person);

        // Verifying that contracts are fetched and saved correctly
        verify(contractRepository).findActiveByClient(person, LocalDate.now());
        verify(contractRepository).saveAll(anyList());
    }
}
