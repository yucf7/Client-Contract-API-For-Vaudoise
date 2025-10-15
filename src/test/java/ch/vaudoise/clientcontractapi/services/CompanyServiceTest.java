package ch.vaudoise.clientcontractapi.services;

import ch.vaudoise.clientcontractapi.models.entities.client.Company;
import ch.vaudoise.clientcontractapi.repositories.client.CompanyRepository;
import ch.vaudoise.clientcontractapi.services.client.CompanyService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link CompanyService}.
 * These tests verify the behavior of the {@link CompanyService} class, ensuring that it 
 * correctly interacts with the {@link CompanyRepository} and {@link ContractService}.
 */
@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private ContractService contractService;

    @InjectMocks
    private CompanyService companyService;

    private Company company;

    /**
     * Setup method to initialize common data for all tests.
     * This method creates a sample Company object with predefined values for testing.
     */
    @BeforeEach
    void setUp() {
        company = new Company();
        company.setId(UUID.randomUUID());
        company.setName("Acme Inc");
        company.setEmail("info@acme.com");
        company.setPhone("+41234567891");
        company.setCompanyIdentifier("aaa-123");
    }

    /**
     * Test to verify that creating a company works when the company identifier is unique.
     * This test checks if the company is successfully saved when its identifier doesn't 
     * already exist in the repository.
     * 
     * @throws Exception if there is an error during method execution
     */
    @Test
    void createCompany_savesWhenIdentifierUnique() {
        // Mocking behavior for unique company identifier
        when(companyRepository.existsByCompanyIdentifier("aaa-123")).thenReturn(false);
        when(companyRepository.save(any(Company.class))).thenReturn(company);

        // Create company and verify
        Company created = companyService.createCompany(company);

        // Assertions to verify the result
        assertNotNull(created);
        assertEquals("Acme Inc", created.getName());
        verify(companyRepository).save(company);
    }

    /**
     * Test to verify that an exception is thrown when attempting to create a company
     * with a non-unique identifier.
     * This test ensures that the service correctly throws an exception when the 
     * company identifier already exists in the repository.
     * 
     * @throws Exception if there is an error during method execution
     */
    @Test
    void createCompany_throwsWhenIdentifierExists() {
        // Mocking behavior for existing company identifier
        when(companyRepository.existsByCompanyIdentifier("aaa-123")).thenReturn(true);

        // Check if exception is thrown
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> companyService.createCompany(company));
        assertTrue(ex.getMessage().contains("already exists"));
        verify(companyRepository, never()).save(any());
    }

    /**
     * Test to verify that updating a company works as expected.
     * This test verifies that the company fields are updated correctly and saved
     * in the repository.
     * 
     * @throws Exception if there is an error during method execution
     */
    @Test
    void updateCompany_updatesFieldsAndSaves() {
        Company updated = new Company();
        updated.setName("Acme Updated");
        updated.setPhone("+41987654322");
        updated.setEmail("contact@acme.com");

        // Mocking the save behavior to return the updated company
        when(companyRepository.save(any(Company.class))).thenAnswer(inv -> inv.getArgument(0));

        // Update company and verify results
        Company res = companyService.updateCompany(company, updated);

        // Assertions to verify the updated fields
        assertEquals("Acme Updated", res.getName());
        assertEquals("+41987654322", res.getPhone());
        verify(companyRepository).save(company);
    }

    /**
     * Test to verify that deleting a company correctly closes associated contracts
     * and deletes the company from the repository.
     * This test checks if the company deletion process is handled correctly, including
     * the closing of contracts.
     * 
     * @throws Exception if there is an error during method execution
     */
    @Test
    void deleteCompany_closesContracts_thenDeletes() {
        // Mocking behavior for contract closing and company deletion
        doNothing().when(contractService).closeContractsOnClientDeletion(company);
        doNothing().when(companyRepository).delete(company);

        // Delete company and verify the actions
        companyService.deleteCompany(company);

        // Verifying that the contract closing and deletion happened
        verify(contractService).closeContractsOnClientDeletion(company);
        verify(companyRepository).delete(company);
    }

    /**
     * Test to verify that retrieving a company by ID returns the correct result.
     * This test checks if the company is retrieved from the repository correctly
     * when an ID is provided.
     * 
     * @throws Exception if there is an error during method execution
     */
    @Test
    void getCompanyById_returnsOptional() {
        UUID id = company.getId();
        when(companyRepository.findById(id)).thenReturn(Optional.of(company));

        // Retrieve company by ID and verify
        Optional<Company> found = companyService.getCompanyById(id);

        // Assertions to verify the result
        assertTrue(found.isPresent());
        assertEquals("Acme Inc", found.get().getName());
    }
}
