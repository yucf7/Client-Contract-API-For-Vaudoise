package ch.vaudoise.clientcontractapi.controllers;

import ch.vaudoise.clientcontractapi.dtos.client.ClientDTO;
import ch.vaudoise.clientcontractapi.dtos.client.CompanyDTO;
import ch.vaudoise.clientcontractapi.dtos.client.PersonDTO;
import ch.vaudoise.clientcontractapi.models.entities.client.Client;
import ch.vaudoise.clientcontractapi.models.enums.ClientType;
import ch.vaudoise.clientcontractapi.services.handlers.ClientHandler;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Unified controller for managing all client types (e.g., Person, Company).
 * <p>
 * This controller delegates client-specific operations (CRUD) to the
 * appropriate
 * {@link ClientHandler} based on the provided {@link ClientType}, using the
 * Strategy pattern.
 */
@RestController
@RequestMapping(BaseController.API_V1 + "/clients")
@RequiredArgsConstructor
public class ClientController extends BaseController {

    /**
     * A mapping of {@link ClientType} to their respective {@link ClientHandler}.
     * Used to route operations to the correct strategy implementation.
     */
    private final Map<ClientType, ClientHandler<? extends Client, ? extends ClientDTO>> handlers;

    /**
     * Retrieves the appropriate {@link ClientHandler} for the given
     * {@link ClientType}.
     *
     * @param type the type of client (e.g., PERSON, COMPANY)
     * @return the corresponding {@link ClientHandler}
     * @throws IllegalArgumentException if no handler is found for the given type
     */
    private ClientHandler<? extends Client, ? extends ClientDTO> getHandler(ClientType type) {
        ClientHandler<? extends Client, ? extends ClientDTO> handler = handlers.get(type);
        if (handler == null) {
            throw new IllegalArgumentException("Unsupported client type: " + type);
        }
        return handler;
    }

    /**
     * Safely handles the creation of a client using dynamic casting to specific DTO
     * types.
     *
     * @param handler the handler responsible for the client type
     * @param dto     the generic DTO to be cast and used for creation
     * @param <D>     the specific type of {@link ClientDTO}
     * @return the created {@link ClientDTO}
     */
    @SuppressWarnings("unchecked")
    private <D extends ClientDTO> D handleCreate(ClientHandler<? extends Client, ? extends ClientDTO> handler,
            ClientDTO dto) {
        ClientHandler<? extends Client, D> specificHandler = (ClientHandler<? extends Client, D>) handler;
        D specificDto = specificHandler.convertToSpecificDto(dto);
        return specificHandler.create(specificDto);
    }

    /**
     * Safely handles the update of a client using dynamic casting to specific DTO
     * types.
     *
     * @param handler the handler responsible for the client type
     * @param id      the ID of the client to update
     * @param dto     the generic DTO to be cast and used for update
     * @param <D>     the specific type of {@link ClientDTO}
     * @return the updated {@link ClientDTO}
     */
    @SuppressWarnings("unchecked")
    private <D extends ClientDTO> D handleUpdate(ClientHandler<? extends Client, ? extends ClientDTO> handler, String id,
            ClientDTO dto) {
        ClientHandler<? extends Client, D> specificHandler = (ClientHandler<? extends Client, D>) handler;
        D specificDto = specificHandler.convertToSpecificDto(dto);
        return specificHandler.update(id, specificDto);
    }

    @Autowired
    public ClientController(List<ClientHandler<? extends Client, ? extends ClientDTO>> handlerList) {
        this.handlers = handlerList.stream()
                .collect(Collectors.toMap(ClientHandler::getSupportedClientType, h -> h));
    }

    /**
     * Retrieves all clients of a given {@link ClientType}.
     *
     * @param clientType the type of client to retrieve
     * @return a list of client DTOs
     */
    @GetMapping
    public ResponseEntity<List<? extends ClientDTO>> getAll(@RequestParam ClientType clientType) {
        ClientHandler<? extends Client, ? extends ClientDTO> handler = getHandler(clientType);
        List<? extends ClientDTO> clients = handler.getAll();
        return ResponseEntity.ok(clients);
    }

    /**
     * Retrieves a client by ID for a given {@link ClientType}.
     *
     * @param clientType the type of client
     * @param id         the ID of the client
     * @return the client DTO if found, otherwise 404 Not Found
     */
    @GetMapping("/{id}")
    public ResponseEntity<? extends ClientDTO> getById(@RequestParam ClientType clientType, @PathVariable String id) {
        ClientHandler<? extends Client, ? extends ClientDTO> handler = getHandler(clientType);
        ClientDTO dto = handler.getById(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    /**
     * Creates a new client of the specified {@link ClientType}.
     *
     * @param clientType the type of client to create
     * @param dto        the client DTO containing creation data
     * @return the created client DTO
     */
    @PostMapping
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload must match the selected clientType.\n\n"
            +
            "- For PERSON: include name, birthdate (yyyy-MM-dd), phone\n" +
            "- For COMPANY: include companyIdentifier", content = @Content(mediaType = "application/json", schema = @Schema(oneOf = {
                    PersonDTO.class, CompanyDTO.class }, discriminatorProperty = "type")))
    public ResponseEntity<? extends ClientDTO> create(@RequestBody ClientDTO dto) {
        ClientHandler<? extends Client, ? extends ClientDTO> handler = getHandler(dto.getType());
        System.out.println("__________\n");
        System.out.println(dto.getType().getClass());
        System.out.println("__________\n");
        ClientDTO created = handleCreate(handler, dto);
        return ResponseEntity.ok(created);
    }

    /**
     * Updates an existing client by ID for the specified {@link ClientType}.
     *
     * @param clientType the type of client
     * @param id         the ID of the client to update
     * @param dto        the client DTO containing updated data
     * @return the updated client DTO, or 404 if not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<? extends ClientDTO> update(@RequestParam ClientType clientType, @PathVariable String id,
            @RequestBody ClientDTO dto) {
        ClientHandler<? extends Client, ? extends ClientDTO> handler = getHandler(clientType);
        ClientDTO updated = handleUpdate(handler, id, dto);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    /**
     * Deletes a client by ID for the specified {@link ClientType}.
     *
     * @param clientType the type of client
     * @param id         the ID of the client to delete
     * @return 204 No Content if deletion is successful
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@RequestParam ClientType clientType, @PathVariable String id) {
        ClientHandler<? extends Client, ? extends ClientDTO> handler = getHandler(clientType);
        handler.delete(id);
        return ResponseEntity.noContent().build();
    }
}
