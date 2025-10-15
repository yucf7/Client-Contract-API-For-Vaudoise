package ch.vaudoise.clientcontractapi.controllers;

import ch.vaudoise.clientcontractapi.dtos.client.ClientDTO;
import ch.vaudoise.clientcontractapi.dtos.client.CompanyDTO;
import ch.vaudoise.clientcontractapi.dtos.client.PersonDTO;
import ch.vaudoise.clientcontractapi.dtos.client.ClientUpdateDTO;
import ch.vaudoise.clientcontractapi.models.enums.ClientType;
import ch.vaudoise.clientcontractapi.services.client.ClientOrchestrationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for {@link ClientController}.
 * Uses {@link WebMvcTest} to test controller behavior with mocked orchestration
 * service.
 */
@WebMvcTest(controllers = ClientController.class)
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientOrchestrationService clientOrchestrationService;

    @Autowired
    private ObjectMapper objectMapper;

    private PersonDTO samplePersonDto;
    private CompanyDTO sampleCompanyDto;
    private UUID sampleId;

    /**
     * Setup method to initialize common data for all tests.
     * This method creates a sample person DTO, a sample company DTO, and a sample UUID 
     * for use across all test methods.
     */
    @BeforeEach
    void setUp() {
        sampleId = UUID.randomUUID();

        // Initialize samplePersonDto
        samplePersonDto = new PersonDTO();
        samplePersonDto.setId(sampleId.toString());
        samplePersonDto.setName("John Doe");
        samplePersonDto.setEmail("john@example.com");
        samplePersonDto.setPhone("+41234567890");
        samplePersonDto.setBirthdate(LocalDate.parse("1980-01-01"));

        // Initialize sampleCompanyDto
        sampleCompanyDto = new CompanyDTO();
        sampleCompanyDto.setId(sampleId.toString());
        sampleCompanyDto.setName("Acme Ltd");
        sampleCompanyDto.setEmail("hello@acme.com");
        sampleCompanyDto.setPhone("+41234567891");
        sampleCompanyDto.setCompanyIdentifier("aaa-123");
    }

    /**
     * Test to verify if getting all clients of type PERSON returns the correct list.
     * This test checks if the controller correctly calls the service and returns a list of 
     * clients when the request is made for PERSON clients.
     * 
     * @throws Exception if there is an error during request execution
     */
    @Test
    void getAll_persons_returnsList() throws Exception {
        // Mock the service method to return a List of PersonDTO
        doReturn(List.of(samplePersonDto))
                .when(clientOrchestrationService)
                .getAllClients(ClientType.PERSON);

        // Perform GET request and assert correct response
        mockMvc.perform(get("/api/v1/clients")
                .param("clientType", "PERSON")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[0].email").value("john@example.com"));

        // Verify that the service method was called exactly once
        verify(clientOrchestrationService, times(1)).getAllClients(ClientType.PERSON);
    }

    /**
     * Test to verify if getting a client by ID for type PERSON returns the correct DTO.
     * This test ensures that the correct DTO is returned when querying for a PERSON client by ID.
     * 
     * @throws Exception if there is an error during request execution
     */
    @Test
    void getById_person_exists_returnsDto() throws Exception {
        // Mock the service method to return the PersonDTO
        when(clientOrchestrationService.getClientById(ClientType.PERSON, sampleId.toString()))
                .thenReturn(samplePersonDto);
        willDoNothing().given(clientOrchestrationService)
                .validateClientExists(ClientType.PERSON, sampleId.toString());

        // Perform GET request and assert correct response
        mockMvc.perform(get("/api/v1/clients/{id}", sampleId.toString())
                .param("clientType", "PERSON")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.birthdate").value("1980-01-01"));

        // Verify service methods were called
        verify(clientOrchestrationService).validateClientExists(ClientType.PERSON, sampleId.toString());
        verify(clientOrchestrationService).getClientById(ClientType.PERSON, sampleId.toString());
    }

    /**
     * Test to verify if creating a new company client returns the correct DTO.
     * This test checks that a company client can be created and that the controller returns
     * the expected DTO.
     * 
     * @throws Exception if there is an error during request execution
     */
    @Test
    void create_company_createsAndReturns() throws Exception {
        // Mock the service method to return the created CompanyDTO
        when(clientOrchestrationService.createClient(any(ClientDTO.class)))
                .thenReturn(sampleCompanyDto);

        // Create JSON with type property for polymorphic deserialization
        String companyJson = """
            {
                "id": "%s",
                "type": "COMPANY",
                "name": "Acme Ltd",
                "email": "hello@acme.com",
                "phone": "+41234567891",
                "companyIdentifier": "aaa-123"
            }
            """.formatted(sampleId.toString());

        // Perform POST request to create a client and assert correct response
        mockMvc.perform(post("/api/v1/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(companyJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Acme Ltd"))
                .andExpect(jsonPath("$.companyIdentifier").value("aaa-123"));

        // Verify service method was called
        verify(clientOrchestrationService).createClient(any(ClientDTO.class));
    }

    /**
     * Test to verify if creating a new person client returns the correct DTO.
     * This test checks that a person client can be created and that the controller returns
     * the expected DTO.
     * 
     * @throws Exception if there is an error during request execution
     */
    @Test
    void create_person_createsAndReturns() throws Exception {
        // Mock the service method to return the created PersonDTO
        when(clientOrchestrationService.createClient(any(ClientDTO.class)))
                .thenReturn(samplePersonDto);

        // Create JSON with type property for polymorphic deserialization
        String personJson = """
            {
                "id": "%s",
                "type": "PERSON",
                "name": "John Doe",
                "email": "john@example.com",
                "phone": "+41234567890",
                "birthdate": "1980-01-01"
            }
            """.formatted(sampleId.toString());

        // Perform POST request to create a client and assert correct response
        mockMvc.perform(post("/api/v1/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(personJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));

        // Verify service method was called
        verify(clientOrchestrationService).createClient(any(ClientDTO.class));
    }

    /**
     * Test to verify if updating a person client works and returns the correct response.
     * This test ensures that the update request for a person client triggers the correct service
     * method and returns the updated DTO.
     * 
     * @throws Exception if there is an error during request execution
     */
    @Test
    void update_person_callsServiceAndReturns() throws Exception {
        ClientUpdateDTO update = new ClientUpdateDTO();
        update.setName("John Updated");
        willDoNothing().given(clientOrchestrationService)
                .validateClientExists(ClientType.PERSON, sampleId.toString());

        // Mock the service method to return the updated PersonDTO
        when(clientOrchestrationService.updateClient(eq(ClientType.PERSON), eq(sampleId.toString()),
                any(ClientUpdateDTO.class)))
                .thenReturn(samplePersonDto);

        // Perform PUT request to update a client and assert correct response
        mockMvc.perform(put("/api/v1/clients/{id}", sampleId.toString())
                .param("clientType", "PERSON")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk());

        // Verify service methods were called
        verify(clientOrchestrationService).validateClientExists(ClientType.PERSON, sampleId.toString());
        verify(clientOrchestrationService).updateClient(eq(ClientType.PERSON), eq(sampleId.toString()),
                any(ClientUpdateDTO.class));
    }

    /**
     * Test to verify if deleting a company client works correctly.
     * This test ensures that when a DELETE request is made for a company client, the 
     * correct service method is triggered and that the response status is No Content.
     * 
     * @throws Exception if there is an error during request execution
     */
    @Test
    void delete_company_callsService() throws Exception {
        willDoNothing().given(clientOrchestrationService)
                .validateClientExists(ClientType.COMPANY, sampleId.toString());

        // Mock the service method to delete the company client
        doNothing().when(clientOrchestrationService).deleteClient(ClientType.COMPANY, sampleId.toString());

        // Perform DELETE request and assert correct response
        mockMvc.perform(delete("/api/v1/clients/{id}", sampleId.toString())
                .param("clientType", "COMPANY"))
                .andExpect(status().isNoContent());

        // Verify the service method was called
        verify(clientOrchestrationService).deleteClient(ClientType.COMPANY, sampleId.toString());
    }
}
