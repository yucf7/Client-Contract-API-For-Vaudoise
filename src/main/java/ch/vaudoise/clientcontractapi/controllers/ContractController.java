package ch.vaudoise.clientcontractapi.controllers;

import ch.vaudoise.clientcontractapi.dtos.ContractDTO;
import ch.vaudoise.clientcontractapi.mappers.CompanyMapper;
import ch.vaudoise.clientcontractapi.mappers.ContractMapper;
import ch.vaudoise.clientcontractapi.mappers.PersonMapper;
import ch.vaudoise.clientcontractapi.models.entities.client.Client;
import ch.vaudoise.clientcontractapi.models.enums.ClientType;
import ch.vaudoise.clientcontractapi.services.client.CompanyService;
import ch.vaudoise.clientcontractapi.services.client.PersonService;
import ch.vaudoise.clientcontractapi.services.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST controller for managing contracts of clients (Person or Company).
 * Uses {@link ContractService} for business logic and {@link ContractMapper}
 * for DTO mapping.
 */
@RestController
@RequestMapping(BaseController.API_V1 + "/contracts")
@RequiredArgsConstructor
public class ContractController extends BaseController {

        private final ContractService contractService;
        private final ContractMapper contractMapper;
        private final PersonService personService;
        private final CompanyService companyService;
        private final PersonMapper personMapper;
        private final CompanyMapper companyMapper;

        /**
         * Get active contracts for a specific client.
         *
         * @param clientId     the ID of the client
         * @param clientType   the type of client (PERSON or COMPANY)
         * @param updatedAfter optional filter for contracts updated after this date
         * @return list of active contract DTOs
         */
        @GetMapping("/client/{clientId}")
        public ResponseEntity<List<ContractDTO>> getActiveContracts(
                        @PathVariable String clientId,
                        @RequestParam ClientType clientType,
                        @RequestParam(required = false) OffsetDateTime updatedAfter) {
                Optional<? extends Client> clientOpt = resolveClient(clientId, clientType);
                if (clientOpt.isEmpty())
                        return ResponseEntity.notFound().build();

                List<ContractDTO> dtos = contractService
                                .getActiveContracts(clientOpt.get(), updatedAfter)
                                .stream()
                                .map(contractMapper::toDTO)
                                .collect(Collectors.toList());

                return ResponseEntity.ok(dtos);
        }

        /**
         * Create a new contract for a client.
         *
         * @param clientId   the ID of the client
         * @param clientType the type of client
         * @param dto        the contract data
         * @return created contract DTO
         */
        @PostMapping("/client/{clientId}")
        public ResponseEntity<ContractDTO> createContract(
                        @PathVariable String clientId,
                        @RequestParam ClientType clientType,
                        @RequestBody ContractDTO dto) {
                Optional<? extends Client> clientOpt = resolveClient(clientId, clientType);
                if (clientOpt.isEmpty())
                        return ResponseEntity.notFound().build();

                var contract = contractMapper.toEntity(dto);
                contract.setClient(clientOpt.get());
                var created = contractService.createContract(contract);
                return ResponseEntity.ok(contractMapper.toDTO(created));
        }

        /**
         * Update the cost amount of a contract.
         *
         * @param contractId the ID of the contract
         * @param cost       the new cost amount
         * @return updated contract DTO
         */
        @PutMapping("/{contractId}/cost")
        public ResponseEntity<ContractDTO> updateContractCost(
                        @PathVariable String contractId,
                        @RequestParam Double cost) {

                UUID contractUUID = contractMapper.map(contractId);

                Optional<ContractDTO> updated = contractService.findById(contractUUID)
                                .map(c -> contractMapper.toDTO(contractService.updateContractCost(c, cost)));

                return updated.map(ResponseEntity::ok)
                                .orElse(ResponseEntity.notFound().build());
        }

        /**
         * Get total sum of active contracts for a client.
         *
         * @param clientId   the ID of the client
         * @param clientType the type of client
         * @return total cost of active contracts
         */
        @GetMapping("/client/{clientId}/sum")
        public ResponseEntity<Double> getTotalActiveContractsAmount(
                        @PathVariable String clientId,
                        @RequestParam ClientType clientType) {
                Optional<? extends Client> clientOpt = resolveClient(clientId, clientType);
                if (clientOpt.isEmpty())
                        return ResponseEntity.notFound().build();

                return ResponseEntity.ok(
                                contractService.getTotalActiveContractsAmount(clientOpt.get()).doubleValue());
        }

        /**
         * Helper method to resolve a client based on its type.
         *
         * @param clientId   the client ID
         * @param clientType the client type
         * @return optional containing the client if found
         */
        private Optional<? extends Client> resolveClient(String clientId, ClientType clientType) {
                return switch (clientType) {
                        case PERSON -> personService.getPersonById(personMapper.map(clientId));
                        case COMPANY -> companyService.getCompanyById(companyMapper.map(clientId));
                };
        }

}
