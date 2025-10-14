package ch.vaudoise.clientcontractapi.services.handlers;

import java.util.List;
import java.util.UUID;

import ch.vaudoise.clientcontractapi.dtos.client.ClientDTO;
import ch.vaudoise.clientcontractapi.models.entities.client.Client;
import ch.vaudoise.clientcontractapi.models.enums.ClientType;

/**
 * Interface for handling client operations for a specific type of client.
 * <p>
 * This interface follows the Strategy pattern, allowing different
 * implementations
 * for each {@link ClientType} (e.g., Person, Company) with their corresponding
 * entity and DTO types.
 *
 * @param <T> the specific type of {@link Client} entity
 * @param <D> the specific type of {@link ClientDTO}
 */
public interface ClientHandler<T extends Client, D extends ClientDTO> {

    /**
     * Returns the client type supported by this handler.
     *
     * @return the {@link ClientType} associated with this handler
     */
    ClientType getClientType();

    /**
     * Retrieves all clients handled by this strategy.
     *
     * @return a list of client DTOs
     */
    List<D> getAll();

    /**
     * Retrieves a client by its ID.
     *
     * @param id the ID of the client
     * @return the client DTO, or {@code null} if not found
     */
    D getById(String id);

    /**
     * Creates a new client using the provided DTO.
     *
     * @param dto the DTO containing client data
     * @return the created client DTO
     */
    D create(D dto);

    /**
     * Updates an existing client with the given ID using the provided DTO.
     *
     * @param id  the ID of the client to update
     * @param dto the DTO containing updated client data
     * @return the updated client DTO, or {@code null} if not found
     */
    D update(String id, D dto);

    /**
     * Deletes a client by its ID.
     *
     * @param id the ID of the client to delete
     */
    void delete(String id);

    /**
     * Converts a generic {@link ClientDTO} into the specific DTO type {@code D}
     * used by this handler.
     *
     * @param genericDto the generic DTO to convert
     * @return the specific DTO instance of type {@code D}
     */
    D convertToSpecificDto(ClientDTO genericDto);

    ClientType getSupportedClientType();

}
