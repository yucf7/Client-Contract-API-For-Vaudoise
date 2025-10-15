package ch.vaudoise.clientcontractapi.controllers;

import ch.vaudoise.clientcontractapi.dtos.ContractDTO;
import ch.vaudoise.clientcontractapi.mappers.ContractMapper;
import ch.vaudoise.clientcontractapi.models.entities.client.Person;
import ch.vaudoise.clientcontractapi.models.enums.ClientType;
import ch.vaudoise.clientcontractapi.services.ContractService;
import ch.vaudoise.clientcontractapi.services.client.ClientResolverService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for {@link ContractController}.
 * Controller is exercised with mocked ContractService, ContractMapper, and
 * ClientResolverService.
 */
@WebMvcTest(controllers = ContractController.class)
class ContractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContractService contractService;

    @MockBean
    private ContractMapper contractMapper;

    @MockBean
    private ClientResolverService clientResolverService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID clientUuid;
    private Person personEntity;
    private ContractDTO sampleDto;

    /**
     * Sets up the test environment by initializing required mock objects and test data.
     */
    @BeforeEach
    void setUp() {
        clientUuid = UUID.randomUUID();

        personEntity = new Person();
        personEntity.setId(clientUuid);

        sampleDto = new ContractDTO();
        sampleDto.setId(UUID.randomUUID().toString());
        sampleDto.setClientId(clientUuid.toString());
        sampleDto.setCostAmount(123.45);
    }

    /**
     * Test to ensure that the endpoint for retrieving active contracts returns a list of contracts.
     * 
     * @throws Exception if there is an error during the request execution
     */
    @Test
    void getActiveContracts_returnsList() throws Exception {
        doReturn(Optional.of(personEntity))
                .when(clientResolverService)
                .resolveClient(ClientType.PERSON, clientUuid.toString());

        // ContractService returns domain contracts; mapper converts to DTOs.
        when(contractService.getActiveContracts(eq(personEntity), any(OffsetDateTime.class)))
                .thenReturn(List.of());
        
        // Perform the GET request and ensure OK status
        mockMvc.perform(get("/api/v1/contracts/{clientId}", clientUuid.toString())
                .param("clientType", "PERSON"))
                .andExpect(status().isOk());

        verify(clientResolverService).resolveClient(ClientType.PERSON, clientUuid.toString());
        verify(contractService).getActiveContracts(personEntity, null);
    }

    /**
     * Test to ensure that the endpoint for creating a contract correctly creates and returns a contract DTO.
     * 
     * @throws Exception if there is an error during the request execution
     */
    @Test
    void createContract_createsAndReturnsDto() throws Exception {
        doReturn(Optional.of(personEntity))
                .when(clientResolverService)
                .resolveClient(ClientType.PERSON, clientUuid.toString());

        // map DTO->entity and entity->dto flow
        when(contractMapper.toEntity(any(ContractDTO.class)))
                .thenReturn(new ch.vaudoise.clientcontractapi.models.entities.Contract());
        when(contractService.createContract(any(ch.vaudoise.clientcontractapi.models.entities.Contract.class)))
                .thenReturn(new ch.vaudoise.clientcontractapi.models.entities.Contract());
        when(contractMapper.toDTO(any(ch.vaudoise.clientcontractapi.models.entities.Contract.class)))
                .thenReturn(sampleDto);

        mockMvc.perform(post("/api/v1/contracts/{clientId}", clientUuid.toString())
                .param("clientType", "PERSON")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientId").value(clientUuid.toString()));

        verify(contractService).createContract(any());
    }

    /**
     * Test to ensure that the endpoint for updating a contract's cost works as expected.
     * 
     * @throws Exception if there is an error during the request execution
     */
    @Test
    void updateContractCost_updatesAndReturns() throws Exception {
        UUID contractUuid = UUID.randomUUID();
        ch.vaudoise.clientcontractapi.models.entities.Contract entity = new ch.vaudoise.clientcontractapi.models.entities.Contract();

        // contractMapper.map(contractId) should convert String->UUID in your mapper - mock it
        when(contractMapper.map(eq(contractUuid.toString()))).thenReturn(contractUuid);

        when(contractService.findById(contractUuid)).thenReturn(Optional.of(entity));
        when(contractService.updateContractCost(eq(entity), eq(200.0))).thenReturn(entity);
        when(contractMapper.toDTO(entity)).thenReturn(sampleDto);

        mockMvc.perform(put("/api/v1/contracts/{contractId}/cost", contractUuid.toString())
                .param("cost", "200.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sampleDto.getId()));

        verify(contractService).updateContractCost(entity, 200.0);
    }

    /**
     * Test to ensure that the endpoint for retrieving the total active contract amount returns the correct value.
     * 
     * @throws Exception if there is an error during the request execution
     */
    @Test
    void getTotalActiveContractsAmount_returnsNumber() throws Exception {
        doReturn(Optional.of(personEntity))
                .when(clientResolverService)
                .resolveClient(ClientType.PERSON, clientUuid.toString());

        when(contractService.getTotalActiveContractsAmount(personEntity))
                .thenReturn(java.math.BigDecimal.valueOf(777.77));

        mockMvc.perform(get("/api/v1/contracts/{clientId}/sum", clientUuid.toString())
                .param("clientType", "PERSON"))
                .andExpect(status().isOk())
                .andExpect(content().string("777.77"));

        verify(contractService).getTotalActiveContractsAmount(personEntity);
    }
}
