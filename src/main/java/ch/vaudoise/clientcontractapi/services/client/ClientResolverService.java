package ch.vaudoise.clientcontractapi.services.client;

import ch.vaudoise.clientcontractapi.models.entities.client.Client;
import ch.vaudoise.clientcontractapi.models.enums.ClientType;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

/**
 * Service responsible for resolving a {@link Client} based on its type and ID.
 * This service uses {@link PersonService} and {@link CompanyService} to
 * retrieve
 * the appropriate client entity based on the provided {@link ClientType}.
 */
@Service
@RequiredArgsConstructor
public class ClientResolverService {

    private final PersonService personService;
    private final CompanyService companyService;

    /**
     * Resolves a {@link Client} entity based on the provided {@link ClientType} and
     * client ID.
     * 
     * @param type The type of client to resolve (either {@link ClientType#PERSON}
     *             or {@link ClientType#COMPANY}).
     * @param id   The ID of the client to retrieve.
     * @return The resolved {@link Client} entity.
     * @throws IllegalArgumentException if the client is not found for the given
     *                                  type.
     */
    public Optional<? extends Client> resolveClient(ClientType type, String id) {
        UUID uuid = UUID.fromString(id);
        return switch (type) {
            case PERSON -> personService.getEntityById(uuid).map(client -> (Client) client);
            case COMPANY -> companyService.getEntityById(uuid).map(client -> (Client) client);
        };
    }

}
