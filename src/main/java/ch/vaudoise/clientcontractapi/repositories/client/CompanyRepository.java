package ch.vaudoise.clientcontractapi.repositories.client;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ch.vaudoise.clientcontractapi.models.entities.client.Company;

/**
 * Repository interface for {@link Company} entity.
 * Provides database operations for retrieving and manipulating company records.
 */
@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    /**
     * Finds a {@link Company} by its unique company identifier.
     * 
     * @param companyIdentifier the unique identifier of the company
     * @return an {@link Optional} containing the {@link Company} if found, otherwise empty
     */
    Optional<Company> findByCompanyIdentifier(String companyIdentifier);

    /**
     * Checks if a {@link Company} with the specified company identifier exists in the database.
     * 
     * @param companyIdentifier the unique identifier of the company
     * @return {@code true} if a company with the specified identifier exists, {@code false} otherwise
     */
    boolean existsByCompanyIdentifier(String companyIdentifier);
}
