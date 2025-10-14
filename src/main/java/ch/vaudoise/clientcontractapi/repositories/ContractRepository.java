package ch.vaudoise.clientcontractapi.repositories;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ch.vaudoise.clientcontractapi.models.entities.Contract;
import ch.vaudoise.clientcontractapi.models.entities.client.Client;

/**
 * Repository interface for {@link Contract} entity.
 * Provides database operations for retrieving and manipulating contracts.
 */
@Repository
public interface ContractRepository extends JpaRepository<Contract, UUID> {

    /**
     * Finds all active contracts for a given client.
     * A contract is considered active if its end date is either null or after today's date.
     *
     * @param client the {@link Client} for whom to retrieve active contracts
     * @param today the current date used for filtering active contracts
     * @return a list of active {@link Contract} entities for the specified client
     */
    @Query("""
        SELECT c FROM Contract c
        WHERE c.client = :client
          AND (c.endDate IS NULL OR c.endDate > :today)
    """)
    List<Contract> findActiveByClient(@Param("client") Client client, @Param("today") LocalDate today);

    /**
     * Finds all active contracts for a given client that have been updated after a specified date.
     * A contract is considered active if its end date is either null or after today's date.
     * 
     * @param client the {@link Client} for whom to retrieve active contracts
     * @param today the current date used for filtering active contracts
     * @param updatedAfter the {@link OffsetDateTime} after which the contract was updated
     * @return a list of active {@link Contract} entities for the specified client
     */
    @Query("""
        SELECT c FROM Contract c
        WHERE c.client = :client
          AND (c.endDate IS NULL OR c.endDate > :today)
          AND (:updatedAfter IS NULL OR c.lastModified > :updatedAfter)
    """)
    List<Contract> findActiveByClientAndUpdatedAfter(
            @Param("client") Client client,
            @Param("today") LocalDate today,
            @Param("updatedAfter") OffsetDateTime updatedAfter
    );

    /**
     * Calculates the total cost of all active contracts for a given client.
     * A contract is considered active if its end date is either null or after today's date.
     * The result is returned as a {@link BigDecimal} for precision.
     *
     * @param client the {@link Client} for whom to calculate the total active contract cost
     * @param today the current date used for filtering active contracts
     * @return the total sum of cost amounts for all active contracts for the client
     */
    @Query("""
        SELECT COALESCE(SUM(c.costAmount), 0)
        FROM Contract c
        WHERE c.client = :client
          AND (c.endDate IS NULL OR c.endDate > :today)
    """)
    BigDecimal sumActiveCostByClient(@Param("client") Client client, @Param("today") LocalDate today);
}
