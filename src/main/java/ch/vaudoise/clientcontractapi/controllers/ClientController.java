package ch.vaudoise.clientcontractapi.controllers;

import ch.vaudoise.clientcontractapi.dtos.client.ClientDTO;
import ch.vaudoise.clientcontractapi.dtos.client.ClientUpdateDTO;
import ch.vaudoise.clientcontractapi.dtos.client.CompanyDTO;
import ch.vaudoise.clientcontractapi.dtos.client.PersonDTO;
import ch.vaudoise.clientcontractapi.models.enums.ClientType;
import ch.vaudoise.clientcontractapi.services.client.ClientOrchestrationService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(BaseController.API_V1 + "/clients")
@RequiredArgsConstructor
public class ClientController extends BaseController {

    private final ClientOrchestrationService clientOrchestrationService;

    /**
     * Retrieves all clients of a given {@link ClientType}.
     *
     * @param clientType the type of client to retrieve
     * @return a list of client DTOs
     */
    @GetMapping
    public ResponseEntity<List<? extends ClientDTO>> getAll(@RequestParam ClientType clientType) {
        List<? extends ClientDTO> clients = clientOrchestrationService.getAllClients(clientType);
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
        clientOrchestrationService.validateClientExists(clientType, id);
        ClientDTO dto = clientOrchestrationService.getClientById(clientType, id);
        return ResponseEntity.ok(dto);
    }

    /**
     * Creates a new client of the specified {@link ClientType}.
     *
     * @param dto the client DTO containing creation data
     * @return the created client DTO
     */
    @PostMapping
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload must match the selected clientType.\n\n"
            +
            "- For PERSON: birthdate (yyyy-MM-dd)\n" +
            "- For COMPANY: include companyIdentifier", content = @Content(mediaType = "application/json", schema = @Schema(oneOf = {
                    PersonDTO.class, CompanyDTO.class }, discriminatorProperty = "type")))
    public ResponseEntity<? extends ClientDTO> create(@Valid @RequestBody ClientDTO dto) {
        ClientDTO created = clientOrchestrationService.createClient(dto);
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
    public ResponseEntity<? extends ClientDTO> update(
            @RequestParam ClientType clientType,
            @PathVariable String id,
            @RequestBody ClientUpdateDTO dto) {

        clientOrchestrationService.validateClientExists(clientType, id);
        ClientDTO updated = clientOrchestrationService.updateClient(clientType, id, dto);
        return ResponseEntity.ok(updated);
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
        clientOrchestrationService.validateClientExists(clientType, id);
        clientOrchestrationService.deleteClient(clientType, id);
        return ResponseEntity.noContent().build();
    }
}