package ch.vaudoise.clientcontractapi.services.client;

import ch.vaudoise.clientcontractapi.models.entities.client.Company;
import ch.vaudoise.clientcontractapi.repositories.client.CompanyRepository;
import ch.vaudoise.clientcontractapi.services.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing operations related to {@link Company}.
 * Provides methods to create, update, retrieve, and delete companies.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CompanyService implements ClientService<Company> {

    private final CompanyRepository companyRepository;
    private final ContractService contractService;

    /**
     * Retrieves a list of all companies from the repository.
     *
     * @return a list of all {@link Company} entities
     */
    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    /**
     * Retrieves a company by its unique ID.
     *
     * @param id the unique identifier of the company
     * @return an {@link Optional} containing the {@link Company} if found, or empty if not found
     */
    public Optional<Company> getCompanyById(Long id) {
        return companyRepository.findById(id);
    }

    /**
     * Retrieves a company by its unique company identifier.
     *
     * @param identifier the unique company identifier
     * @return an {@link Optional} containing the {@link Company} if found, or empty if not found
     */
    public Optional<Company> getCompanyByIdentifier(String identifier) {
        return companyRepository.findByCompanyIdentifier(identifier);
    }

    /**
     * Creates a new {@link Company} entity and saves it to the repository.
     * Throws an exception if the company identifier already exists.
     *
     * @param company the {@link Company} entity to create
     * @return the created {@link Company} entity
     * @throws IllegalArgumentException if the company identifier already exists
     */
    public Company createCompany(Company company) {
        if (companyRepository.existsByCompanyIdentifier(company.getCompanyIdentifier())) {
            throw new IllegalArgumentException("Company identifier already exists");
        }
        return companyRepository.save(company);
    }

    /**
     * Updates an existing {@link Company} entity with new data.
     *
     * @param existing    the existing {@link Company} entity to update
     * @param updatedData the new data to update the existing entity with
     * @return the updated {@link Company} entity
     */
    public Company updateCompany(Company existing, Company updatedData) {
        existing.setName(updatedData.getName());
        existing.setEmail(updatedData.getEmail());
        existing.setPhone(updatedData.getPhone());
        existing.setUpdatedAt(LocalDate.now());
        return companyRepository.save(existing);
    }

    /**
     * Deletes a {@link Company} entity and ensures related contracts are closed.
     *
     * @param company the {@link Company} entity to delete
     */
    public void deleteCompany(Company company) {
        contractService.closeContractsOnClientDeletion(company);
        companyRepository.delete(company);
    }

    /**
     * Retrieves a {@link Company} entity by its unique identifier.
     *
     * @param id the unique identifier of the {@link Company} entity
     * @return an {@link Optional} containing the {@link Company} if found, or empty
     *         if not found
     */
    @Override
    public Optional<Company> getEntityById(Long id) {
        return companyRepository.findById(id);
    }
}
