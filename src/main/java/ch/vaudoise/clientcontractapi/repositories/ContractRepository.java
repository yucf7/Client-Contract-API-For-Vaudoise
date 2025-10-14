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
   * A contract is considered active if its end date is either null or after
   * today's date.
   *
   * @param client the {@link Client} for whom to retrieve active contracts
   * @param today  the current date used for filtering active contracts
   * @return a list of active {@link Contract} entities for the specified client
   */
  @Query("""
          SELECT c FROM Contract c
          WHERE c.client = :client
            AND (c.endDate IS NULL OR c.endDate > :today)
      """)
  List<Contract> findActiveByClient(@Param("client") Client client, @Param("today") LocalDate today);

  /**
   * Finds all active contracts for the given client where the contract's end date
   * is either null
   * (meaning no end date) or later than the specified date (typically the current
   * date).
   *
   * Active contracts are those considered valid as of the given date.
   *
   * @param client the client whose contracts are being queried
   * @param today  the date used to determine if the contract is active (usually
   *               current date)
   * @return a list of active contracts for the specified client
   */
  @Query("""
          SELECT c FROM Contract c
          WHERE c.client = :client
            AND (c.endDate IS NULL OR c.endDate > :today)
      """)
  List<Contract> findActiveContracts(
      @Param("client") Client client,
      @Param("today") LocalDate today);

  /**
   * Finds all active contracts for the given client where the contract's end date
   * is either null
   * or later than the specified date (usually the current date), and where the
   * contract has been
   * last modified after the provided date.
   *
   * This method is useful to fetch only contracts updated since a certain
   * timestamp.
   *
   * @param client       the client whose contracts are being queried
   * @param today        the date used to determine if the contract is active
   *                     (usually current date)
   * @param updatedAfter the cutoff date for the last modification timestamp;
   *                     contracts modified
   *                     after this date will be returned
   * @return a list of active contracts for the specified client updated after the
   *         given timestamp
   */
  @Query("""
          SELECT c FROM Contract c
          WHERE c.client = :client
            AND (c.endDate IS NULL OR c.endDate > :today)
            AND c.lastModified > :updatedAfter
      """)
  List<Contract> findActiveContractsUpdatedAfter(
      @Param("client") Client client,
      @Param("today") LocalDate today,
      @Param("updatedAfter") OffsetDateTime updatedAfter);

  /**
   * Calculates the total cost of all active contracts for a given client.
   * A contract is considered active if its end date is either null or after
   * today's date.
   * The result is returned as a {@link BigDecimal} for precision.
   *
   * @param client the {@link Client} for whom to calculate the total active
   *               contract cost
   * @param today  the current date used for filtering active contracts
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
