package ch.vaudoise.clientcontractapi.services.client;

import ch.vaudoise.clientcontractapi.dtos.client.*;
import ch.vaudoise.clientcontractapi.models.entities.client.Client;
import ch.vaudoise.clientcontractapi.models.entities.client.Company;
import ch.vaudoise.clientcontractapi.models.entities.client.Person;
import ch.vaudoise.clientcontractapi.models.enums.ClientType;
import ch.vaudoise.clientcontractapi.services.ContractService;
import ch.vaudoise.clientcontractapi.services.handlers.ClientHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service that orchestrates client operations by delegating
 * calls to the appropriate {@link ClientHandler} based on {@link ClientType}.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ClientOrchestrationService {

    private final Map<ClientType, ClientHandler<? extends Client, ? extends ClientDTO>> handlers;
    private final PersonService personService;
    private final CompanyService companyService;
    private final ClientResolverService clientResolverService;
    private final ContractService contractService;

    /**
     * Constructor that collects handlers into a map by client type.
     *
     * @param handlerList           list of available ClientHandler beans
     * @param personService         service handling Person clients
     * @param companyService        service handling Company clients
     * @param clientResolverService service for resolving clients
     * @param contractService       service for managing contracts
     */
    @Autowired
    public ClientOrchestrationService(
            List<ClientHandler<? extends Client, ? extends ClientDTO>> handlerList,
            PersonService personService,
            CompanyService companyService,
            ClientResolverService clientResolverService,
            ContractService contractService) {

        this.handlers = handlerList.stream()
                .collect(Collectors.toMap(ClientHandler::getSupportedClientType, h -> h));
        this.personService = personService;
        this.companyService = companyService;
        this.clientResolverService = clientResolverService;
        this.contractService = contractService;
    }

    /**
     * Retrieves all clients of a given {@link ClientType}.
     *
     * @param clientType the type of clients to retrieve
     * @return a list of client DTOs of the specified type
     */
    public List<? extends ClientDTO> getAllClients(ClientType clientType) {
        ClientHandler<? extends Client, ? extends ClientDTO> handler = getHandler(clientType);
        return handler.getAll();
    }

    /**
     * Retrieves a client by ID and {@link ClientType}.
     *
     * @param clientType the type of the client
     * @param id         the client's unique identifier
     * @return the client DTO matching the ID and type
     */
    public ClientDTO getClientById(ClientType clientType, String id) {
        ClientHandler<? extends Client, ? extends ClientDTO> handler = getHandler(clientType);
        return handler.getById(id);
    }

    /**
     * Creates a new client with appropriate type conversion based on the DTO.
     *
     * @param dto the client DTO containing client data
     * @return the created client DTO
     */
    @SuppressWarnings("unchecked")
    public ClientDTO createClient(ClientDTO dto) {
        ClientHandler<? extends Client, ? extends ClientDTO> handler = getHandler(dto.getType());

        if (dto.getType() == ClientType.PERSON) {
            ClientHandler<Person, PersonDTO> personHandler = (ClientHandler<Person, PersonDTO>) handler;
            PersonDTO specificDto = personHandler.convertToSpecificDto(dto);
            return personHandler.create(specificDto);
        } else {
            ClientHandler<Company, CompanyDTO> companyHandler = (ClientHandler<Company, CompanyDTO>) handler;
            CompanyDTO specificDto = companyHandler.convertToSpecificDto(dto);
            return companyHandler.create(specificDto);
        }
    }

    /**
     * Updates an existing client while preventing updates to immutable fields.
     *
     * @param clientType the type of the client
     * @param id         the unique identifier of the client to update
     * @param dto        the client DTO containing updated data
     * @return the updated client DTO
     * @throws IllegalArgumentException if the client type parameter and DTO type
     *                                  mismatch
     */
    @SuppressWarnings("unchecked")
    public ClientDTO updateClient(ClientType clientType, String id, ClientUpdateDTO dto) {

        ClientHandler<? extends Client, ? extends ClientDTO> handler = getHandler(clientType);

        if (clientType == ClientType.PERSON) {
            ClientHandler<Person, PersonDTO> personHandler = (ClientHandler<Person, PersonDTO>) handler;

            // Get existing person data first
            PersonDTO existingPerson = personHandler.getById(id);

            // Convert update DTO and merge with existing data
            PersonDTO updateData = personHandler.convertToSpecificDto(dto);
            PersonDTO mergedPerson = mergePersonData(existingPerson, updateData);
            clearImmutableFields(mergedPerson);

            return personHandler.update(id, mergedPerson);
        } else {
            ClientHandler<Company, CompanyDTO> companyHandler = (ClientHandler<Company, CompanyDTO>) handler;

            // Get existing company data first
            CompanyDTO existingCompany = companyHandler.getById(id);

            // Convert update DTO and merge with existing data
            CompanyDTO updateData = companyHandler.convertToSpecificDto(dto);
            CompanyDTO mergedCompany = mergeCompanyData(existingCompany, updateData);
            clearImmutableFields(mergedCompany);

            return companyHandler.update(id, mergedCompany);
        }
    }

    private PersonDTO mergePersonData(PersonDTO existing, PersonDTO update) {
        PersonDTO merged = new PersonDTO();
        // Only update fields that are provided in the request (non-null)
        merged.setName(update.getName() != null ? update.getName() : existing.getName());
        merged.setEmail(update.getEmail() != null ? update.getEmail() : existing.getEmail());
        merged.setPhone(update.getPhone() != null ? update.getPhone() : existing.getPhone());
        merged.setId(existing.getId());
        return merged;
    }

    private CompanyDTO mergeCompanyData(CompanyDTO existing, CompanyDTO update) {
        CompanyDTO merged = new CompanyDTO();
        // Only update fields that are provided in the request (non-null)
        merged.setName(update.getName() != null ? update.getName() : existing.getName());
        merged.setEmail(update.getEmail() != null ? update.getEmail() : existing.getEmail());
        merged.setPhone(update.getPhone() != null ? update.getPhone() : existing.getPhone());
        // Copy other necessary fields from existing
        merged.setId(existing.getId());
        return merged;
    }

    /**
     * Gets the handler corresponding to the given client type.
     *
     * @param type the client type
     * @return the ClientHandler supporting the given client type
     * @throws IllegalArgumentException if no handler is found for the type
     */
    private ClientHandler<? extends Client, ? extends ClientDTO> getHandler(ClientType type) {
        ClientHandler<? extends Client, ? extends ClientDTO> handler = handlers.get(type);
        if (handler == null) {
            throw new IllegalArgumentException(
                    "Unsupported client type: " + type + ". Available handlers: " + handlers.keySet());
        }
        return handler;
    }

    /**
     * Clears immutable fields from the given client DTO to prevent updates.
     *
     * @param dto the client DTO to clear immutable fields from
     */
    private void clearImmutableFields(ClientDTO dto) {
        if (dto instanceof PersonDTO) {
            ((PersonDTO) dto).setBirthdate(null);
        } else if (dto instanceof CompanyDTO) {
            ((CompanyDTO) dto).setCompanyIdentifier(null);
        }
    }

    /**
     * Validates that a client exists with the given ID and type.
     *
     * @param clientType the client type
     * @param id         the client ID
     * @throws IllegalArgumentException if the client does not exist
     */
    public void validateClientExists(ClientType clientType, String id) {
        UUID uuid = UUID.fromString(id);
        boolean exists = switch (clientType) {
            case PERSON -> personService.getEntityById(uuid).isPresent();
            case COMPANY -> companyService.getEntityById(uuid).isPresent();
        };

        if (!exists) {
            throw new IllegalArgumentException("Client not found with id: " + id + " and type: " + clientType);
        }
    }

    /**
     * Deletes a client by ID for the specified {@link ClientType}.
     * Also closes contracts related to the client before deletion.
     *
     * @param clientType the client type
     * @param id         the unique identifier of the client to delete
     * @throws IllegalArgumentException if the client is not found
     */
    public void deleteClient(ClientType clientType, String id) {
        // Use ClientResolverService directly
        Client client = clientResolverService.resolveClient(clientType, id)
                .orElseThrow(() -> new IllegalArgumentException("Client not found"));

        // Close contracts associated with the client before deletion
        contractService.closeContractsOnClientDeletion(client);

        // Delete the client entity using the appropriate handler
        ClientHandler<? extends Client, ? extends ClientDTO> handler = getHandler(clientType);
        handler.delete(id);
    }
}
