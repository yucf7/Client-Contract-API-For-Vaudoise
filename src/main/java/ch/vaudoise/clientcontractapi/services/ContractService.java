package ch.vaudoise.clientcontractapi.services;

import ch.vaudoise.clientcontractapi.models.entities.Contract;
import ch.vaudoise.clientcontractapi.models.entities.client.Client;
import ch.vaudoise.clientcontractapi.repositories.ContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service class for managing operations related to {@link Contract}.
 * Provides methods for creating, updating, retrieving, and deleting contracts.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ContractService {

    private final ContractRepository contractRepository;

    /**
     * Retrieves a contract by its unique identifier.
     *
     * @param id the unique identifier of the {@link Contract} entity
     * @return an {@link Optional} containing the {@link Contract} if found, or
     *         empty if not found
     */
    public Optional<Contract> findById(UUID id) {
        return contractRepository.findById(id);
    }

    /**
     * Creates a new contract with the provided details.
     * If the start date is null, it sets the current date as the start date.
     * Sets the current time as the last modified time.
     *
     * @param contract the {@link Contract} entity to create
     * @return the created {@link Contract} entity
     */
    public Contract createContract(Contract contract) {
        if (contract.getStartDate() == null) {
            contract.setStartDate(LocalDate.now());
        }
        contract.setLastModified(LocalDateTime.now());
        return contractRepository.save(contract);
    }

    /**
     * Updates the cost of an existing contract.
     * Sets the current time as the last modified time.
     *
     * @param contract the {@link Contract} entity to update
     * @param newCost  the new cost amount for the contract
     * @return the updated {@link Contract} entity
     */
    public Contract updateContractCost(Contract contract, Double newCost) {
        contract.setCostAmount(newCost);
        contract.setLastModified(LocalDateTime.now());
        return contractRepository.save(contract);
    }

    /**
     * Retrieves the active contracts for a given client.
     * A contract is considered active if its end date is either null or after
     * today's date.
     *
     * @param client the {@link Client} for whom to retrieve active contracts
     * @return a list of active {@link Contract} entities for the client
     */
    public List<Contract> getActiveContracts(Client client) {
        LocalDate today = LocalDate.now();
        return contractRepository.findActiveByClient(client, today);
    }

    /**
     * Retrieves the active contracts for a given client, updated after a specific
     * date.
     * A contract is considered active if its end date is either null or after
     * today's date.
     *
     * @param client       the {@link Client} for whom to retrieve active contracts
     * @param updatedAfter the {@link OffsetDateTime} after which the contracts were
     *                     updated
     * @return a list of active {@link Contract} entities for the client
     */
    public List<Contract> getActiveContracts(Client client, OffsetDateTime updatedAfter) {
        LocalDate today = LocalDate.now();
        return contractRepository.findActiveByClientAndUpdatedAfter(client, today, updatedAfter);
    }

    /**
     * Returns the total cost of all active contracts for a given client.
     * The sum is computed at the database level using {@link BigDecimal}.
     *
     * @param client the {@link Client} for whom to calculate the total cost of
     *               active contracts
     * @return the total cost of active contracts for the client
     */
    public BigDecimal getTotalActiveContractsAmount(Client client) {
        LocalDate today = LocalDate.now();
        return contractRepository.sumActiveCostByClient(client, today);
    }

    /**
     * Closes all active contracts for a client by setting their end date to today's
     * date.
     *
     * @param client the {@link Client} for whom to close the contracts
     */
    public void closeContractsOnClientDeletion(Client client) {
        LocalDate today = LocalDate.now();
        List<Contract> contracts = contractRepository.findActiveByClient(client, today);
        contracts.forEach(c -> c.setEndDate(today));
        contractRepository.saveAll(contracts);
    }
}
