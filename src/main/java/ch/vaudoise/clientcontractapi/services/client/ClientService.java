package ch.vaudoise.clientcontractapi.services.client;

import ch.vaudoise.clientcontractapi.models.entities.client.Client;

import java.util.Optional;
import java.util.UUID;

/**
 * Generic service interface for retrieving client entities by ID.
 *
 * @param <T> the specific type of {@link Client} this service handles
 */
public interface ClientService<T extends Client> {

    /**
     * Retrieves a client entity by its unique ID.
     *
     * @param id the ID of the client to retrieve
     * @return an {@link Optional} containing the client entity if found, or empty if not found
     */
    Optional<T> getEntityById(UUID id);
}
